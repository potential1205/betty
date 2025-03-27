import { create } from 'zustand';
import { allGames, userTokenDummy, walletDummy, Transaction } from '../constants/dummy';
import { teamColors } from '../constants/colors';

interface GameData {
  id: number;
  [team1: string]: number | string;  // 첫 번째 팀 스코어
  [team2: string]: number | string;  // 두 번째 팀 스코어
  inning: number;  // 회차
  status: "초" | "말";  // 이닝 상태
}

interface TeamToken {
  team: string;
  amount: number;
}

interface Proposal {
  id: number;
  team: string;
  title: string;
  description: string;
  requiredTokens: number;
  currentVotes: number;
  targetVotes: number;
  deadline: string;
}

interface AppState {
  currentIndex: number;
  isSidebarOpen: boolean;
  games: GameData[];
  userTokens: TeamToken[];
  myProposals: Proposal[];
  proposals: Proposal[];
  selectedTeam: string;
  isSuggestModalOpen: boolean;
  teams: string[];
  bettyPrice: number;
  bettyBalance: number;
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
  addProposal: (proposal: Omit<Proposal, 'id'>) => void;
  useTeamToken: (team: string, amount: number) => boolean;
  updateUserTokens: (team: string, amount: number) => void;
  SUGGEST_COST: number;
  VOTE_COST: number;
  walletInfo: typeof walletDummy;
  activeTab: 'ASSETS' | 'TRANSACTIONS' | 'CHARGE';
  setActiveTab: (tab: 'ASSETS' | 'TRANSACTIONS' | 'CHARGE') => void;
  addTransaction: (transaction: Omit<Transaction, 'id'>) => void;
  updateWalletBalance: (amount: number) => void;
  updateTokenBalance: (team: string, amount: number, btcValue: number) => void;
  nickname: string;
  setNickname: (name: string) => void;
  isChargeModalOpen: boolean;
  setIsChargeModalOpen: (isOpen: boolean) => void;
  chargeBetty: (amount: number) => void;
}

export const useStore = create<AppState>((set, get) => ({
  currentIndex: 0,
  isSidebarOpen: false,
  games: allGames,
  userTokens: userTokenDummy,
  myProposals: [],
  proposals: [],
  selectedTeam: userTokenDummy[0]?.team || 'bears',
  isSuggestModalOpen: false,
  teams: Object.keys(teamColors),
  bettyPrice: 0.0001,
  bettyBalance: 1000,
  setBettyPrice: (price: number) => set({ bettyPrice: price }),
  setBettyBalance: (balance: number) => set({ bettyBalance: balance }),
  setCurrentIndex: (index) => set({ currentIndex: index }),
  handleNext: () => set((state) => ({ 
    currentIndex: state.currentIndex === state.games.length - 1 ? 0 : state.currentIndex + 1 
  })),
  handlePrev: () => set((state) => ({ 
    currentIndex: state.currentIndex === 0 ? state.games.length - 1 : state.currentIndex - 1 
  })),
  toggleSidebar: (isOpen) => set({ isSidebarOpen: isOpen }),
  updateGameData: (newData) => set({ games: newData }),
  addMyProposal: (proposal) => set((state) => ({ 
    myProposals: [...state.myProposals, proposal],
    proposals: [...state.proposals, proposal]
  })),
  setSelectedTeam: (team) => set({ selectedTeam: team }),
  toggleSuggestModal: (isOpen) => set({ isSuggestModalOpen: isOpen }),
  updateProposal: (proposalId, updates) => set((state) => {
    const proposal = state.proposals.find(p => p.id === proposalId);
    
    if (!proposal) return state;

    if (updates.currentVotes !== undefined) {
      const tokenSuccess = get().useTeamToken(proposal.team, get().VOTE_COST);
      if (!tokenSuccess) return state;
    }

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
    const result = [];
    
    let prevIndex = currentIndex - 1;
    if (prevIndex < 0) prevIndex = teams.length - 1;
    
    let currentTeam = teams[currentIndex];
    
    let nextIndex = currentIndex + 1;
    if (nextIndex >= teams.length) nextIndex = 0;
    
    result.push(teams[prevIndex]);
    result.push(currentTeam);
    result.push(teams[nextIndex]);
    
    return [...new Set(result)];
  },
  handlePrevTeam: () => set((state) => {
    const currentIndex = state.teams.indexOf(state.selectedTeam);
    const newIndex = currentIndex > 0 ? currentIndex - 1 : state.teams.length - 1;
    return { selectedTeam: state.teams[newIndex] };
  }),
  handleNextTeam: () => set((state) => {
    const currentIndex = state.teams.indexOf(state.selectedTeam);
    const newIndex = currentIndex < state.teams.length - 1 ? currentIndex + 1 : 0;
    return { selectedTeam: state.teams[newIndex] };
  }),
  addProposal: (proposalData) => set((state) => {
    const tokenSuccess = get().useTeamToken(proposalData.team, get().SUGGEST_COST);
    if (!tokenSuccess) return state;

    const newProposal = {
      ...proposalData,
      id: state.proposals.length + 1,
      currentVotes: 0,
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
  walletInfo: walletDummy,
  activeTab: 'ASSETS',
  setActiveTab: (tab) => set({ activeTab: tab }),
  addTransaction: (transaction) => set((state) => ({
    walletInfo: {
      ...state.walletInfo,
      transactions: [
        {
          ...transaction,
          id: state.walletInfo.transactions.length + 1
        },
        ...state.walletInfo.transactions
      ]
    }
  })),
  updateWalletBalance: (amount) => set((state) => ({
    walletInfo: {
      ...state.walletInfo,
      totalBTC: state.walletInfo.totalBTC + amount
    }
  })),
  updateTokenBalance: (team, amount, btcValue) => set((state) => {
    // 기존 토큰이 있는지 확인
    const existingToken = state.userTokens.find(token => token.team === team);
    
    // BTC 가격 계산 (1 BETTY = 0.0001 BTC 기준)
    const btcAmount = amount * state.bettyPrice;
    
    // 새로운 userTokens 배열 생성
    const newUserTokens = existingToken
      ? state.userTokens.map(token =>
          token.team === team
            ? { ...token, amount: token.amount + amount }
            : token
        )
      : [...state.userTokens, { team, amount }];

    // 새로운 walletInfo.tokens 배열 생성
    const newWalletTokens = existingToken
      ? state.walletInfo.tokens.map(token =>
          token.team === team
            ? { ...token, amount: token.amount + amount, btcValue: token.btcValue + btcAmount }
            : token
        )
      : [...state.walletInfo.tokens, { team, amount, btcValue: btcAmount }];

    // 거래 내역 추가
    const newTransaction = {
      id: state.walletInfo.transactions.length + 1,
      type: amount > 0 ? 'BUY' : 'SELL',
      date: new Date().toLocaleDateString(),
      amount: Math.abs(btcAmount),
      tokenName: team,
      tokenAmount: Math.abs(amount),
      tokenPrice: state.bettyPrice
    };

    return {
      userTokens: newUserTokens,
      walletInfo: {
        ...state.walletInfo,
        tokens: newWalletTokens,
        transactions: [newTransaction, ...state.walletInfo.transactions]
      }
    };
  }),
  nickname: '',
  setNickname: (name) => set({ nickname: name.slice(0, 10) }),
  isChargeModalOpen: false,
  setIsChargeModalOpen: (isOpen) => set({ isChargeModalOpen: isOpen }),
  chargeBetty: (amount: number) => set((state) => {
    const btcAmount = amount / 100; // 1 BTC = 100원 기준
    return {
      bettyBalance: state.bettyBalance + amount,
      walletInfo: {
        ...state.walletInfo,
        totalBTC: state.walletInfo.totalBTC + btcAmount,
        transactions: [
          {
            id: state.walletInfo.transactions.length + 1,
            type: 'CHARGE',
            date: new Date().toLocaleDateString(),
            amount: amount
          },
          ...state.walletInfo.transactions
        ]
      }
    };
  }),
}));