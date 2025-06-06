import axiosInstance from './axios';

// 팀 정보 인터페이스
interface Team {
  id: number;
  teamCode: string;
  teamName: string;
  tokenName: string;
}

// 시간 정보 인터페이스
interface Time {
  hour: number;
  minute: number;
  second: number;
  nano: number;
}

// 경기 정보 인터페이스
interface Game {
  id: number;
  homeTeam: Team;
  awayTeam: Team;
  stadium: string;
  season: number;
  gameDate: string;
  startTime: Time;
  status: string;
  homeScore?: number;  // 추가: API 응답에 없을 수 있지만 사용할 가능성이 있는 필드
  awayScore?: number;  // 추가: API 응답에 없을 수 있지만 사용할 가능성이 있는 필드
  inning?: number;     // 추가: API 응답에 없을 수 있지만 사용할 가능성이 있는 필드
}

// API 응답 인터페이스
interface GameDetailResponse {
  game: Game;
}

/**
 * 경기 상세 정보를 조회합니다.
 * @param gameId 조회할 경기의 ID
 * @returns 경기 상세 정보
 */
export const getGameDetail = async (gameId: number): Promise<Game> => {
  try {
    console.log(`경기 상세 정보 조회 요청: gameId=${gameId}`);
    const response = await axiosInstance.get<GameDetailResponse>(`/home/games/${gameId}`);
    console.log('경기 상세 정보 응답:', response.data);
    return response.data.game;
  } catch (error: any) {
    console.error('경기 상세 정보 조회 실패:', error);
    if (error.response) {
      throw new Error(error.response.data?.message || '경기 상세 정보 조회 실패');
    }
    throw error;
  }
};

/**
 * 라이브 투표를 제출합니다.
 * @param gameId 게임 ID
 * @param selectedAnswer 선택한 답변
 * @param problemId 문제 ID
 * @returns 응답 데이터
 */
export const submitLiveVote = async (
  gameId: number,
  selectedAnswer: string,
  problemId: string
): Promise<any> => {
  try {
    console.log(`라이브 투표 제출 요청: gameId=${gameId}, problemId=${problemId}, answer=${selectedAnswer}`);
    const response = await axiosInstance.post(`/home/games/${gameId}/votes/live`, {
      gameId,
      selectedAnswer,
      problemId
    });
    console.log('라이브 투표 제출 응답:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('라이브 투표 제출 실패:', error);
    if (error.response) {
      throw new Error(error.response.data?.message || '라이브 투표 제출 실패');
    }
    throw error;
  }
};

export default {
  getGameDetail,
  submitLiveVote
}; 