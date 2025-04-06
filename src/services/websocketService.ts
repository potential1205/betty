import { Client, StompHeaders } from "@stomp/stompjs";
import { Pixel } from "../types/types";
import { getAccessToken } from "../apis/axios";

interface WebSocketServiceProps {
  onConnect: () => void;
  onDisconnect: () => void;
  onInitialCanvas: (pixels: Pixel[]) => void;
  onPixelUpdate: (pixel: Pixel | PixelUpdateMessage) => void;
}

// 환경 변수에서 API URL 가져오기
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// WebSocket URL 생성 함수 - 액세스 토큰을 쿼리 파라미터로 추가
const getWebSocketUrl = () => {
  const token = getAccessToken();
  
  // API URL에서 호스트만 추출
  const url = new URL(API_URL);
  
  // HTTPS인 경우 WSS, HTTP인 경우 WS 사용
  const protocol = url.protocol === 'https:' ? 'wss:' : 'ws:';
  
  // 최종 WebSocket URL 생성 (액세스 토큰을 쿼리 파라미터로 추가)
  return `${protocol}//${url.host}/ws?access_token=${token}`;
};

// 새로운 메시지 타입 정의 수정
interface PixelUpdateMessage {
  gameId: number;  // 문자열에서 숫자로 변경
  teamId: number;  // 문자열에서 숫자로 변경
  r: number;       // 행(row)
  c: number;       // 열(column)
  color: string;
  walletAddress?: string; // 백엔드에서 설정됨
}

export function useWebSocketService({
  onConnect,
  onDisconnect,
  onInitialCanvas,
  onPixelUpdate,
}: WebSocketServiceProps) {
  let stompClient: Client | null = null;

  // gameId와 teamCode 매개변수 기본값 수정
  const connect = (walletAddress: string, gameId: string = '1', teamCode: string = '1') => {
    const token = getAccessToken();
    if (!token) {
      console.error("인증 토큰이 없습니다");
      return;
    }
    
    const wsUrl = getWebSocketUrl();
    
    stompClient = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        // 전체 캔버스 상태 구독
        stompClient?.subscribe(`/topic/board/${parseInt(gameId, 10)}/${parseInt(teamCode, 10)}`, (message) => {
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
            console.error('초기 캔버스 데이터 파싱 오류:', error);
          }
        });

        // 픽셀 업데이트 구독
        stompClient?.subscribe(`/topic/pixelUpdate/${parseInt(gameId, 10)}/${parseInt(teamCode, 10)}`, (message) => {
          try {
            const pixel = JSON.parse(message.body);
            if (onPixelUpdate) onPixelUpdate(pixel);
          } catch (error) {
            console.error('픽셀 업데이트 데이터 파싱 오류:', error);
          }
        });
        
        // 게임 종료 메시지 구독
        stompClient?.subscribe(`/topic/gameEnd/${parseInt(gameId, 10)}/${parseInt(teamCode, 10)}`, (message) => {
          if (message.body === "GAME_OVER") {
            onDisconnect();
          }
        });

        // 초기 캔버스 데이터 요청
        stompClient?.publish({
          destination: `/app/getBoard/${parseInt(gameId, 10)}/${parseInt(teamCode, 10)}`,
          body: JSON.stringify({})
        });

        if (onConnect) onConnect();
      },
      onDisconnect: () => {
        if (onDisconnect) onDisconnect();
      },
      onStompError: (frame) => {
        console.error('STOMP 오류:', frame.headers['message'], frame.body);
        if (onDisconnect) onDisconnect();
      },
      onWebSocketError: (event) => {
        console.error('웹소켓 오류:', event);
        if (onDisconnect) onDisconnect();
      }
    });

    stompClient.activate();
  };

  const disconnect = () => {
    stompClient?.deactivate();
  };

  const sendPixel = (pixel: Pixel, gameId: string = '1', teamCode: string = '1') => {
    if (stompClient?.connected) {
      const message: PixelUpdateMessage = {
        gameId: parseInt(gameId, 10),
        teamId: parseInt(teamCode, 10),
        r: pixel.y,
        c: pixel.x,
        color: pixel.color,
        walletAddress: pixel.walletAddress,
      };

      stompClient.publish({
        destination: `/app/updatePixel`,
        body: JSON.stringify(message)
      });
    } else {
      console.error("웹소켓 연결이 없습니다");
    }
  };

  return {
    connect,
    disconnect,
    sendPixel,
  };
} 