import { create } from 'zustand';
import { allGames, userTokenDummy, marketNFTs as dummyNFTs, userPixelNFTs } from '../constants/dummy';
import { teamColors } from '../constants/colors';
import { NFT } from '../constants/dummy';

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
  marketNFTs: NFT[];
  purchaseNFT: (nftId: number) => boolean;
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
  addNFT: (nft: Omit<NFT, 'id'>) => void;
  userPixelNFTs: PixelNFT[];
  addUserPixelNFT: (nft: Omit<PixelNFT, 'id' | 'isListed'>) => void;
  listNFTForSale: (nftId: number) => void;
  savedNFTs: NFT[];
  saveNFTToMyPage: (nft: NFT) => void;
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
  marketNFTs: dummyNFTs,
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

    // 투표 시 1토큰 사용 확인
    if (updates.currentVotes !== undefined) {
      const tokenSuccess = get().useTeamToken(proposal.team, get().VOTE_COST);
      if (!tokenSuccess) return state; // 토큰이 부족하면 업데이트하지 않음
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
    
    // 이전 팀
    let prevIndex = currentIndex - 1;
    if (prevIndex < 0) prevIndex = teams.length - 1;
    
    // 현재 팀
    let currentTeam = teams[currentIndex];
    
    // 다음 팀
    let nextIndex = currentIndex + 1;
    if (nextIndex >= teams.length) nextIndex = 0;
    
    // 중복 체크하여 배열에 추가
    result.push(teams[prevIndex]);
    result.push(currentTeam);
    result.push(teams[nextIndex]);
    
    // 중복된 팀 제거
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
    const tokenSuccess = get().useTeamToken(proposalData.team, get().SUGGEST_COST); // 3 토큰 사용
    if (!tokenSuccess) return state; // 토큰이 부족하면 제안 생성하지 않음

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
      return false; // 토큰이 부족하면 false 반환
    }

    // 토큰 차감
    set({
      userTokens: state.userTokens.map(token =>
        token.team === team
          ? { ...token, amount: token.amount - amount }
          : token
      )
    });

    return true; // 토큰 사용 성공
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
  addNFT: () => {},
  userPixelNFTs: userPixelNFTs,
  addUserPixelNFT: (nft) => set((state) => ({
    userPixelNFTs: [...state.userPixelNFTs, {
      ...nft,
      id: state.userPixelNFTs.length + 1,
      isListed: false,
      isPurchased: true
    }]
  })),
  listNFTForSale: (nftId) => set((state) => ({
    userPixelNFTs: state.userPixelNFTs.map(nft =>
      nft.id === nftId ? { ...nft, isListed: true } : nft
    )
  })),
  savedNFTs: [],
  saveNFTToMyPage: (nft: NFT) => set((state) => {
    const isAlreadySaved = state.savedNFTs.some(savedNFT => savedNFT.id === nft.id);
    if (isAlreadySaved) {
      alert('이미 저장된 NFT입니다.');
      return state;
    }

    return {
      savedNFTs: [...state.savedNFTs, nft]
    };
  }),
}));