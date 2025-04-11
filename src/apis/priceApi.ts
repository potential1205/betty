import axiosInstance from './axios';

// 토큰 인터페이스
export interface Token {
  id: number;
  tokenName: string;
  tokenAddress: string;
  description: string;
  createdAt: string;
}

// 가격 데이터 인터페이스
export interface PriceData {
  id: number;
  token: Token;
  tokenName: string;
  price: number;
  updatedAt: string;
}

/**
 * 시간별 토큰 가격 데이터를 조회합니다.
 * @param tokenId 토큰 ID
 * @param startOfHour 시작 시간 (ISO 8601 형식)
 * @param endOfHour 종료 시간 (ISO 8601 형식)
 * @returns 시간별 가격 데이터 배열
 */
export const getHourlyPrices = async (
  tokenId: number,
  startOfHour: string,
  endOfHour: string
): Promise<PriceData[]> => {
  try {
    const response = await axiosInstance.get<PriceData[]>(
      `/prices/hourly/${tokenId}`,
      {
        params: { startOfHour, endOfHour }
      }
    );
    return response.data;
  } catch (error) {
    console.error('시간별 토큰 가격 조회 실패:', error);
    throw error;
  }
};

/**
 * 일별 토큰 가격 데이터를 조회합니다.
 * @param tokenId 토큰 ID
 * @returns 일별 가격 데이터 배열
 */
export const getDailyPrices = async (tokenId: number): Promise<PriceData[]> => {
  try {
    const response = await axiosInstance.get<PriceData[]>(`/prices/daily/${tokenId}`);
    return response.data;
  } catch (error) {
    console.error('일별 토큰 가격 조회 실패:', error);
    throw error;
  }
};

/**
 * 시간별 가격 데이터를 차트 형식으로 변환합니다.
 * @param data 시간별 가격 데이터 배열
 * @returns 차트용 데이터 배열 (시간, 가격)
 */
export const formatHourlyDataForChart = (data: PriceData[]): [string, number][] => {
  return data.map(item => {
    const date = new Date(item.updatedAt);
    const formattedTime = `${date.getHours()}:00`;
    return [formattedTime, item.price];
  });
};

/**
 * 일별 가격 데이터를 차트 형식으로 변환합니다.
 * @param data 일별 가격 데이터 배열
 * @returns 차트용 데이터 배열 (날짜, 가격)
 */
export const formatDailyDataForChart = (data: PriceData[]): [string, number][] => {
  return data.map(item => {
    const date = new Date(item.updatedAt);
    const formattedDate = `${date.getMonth() + 1}/${date.getDate()}`;
    return [formattedDate, item.price];
  });
};

/**
 * 현재 날짜를 기준으로 며칠 전의 날짜를 ISO 8601 형식으로 반환합니다.
 * @param daysAgo 며칠 전
 * @returns ISO 8601 형식의 날짜 문자열
 */
export const getDateBeforeDays = (daysAgo: number): string => {
  const date = new Date();
  date.setDate(date.getDate() - daysAgo);
  return date.toISOString();
};

/**
 * 현재 시간을 기준으로 몇 시간 전의 시간을 ISO 8601 형식으로 반환합니다.
 * @param hoursAgo 몇 시간 전
 * @returns ISO 8601 형식의 시간 문자열
 */
export const getTimeBeforeHours = (hoursAgo: number): string => {
  const date = new Date();
  date.setHours(date.getHours() - hoursAgo);
  return date.toISOString();
};

/**
 * 토큰의 지난 24시간 가격 데이터를 조회합니다.
 * @param tokenId 토큰 ID
 * @returns 24시간 동안의 시간별 가격 데이터
 */
export const getLast24HoursPrices = async (tokenId: number): Promise<PriceData[]> => {
  const endTime = new Date().toISOString();
  const startTime = getTimeBeforeHours(24);
  
  return getHourlyPrices(tokenId, startTime, endTime);
};

/**
 * 토큰의 지난 7일간 일별 가격 데이터를 조회합니다.
 * @param tokenId 토큰 ID
 * @returns 7일간의 일별 가격 데이터
 */
export const getLast7DaysPrices = async (tokenId: number): Promise<PriceData[]> => {
  return getDailyPrices(tokenId);
}; 