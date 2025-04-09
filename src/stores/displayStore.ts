import { create } from 'zustand';
import { registerAccessTx, checkAccessPermission } from '../apis/displayApi';
import { persist } from 'zustand/middleware';

interface DisplayState {
  isLoading: boolean;
  error: string | null;
  successMessage: string | null;
  hasAccessPermission: boolean;
  currentGameId: string;
  currentTeamId: string;
  isGameEnded: boolean;
  
  // 접근 권한 등록 관련 액션
  registerAccess: (gameId: string, teamId: string, txHash: string) => Promise<boolean>;
  // 현재 게임 및 팀 ID 설정
  setCurrentIds: (gameId: string, teamId: string) => void;
  // 액세스 권한 상태 설정
  setAccessPermission: (hasPermission: boolean) => void;
  // 게임 상태 초기화
  resetGameState: () => void;
  // 상태 초기화
  resetMessages: () => void;
  // 웹소켓 메시지로 게임 종료 상태 설정 (API 호출 없이)
  setGameEnded: (isEnded: boolean) => void;
  // 접근 권한 조회 액션
  checkAccess: (gameId: string, teamId: string) => Promise<boolean>;
}

export const useDisplayStore = create<DisplayState>()(
  persist(
    (set, get) => ({
      isLoading: false,
      error: null,
      successMessage: null,
      hasAccessPermission: false,
      currentGameId: '1', // 기본값 설정
      currentTeamId: '1', // 기본값 설정
      isGameEnded: false,
      
      registerAccess: async (gameId: string, teamId: string, txHash: string) => {
        set({ isLoading: true, error: null, successMessage: null });
        
        try {
          const response = await registerAccessTx(parseInt(gameId), parseInt(teamId), txHash);
          
          if (response && response.success) {
            set({ 
              isLoading: false,
              hasAccessPermission: true, 
              successMessage: `게임 ${gameId}, 팀 ${teamId}에 대한 접근 권한이 등록되었습니다.`,
              currentGameId: gameId,
              currentTeamId: teamId,
              isGameEnded: false
            });
            
            // 콘솔에 현재 ID 로깅하여 디버깅 지원
            console.log(`액세스 권한 등록 성공! 게임 ID: ${gameId}, 팀 ID: ${teamId}`);
            
            return true;
          } else {
            throw new Error('서버 응답이 성공이 아닙니다.');
          }
        } catch (error: any) {
          set({ 
            isLoading: false, 
            error: error.message || '접근 권한 등록 중 오류가 발생했습니다.' 
          });
          return false;
        }
      },
      
      resetGameState: () => {
        set({ 
          isGameEnded: false,
          hasAccessPermission: false,
          error: null,
          successMessage: null
        });
      },
      
      setCurrentIds: (gameId: string, teamId: string) => {
        // 값이 변경될 때만 상태 업데이트
        if (gameId !== get().currentGameId || teamId !== get().currentTeamId) {
          console.log(`ID 업데이트: 게임 ID ${gameId}, 팀 ID ${teamId}`);
          set({ currentGameId: gameId, currentTeamId: teamId });
        }
      },
      
      setAccessPermission: (hasPermission: boolean) => {
        set({ hasAccessPermission: hasPermission });
      },
      
      resetMessages: () => {
        set({ error: null, successMessage: null });
      },
      
      // 웹소켓 메시지로 게임 종료 상태 설정 (API 호출 없이)
      setGameEnded: (isEnded: boolean) => {
        set({ 
          isGameEnded: isEnded,
          successMessage: isEnded ? '게임이 종료되었습니다.' : null,
          error: null,
        });
      },
      
      // 접근 권한 조회 액션
      checkAccess: async (gameId: string, teamId: string) => {
        set({ isLoading: true, error: null });
        
        try {
          console.log(`[DisplayStore] 접근 권한 확인 시작 - 게임ID: ${gameId}, 팀ID: ${teamId}`);
          
          // 권한 확인 API 호출
          const response = await checkAccessPermission(parseInt(gameId), parseInt(teamId));
          const hasAccess = response.hasAccessPermission;
          
          // 결과 상태 업데이트
          set({ 
            isLoading: false,
            hasAccessPermission: hasAccess,
            currentGameId: gameId,
            currentTeamId: teamId
          });
          
          console.log(`[DisplayStore] 접근 권한 확인 완료 - 결과: ${hasAccess ? '접근 허용' : '접근 거부'}`);
          return hasAccess;
        } catch (error: any) {
          console.error('[DisplayStore] 접근 권한 확인 중 오류 발생:', error);
          
          // 오류 발생 시 접근 권한 거부 상태로 설정
          set({ 
            isLoading: false, 
            error: error.message || '접근 권한 확인 중 오류가 발생했습니다.',
            hasAccessPermission: false 
          });
          return false;
        }
      }
    }),
    {
      name: 'display-storage', // 로컬 스토리지 키 이름
      partialize: (state) => ({
        // 영구 저장할 상태 필드만 선택
        hasAccessPermission: state.hasAccessPermission,
        currentGameId: state.currentGameId,
        currentTeamId: state.currentTeamId,
        isGameEnded: state.isGameEnded,
      }),
    }
  )
);
