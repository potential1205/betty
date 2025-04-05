import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { backendLogin, backendLogout, getNickname, registerNickname } from '../apis/authApi';
import { initWeb3Auth, web3auth } from '../utils/web3auth';
import { WALLET_ADAPTERS } from '@web3auth/base';
import { useWalletStore } from './walletStore';
import { useStore } from './useStore';
import { IProvider } from '@web3auth/base';

interface UserState {
  walletAddress: string | null;
  nickname: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  needsNickname: boolean;
  isInitialized: boolean;

  // 액션
  initialize: () => Promise<void>;
  login: (provider: string) => Promise<boolean>;
  logout: () => Promise<void>;
  reset: () => void;
  setNicknameRequired: (required: boolean) => void;
  registerNickname: (nickname: string) => Promise<boolean>;
  checkNickname: () => Promise<boolean>;
}

export const useUserStore = create<UserState>()(
  persist(
    (set, get) => ({
      walletAddress: null,
      nickname: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,
      needsNickname: false,
      isInitialized: false,

      initialize: async () => {
        try {
          set({ isLoading: true, error: null });
          
          // Web3Auth 초기화
          if (!web3auth.connected) {
            console.log('Web3Auth 초기화 시작...');
            await web3auth.init();
            console.log('Web3Auth 초기화 완료');
          } else {
            console.log('Web3Auth 이미 연결됨');
          }
          
          set({ isInitialized: true, isLoading: false });
        } catch (error) {
          console.error('초기화 실패:', error);
          set({ 
            isInitialized: true, 
            isLoading: false,
            isAuthenticated: false,
            error: error instanceof Error ? error.message : '초기화 실패'
          });
        }
      },

      checkNickname: async () => {
        try {
          // useStore에서 닉네임 확인
          const { nickname: storeNickname } = useStore.getState();
          if (storeNickname) {
            set({
              nickname: storeNickname,
              needsNickname: false
            });
            return true;
          }

          // 로컬 스토리지에서 닉네임 확인
          const storedNickname = localStorage.getItem('user-storage');
          if (storedNickname) {
            const parsed = JSON.parse(storedNickname);
            if (parsed.nickname) {
              set({
                nickname: parsed.nickname,
                needsNickname: false
              });
              return true;
            }
          }

          // 로컬에 없으면 서버에서 조회
          const response = await getNickname();
          set({
            nickname: response.nickname,
            needsNickname: false
          });
          return true;
        } catch (error) {
          console.log('닉네임 등록이 필요합니다');
          set({
            needsNickname: true
          });
          return false;
        }
      },

      login: async (provider: string) => {
        try {
          set({ isLoading: true, error: null });

          // 1단계: Web3Auth 초기화 및 연결
          await initWeb3Auth();

          if (!web3auth.connected) {
            console.log('Web3Auth 로그인 시도...');
            await web3auth.connectTo(WALLET_ADAPTERS.AUTH, {
              loginProvider: provider,
            });
          }

          if (!web3auth.connected) {
            throw new Error('Web3Auth 연결 실패');
          }

          // idToken 가져오기
          console.log('idToken 요청...');
          const userInfo = await web3auth.getUserInfo();
          if (!userInfo.idToken) {
            throw new Error('ID 토큰을 가져올 수 없습니다');
          }

          // 지갑 주소 가져오기
          const web3authProvider = await web3auth.provider;
          if (web3authProvider) {
            try {
              const accounts = await web3authProvider.request({
                method: 'eth_accounts'
              }) as string[];
              
              if (accounts && accounts.length > 0) {
                console.log('지갑 주소:', accounts[0]);
                set({ walletAddress: accounts[0].toLowerCase() });
              }
            } catch (error) {
              console.error('지갑 주소 가져오기 실패:', error);
            }
          }

          // 2단계: 백엔드 로그인
          console.log('백엔드 로그인 시도...');
          await backendLogin(userInfo.idToken);
          console.log('백엔드 로그인 성공');

          set({ isAuthenticated: true, isLoading: false });
          return true;
        } catch (error: any) {
          console.error('로그인 실패:', error);
          
          if (error.message === 'Web3Auth 연결 실패' || 
              error.message === 'ID 토큰을 가져올 수 없습니다' ||
              !error.response?.status) {
            if (web3auth.connected) {
              await web3auth.logout();
            }
          }
          
          let errorMessage = '알 수 없는 오류가 발생했습니다';
          if (error.response?.data?.message) {
            errorMessage = error.response.data.message;
          } else if (error.message) {
            errorMessage = error.message;
          }
          
          set({
            isLoading: false,
            error: errorMessage,
          });
          
          return false;
        }
      },

      logout: async () => {
        try {
          set({ isLoading: true });
          
          // 1. 백엔드 로그아웃
          console.log('백엔드 로그아웃 시도...');
          await backendLogout();
          
          // 2. Web3Auth 로그아웃
          console.log('Web3Auth 로그아웃 시도...');
          if (web3auth.connected) {
            await web3auth.logout();
          }
          console.log('Web3Auth 로그아웃 완료');
          // 3. 상태 초기화
          get().reset();
          
        } catch (error) {
          console.error('로그아웃 실패:', error);
          get().reset();
        }
      },

      registerNickname: async (nickname: string) => {
        try {
          set({ isLoading: true, error: null });
          
          const response = await registerNickname(nickname);
          
          set({
            nickname: response.nickname,
            needsNickname: false,
            isAuthenticated: true,
            isLoading: false,
          });
          
          return true;
        } catch (error: any) {
          console.error('닉네임 등록 실패:', error);
          
          let errorMessage = '닉네임 등록에 실패했습니다';
          if (error.response?.data?.message) {
            errorMessage = error.response.data.message;
          } else if (error.message) {
            errorMessage = error.message;
          }
          
          set({
            isLoading: false,
            error: errorMessage,
          });
          
          return false;
        }
      },

      setNicknameRequired: (required: boolean) => {
        set({ needsNickname: required });
      },

      reset: () => {
        set({
          walletAddress: null,
          nickname: null,
          isAuthenticated: false,
          isLoading: false,
          error: null,
          needsNickname: false,
        });
      },
    }),
    {
      name: 'user-storage',
      partialize: (state) => ({
        nickname: state.nickname,
        isAuthenticated: state.isAuthenticated,
        walletAddress: state.walletAddress,
      }),
    }
  )
); 