export const gameData = {
  "두산": 3,
  "롯데": 2,
  "inning": 7,
  "status": "말"
};

export const userTokenDummy = [
  {
    team: "삼성",
    amount: 5
  },
  {
    team: "두산",
    amount: 3
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

export interface TeamTokenPrice {
  team: string;
  price: number;  // 1 팬토큰당 베티코인 가격
  change24h: number;  // 24시간 가격 변동률 (%)
  volume24h: number;  // 24시간 거래량
}

export interface ChartData {
  timestamp: string;
  price: number;
}

export const teamTokenPrices: TeamTokenPrice[] = [
  {
    team: "키움",
    price: 100,
    change24h: 2.5,
    volume24h: 1500
  },
  {
    team: "두산",
    price: 100,
    change24h: -1.2,
    volume24h: 2000
  },
  {
    team: "롯데",
    price: 100,
    change24h: 0.8,
    volume24h: 1800
  },
  {
    team: "삼성",
    price: 100,
    change24h: 1.5,
    volume24h: 2200
  },
  {
    team: "한화",
    price: 100,
    change24h: -0.5,
    volume24h: 1200
  },
  {
    team: "KIA",
    price: 100,
    change24h: 3.2,
    volume24h: 1900
  },
  {
    team: "LG",
    price: 100,
    change24h: 1.8,
    volume24h: 2100
  },
  {
    team: "SSG",
    price: 100,
    change24h: -2.1,
    volume24h: 1700
  },
  {
    team: "NC",
    price: 100,
    change24h: 0.3,
    volume24h: 1600
  },
  {
    team: "KT",
    price: 100,
    change24h: 1.1,
    volume24h: 1400
  }
];

export const teamChartData: { [key: string]: ChartData[] } = {
  "키움": [
    { timestamp: "2024-01-15 00:00", price: 98 },
    { timestamp: "2024-01-15 06:00", price: 101 },
    { timestamp: "2024-01-15 12:00", price: 99 },
    { timestamp: "2024-01-15 18:00", price: 100 },
    { timestamp: "2024-01-16 00:00", price: 102.5 }
  ],
  "두산": [
    { timestamp: "2024-01-15 00:00", price: 100 },
    { timestamp: "2024-01-15 06:00", price: 99 },
    { timestamp: "2024-01-15 12:00", price: 98.5 },
    { timestamp: "2024-01-15 18:00", price: 98.8 },
    { timestamp: "2024-01-16 00:00", price: 98.8 }
  ],
  // ... 다른 팀들의 차트 데이터도 비슷한 형식으로 추가
};

export const teamColors = {
  "키움": { bg: "#570514", text: "#B07F4A" },
  "두산": { bg: "#1A1748", text: "#EB1D25" },
  "롯데": { bg: "#041E42", text: "#D00F31" },
  "삼성": { bg: "#074CA1", text: "#C0C0C0" },
  "한화": { bg: "#FC4E00", text: "#07111F" },
  "KIA": { bg: "#EA0029", text: "#06141F" },
  "LG": { bg: "#C30452", text: "#000000" },
  "SSG": { bg: "#CE0E2D", text: "#FFB81C" },
  "NC": { bg: "#315288", text: "#AF917B" },
  "KT": { bg: "#000000", text: "#EB1C24" }
};

export const formatTeamName = (team: string) => {
  const teamNames: { [key: string]: string } = {
    '두산': '두산',
    '롯데': '롯데',
    '키움': '키움',
    'KIA': 'KIA',
    'LG': 'LG',
    '한화': '한화',
    'SSG': 'SSG',
    '삼성': '삼성',
    'NC': 'NC',
    'KT': 'KT'
  };
  return teamNames[team] || team;
};

export const formatTeamCode = (team: string) => {
  const teamCodes: { [key: string]: string } = {
    '두산': 'DSB',    // 두산 베어스
    '롯데': 'LTG',    // 롯데 자이언츠
    '키움': 'KWH',    // 키움 히어로즈
    'KIA': 'KIA',     // KIA 타이거즈
    'LG': 'LGT',      // LG 트윈스
    '한화': 'HHE',    // 한화 이글스
    'SSG': 'SSG',     // SSG 랜더스
    '삼성': 'SAL',    // 삼성 라이온즈
    'NC': 'NCD',      // NC 다이노스
    'KT': 'KTW'       // KT 위즈
  };
  return teamCodes[team] || team;
};
