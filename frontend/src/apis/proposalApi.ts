import axiosInstance from './axios';

// 응답 인터페이스 정의
export interface Proposal {
  id: number;
  walletId: number;
  teamId: number;
  title: string;
  content: string;
  targetCount: number;
  currentCount: number;
  txHash: string;
  contentHash: string;
  createdAt: string;
  closedAt: string;
}

export interface ProposalListResponse {
  proposalList: Proposal[];
}

export interface ProposalDetailResponse {
  proposal: Proposal;
}

// 안건 등록 응답
export interface CreateProposalResponse {
  proposalId: number;
  contentHash: string;
  closedAt: string;
}

// 안건 해시 등록 응답
export interface RegisterHashResponse {
  success: boolean;
}

// 안건 등록 요청
export interface CreateProposalRequest {
  teamId: number;
  title: string;
  content: string;
}

// 안건 해시 등록 요청
export interface RegisterHashRequest {
  proposalId: number;
  txHash: string;
}

// 안건 투표 요청
export interface VoteProposalRequest {
  teamId: number;
  proposalId: number;
}

// 안건 투표 응답
export interface VoteProposalResponse {
  success: boolean;
}

/**
 * 팀별 안건 목록 조회
 * @param teamId 팀 ID
 * @returns 안건 목록
 */
export async function getTeamProposals(teamId: number): Promise<Proposal[]> {
  try {
    const response = await axiosInstance.get<ProposalListResponse>(`proposals/team/${teamId}`);
    return response.data.proposalList;
  } catch (error: any) {
    console.error(`팀 ${teamId}의 안건 목록 조회 실패:`, error);
    if (error.response) {
      console.error('응답 데이터:', error.response.data);
      console.error('응답 상태:', error.response.status);
    }
    throw new Error(`팀 안건 목록 조회 실패: ${error.message || '알 수 없는 오류'}`);
  }
}

/**
 * 개별 안건 상세 조회
 * @param proposalId 안건 ID
 * @param teamId 팀 ID
 * @returns 안건 상세 정보
 */
export async function getProposalDetail(proposalId: number, teamId: number): Promise<Proposal> {
  try {
    const response = await axiosInstance.get<ProposalDetailResponse>(`proposals/${proposalId}/team/${teamId}`);
    return response.data.proposal;
  } catch (error: any) {
    console.error(`안건 ${proposalId} 상세 조회 실패:`, error);
    if (error.response) {
      console.error('응답 데이터:', error.response.data);
      console.error('응답 상태:', error.response.status);
    }
    throw new Error(`안건 상세 조회 실패: ${error.message || '알 수 없는 오류'}`);
  }
}

/**
 * 안건 등록 - 1단계
 * @param data 안건 등록 데이터 (팀ID, 제목, 내용)
 * @returns 안건 ID, 컨텐츠 해시, 마감일
 */
export async function createProposal(data: CreateProposalRequest): Promise<CreateProposalResponse> {
  try {
    const response = await axiosInstance.post<CreateProposalResponse>('proposals', data);
    return response.data;
  } catch (error: any) {
    console.error('안건 등록 실패:', error);
    if (error.response) {
      console.error('응답 데이터:', error.response.data);
      console.error('응답 상태:', error.response.status);
    }
    throw new Error(`안건 등록 실패: ${error.message || '알 수 없는 오류'}`);
  }
}

/**
 * 안건 해시 등록 - 5단계
 * @param data 안건 ID와 트랜잭션 해시
 * @returns 성공 여부
 */
export async function registerProposalHash(data: RegisterHashRequest): Promise<boolean> {
  try {
    const response = await axiosInstance.post<RegisterHashResponse>('proposals/hash', data);
    return response.data.success;
  } catch (error: any) {
    console.error('안건 해시 등록 실패:', error);
    if (error.response) {
      console.error('응답 데이터:', error.response.data);
      console.error('응답 상태:', error.response.status);
    }
    throw new Error(`안건 해시 등록 실패: ${error.message || '알 수 없는 오류'}`);
  }
}

/**
 * 안건 투표 - 백엔드 API
 * @param data 안건 ID와 팀 ID
 * @returns 성공 여부
 */
export async function voteProposal(data: VoteProposalRequest): Promise<boolean> {
  try {
    const response = await axiosInstance.post<VoteProposalResponse>('proposals/vote', data);
    return response.data.success;
  } catch (error: any) {
    console.error('안건 투표 실패:', error);
    if (error.response) {
      console.error('응답 데이터:', error.response.data);
      console.error('응답 상태:', error.response.status);
    }
    throw new Error(`안건 투표 실패: ${error.message || '알 수 없는 오류'}`);
  }
} 