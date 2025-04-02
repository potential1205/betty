import axiosInstance, { setAccessToken, removeAccessToken } from './axios';

interface NicknameResponse {
  nickname: string;
}

interface ApiError {
  code: number;
  message: string;
}

export const ErrorCodes = {
  NOT_FOUND_WALLET: 1006,
  ALREADY_EXIST_WALLET: 1007,
} as const;

export const backendLogin = async (idToken: string): Promise<void> => {
  try {
    const response = await axiosInstance.post('/auth/login', { idToken });
    const accessToken = response.headers['authorization'];
    if (accessToken) {
      setAccessToken(accessToken.replace('Bearer ', ''));
    }
  } catch (error: any) {
    if (error.response?.data) {
      const apiError: ApiError = error.response.data;
      if (apiError.code === 1006) {
        throw new Error('NOT_FOUND_WALLET');
      }
      throw new Error(apiError.message);
    }
    throw error;
  }
};

export const backendLogout = async (): Promise<void> => {
  try {
    await axiosInstance.post('/auth/logout');
    removeAccessToken();
  } catch (error) {
    console.error('백엔드 로그아웃 실패:', error);
    throw error;
  }
};

export const getNickname = async (): Promise<NicknameResponse> => {
  try {
    const response = await axiosInstance.get('/wallet');
    return response.data;
  } catch (error) {
    console.error('닉네임 조회 실패:', error);
    throw error;
  }
};

export const registerNickname = async (nickname: string): Promise<NicknameResponse> => {
  try {
    const response = await axiosInstance.post('/wallet', { nickname });
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      const apiError: ApiError = error.response.data;
      if (apiError.code === 1007) {
        throw new Error('이미 등록된 지갑입니다.');
      }
      throw new Error(apiError.message);
    }
    throw error;
  }
};