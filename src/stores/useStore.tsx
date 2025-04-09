import { create } from 'zustand';
import axios from 'axios';
import { allGames, userTokenDummy } from '../constants/dummy';

interface GameData {
  id: number;
  inning: number;
  status: "초" | "말";
  [key: string]: number | string | undefined;
}

interface TeamToken {
  team: string;
  amount: number;
}

interface Proposal {
  id: number;
  walletId: number;
  teamId: number;
  title: string;
  content: string;
  targetCount: number;
  currentCount: number;
  createdAt: string;
  closedAt: string;
}

export interface Transaction {
  id: number;
  type: 'BUY' | 'SELL' | 'VOTE' | 'CHARGE';
  team?: string;
  amount: number;
  timestamp: string;
  date?: string;
  tokenName?: string;
  tokenAmount?: number;
  tokenPrice?: number;
}

interface WalletInfo {
  totalBET: number;
  transactions: Transaction[];
  tokens?: Array<{
    team: string;
    amount: number;
    betValue: number;
  }>;
  address?: string;
  nickname?: string;
}

interface GameSchedule {
  season: number;
  gameDate: string;
  startTime: string;
  stadium: string;
  homeTeam: string;
  awayTeam: string;
  status: string;
}

export interface Game {
  id: number;
  gameId: number;
  homeTeamId: number;
  awayTeamId: number;
  homeTeam: string;
  awayTeam: string;
  homeScore: number;
  awayScore: number;
  inning: number;
  status: string;
  schedule: {
    gameId: number;
    homeTeamId: number;
    awayTeamId: number;
    season: number;
    gameDate: string;
    startTime: string;
    stadium: string;
    homeTeamName: string;
    awayTeamName: string;
    status: String;
  };
  homeTeamCode?: string;
  awayTeamCode?: string;
  homeTeamName?: string;
  awayTeamName?: string;
  homeTeamLogo?: string;
  awayTeamLogo?: string;
  homeTeamColor?: string;
  awayTeamColor?: string;
}

export interface Problem {
  problemId: string;
  gameId: string;
  inning: string;
  attackTeam: string;
  batterName: string;
  batterNumber: string;
  questionCode: string;
  description: string;
  options: string[];
  answer: string | null;
  timestamp: number;
  push: boolean;
}

interface AppState {
  currentIndex: number;
  isSidebarOpen: boolean;
  games: GameData[];
  todayGames: Game[];
  setTodayGames: (games: Game[]) => void;
  currentGame: Game | null;
  setCurrentGame: (game: Game | null) => void;
  userTokens: TeamToken[];
  myProposals: Proposal[];
  proposals: Proposal[];
  selectedTeam: string;
  isSuggestModalOpen: boolean;
  teams: string[];
  bettyPrice: number;
  bettyBalance: number;
  loading: {
    proposals: boolean;
    tokenCount: boolean;
    createProposal: boolean;
    vote: boolean;
  };
  error: string | null;
  setBettyPrice: (price: number) => void;
  setBettyBalance: (balance: number) => void;
  setCurrentIndex: (index: number) => void;
  handleNext: () => void;
  handlePrev: () => void;
  toggleSidebar: (isOpen: boolean) => void;
  updateGameData: (newData: GameData[]) => void;
  addMyProposal: (proposal: Proposal) => void;
  setSelectedTeam: (team: string) => void;
  toggleSuggestModal: (isOpen: boolean) => void;
  updateProposal: (proposalId: number, updates: Partial<Proposal>) => void;
  getVisibleTeams: () => string[];
  handlePrevTeam: () => void;
  handleNextTeam: () => void;
  addProposal: (proposal: Omit<Proposal, 'id' | 'walletId' | 'createdAt' | 'closedAt' | 'currentCount'>) => void;
  useTeamToken: (team: string, amount: number) => boolean;
  updateUserTokens: (team: string, amount: number) => void;
  SUGGEST_COST: number;
  VOTE_COST: number;
  walletInfo: WalletInfo;
  activeTab: string;
  setActiveTab: (tab: string) => void;
  addTransaction: (transaction: Omit<Transaction, 'id'>) => void;
  updateWalletBalance: (amount: number) => void;
  updateTokenBalance: (team: string, amount: number, betValue: number) => void;
  nickname: string;
  setNickname: (name: string) => void;
  isChargeModalOpen: boolean;
  setIsChargeModalOpen: (isOpen: boolean) => void;
  chargeBetty: (amount: number) => void;
  updateWalletInfo: (newInfo: Partial<WalletInfo>) => void;
  fetchWalletInfo: () => Promise<void>;
  
  // API 함수들
  fetchProposals: (teamId: number) => Promise<void>;
  submitProposal: (teamId: number, title: string, content: string, targetCount: number) => Promise<void>;
  voteForProposal: (teamId: number, proposalId: number) => Promise<void>;
  fetchTeamTokenCount: (teamId: number) => Promise<number>;
  clearError: () => void;
}

const initialWalletInfo: WalletInfo = {
  totalBET: 100,
  transactions: [],
  tokens: [
    { team: '8', amount: 100, betValue: 1000 },  // 삼성
    { team: '1', amount: 100, betValue: 1000 },  // 두산
    { team: '2', amount: 100, betValue: 1000 }   // 롯데
  ]
};

// 팀 정보 상수
export const TEAMS = {
  DOOSAN: { id: '1', name: '두산', code: 'DSB' },
  LOTTE: { id: '2', name: '롯데', code: 'LTG' },
  KIWOOM: { id: '3', name: '키움', code: 'KWH' },
  KIA: { id: '4', name: 'KIA', code: 'KIA' },
  LG: { id: '5', name: 'LG', code: 'LGT' },
  HANWHA: { id: '6', name: '한화', code: 'HWE' },
  SSG: { id: '7', name: 'SSG', code: 'SSG' },
  SAMSUNG: { id: '8', name: '삼성', code: 'SSL' },
  NC: { id: '9', name: 'NC', code: 'NCD' },
  KT: { id: '10', name: 'KT', code: 'KTW' }
} as const;

// ID로 팀 정보 조회
export const getTeamById = (id: string) => {
  return Object.values(TEAMS).find(team => team.id === id);
};

// API 관련 함수들
const api = {
  getProposalList: async (teamId: number) => {
    try {
      const response = await axios.get(`/api/v1/proposals/team/${teamId}`);
      return response.data;
    } catch (error) {
      console.error('안건 목록 조회 실패:', error);
      throw error;
    }
  },
  
  getProposalDetail: async (proposalId: number, teamId: number) => {
    try {
      const response = await axios.get(`/api/v1/proposals/${proposalId}/team/${teamId}`);
      return response.data;
    } catch (error) {
      console.error('안건 상세 조회 실패:', error);
      throw error;
    }
  },
  
  createProposal: async (teamId: number, title: string, content: string, targetCount: number) => {
    try {
      const response = await axios.post('/api/v1/proposals', {
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
  },
  
  voteProposal: async (teamId: number, proposalId: number) => {
    try {
      const response = await axios.post('/api/v1/proposals/vote', {
        teamId,
        proposalId
      });
      return response.data;
    } catch (error) {
      console.error('안건 투표 실패:', error);
      throw error;
    }
  },
  
  getTeamTokenCount: async (teamId: number) => {
    try {
      const response = await axios.get(`/api/v1/proposals/team/${teamId}/token/count`);
      return response.data.teamTokenCount;
    } catch (error) {
      console.error('팀 토큰 개수 조회 실패:', error);
      throw error;
    }
  }
};

export const useStore = create<AppState>((set, get) => ({
  currentIndex: 0,
  isSidebarOpen: false,
  games: allGames.map(game => ({
    ...game,
    status: game.status as "초" | "말"
  })),
  todayGames: [],
  setTodayGames: (games) => set({ todayGames: games }),
  currentGame: null,
  setCurrentGame: (game) => set({ currentGame: game }),
  userTokens: userTokenDummy,
  myProposals: [],
  proposals: [],
  selectedTeam: '1',
  isSuggestModalOpen: false,
  teams: Object.values(TEAMS).map(team => team.id),
  bettyPrice: 1,
  bettyBalance: 1000,
  loading: {
    proposals: false,
    tokenCount: false,
    createProposal: false,
    vote: false
  },
  error: null,
  
  // 기본 설정 함수들
  setBettyPrice: (price: number) => set({ bettyPrice: price }),
  setBettyBalance: (balance: number) => set({ bettyBalance: balance }),
  setCurrentIndex: (index) => set({ currentIndex: index }),
  handleNext: () => set((state) => ({ 
    currentIndex: Math.min(state.currentIndex + 1, state.games.length - 1)
  })),
  handlePrev: () => set((state) => ({ 
    currentIndex: Math.max(state.currentIndex - 1, 0)
  })),
  toggleSidebar: (isOpen) => set({ isSidebarOpen: isOpen }),
  updateGameData: (newData) => set({ games: newData }),
  addMyProposal: (proposal) => set((state) => ({ 
    myProposals: [...state.myProposals, proposal]
  })),
  setSelectedTeam: (team) => {
    set({ selectedTeam: team });
    // 팀 변경 시 자동으로 데이터 로드
    const teamId = parseInt(team);
    get().fetchProposals(teamId);
    get().fetchTeamTokenCount(teamId);
  },
  toggleSuggestModal: (isOpen) => set({ isSuggestModalOpen: isOpen }),
  updateProposal: (proposalId, updates) => set((state) => {
    const proposal = state.proposals.find(p => p.id === proposalId);
    if (!proposal) return state;

    return {
      proposals: state.proposals.map(p => 
        p.id === proposalId ? { ...p, ...updates } : p
      ),
      myProposals: state.myProposals.map(p =>
        p.id === proposalId ? { ...p, ...updates } : p
      )
    };
  }),
  getVisibleTeams: () => {
    const { teams, selectedTeam } = get();
    const currentIndex = teams.indexOf(selectedTeam);
    
    if (currentIndex === -1) return [teams[0], teams[1], teams[2]];
    
    const prevIndex = currentIndex === 0 ? teams.length - 1 : currentIndex - 1;
    const nextIndex = currentIndex === teams.length - 1 ? 0 : currentIndex + 1;
    
    return [teams[prevIndex], teams[currentIndex], teams[nextIndex]];
  },
  handlePrevTeam: () => set((state) => {
    const currentIndex = state.teams.indexOf(state.selectedTeam);
    const newIndex = currentIndex > 0 ? currentIndex - 1 : state.teams.length - 1;
    const newTeam = state.teams[newIndex];
    
    // 팀 변경 시 자동으로 데이터 로드
    const teamId = parseInt(newTeam);
    get().fetchProposals(teamId);
    get().fetchTeamTokenCount(teamId);
    
    return { selectedTeam: newTeam };
  }),
  handleNextTeam: () => set((state) => {
    const currentIndex = state.teams.indexOf(state.selectedTeam);
    const newIndex = currentIndex < state.teams.length - 1 ? currentIndex + 1 : 0;
    const newTeam = state.teams[newIndex];
    
    // 팀 변경 시 자동으로 데이터 로드
    const teamId = parseInt(newTeam);
    get().fetchProposals(teamId);
    get().fetchTeamTokenCount(teamId);
    
    return { selectedTeam: newTeam };
  }),
  addProposal: (proposalData) => set((state) => {
    const newProposal: Proposal = {
      ...proposalData,
      id: state.proposals.length + 1,
      walletId: 0,
      currentCount: 0,
      createdAt: new Date().toISOString(),
      closedAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString()
    };

    return {
      proposals: [...state.proposals, newProposal],
      myProposals: [...state.myProposals, newProposal]
    };
  }),
  useTeamToken: (team: string, amount: number) => {
    const state = get();
    const userToken = state.userTokens.find(token => token.team === team);
    
    if (!userToken || userToken.amount < amount) {
      return false;
    }

    set({
      userTokens: state.userTokens.map(token =>
        token.team === team
          ? { ...token, amount: token.amount - amount }
          : token
      )
    });

    return true;
  },
  updateUserTokens: (team: string, amount: number) => set(state => ({
    userTokens: state.userTokens.map(token =>
      token.team === team
        ? { ...token, amount: token.amount + amount }
        : token
    )
  })),
  SUGGEST_COST: 3,
  VOTE_COST: 1,
  walletInfo: initialWalletInfo,
  activeTab: 'ASSETS',
  setActiveTab: (tab) => set({ activeTab: tab }),
  addTransaction: (transaction) => set((state) => ({
    walletInfo: {
      ...state.walletInfo,
      transactions: [
        {
          ...transaction,
          id: state.walletInfo.transactions.length + 1,
          timestamp: new Date().toISOString()
        },
        ...state.walletInfo.transactions
      ]
    }
  })),
  updateWalletBalance: (amount) => set((state) => ({
    walletInfo: {
      ...state.walletInfo,
      totalBET: state.walletInfo.totalBET + amount
    }
  })),
  updateTokenBalance: (team, amount, betValue) => set((state) => {
    const newTransaction: Transaction = {
      id: state.walletInfo.transactions.length + 1,
      type: amount > 0 ? 'BUY' : 'SELL',
      team,
      amount: Math.abs(betValue),
      timestamp: new Date().toISOString(),
      date: new Date().toLocaleDateString(),
      tokenName: team,
      tokenAmount: Math.abs(amount),
      tokenPrice: state.bettyPrice
    };

    const newTokens = state.walletInfo.tokens || [];
    const existingTokenIndex = newTokens.findIndex(t => t.team === team);

    if (existingTokenIndex >= 0) {
      newTokens[existingTokenIndex] = {
        ...newTokens[existingTokenIndex],
        amount: newTokens[existingTokenIndex].amount + amount,
        betValue: newTokens[existingTokenIndex].betValue + betValue
      };
    } else {
      newTokens.push({ team, amount, betValue });
    }

    return {
      userTokens: state.userTokens.map(token =>
        token.team === team
          ? { ...token, amount: token.amount + amount }
          : token
      ),
      walletInfo: {
        ...state.walletInfo,
        tokens: newTokens,
        transactions: [newTransaction, ...state.walletInfo.transactions]
      }
    };
  }),
  nickname: '',
  setNickname: (name) => set({ nickname: name.slice(0, 10) }),
  isChargeModalOpen: false,
  setIsChargeModalOpen: (isOpen) => set({ isChargeModalOpen: isOpen }),
  chargeBetty: (amount: number) => set((state) => {
    const newTransaction: Transaction = {
      id: state.walletInfo.transactions.length + 1,
      type: 'CHARGE',
      amount,
      timestamp: new Date().toISOString(),
      date: new Date().toLocaleDateString()
    };

    return {
      bettyBalance: state.bettyBalance + amount,
      walletInfo: {
        ...state.walletInfo,
        totalBET: state.walletInfo.totalBET + amount,
        transactions: [newTransaction, ...state.walletInfo.transactions]
      }
    };
  }),
  updateWalletInfo: (newInfo: Partial<WalletInfo>) => set((state) => ({
    walletInfo: {
      ...state.walletInfo,
      ...newInfo,
      totalBET: newInfo.totalBET ?? state.walletInfo.totalBET ?? 0,
      tokens: newInfo.tokens ?? state.walletInfo.tokens ?? [],
      transactions: newInfo.transactions ?? state.walletInfo.transactions ?? [],
    }
  })),

  fetchWalletInfo: async () => {
    try {
      const data = await import('../apis/axios').then(mod => mod.getWalletBalances());

      const tokenList = data.tokens.map((t: any) => ({
        team: t.tokenName,
        amount: Number(t.balance),
        betValue: Number(t.balance) * get().bettyPrice
      }));

      const total = Number(data.totalBet);

      get().updateWalletInfo({
        address: data.walletAddress,
        nickname: data.nickname,
        totalBET: total,
        tokens: tokenList
      });
    } catch (error) {
      console.error('fetchWalletInfo 실패:', error);
    }
  },
  
  // API 연동 함수들
  clearError: () => set({ error: null }),
  
  fetchProposals: async (teamId: number) => {
    set(state => ({ 
      loading: { ...state.loading, proposals: true },
      error: null
    }));
    
    try {
      const response = await api.getProposalList(teamId);
      set({ 
        proposals: response.proposalList,
        loading: { ...get().loading, proposals: false }
      });
      
      // 내가 작성한 제안 필터링 (추후 API가 제공하면 개선 필요)
      const myProposals = response.proposalList.filter(
        (p: Proposal) => p.walletId === 0 // 실제로는 로그인한 사용자의 ID와 비교해야 함
      );
      
      set({ myProposals });
    } catch (error) {
      console.error('Failed to fetch proposals:', error);
      set({ 
        proposals: [],
        loading: { ...get().loading, proposals: false },
        error: error instanceof Error ? error.message : '안건 목록 조회 중 오류가 발생했습니다.'
      });
    }
  },
  
  submitProposal: async (teamId: number, title: string, content: string, targetCount: number) => {
    set(state => ({ 
      loading: { ...state.loading, createProposal: true },
      error: null
    }));
    
    try {
      // 토큰 차감 로직 (실제로는 백엔드에서 처리)
      const hasEnoughTokens = get().useTeamToken(String(teamId), get().SUGGEST_COST);
      
      if (!hasEnoughTokens) {
        throw new Error(`토큰이 부족합니다. 제안 등록에는 ${get().SUGGEST_COST} 토큰이 필요합니다.`);
      }
      
      await api.createProposal(teamId, title, content, targetCount);
      
      // 제안 등록 후 트랜잭션 추가
      get().addTransaction({
        type: 'VOTE',
        team: String(teamId),
        amount: get().SUGGEST_COST,
        timestamp: new Date().toISOString(),
        tokenName: String(teamId),
        tokenAmount: get().SUGGEST_COST,
        tokenPrice: get().bettyPrice
      });
      
      // 데이터 다시 불러오기
      await get().fetchProposals(teamId);
      await get().fetchTeamTokenCount(teamId);
      
      set(state => ({ 
        loading: { ...state.loading, createProposal: false },
        isSuggestModalOpen: false // 제안 모달 닫기
      }));
    } catch (error) {
      console.error('Failed to submit proposal:', error);
      set(state => ({ 
        loading: { ...state.loading, createProposal: false },
        error: error instanceof Error ? error.message : '제안 등록 중 오류가 발생했습니다.'
      }));
      throw error;
    }
  },
  
  voteForProposal: async (teamId: number, proposalId: number) => {
    set(state => ({ 
      loading: { ...state.loading, vote: true },
      error: null
    }));
    
    try {
      // 토큰 차감 로직 (실제로는 백엔드에서 처리)
      const hasEnoughTokens = get().useTeamToken(String(teamId), get().VOTE_COST);
      
      if (!hasEnoughTokens) {
        throw new Error(`토큰이 부족합니다. 투표에는 ${get().VOTE_COST} 토큰이 필요합니다.`);
      }
      
      await api.voteProposal(teamId, proposalId);
      
      // 투표 후 트랜잭션 추가
      get().addTransaction({
        type: 'VOTE',
        team: String(teamId),
        amount: get().VOTE_COST,
        timestamp: new Date().toISOString(),
        tokenName: String(teamId),
        tokenAmount: get().VOTE_COST,
        tokenPrice: get().bettyPrice
      });
      
      // 데이터 다시 불러오기
      await get().fetchProposals(teamId);
      await get().fetchTeamTokenCount(teamId);
      
      set(state => ({ 
        loading: { ...state.loading, vote: false }
      }));
    } catch (error) {
      console.error('Failed to vote for proposal:', error);
      set(state => ({ 
        loading: { ...state.loading, vote: false },
        error: error instanceof Error ? error.message : '투표 중 오류가 발생했습니다.'
      }));
      throw error;
    }
  },
  
  fetchTeamTokenCount: async (teamId: number) => {
    set(state => ({ 
      loading: { ...state.loading, tokenCount: true }
    }));
    
    try {
      const tokenCount = await api.getTeamTokenCount(teamId);
      
      // userTokens 업데이트
      set(state => ({
        userTokens: state.userTokens.map(token => 
          token.team === String(teamId)
            ? { ...token, amount: tokenCount }
            : token
        ),
        loading: { ...state.loading, tokenCount: false }
      }));
      
      return tokenCount;
    } catch (error) {
      console.error('Failed to fetch team token count:', error);
      set(state => ({ 
        loading: { ...state.loading, tokenCount: false },
        error: error instanceof Error ? error.message : '토큰 개수 조회 중 오류가 발생했습니다.'
      }));
      return 0;
    }
  }
}));