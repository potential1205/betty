import axiosInstance from './axios';


/**
 * 전광판 접근 권한을 조회합니다.
 * 현재 사용자가 특정 게임의 특정 팀 전광판에 접근할 수 있는지 확인합니다.
 * 
 * @param gameId 게임 ID
 * @param teamId 팀 ID
 * @returns 성공 시 { success: true, hasAccessPermission: boolean } 객체 반환
 */
export async function checkAccessPermission(gameId: number, teamId: number): Promise<{ success: boolean, hasAccessPermission: boolean }> {
  try {
    const response = await axiosInstance.get(`/display/games/${gameId}/teams/${teamId}/access`);
    // API 응답이 { success: true }만 반환하므로, 성공하면 접근 권한이 있는 것으로 간주
    return { 
      success: response.data.success,
      hasAccessPermission: response.data.success
    };
  } catch (error: any) {
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403) {
        return { success: false, hasAccessPermission: false }; // 권한이 없는 경우
      }
      throw new Error(error.response.data?.message || '접근 권한 조회 실패');
    }
    return { success: false, hasAccessPermission: false }; // 네트워크 오류 등의 경우
  }
}


export async function registerAccessTx(gameId: number, teamId: number, txHash: string): Promise<any> {
  try {
    const response = await axiosInstance.post(`/display/games/${gameId}/teams/${teamId}/access`, {
      txHash
    });
    return response.data;
  } catch (error: any) {
    if (error.response) {
      throw new Error(error.response.data?.message || '접근 권한 등록 실패');
    }
    throw error;
  }
}

