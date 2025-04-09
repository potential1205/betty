import { Client, StompHeaders } from "@stomp/stompjs";
import { Pixel } from "../types/types";
import { getAccessToken } from "../apis/axios";
import { useDisplayStore } from "../stores/displayStore";

interface WebSocketServiceProps {
  onConnect?: () => void;
  onDisconnect?: () => void;
  onInitialCanvas?: (pixels: Pixel[]) => void;
  onPixelUpdate?: (pixel: Pixel) => void;
  onGameEnd?: () => void;
}

// 환경 변수에서 API URL 가져오기
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// WebSocket URL 생성 함수 - 액세스 토큰, game_id, team_id를 쿼리 파라미터로 추가
const getWebSocketUrl = (gameId: string, teamId: string) => {
  const token = getAccessToken();
  
  // API URL에서 호스트만 추출
  const url = new URL(API_URL);
  
  // HTTPS인 경우 WSS, HTTP인 경우 WS 사용
  const protocol = url.protocol === 'https:' ? 'wss:' : 'ws:';
  
  // 최종 WebSocket URL 생성 (액세스 토큰, game_id, team_id, type을 쿼리 파라미터로 추가)
  const wsUrl = `${protocol}//${url.host}/ws?access_token=${token}&game_id=${gameId}&team_id=${teamId}&type=display`;
  console.log(`웹소켓 URL 생성: ${wsUrl}`);
  return wsUrl;
};

// 새로운 메시지 타입 정의
interface PixelUpdateMessage {
  gameId: number;
  teamId: number;
  r: number;
  c: number;
  color: string;
  walletAddress?: string;
}

// 전역 연결 상태 관리 변수 추가
let isConnecting = false;
let isConnected = false;
let stompClient: Client | null = null;

export function useWebSocketService({
  onConnect,
  onDisconnect,
  onInitialCanvas,
  onPixelUpdate,
  onGameEnd
}: WebSocketServiceProps) {
  // connect 함수 개선
  const connect = (gameId: number, teamId: number) => {
    // 이미 연결 중이거나 연결된 경우 중복 연결 방지
    if (isConnecting || isConnected) {
      console.log("[WebSocketService] 이미 웹소켓 연결 중이거나 연결되어 있습니다");
      return;
    }

    const token = getAccessToken();
    if (!token) {
      console.error("[WebSocketService] 인증 토큰이 없습니다");
      return;
    }
    
    isConnecting = true;
    
    // gameId와 teamId를 문자열로 변환
    const gameIdStr = gameId.toString();
    const teamIdStr = teamId.toString();
    
    console.log(`[WebSocketService] 웹소켓 연결 시도: 게임 ID ${gameIdStr}, 팀 ID ${teamIdStr}`);
    
    // 게임 ID와 팀 ID를 포함한 WebSocket URL 생성
    const wsUrl = getWebSocketUrl(gameIdStr, teamIdStr);
    
    stompClient = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log(`[WebSocketService] 웹소켓 연결 성공: 게임 ID ${gameIdStr}, 팀 ID ${teamIdStr}`);
        isConnecting = false;
        isConnected = true;
        
        // 전체 캔버스 상태 구독
        stompClient?.subscribe(`/topic/board/${gameId}/${teamId}`, (message) => {
          try {
            const pixelGrid = JSON.parse(message.body);
            
            if (Array.isArray(pixelGrid)) {
              const flatPixels: Pixel[] = [];
              
              for (let r = 0; r < pixelGrid.length; r++) {
                const row = pixelGrid[r];
                if (Array.isArray(row)) {
                  for (let c = 0; c < row.length; c++) {
                    const pixelData = row[c];
                    if (pixelData.color && pixelData.color !== "ffffff") {
                      flatPixels.push({
                        x: c,
                        y: r,
                        color: pixelData.color.startsWith('#') ? pixelData.color : `#${pixelData.color}`,
                        walletAddress: pixelData.walletAddress || ""
                      });
                    }
                  }
                }
              }
              
              if (onInitialCanvas) onInitialCanvas(flatPixels);
            }
          } catch (error) {
            console.error('[WebSocketService] 초기 캔버스 데이터 파싱 오류:', error);
          }
        });

        // 픽셀 업데이트 구독
        stompClient?.subscribe(`/topic/pixelUpdate/${gameId}/${teamId}`, (message) => {
          try {
            const pixel = JSON.parse(message.body);
            if (onPixelUpdate) onPixelUpdate(pixel);
          } catch (error) {
            console.error('[WebSocketService] 픽셀 업데이트 데이터 파싱 오류:', error);
          }
        });
        
        // 게임 종료 메시지 구독
        stompClient?.subscribe(`/topic/gameEnd/${gameId}/${teamId}`, (message) => {
          console.log(`[WebSocketService] 게임 종료 메시지 수신: ${message.body}`);
          
          if (message.body === "GAME_OVER" && onGameEnd) {
            console.log('[WebSocketService] 게임 종료 이벤트 발생: 캔버스를 닫고 게임 종료 상태로 전환합니다.');
            onGameEnd();
          }
        });

        // 초기 캔버스 데이터 요청
        stompClient?.publish({
          destination: `/app/getBoard/${gameId}/${teamId}`,
          body: JSON.stringify({})
        });

        if (onConnect) onConnect();
      },
      onDisconnect: () => {
        console.log('[WebSocketService] 웹소켓 연결 종료');
        isConnecting = false;
        isConnected = false;
        if (onDisconnect) onDisconnect();
      },
      onStompError: (frame) => {
        console.error('[WebSocketService] STOMP 오류:', frame.headers['message'], frame.body);
        isConnecting = false;
        isConnected = false;
        if (onDisconnect) onDisconnect();
      },
      onWebSocketError: (event) => {
        console.error('[WebSocketService] 웹소켓 오류:', event);
        isConnecting = false;
        isConnected = false;
        if (onDisconnect) onDisconnect();
      }
    });

    stompClient.activate();
  };

  // disconnect 함수 개선
  const disconnect = () => {
    if (!stompClient) {
      console.log('[WebSocketService] 웹소켓 클라이언트가 없습니다. 연결 해제 불필요');
      isConnected = false;
      isConnecting = false;
      return;
    }
    
    if (stompClient.connected) {
      console.log('[WebSocketService] 웹소켓 연결 종료 요청');
      stompClient.deactivate();
    } else {
      console.log('[WebSocketService] 이미 연결이 해제되었거나 연결 중이 아닙니다');
      isConnected = false;
      isConnecting = false;
      // 콜백 호출이 필요한 경우에만 호출
      if (!isConnected && !isConnecting && onDisconnect) {
        onDisconnect();
      }
    }
  };

  const sendPixel = (pixel: Pixel) => {
    if (!stompClient?.connected) {
      console.error("[WebSocketService] 웹소켓 연결이 없습니다");
      return;
    }
    
    // 스토어에서 현재 게임 ID와 팀 ID 가져오기
    const { currentGameId, currentTeamId } = useDisplayStore.getState();
    
    if (!currentGameId || !currentTeamId) {
      console.error("[WebSocketService] 게임 ID 또는 팀 ID가 설정되지 않았습니다");
      return;
    }
    
    // 정수로 변환
    const gameIdInt = parseInt(currentGameId, 10);
    const teamIdInt = parseInt(currentTeamId, 10);
    
    const message: PixelUpdateMessage = {
      gameId: gameIdInt,
      teamId: teamIdInt,
      r: pixel.y,
      c: pixel.x,
      color: pixel.color,
      walletAddress: pixel.walletAddress,
    };

    console.log(`[WebSocketService] 픽셀 업데이트 전송: 게임 ID ${gameIdInt}, 팀 ID ${teamIdInt}, 위치 (${pixel.x}, ${pixel.y}), 색상 ${pixel.color}`);
    
    stompClient.publish({
      destination: `/app/updatePixel`,
      body: JSON.stringify(message)
    });
  };

  // 현재 연결 상태 확인 함수 추가
  const getConnectionStatus = () => {
    return {
      isConnecting,
      isConnected
    };
  };

  return {
    connect,
    disconnect,
    sendPixel,
    getConnectionStatus // 연결 상태 확인 함수 추가
  };
} 