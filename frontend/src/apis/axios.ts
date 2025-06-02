import axios from 'axios';
import { userTokenDummy, formatTeamName } from '../constants/dummy';

// 로컬 스토리지 키 상수
const ACCESS_TOKEN_KEY = 'accessToken';

const axiosInstance = axios.create({
  baseURL: 'https://j12a609.p.ssafy.io/api/v1',
  timeout: 0,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  withCredentials: true,
});

// accessToken 설정 함수 (localStorage에만 저장)
export const setAccessToken = (token: string) => {
  localStorage.setItem(ACCESS_TOKEN_KEY, token);
};

// accessToken 제거 함수
export const removeAccessToken = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
};

// accessToken 가져오기 함수
export const getAccessToken = () => {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
};

// 모든 요청에 토큰 자동 추가
axiosInstance.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    console.log('=== Axios Request Interceptor Debug ===');
    console.log('1. Request URL:', config.url);
    console.log('2. Request method:', config.method);
    console.log('3. Access token:', token);
    console.log('4. Request headers:', config.headers);
    console.log('=== End Debug ===');

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => {
    console.log('=== Axios Response Interceptor Debug ===');
    console.log('1. Response URL:', response.config.url);
    console.log('2. Response status:', response.status);
    console.log('3. Response headers:', response.headers);
    console.log('=== End Debug ===');
    return response;
  },
  async (error) => {
    const errorCode = error.response?.data?.code;
    console.log('=== Axios Error Interceptor Debug ===');
    console.log('1. Error URL:', error.config?.url);
    console.log('2. Error status:', error.response?.status);
    console.log('3. Error code:', errorCode);
    console.log('4. Error message:', error.response?.data?.message);
    console.log('5. Error response:', error.response);
    console.log('=== End Debug ===');
    
    // 인증 관련 에러 코드들
    if ([1002, 1003, 1004, 1005].includes(errorCode)) {
      removeAccessToken();
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

// 지갑 정보 조회
export const getWalletInfo = async () => {
  const response = await axiosInstance.get('/wallet');
  return response.data;
};

// 지갑 등록
export const registerWallet = async () => {
  const response = await axiosInstance.post('/wallet/register');
  return response.data;
};

// 지갑 잔고 
export const getWalletBalances = async () => {
  try {
    const response = await axiosInstance.get('/wallet/balances');
    return response.data;
  } catch (error) {
    console.error('지갑 잔고 조회 실패:', error);
    throw error;
  }
};

export interface GameSchedule {
  season: number;
  gameDate: string;
  startTime: string;
  stadium: string;
  homeTeam: string;
  awayTeam: string;
  status: string;
}

export interface GameSchedulesResponse {
  schedules: GameSchedule[];
}

export const getTodayGames = async () => {
  try {
    const response = await axiosInstance.get<GameSchedulesResponse>('/home/games/today');
    return response.data;
  } catch (error) {
    console.error('오늘의 경기 조회 실패:', error);
    throw error;
  }
};

// 안건 등록
export const createProposal = async (teamId: number, title: string, content: string, targetCount: number) => {
  try {
    const response = await axiosInstance.post('/proposals', {
      teamId,
      title,
      content,
      targetCount
    });
    return response.data;
  } catch (error) {
    console.error('안건 등록 실패:', error);
    throw error;
  }
};

// 안건 투표
export const voteProposal = async (teamId: number, proposalId: number) => {
  try {
    const response = await axiosInstance.post('/proposals/vote', {
      teamId,
      proposalId
    });
    return response.data;
  } catch (error) {
    console.error('안건 투표 실패:', error);
    throw error;
  }
};

// 안건 상세 조회
export const getProposalDetail = async (teamId: number, proposalId: number) => {
  try {
    const response = await axiosInstance.get(`/proposals/${proposalId}/team/${teamId}`);
    return response.data;
  } catch (error) {
    console.error('안건 상세 조회 실패:', error);
    throw error;
  }
};

// 안건 목록 조회
export const getProposalList = async (teamId: number) => {
  try {
    const response = await axiosInstance.get(`/proposals/team/${teamId}`);
    return response.data;
  } catch (error) {
    console.error('안건 목록 조회 실패:', error);
    throw error;
  }
};

// 팀 토큰 개수 조회
export const getTeamTokenCount = async (teamId: number) => {
  try {
    const response = await axiosInstance.get(`/proposals/team/${teamId}/token/count`);
    return response.data;
  } catch (error: any) {
    // 404 에러이고 팬토큰이 없는 경우
    if (error.response?.status === 404 && error.response?.data?.code === 4001) {
      return { teamTokenCount: 0 };
    }
    console.error('팀 토큰 개수 조회 실패:', error);
    throw error;
  }
};

export default axiosInstance; 