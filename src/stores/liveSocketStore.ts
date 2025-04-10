import { create } from 'zustand';
import { Client } from '@stomp/stompjs';
import { getAccessToken } from '../apis/axios';
import { GameState, Problem } from '../services/LiveVotingWebsocketService';
import { submitLiveVote } from '../apis/gameApi';

// 게임 문제 히스토리 인터페이스
interface ProblemHistory extends Problem {
  userAnswer: string | null;
}

interface LiveSocketState {
  // 연결 상태
  isConnecting: boolean;
  isConnected: boolean;
  
  // 연결 정보
  currentGameId: number | null;
  
  // 데이터 상태
  currentProblem: Problem | null;
  problemHistory: ProblemHistory[];
  isActive: boolean;
  timeLeft: number;
  selectedAnswer: string | null;
  
  // 내부 상태
  stompClient: Client | null;
  
  // 웹소켓 URL 생성
  getWebSocketUrl: (gameId: string) => string;
  
  // 웹소켓 콜백 함수
  setCallbacks: (callbacks: {
    onConnect?: () => void;
    onDisconnect?: () => void;
    onGameUpdate?: (gameState: GameState) => void;
    onProblemReceived?: (problem: Problem) => void;
  }) => void;
  
  // 웹소켓 액션
  connect: (gameId: number) => void;
  disconnect: () => void;
  submitAnswer: (gameId: number, problemId: string, answer: string) => Promise<boolean>;
  
  // 연결 상태 확인
  getConnectionStatus: () => { isConnecting: boolean; isConnected: boolean };
  
  // 타이머 및 상태 관리
  startTimer: () => void;
  stopTimer: () => void;
  resetProblem: () => void;
  setSelectedAnswer: (answer: string | null) => void;
  addToHistory: (problem: Problem, userAnswer: string | null) => void;
}

// API URL 가져오기
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const useLiveSocketStore = create<LiveSocketState>((set, get) => {
  // 타이머 인터벌 ID
  let timerInterval: number | NodeJS.Timeout | null = null;
  
  // 콜백 함수 저장용 객체
  let callbacks = {
    onConnect: null as (() => void) | null,
    onDisconnect: null as (() => void) | null,
    onGameUpdate: null as ((gameState: GameState) => void) | null,
    onProblemReceived: null as ((problem: Problem) => void) | null,
  };
  
  return {
    // 초기 상태
    isConnecting: false,
    isConnected: false,
    currentGameId: null,
    currentProblem: null,
    problemHistory: [],
    isActive: false,
    timeLeft: 0,
    selectedAnswer: null,
    stompClient: null,
    
    // WebSocket URL 생성 함수
    getWebSocketUrl: (gameId: string) => {
      const token = getAccessToken();
      // API URL에서 호스트만 추출
      const url = new URL(API_URL);
      // HTTPS인 경우 WSS, HTTP인 경우 WS 사용
      const protocol = url.protocol === 'https:' ? 'wss:' : 'ws:';
      // 최종 WebSocket URL 생성
      return `${protocol}//${url.host}/ws?access_token=${token}&game_id=${gameId}&type=game`;
    },
    
    // 콜백 설정 함수
    setCallbacks: (newCallbacks) => {
      callbacks = {
        ...callbacks,
        ...newCallbacks
      };
    },
    
    // 웹소켓 연결 함수
    connect: (gameId: number) => {
      const state = get();
      
      // 이미 동일한 게임에 연결 중이거나 연결되어 있는 경우 중복 연결 방지
      if (state.isConnecting || (state.isConnected && state.currentGameId === gameId)) {
        console.log(`[LiveSocketStore] 이미 게임 ID ${gameId}에 연결 중이거나 연결되어 있습니다`);
        return;
      }
      
      const token = getAccessToken();
      if (!token) {
        console.error("[LiveSocketStore] 인증 토큰이 없습니다");
        return;
      }
      
      // 이미 연결된 클라이언트 정리
      if (state.stompClient) {
        try {
          console.log("[LiveSocketStore] 새 연결 전 기존 연결 정리 중");
          state.stompClient.deactivate();
        } catch (error) {
          console.error("[LiveSocketStore] 기존 연결 정리 중 오류:", error);
        }
      }
      
      set({ isConnecting: true });
      const gameIdStr = gameId.toString();
      
      console.log(`[LiveSocketStore] 웹소켓 연결 시도: 게임 ID ${gameIdStr}`);
      
      // 웹소켓 URL 생성
      const wsUrl = state.getWebSocketUrl(gameIdStr);
      
      const newStompClient = new Client({
        brokerURL: wsUrl,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log(`[LiveSocketStore] 웹소켓 연결 성공: 게임 ID ${gameIdStr}`);
          set({ 
            isConnecting: false, 
            isConnected: true,
            currentGameId: gameId
          });
          
          // 이닝, 점수 정보 구독
          newStompClient.subscribe(`/topic/game/${gameIdStr}`, (message) => {
            try {
              console.log(`[LiveSocketStore] 원본 메시지: ${message.body}`);
              const gameInfo = JSON.parse(message.body);
              
              if (callbacks.onGameUpdate) {
                // score 문자열 형식을 homeScore와 awayScore로 파싱 (오류 처리 강화)
                let homeScore = 0;
                let awayScore = 0;
                let currentInning = '1회 초';
                
                try {
                  // score 형식이 "0:0" 형태인 경우
                  if (gameInfo.score && gameInfo.score.includes(':')) {
                    const scoreParts = gameInfo.score.split(':');
                    homeScore = parseInt(scoreParts[0].trim(), 10) || 0;
                    awayScore = parseInt(scoreParts[1].trim(), 10) || 0;
                  }

                  // 이닝 정보 처리
                  if (gameInfo.inning) {
                    currentInning = gameInfo.inning;
                  }
                } catch (error) {
                  console.error('[LiveSocketStore] 점수/이닝 파싱 오류:', error);
                }
                
                const gameState = {
                  inning: currentInning,
                  score: {
                    homeScore,
                    awayScore
                  },
                  status: gameInfo.status || 'LIVE'
                };
                
                console.log('[LiveSocketStore] 게임 상태 업데이트:', gameState);
                callbacks.onGameUpdate(gameState);
              }
            } catch (error) {
              console.error('[LiveSocketStore] 게임 데이터 파싱 오류:', error);
            }
          });
          
          // 문제 구독
          newStompClient.subscribe(`/topic/problem/${gameIdStr}`, (message) => {
            try {
              console.log(`[LiveSocketStore] 원본 문제 메시지: ${message.body}`);
              const problemData = JSON.parse(message.body);
              
              if (problemData.redisGameProblem && callbacks.onProblemReceived) {
                const problem = problemData.redisGameProblem;
                
                // 필수 필드 확인
                if (!problem.problemId || !problem.gameId || !problem.description || !Array.isArray(problem.options)) {
                  console.error('[LiveSocketStore] 문제 데이터 형식 오류:', problem);
                  return;
                }
                
                // 안전하게 문제 데이터 전달
                const processedProblem = {
                  problemId: problem.problemId,
                  gameId: problem.gameId,
                  inning: problem.inning || '1회 초',
                  attackTeam: problem.attackTeam || '공격팀 정보 없음',
                  batterName: problem.batterName || '타자 정보 없음',
                  questionCode: problem.questionCode || '',
                  description: problem.description,
                  options: problem.options,
                  answer: problem.answer || null,
                  push: typeof problem.push === 'boolean' ? problem.push : true,
                  timestamp: problem.timestamp || String(Date.now())
                };
                
                // 현재 문제 설정 및 타이머 시작
                set({ 
                  currentProblem: processedProblem,
                  isActive: true,
                  timeLeft: 10,
                  selectedAnswer: null
                });
                
                // 문제 수신 콜백 호출
                callbacks.onProblemReceived(processedProblem);
                
                // 타이머 시작
                get().startTimer();
              }
            } catch (error) {
              console.error('[LiveSocketStore] 문제 데이터 파싱 오류:', error);
            }
          });
          
          if (callbacks.onConnect) callbacks.onConnect();
        },
        onDisconnect: () => {
          console.log('[LiveSocketStore] 웹소켓 연결 종료');
          set({ 
            isConnecting: false, 
            isConnected: false,
            stompClient: null
          });
          if (callbacks.onDisconnect) callbacks.onDisconnect();
        },
        onStompError: (frame) => {
          console.error('[LiveSocketStore] STOMP 오류:', frame.headers['message'], frame.body);
          set({ 
            isConnecting: false, 
            isConnected: false,
            stompClient: null
          });
          if (callbacks.onDisconnect) callbacks.onDisconnect();
        },
        onWebSocketError: (event) => {
          console.error('[LiveSocketStore] 웹소켓 오류:', event);
          set({ 
            isConnecting: false, 
            isConnected: false,
            stompClient: null
          });
          if (callbacks.onDisconnect) callbacks.onDisconnect();
        }
      });
      
      // 웹소켓 클라이언트 저장 및 활성화
      set({ stompClient: newStompClient });
      newStompClient.activate();
    },
    
    // 웹소켓 연결 해제 함수
    disconnect: () => {
      const { stompClient, isConnected } = get();
      
      if (!stompClient) {
        console.log('[LiveSocketStore] 웹소켓 클라이언트가 없습니다');
        set({ 
          isConnected: false, 
          isConnecting: false,
          currentGameId: null
        });
        return;
      }
      
      // 타이머 중지
      get().stopTimer();
      
      if (stompClient.connected) {
        console.log('[LiveSocketStore] 웹소켓 연결 종료 요청');
        stompClient.deactivate();
      } else {
        console.log('[LiveSocketStore] 이미 연결이 해제되었거나 연결 중이 아닙니다');
        set({ 
          isConnected: false, 
          isConnecting: false,
          stompClient: null,
          currentGameId: null
        });
        
        if (!isConnected && callbacks.onDisconnect) {
          callbacks.onDisconnect();
        }
      }
    },
    
    // 답변 제출 함수
    submitAnswer: async (gameId: number, problemId: string, answer: string) => {
      // 선택한 답변 설정 (UI 즉시 업데이트)
      set({ selectedAnswer: answer });
      
      // 항상 REST API로 답변 제출
      console.log(`[LiveSocketStore] 답변 제출: 게임 ID ${gameId}, 문제 ID ${problemId}, 선택 답변 ${answer}`);
      try {
        const result = await submitLiveVote(gameId, answer, problemId);
        console.log('[LiveSocketStore] REST API 답변 제출 성공:', result);
        return true;
      } catch (error) {
        console.error('[LiveSocketStore] REST API 답변 제출 오류:', error);
        return false;
      }
    },
    
    // 타이머 시작 함수
    startTimer: () => {
      // 기존 타이머 정리
      get().stopTimer();
      
      // 새 타이머 시작
      timerInterval = setInterval(() => {
        const { timeLeft, isActive, currentProblem } = get();
        
        if (!isActive || timeLeft <= 0) {
          get().stopTimer();
          return;
        }
        
        if (timeLeft === 1) {
          // 타이머 종료 시 히스토리에 추가
          if (currentProblem) {
            get().addToHistory(currentProblem, null);
          }
          set({ 
            timeLeft: 0,
            isActive: false
          });
          get().stopTimer();
        } else {
          set({ timeLeft: timeLeft - 1 });
        }
      }, 1000);
    },
    
    // 타이머 정지 함수
    stopTimer: () => {
      if (timerInterval) {
        clearInterval(timerInterval as NodeJS.Timeout);
        timerInterval = null;
      }
    },
    
    // 문제 초기화 함수
    resetProblem: () => {
      get().stopTimer();
      set({
        currentProblem: null,
        isActive: false,
        timeLeft: 0,
        selectedAnswer: null
      });
    },
    
    // 선택한 답변 설정
    setSelectedAnswer: (answer: string | null) => {
      set({ selectedAnswer: answer });
    },
    
    // 히스토리에 문제 추가
    addToHistory: (problem: Problem, userAnswer: string | null) => {
      const historyItem: ProblemHistory = {
        ...problem,
        userAnswer
      };
      
      set(state => ({
        problemHistory: [...state.problemHistory, historyItem]
      }));
    },
    
    // 연결 상태 확인
    getConnectionStatus: () => ({ isConnecting: get().isConnecting, isConnected: get().isConnected }),
  };
}); 