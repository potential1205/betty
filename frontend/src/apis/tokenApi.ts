import axiosInstance from './axios';

interface TokenAddressResponse {
  tokenAddress: string;
}

interface TokenNameResponse {
  tokenName: string;
}

interface BettyPriceResponse {
  price: number;
}

interface TokenInfoResponse {
  balance: number;
  tokenAddress: string;
  tokenName: string;
}

interface TokenBalance {
  tokenName: string;
  balance: number;
}

interface WalletBalanceResponse {
  walletAddress: string;
  nickname: string;
  totalBet: number;
  tokens: TokenBalance[];
}

// 팀 ID로 토큰 주소 조회
export async function getTeamTokenAddress(teamId: number): Promise<string> {
  try {
    const response = await axiosInstance.get<TokenAddressResponse>(`tokens/address/teams/${teamId}`);
    return response.data.tokenAddress;
  } catch (error: any) {
    if (error.response) {
      throw new Error(error.response.data?.message || '토큰 주소 조회 실패');
    }
    throw error;
  }
}

// 팀 ID로 토큰 이름 조회
export async function getTeamTokenName(teamId: number): Promise<string> {
  try {
    const response = await axiosInstance.get<TokenNameResponse>(`/tokens/name/teams/${teamId}`);
    return response.data.tokenName;
  } catch (error: any) {
    if (error.response) {
      throw new Error(error.response?.data?.message || '토큰 이름 조회 실패');
    }
    throw error;
  }
}

// 베티코인 잔액 조회
export async function getBettyPrice(): Promise<number> {
  try {
    const response = await axiosInstance.get<BettyPriceResponse>('tokens/price/betty');
    return response.data.price;
  } catch (error: any) {
    if (error.response) {
      throw new Error(error.response?.data?.message || '베티코인 잔액 조회 실패');
    }
    throw error;
  }
}

/**
 * 토큰 ID로 토큰 정보를 조회합니다.
 * @param tokenId - 토큰 ID
 * @returns 토큰 잔액, 주소, 이름 정보
 */
export async function getTokenInfo(tokenId: number): Promise<TokenInfoResponse> {
  try {
    const response = await axiosInstance.get<TokenInfoResponse>(`/tokens/${tokenId}`);
    console.log(`[TokenAPI] 토큰 정보 조회 성공 (ID: ${tokenId}):`, response.data);
    return response.data;
  } catch (error: any) {
    console.error(`[TokenAPI] 토큰 정보 조회 실패 (ID: ${tokenId}):`, error);
    if (error.response) {
      throw new Error(error.response?.data?.message || '토큰 정보 조회 실패');
    }
    throw error;
  }
}

/**
 * 현재 로그인한 사용자의 잔고와 토큰 목록을 조회합니다.
 * @returns 지갑 주소, 닉네임, 총 BET, 토큰 목록 정보
 */
export async function getWalletBalance(): Promise<WalletBalanceResponse> {
  try {
    const response = await axiosInstance.get<WalletBalanceResponse>('/wallet/balances');
    console.log('[TokenAPI] 지갑 잔고 조회 성공:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('[TokenAPI] 지갑 잔고 조회 실패:', error);
    if (error.response) {
      throw new Error(error.response?.data?.message || '지갑 잔고 조회 실패');
    }
    throw error;
  }
} 