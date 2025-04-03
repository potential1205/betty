import { create } from 'zustand';
import { allGames, userTokenDummy } from '../constants/dummy';
import { colors } from '../constants/colors';

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
  team: string;
  title: string;
  description: string;
  requiredTokens: number;
  currentVotes: number;
  targetVotes: number;
  deadline: string;
  status: 'pending' | 'approved';
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
  totalBTC: number;
  transactions: Transaction[];
  tokens?: Array<{
    team: string;
    amount: number;
    btcValue: number;
  }>;
  address?: string;
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
  walletInfo: WalletInfo;
  activeTab: string;
  setActiveTab: (tab: string) => void;
  addTransaction: (transaction: Omit<Transaction, 'id'>) => void;
  updateWalletBalance: (amount: number) => void;
  updateTokenBalance: (team: string, amount: number, btcValue: number) => void;
  nickname: string;
  setNickname: (name: string) => void;
  isChargeModalOpen: boolean;
  setIsChargeModalOpen: (isOpen: boolean) => void;
  chargeBetty: (amount: number) => void;
}

const initialWalletInfo: WalletInfo = {
  totalBTC: 100,
  transactions: [],
  tokens: []
};

export const useStore = create<AppState>((set, get) => ({
  currentIndex: 0,
  isSidebarOpen: false,
  games: allGames.map(game => ({
    ...game,
    status: game.status as "초" | "말"
  })),
  userTokens: userTokenDummy,
  myProposals: [],
  proposals: [],
  selectedTeam: Object.keys(colors)[0] || 'bears',
  isSuggestModalOpen: false,
  teams: Object.keys(colors),
  bettyPrice: 1,
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

    const newProposal: Proposal = {
      ...proposalData,
      id: state.proposals.length + 1,
      currentVotes: 0,
      status: 'pending' as const
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
      totalBTC: state.walletInfo.totalBTC + amount
    }
  })),
  updateTokenBalance: (team, amount, btcValue) => set((state) => {
    const newTransaction: Transaction = {
      id: state.walletInfo.transactions.length + 1,
      type: amount > 0 ? 'BUY' : 'SELL',
      team,
      amount: Math.abs(btcValue),
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
        btcValue: newTokens[existingTokenIndex].btcValue + btcValue
      };
    } else {
      newTokens.push({ team, amount, btcValue });
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
        totalBTC: state.walletInfo.totalBTC + amount,
        transactions: [newTransaction, ...state.walletInfo.transactions]
      }
    };
  }),
}));