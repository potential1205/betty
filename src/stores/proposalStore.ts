import { create } from 'zustand';
import { 
  getTeamProposals, 
  getProposalDetail, 
  createProposal, 
  registerProposalHash,
  voteProposal,
  Proposal, 
  CreateProposalRequest, 
  CreateProposalResponse,
  RegisterHashRequest,
  VoteProposalRequest
} from '../apis/proposalApi';

interface ProposalState {
  isLoading: boolean;
  error: string | null;
  // 현재 선택된 팀 ID
  selectedTeamId: number;
  // 선택된 팀의 안건 목록
  proposals: Proposal[];
  // 현재 조회 중인 안건 상세 정보
  currentProposal: Proposal | null;
  // 안건 등록 성공 메시지
  successMessage: string | null;
  // 현재 등록 중인 안건 정보
  pendingProposal: CreateProposalResponse | null;
  
  // 팀 선택 변경 핸들러
  selectTeam: (teamId: number) => void;
  // 팀 안건 목록 가져오기
  fetchTeamProposals: (teamId: number) => Promise<Proposal[]>;
  // 안건 상세 정보 가져오기
  fetchProposalDetail: (proposalId: number, teamId: number) => Promise<Proposal | null>;
  // 현재 안건 상태 초기화
  resetCurrentProposal: () => void;
  // 안건 등록 - 1단계
  createProposal: (data: CreateProposalRequest) => Promise<CreateProposalResponse | null>;
  // 안건 해시 등록 - 5단계
  registerProposalHash: (proposalId: number, txHash: string) => Promise<boolean>;
  // 안건 투표 - 백엔드 API
  voteProposal: (proposalId: number, teamId: number) => Promise<boolean>;
  // 상태 초기화
  resetMessages: () => void;
  // 대기 중인 안건 초기화
  resetPendingProposal: () => void;
}

export const useProposalStore = create<ProposalState>((set, get) => ({
  isLoading: false,
  error: null,
  selectedTeamId: 1, // 기본값으로 1번 팀 선택
  proposals: [],
  currentProposal: null,
  successMessage: null,
  pendingProposal: null,
  
  selectTeam: (teamId: number) => {
    set({ selectedTeamId: teamId });
    // 팀이 변경되면 해당 팀의 안건 목록 가져오기
    get().fetchTeamProposals(teamId);
  },
  
  fetchTeamProposals: async (teamId: number) => {
    set({ isLoading: true, error: null });
    
    try {
      const proposals = await getTeamProposals(teamId);
      set({ 
        isLoading: false, 
        proposals: proposals
      });
      return proposals;
    } catch (error: any) {
      set({ 
        isLoading: false, 
        error: error.message || '안건 목록 조회 중 오류가 발생했습니다.' 
      });
      return [];
    }
  },
  
  fetchProposalDetail: async (proposalId: number, teamId: number) => {
    set({ isLoading: true, error: null });
    
    try {
      const proposal = await getProposalDetail(proposalId, teamId);
      set({ 
        isLoading: false, 
        currentProposal: proposal 
      });
      return proposal;
    } catch (error: any) {
      set({ 
        isLoading: false, 
        error: error.message || '안건 상세 조회 중 오류가 발생했습니다.' 
      });
      return null;
    }
  },
  
  resetCurrentProposal: () => {
    set({ currentProposal: null });
  },
  
  createProposal: async (data: CreateProposalRequest) => {
    set({ isLoading: true, error: null, successMessage: null });
    
    try {
      const response = await createProposal(data);
      set({ 
        isLoading: false, 
        pendingProposal: response,
        successMessage: '안건이 임시 등록되었습니다. 스마트 컨트랙트에 등록해주세요.'
      });
      return response;
    } catch (error: any) {
      set({ 
        isLoading: false, 
        error: error.message || '안건 등록 중 오류가 발생했습니다.' 
      });
      return null;
    }
  },
  
  registerProposalHash: async (proposalId: number, txHash: string) => {
    set({ isLoading: true, error: null, successMessage: null });
    
    try {
      const success = await registerProposalHash({ proposalId, txHash });
      
      if (success) {
        // 해시 등록 성공 시 해당 팀의 안건 목록 다시 불러오기
        await get().fetchTeamProposals(get().selectedTeamId);
        
        set({ 
          isLoading: false,
          pendingProposal: null,
          successMessage: '안건이 성공적으로 등록되었습니다.'
        });
      }
      
      return success;
    } catch (error: any) {
      set({ 
        isLoading: false, 
        error: error.message || '안건 해시 등록 중 오류가 발생했습니다.' 
      });
      return false;
    }
  },
  
  voteProposal: async (proposalId: number, teamId: number) => {
    set({ isLoading: true, error: null, successMessage: null });
    
    try {
      const success = await voteProposal({ proposalId, teamId });
      
      if (success) {
        // 투표 성공 시 해당 팀의 안건 목록과 현재 안건 상세 정보 다시 불러오기
        await get().fetchTeamProposals(teamId);
        
        if (get().currentProposal) {
          await get().fetchProposalDetail(proposalId, teamId);
        }
        
        set({ 
          isLoading: false,
          successMessage: '투표가 성공적으로 등록되었습니다.'
        });
      }
      
      return success;
    } catch (error: any) {
      set({ 
        isLoading: false, 
        error: error.message || '투표 등록 중 오류가 발생했습니다.' 
      });
      return false;
    }
  },
  
  resetMessages: () => {
    set({ error: null, successMessage: null });
  },
  
  resetPendingProposal: () => {
    set({ pendingProposal: null });
  }
})); 