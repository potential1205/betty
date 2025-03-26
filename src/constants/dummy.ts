export const gameData = {
  "두산": 3,
  "롯데": 2,
  "inning": 7,
  "status": "말"
};

export const userTokenDummy = [
  {
    team: "삼성",
    amount: 100
  }
];

export const allGames = [
  {
    id: 1,
    "두산": 3,
    "롯데": 2,
    "inning": 7,
    "status": "말"
  },
  {
    id: 2,
    "키움": 1,
    "KIA": 4,
    "inning": 5,
    "status": "초"
  },
  {
    id: 3,
    "LG": 0,
    "한화": 0,
    "inning": 1,
    "status": "초"
  },
  {
    id: 4,
    "SSG": 5,
    "삼성": 3,
    "inning": 8,
    "status": "말"
  }
];

export interface NFT {
  id: number;
  name: string;
  matchTeams: string[];
  price: number;
  paymentToken: string;
  image: string;
  description: string;
  tokenId: string;
  creator: string;
  createdAt: string;
}

export const marketNFTs: NFT[] = [
  {
    id: 1,
    name: "두산 vs 롯데 승리의 순간",
    matchTeams: ["두산", "롯데"],
    price: 50,
    paymentToken: "DOOSAN",
    image: "/pixels/doosan-lotte-1.jpg",
    description: "두산 vs 롯데 경기, 7회 말 역전의 순간을 담은 픽셀 아트",
    tokenId: "0x123...",
    creator: "BETTY #1234",
    createdAt: "2024-01-15 20:45"
  },
  {
    id: 2,
    name: "롯데 응원 픽셀 아트",
    matchTeams: ["두산", "롯데"],
    price: 30,
    paymentToken: "LOTTE",
    image: "/pixels/lotte-cheer.jpg",
    description: "2024.01.15 롯데 자이언츠 응원단 모습을 담은 픽셀 아트 NFT",
    tokenId: "0x456...",
    createdAt: "2024-01-15 19:30",
    creator: "BETTY #5678"
  },
  {
    id: 3,
    name: "양의지 홈런 순간",
    matchTeams: ["두산", "삼성"],
    price: 500,
    paymentToken: "DOOSAN",
    image: "/pixels/doosan-homerun.jpg",
    description: "두산 베어스 양의지 선수의 역전 홈런 순간을 담은 픽셀 아트",
    tokenId: "0x789...",
    creator: "BETTY #9012",
    createdAt: "2024-01-15 21:15"
  },
  {
    id: 4,
    name: "삼성 라이온즈 끝내기 승리",
    matchTeams: ["삼성", "SSG"],
    price: 100, 
    paymentToken: "SAMSUNG",
    image: "/pixels/samsung-victory.jpg",
    description: "삼성 라이온즈의 극적인 끝내기 승리 순간을 담은 픽셀 아트",
    tokenId: "0xabc...",
    creator: "BETTY #3456",
    createdAt: "2024-01-15 22:00"
  }
];

export interface PixelNFT {
  id: number;
  image: string;
  createdAt: string;
  matchTeams: string[];  // 경기 당시 팀들
  creator: string;
  isListed: boolean;     // 마켓에 올라갔는지 여부
}

export const userPixelNFTs: PixelNFT[] = [
  {
    id: 1,
    image: "/pixels/user-pixel-1.jpg",
    createdAt: "2024-01-15 20:45",
    matchTeams: ["두산", "롯데"],
    creator: "BETTY #1234",
    isListed: false
  }
];

export interface Quiz {
  id: number;
  question: string;
  options: string[];
  answer: number;
}

export interface QuizHistory {
  quizId: number;
  userAnswer: number | null;
  answeredAt: string;
  totalVotes: number;
  optionVotes: number[];
}

export const quizData: Quiz[] = [
  {
    id: 1,
    question: "다음 타자는 어떤 결과를 낼까요?",
    options: ["안타", "2루타", "3루타", "아웃"],
    answer: 3
  },
  {
    id: 2,
    question: "이번 이닝 득점은?",
    options: ["0점", "1점", "2점", "3점 이상"],
    answer: 0
  },
  {
    id: 3,
    question: "다음 투구는 어떤 구종일까요?",
    options: ["직구", "슬라이더", "커브", "체인지업"],
    answer: 1
  },
  {
    id: 4,
    question: "이번 이닝 안타 개수는?",
    options: ["0개", "1개", "2개", "3개 이상"],
    answer: 2
  },
  {
    id: 5,
    question: "다음 타자는 어떤 결과로 출루할까요?",
    options: ["안타", "볼넷", "사구", "출루 실패"],
    answer: 1
  }
];

export interface Transaction {
  id: number;
  type: 'BUY' | 'SELL' | 'CHARGE';
  amount: number;
  tokenAmount?: number;
  tokenPrice?: number;
  tokenName?: string;
  date: string;
}

export interface WalletInfo {
  address: string;
  totalBTC: number;
  tokens: {
    team: string;
    amount: number;
    btcValue: number;
  }[];
  transactions: Transaction[];
}

export const walletDummy: WalletInfo = {
  address: "0x1234...5678",
  totalBTC: 1000, // 100,000원
  tokens: [
    {
      team: "삼성",
      amount: 5,
      btcValue: 50 // 10 BTC per token
    },
    {
      team: "두산",
      amount: 3,
      btcValue: 30 // 10 BTC per token
    }
  ],
  transactions: [
    {
      id: 1,
      type: 'CHARGE',
      amount: 100000,
      date: "2024-01-15 14:30"
    },
    {
      id: 2,
      type: 'BUY',
      amount: 50,
      tokenAmount: 5,
      tokenPrice: 10,
      tokenName: "삼성",
      date: "2024-01-15 15:00"
    },
    {
      id: 3,
      type: 'SELL',
      amount: 30,
      tokenAmount: 3,
      tokenPrice: 10,
      tokenName: "두산",
      date: "2024-01-15 16:30"
    }
  ]
};
