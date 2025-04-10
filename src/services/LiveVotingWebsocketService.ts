import { Client, StompHeaders } from "@stomp/stompjs";
import { getAccessToken } from "../apis/axios";
import { useLiveSocketStore } from "../stores/liveSocketStore";

// 환경 변수에서 API URL 가져오기
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// 문제 데이터 인터페이스 (RedisGameProblem)
export interface Problem {
  problemId: string;
  gameId: string;
  inning: string;
  attackTeam: string;
  batterName: string;
  questionCode: string;
  description: string;
  options: string[];
  answer: string | null;
  push: boolean;
  timestamp: string;
}

// 게임 정보 DTO
export interface GameInfoDto {
  inning: string;
  score: string;
}

// 게임 문제 DTO
export interface GameProblemDto {
  redisGameProblem: Problem;
}

// 게임 상태 인터페이스 (클라이언트 내부용)
export interface GameState {
  inning: string;
  score: {
    homeScore: number;
    awayScore: number;
  };
  status: string;
}

// 콜백 인터페이스
interface LiveVotingCallbacks {
  onConnect?: () => void;
  onDisconnect?: () => void;
  onGameUpdate?: (gameState: GameState) => void;
  onProblemReceived?: (problem: Problem) => void;
}

// WebSocket URL 생성 함수
const getWebSocketUrl = (gameId: string) => {
  const token = getAccessToken();
  
  // API URL에서 호스트만 추출
  const url = new URL(API_URL);
  
  // HTTPS인 경우 WSS, HTTP인 경우 WS 사용
  const protocol = url.protocol === 'https:' ? 'wss:' : 'ws:';
  
  // 최종 WebSocket URL 생성 (액세스 토큰, game_id, type을 쿼리 파라미터로 추가)
  const wsUrl = `${protocol}//${url.host}/ws?access_token=${token}&game_id=${gameId}&type=game`;
  console.log(`[LiveVotingService] 웹소켓 URL 생성: ${wsUrl}`);
  return wsUrl;
};

/**
 * 이 훅은 기존 코드와의 호환성을 위해 남겨두었습니다.
 * 내부적으로는 liveSocketStore를 사용하여 실제 상태 관리와 웹소켓 연결을 처리합니다.
 */
export function useLiveVotingWebsocketService({
  onConnect,
  onDisconnect,
  onGameUpdate,
  onProblemReceived
}: LiveVotingCallbacks) {
  // liveSocketStore에서 필요한 메소드 가져오기
  const { 
    connect: storeConnect, 
    disconnect: storeDisconnect,
    submitAnswer: storeSubmitAnswer,
    setCallbacks,
    getConnectionStatus: storeGetConnectionStatus
  } = useLiveSocketStore.getState();
  
  // 콜백 설정
  setCallbacks({
    onConnect,
    onDisconnect,
    onGameUpdate,
    onProblemReceived
  });
  
  // 연결 함수 (스토어에 위임)
  const connect = (gameId: number) => {
    console.log(`[LiveVotingService] 웹소켓 연결 시도 (스토어로 위임): 게임 ID ${gameId}`);
    storeConnect(gameId);
  };
  
  // 연결 해제 함수 (스토어에 위임)
  const disconnect = () => {
    console.log('[LiveVotingService] 웹소켓 연결 종료 요청 (스토어로 위임)');
    storeDisconnect();
  };
  
  // 연결 상태 확인 함수 (스토어에 위임)
  const getConnectionStatus = () => {
    return storeGetConnectionStatus();
  };

  // 답변 제출 함수 (스토어에 위임)
  const submitAnswer = (gameId: number, problemId: string, answer: string) => {
    console.log(`[LiveVotingService] 답변 제출 (스토어로 위임): 게임 ID ${gameId}, 문제 ID ${problemId}`);
    return storeSubmitAnswer(gameId, problemId, answer);
  };
  
  return {
    connect,
    disconnect,
    getConnectionStatus,
    submitAnswer
  };
}