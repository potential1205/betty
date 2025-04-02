import axios from 'axios';

// 로컬 스토리지 키 상수
const ACCESS_TOKEN_KEY = 'accessToken';

const axiosInstance = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/v1`,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
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
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const errorCode = error.response?.data?.code;
    
    // 인증 관련 에러 코드들
    if ([1002, 1003, 1004, 1005].includes(errorCode)) {
      removeAccessToken();
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

export default axiosInstance; 