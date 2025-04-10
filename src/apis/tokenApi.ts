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
    const response = await axiosInstance.get<BettyPriceResponse>('/api/v1/tokens/price/betty');
    return response.data.price;
  } catch (error: any) {
    if (error.response) {
      throw new Error(error.response?.data?.message || '베티코인 잔액 조회 실패');
    }
    throw error;
  }
} 