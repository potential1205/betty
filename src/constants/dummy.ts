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
