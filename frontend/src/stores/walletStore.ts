import { create } from 'zustand';
import { ethers } from 'ethers';
import RPC from '../utils/ethersRPC';
import { web3auth } from '../utils/web3auth';
import { getTeamTokenAddress } from '../apis/tokenApi';

// ERC20 토큰 ABI - balanceOf 함수만 필요
const ERC20_ABI = [
  "function balanceOf(address owner) view returns (uint256)"
];

interface WalletState {
  isLoading: boolean;
  error: string | null;
  lastTransaction: string | null;
  lastSignedMessage: string | null;
  balance: string | null;
  tokenBalance: string | null;
  tokenBalanceLoading: boolean;

  // 액션
  getAccounts: () => Promise<string>;
  getBalance: () => Promise<string>;
  signMessage: () => Promise<string>;
  sendTransaction: () => Promise<string>;
  exportPrivateKey: () => Promise<string | null>;
  getTokenBalance: (teamId: number) => Promise<string>;
}

export const useWalletStore = create<WalletState>()((set, get) => ({
  isLoading: false,
  error: null,
  lastTransaction: null,
  lastSignedMessage: null,
  balance: null,
  tokenBalance: null,
  tokenBalanceLoading: false,

  getAccounts: async () => {
    try {
      set({ isLoading: true, error: null });
      
      const provider = web3auth.provider;
      if (!provider) {
        throw new Error("provider not initialized yet");
      }
      
      const address = await RPC.getAccounts(provider);
      set({ isLoading: false });
      return address;
    } catch (error) {
      set({ 
        isLoading: false, 
        error: error instanceof Error ? error.message : "계정 조회에 실패했습니다" 
      });
      return "";
    }
  },

  getBalance: async () => {
    try {
      set({ isLoading: true, error: null });
      
      const provider = web3auth.provider;
      if (!provider) {
        throw new Error("provider not initialized yet");
      }
      
      const balance = await RPC.getBalance(provider);
      set({ isLoading: false, balance });
      return balance;
    } catch (error) {
      set({ 
        isLoading: false, 
        error: error instanceof Error ? error.message : "잔액 조회에 실패했습니다" 
      });
      return "";
    }
  },

  signMessage: async () => {
    try {
      set({ isLoading: true, error: null });
      
      const provider = web3auth.provider;
      if (!provider) {
        throw new Error("provider not initialized yet");
      }
      
      const signedMessage = await RPC.signMessage(provider);
      set({ isLoading: false, lastSignedMessage: signedMessage });
      return signedMessage;
    } catch (error) {
      set({ 
        isLoading: false, 
        error: error instanceof Error ? error.message : "메시지 서명에 실패했습니다" 
      });
      return "";
    }
  },

  sendTransaction: async () => {
    try {
      set({ isLoading: true, error: null });
      
      const provider = web3auth.provider;
      if (!provider) {
        throw new Error("provider not initialized yet");
      }
      
      const receipt = await RPC.sendTransaction(provider);
      set({ isLoading: false, lastTransaction: receipt });
      return receipt;
    } catch (error) {
      set({ 
        isLoading: false, 
        error: error instanceof Error ? error.message : "트랜잭션 전송에 실패했습니다" 
      });
      return "";
    }
  },

  exportPrivateKey: async () => {
    try {
      set({ isLoading: true, error: null });
      
      const provider = web3auth.provider;
      if (!provider) {
        throw new Error("provider not initialized yet");
      }
      
      const privateKey = await provider.request({
        method: "eth_private_key",
      });
      
      set({ isLoading: false });
      return privateKey as string;
    } catch (error) {
      set({ 
        isLoading: false, 
        error: error instanceof Error ? error.message : "개인키 내보내기에 실패했습니다" 
      });
      return null;
    }
  },
  
  // 팀 토큰 잔액 조회 함수 개선
  getTokenBalance: async (teamId: number) => {
    try {
      set({ tokenBalanceLoading: true, error: null });
      
      // 1. web3auth 연결 상태 확인
      const web3authProvider = web3auth.provider;
      if (!web3authProvider) {
        console.log("[토큰잔액] web3auth 연결 안됨");
        set({ tokenBalanceLoading: false });
        return "0"; // 연결되지 않았을 경우 0 반환
      }
      
      // 2. 사용자 지갑 주소 가져오기
      const walletAddress = await get().getAccounts();
      if (!walletAddress) {
        console.log("[토큰잔액] 지갑 주소 없음");
        set({ tokenBalanceLoading: false });
        return "0"; // 주소가 없을 경우 0 반환
      }
      
      // 3. 토큰 주소 가져오기
      const tokenAddress = await getTeamTokenAddress(teamId);
      if (!tokenAddress) {
        throw new Error(`팀 ID ${teamId}의 토큰 주소를 찾을 수 없습니다`);
      }
      
      console.log(`[토큰잔액] 팀 ID: ${teamId}, 토큰 주소: ${tokenAddress}, 지갑 주소: ${walletAddress}`);
      
      // 4. ERC20 컨트랙트 연결 및 잔액 조회
      const RPC_URL = import.meta.env.VITE_RPC_URL;
      const ethersProvider = new ethers.JsonRpcProvider(RPC_URL);
      const erc20Contract = new ethers.Contract(tokenAddress, ERC20_ABI, ethersProvider);
      
      // 5. 잔액 조회
      const balance = await erc20Contract.balanceOf(walletAddress);
      const formattedBalance = ethers.formatEther(balance);
      
      console.log(`[토큰잔액] 잔액: ${formattedBalance} (${balance})`);
      
      set({ tokenBalance: formattedBalance, tokenBalanceLoading: false });
      return formattedBalance;
    } catch (error) {
      console.error("[토큰잔액] 조회 실패:", error);
      set({ 
        tokenBalanceLoading: false, 
        error: error instanceof Error ? error.message : "토큰 잔액 조회에 실패했습니다" 
      });
      return "0";
    }
  }
})); 