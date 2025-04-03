import { create } from 'zustand';
import RPC from '../utils/ethersRPC.ts';
import { web3auth } from '../utils/web3auth.ts';

interface WalletState {
  isLoading: boolean;
  error: string | null;
  lastTransaction: string | null;
  lastSignedMessage: string | null;
  balance: string | null;

  // 액션
  getAccounts: () => Promise<string>;
  getBalance: () => Promise<string>;
  signMessage: () => Promise<string>;
  sendTransaction: () => Promise<string>;
  exportPrivateKey: () => Promise<string | null>;
}

export const useWalletStore = create<WalletState>()((set, get) => ({
  isLoading: false,
  error: null,
  lastTransaction: null,
  lastSignedMessage: null,
  balance: null,

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
})); 