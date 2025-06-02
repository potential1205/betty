import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { formatTeamCode, formatTeamName, teamColors, teamTokenPrices } from '../constants/dummy';
import bettyImg from '../assets/bettycoin.png';
import backImg from '../assets/back_black.png';
import { useStore } from '../stores/useStore';
import CandleChart from './CandleChart';
import { buyFanToken as apiBuyFanToken, sellFanToken as apiSellFanToken, swapFanToken as apiSwapFanToken } from '../apis/exchangeApi';
import { teamToTokenIdMap } from '../constants/tokenMap';
import { getTokenInfo, getWalletBalance, getBettyPrice } from '../apis/tokenApi';
import { getExchangeContract, buyFanToken, sellFanToken, swapFanToken } from '../utils/exchangeContract';
import { useWalletStore } from '../stores/walletStore';
import { ethers } from 'ethers';
import { getLast24HoursPrices, getLast7DaysPrices } from '../apis/priceApi';
import { CandlestickData, Time } from 'lightweight-charts';

type TeamColor = {
  bg: string;
  text: string;
};

type TeamColors = {
  [key: string]: TeamColor;
};

interface BuyFanTokenProps {
  isOpen: boolean;
  onClose: () => void;
  team: string;
  price: number;
  tokenId: number;
}

type Mode = 'buy' | 'swap' | 'sell';

interface TokenInfo {
  balance: number;
  tokenAddress: string;
  tokenName: string;
}

interface WalletToken {
  tokenName: string;
  balance: number;
}

// 더미 차트 데이터 생성 함수
const generateDummyDailyData = (basePrice: number): CandlestickData[] => {
  const data: CandlestickData[] = [];
  const today = new Date();
  
  // 과거 7일간의 데이터 생성
  for (let i = 6; i >= 0; i--) {
    const date = new Date();
    date.setDate(today.getDate() - i);
    const dateStr = date.toISOString().split('T')[0];
    
    // 일별 변동폭 (±3% 내외)
    const variation = 0.97 + Math.random() * 0.06; // 0.97~1.03
    
    // 첫날 데이터는 기준가격, 이후 이전 날의 종가를 기반으로 변동
    const prevClose = i === 6 ? basePrice : data[data.length-1].close;
    const close = i === 0 ? basePrice : prevClose * variation;
    
    // 일중 변동폭 (±2% 내외)
    const dayVariation = 0.98 + Math.random() * 0.04; // 0.98~1.02
    const open = close * dayVariation;
    
    const high = Math.max(open, close) * (1 + Math.random() * 0.02); // 최대 2% 상승
    const low = Math.min(open, close) * (1 - Math.random() * 0.02); // 최대 2% 하락
    
    data.push({
      time: dateStr,
      open,
      high,
      low,
      close
    });
  }
  
  return data;
};

const generateDummyHourlyData = (basePrice: number): CandlestickData[] => {
  const data: CandlestickData[] = [];
  const now = new Date();
  
  // 현재부터 24시간 전까지의 시간별 데이터 생성
  for (let i = 23; i >= 0; i--) {
    const date = new Date();
    date.setHours(now.getHours() - i);
    // ISO 시간 형식으로 포맷팅 (시간 단위로 맞춤)
    const timeStr = date.toISOString().split(':')[0] + ':00:00Z';
    
    // 시간별 변동폭 (±1.5% 내외)
    const variation = 0.985 + Math.random() * 0.03; // 0.985~1.015
    
    // 첫 시간 데이터는 기준가격, 이후 이전 시간의 종가를 기반으로 변동
    const prevClose = i === 23 ? basePrice * 0.95 : data[data.length-1].close;
    const close = i === 0 ? basePrice : prevClose * variation;
    
    // 시간내 변동폭 (±1% 내외)
    const hourVariation = 0.99 + Math.random() * 0.02; // 0.99~1.01
    const open = close * hourVariation;
    
    const high = Math.max(open, close) * (1 + Math.random() * 0.01); // 최대 1% 상승
    const low = Math.min(open, close) * (1 - Math.random() * 0.01); // 최대 1% 하락
    
    data.push({
      time: timeStr,
      open,
      high,
      low,
      close
    });
  }
  
  return data;
};

const BuyFanToken: React.FC<BuyFanTokenProps> = ({ isOpen, onClose, team, price, tokenId }) => {
  const [amount, setAmount] = useState<string>('');
  const [showSuccess, setShowSuccess] = useState(false);
  const [mode, setMode] = useState<Mode>('buy');
  const [selectedToken, setSelectedToken] = useState<string | null>(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [currentTokenInfo, setCurrentTokenInfo] = useState<TokenInfo | null>(null);
  const [selectedTokenInfo, setSelectedTokenInfo] = useState<TokenInfo | null>(null);
  const [bettyInfo, setBettyInfo] = useState<TokenInfo | null>(null);
  const [walletTokens, setWalletTokens] = useState<WalletToken[]>([]);
  const { userTokens, setBettyBalance, bettyPrice } = useStore();
  const [dailyData, setDailyData] = useState<CandlestickData[]>([]);
  const [hourlyData, setHourlyData] = useState<CandlestickData[]>([]);
  const [chartLoading, setChartLoading] = useState<boolean>(true);
  const [lastChartUpdateTime, setLastChartUpdateTime] = useState<Date>(new Date());
  const [timeUntilChartRefresh, setTimeUntilChartRefresh] = useState<number>(300);
  const [chartPeriod, setChartPeriod] = useState<'1D' | '1H'>('1D');

  // 차트 데이터 로드 - 선택된 기간에 따라 적절한 API만 호출
  useEffect(() => {
    const loadChartData = async () => {
      if (!tokenId) return;
      
      setChartLoading(true);
      try {
        if (chartPeriod === '1D') {
          // 일별 데이터만 가져오기
          const dailyPrices = await getLast7DaysPrices(tokenId);
          console.log('가져온 일별 데이터:', dailyPrices);
          
          // 일별 데이터 변환
          const processedDailyData: CandlestickData[] = dailyPrices.map((item, index, arr) => {
            const time = new Date(item.updatedAt).toISOString().split('T')[0];
            const close = item.price;
            
            // 첫번째 항목이거나 이전 데이터가 없는 경우 임의 계산, 그렇지 않으면 이전 데이터 사용
            const open = index === 0 ? close * 0.95 : arr[index - 1].price;
            
            // 변동폭 계산 (일반적으로 5% 내외로 설정)
            const high = Math.max(open, close) * 1.03;
            const low = Math.min(open, close) * 0.97;
            
            return { 
              time, 
              open, 
              high, 
              low, 
              close 
            };
          });
          
          // 데이터가 있는 경우에만 설정
          if (processedDailyData.length > 0) {
            setDailyData(processedDailyData);
          } else {
            // 데이터가 없는 경우 더미 데이터 생성
            console.log('일별 데이터가 없어 더미 데이터를 사용합니다.');
            setDailyData(generateDummyDailyData(price));
          }
          
        } else {
          // 시간별 데이터만 가져오기
          const hourlyPrices = await getLast24HoursPrices(tokenId);
          console.log('가져온 시간별 데이터:', hourlyPrices);
          
          // 시간별 데이터 변환
          const processedHourlyData: CandlestickData[] = hourlyPrices.map((item, index, arr) => {
            const date = new Date(item.updatedAt);
            // ISO 형식으로 시간 포맷팅 (시간 단위로 맞춤)
            const time = date.toISOString().split(':')[0] + ':00:00Z';
            const close = item.price;
            
            // 첫번째 항목이거나 이전 데이터가 없는 경우 임의 계산, 그렇지 않으면 이전 데이터 사용
            const open = index === 0 ? close * 0.98 : arr[index - 1].price;
            
            // 변동폭 계산 (일반적으로 2% 내외로 설정)
            const high = Math.max(open, close) * 1.01;
            const low = Math.min(open, close) * 0.99;
            
            return { 
              time, 
              open, 
              high, 
              low, 
              close 
            };
          });
          
          // 데이터가 있는 경우에만 설정
          if (processedHourlyData.length > 0) {
            setHourlyData(processedHourlyData);
          } else {
            // 데이터가 없는 경우 더미 데이터 생성
            console.log('시간별 데이터가 없어 더미 데이터를 사용합니다.');
            setHourlyData(generateDummyHourlyData(price));
          }
        }
        
        setLastChartUpdateTime(new Date());
        setTimeUntilChartRefresh(300);
      } catch (error) {
        console.error(`${chartPeriod === '1D' ? '일별' : '시간별'} 차트 데이터 로드 실패:`, error);
        
        // API 호출 실패 시 더미 데이터 생성
        if (chartPeriod === '1D') {
          console.log('일별 API 호출 실패로 더미 데이터를 사용합니다.');
          setDailyData(generateDummyDailyData(price));
        } else {
          console.log('시간별 API 호출 실패로 더미 데이터를 사용합니다.');
          setHourlyData(generateDummyHourlyData(price));
        }
      } finally {
        setChartLoading(false);
      }
    };
    
    if (isOpen && tokenId) {
      loadChartData();
    }
    
    // 5분마다 차트 데이터 자동 새로고침
    const refreshInterval = setInterval(() => {
      if (isOpen && tokenId) {
        loadChartData();
        setLastChartUpdateTime(new Date());
        setTimeUntilChartRefresh(300);
      }
    }, 300000); // 5분 = 300,000ms
    
    return () => clearInterval(refreshInterval);
  }, [isOpen, tokenId, price, chartPeriod]); // chartPeriod를 의존성 배열에 추가
  
  // 탭 변경 처리 함수
  const handlePeriodChange = (period: '1D' | '1H') => {
    setChartPeriod(period);
  };

  // 남은 시간 타이머 설정
  useEffect(() => {
    // 타이머 1초마다 업데이트
    const timerInterval = setInterval(() => {
      setTimeUntilChartRefresh(prev => {
        if (prev <= 1) return 300;
        return prev - 1;
      });
    }, 1000);
    
    return () => clearInterval(timerInterval);
  }, []);
  
  // 마지막 업데이트 시간 포맷팅
  const formatLastUpdateTime = (date: Date): string => {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
  };
  
  // 남은 시간 포맷팅 (분:초)
  const formatRemainingTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };
  
  // 수동 차트 새로고침
  const handleRefreshChart = async () => {
    if (!tokenId) return;
    
    setChartLoading(true);
    
    // 기존 데이터 백업
    const previousDailyData = [...dailyData];
    const previousHourlyData = [...hourlyData];
    
    try {
      // 선택된 기간에 따라 적절한 API만 호출
      if (chartPeriod === '1D') {
        // 일별 데이터 가져오기
        const dailyPrices = await getLast7DaysPrices(tokenId);
        console.log('갱신된 일별 데이터:', dailyPrices);
        
        // 일별 데이터 변환
        const processedDailyData: CandlestickData[] = dailyPrices.map((item, index, arr) => {
          const time = new Date(item.updatedAt).toISOString().split('T')[0];
          const close = item.price;
          
          // 첫번째 항목이거나 이전 데이터가 없는 경우 임의 계산, 그렇지 않으면 이전 데이터 사용
          const open = index === 0 ? close * 0.95 : arr[index - 1].price;
          
          // 변동폭 계산
          const high = Math.max(open, close) * 1.03;
          const low = Math.min(open, close) * 0.97;
          
          return { 
            time, 
            open, 
            high, 
            low, 
            close 
          };
        });
        
        // API 데이터가 있는 경우만 업데이트
        if (processedDailyData.length > 0) {
          setDailyData(processedDailyData);
        } else if (previousDailyData.length === 0) {
          // 이전 데이터도 없는 경우만 더미 데이터 생성
          console.log('일별 데이터가 없어 더미 데이터를 사용합니다.');
          setDailyData(generateDummyDailyData(price));
        } else {
          // 이전 더미 데이터가 있으면 그대로 유지
          console.log('API에서 데이터를 받아오지 못했지만 기존 데이터를 유지합니다.');
        }
        
      } else {
        // 시간별 데이터 가져오기
        const hourlyPrices = await getLast24HoursPrices(tokenId);
        console.log('갱신된 시간별 데이터:', hourlyPrices);
        
        // 시간별 데이터 변환
        const processedHourlyData: CandlestickData[] = hourlyPrices.map((item, index, arr) => {
          const date = new Date(item.updatedAt);
          const time = date.toISOString().split(':')[0] + ':00:00Z';
          const close = item.price;
          
          // 첫번째 항목이거나 이전 데이터가 없는 경우 임의 계산, 그렇지 않으면 이전 데이터 사용
          const open = index === 0 ? close * 0.98 : arr[index - 1].price;
          
          // 변동폭 계산
          const high = Math.max(open, close) * 1.01;
          const low = Math.min(open, close) * 0.99;
          
          return { 
            time, 
            open, 
            high, 
            low, 
            close 
          };
        });
        
        // API 데이터가 있는 경우만 업데이트
        if (processedHourlyData.length > 0) {
          setHourlyData(processedHourlyData);
        } else if (previousHourlyData.length === 0) {
          // 이전 데이터도 없는 경우만 더미 데이터 생성
          console.log('시간별 데이터가 없어 더미 데이터를 사용합니다.');
          setHourlyData(generateDummyHourlyData(price));
        } else {
          // 이전 더미 데이터가 있으면 그대로 유지
          console.log('API에서 데이터를 받아오지 못했지만 기존 데이터를 유지합니다.');
        }
      }
      
      setLastChartUpdateTime(new Date());
      setTimeUntilChartRefresh(300);
    } catch (error) {
      console.error(`${chartPeriod === '1D' ? '일별' : '시간별'} 차트 데이터 갱신 실패:`, error);
      
      // API 호출 실패 시 기존 데이터 유지
      if (chartPeriod === '1D') {
        if (previousDailyData.length === 0) {
          // 기존 데이터가 없는 경우에만 더미 데이터 생성
          console.log('일별 API 호출 실패로 더미 데이터를 사용합니다.');
          setDailyData(generateDummyDailyData(price));
        } else {
          console.log('일별 API 호출 실패, 기존 데이터를 유지합니다.');
        }
      } else {
        if (previousHourlyData.length === 0) {
          // 기존 데이터가 없는 경우에만 더미 데이터 생성
          console.log('시간별 API 호출 실패로 더미 데이터를 사용합니다.');
          setHourlyData(generateDummyHourlyData(price));
        } else {
          console.log('시간별 API 호출 실패, 기존 데이터를 유지합니다.');
        }
      }
    } finally {
      setChartLoading(false);
      // 새로고침 UI를 위해 업데이트 시간은 갱신
      setLastChartUpdateTime(new Date()); 
      setTimeUntilChartRefresh(300);
    }
  };

  // BET 토큰 정보 로드 (getBettyPrice API 사용)
  useEffect(() => {
    const loadBettyInfo = async () => {
      try {
        console.log('BET 토큰 정보 로드 시작');
        
        // 1. 지갑 잔고에서 BET 토큰 정보 가져오기
        const walletInfo = await getWalletBalance();
        const betToken = walletInfo.tokens.find(t => t.tokenName === 'BET');
        
        // 2. 베티코인 가격 조회 API 호출
        const betPrice = await getBettyPrice();
        console.log('받아온 BET 토큰 가격:', betPrice);
        
        // 3. 베티코인 정보 설정
        if (betToken) {
          const bettyTokenInfo: TokenInfo = {
            balance: betToken.balance,
            tokenAddress: '', // 주소 정보는 필요한 경우 다른 API로 가져와야 함
            tokenName: 'BET'
          };
          
          setBettyInfo(bettyTokenInfo);
          setBettyBalance(betToken.balance);
          console.log('BET 토큰 잔액 업데이트:', betToken.balance);
        }
      } catch (error) {
        console.error('BET 토큰 정보 로드 실패:', error);
      }
    };
    
    if (isOpen) {
      loadBettyInfo();
    }
  }, [isOpen, setBettyBalance]);

  // 지갑 토큰 목록 로드
  useEffect(() => {
    const loadWalletTokens = async () => {
      try {
        console.log('지갑 잔고 조회 시작');
        const walletInfo = await getWalletBalance();
        console.log('받아온 지갑 정보:', walletInfo);
        setWalletTokens(walletInfo.tokens);
      } catch (error) {
        console.error('지갑 잔고 조회 실패:', error);
      }
    };
    
    if (isOpen) {
      loadWalletTokens();
    }
  }, [isOpen]);

  // 현재 토큰 정보 로드
  useEffect(() => {
    const loadTokenInfo = async () => {
      try {
        const info = await getTokenInfo(tokenId);
        setCurrentTokenInfo(info);
      } catch (error) {
        console.error('토큰 정보 로드 실패:', error);
      }
    };
    
    if (isOpen && tokenId) {
      loadTokenInfo();
    }
  }, [isOpen, tokenId]);

  // 선택된 토큰 정보 로드 (스왑/판매 모드)
  useEffect(() => {
    const loadSelectedTokenInfo = async () => {
      if (!selectedToken) return;
      
      try {
        const selectedTokenId = teamToTokenIdMap[selectedToken];
        const info = await getTokenInfo(selectedTokenId);
        setSelectedTokenInfo(info);
      } catch (error) {
        console.error('선택된 토큰 정보 로드 실패:', error);
      }
    };

    if ((mode === 'swap' || mode === 'sell') && selectedToken) {
      loadSelectedTokenInfo();
    }
  }, [mode, selectedToken]);

  const handleNumberClick = (num: string) => {
    if (amount.length < 10) {
      setAmount(prev => prev + num);
    }
  };

  const handleDelete = () => {
    setAmount(prev => prev.slice(0, -1));
  };

  // 거래 후 BET 토큰 잔액 새로고침
  const refreshBettyBalance = async () => {
    try {
      const walletInfo = await getWalletBalance();
      const betToken = walletInfo.tokens.find(t => t.tokenName === 'BET');
      
      if (betToken) {
        const bettyTokenInfo: TokenInfo = {
          balance: betToken.balance,
          tokenAddress: '',
          tokenName: 'BET'
        };
        
        setBettyInfo(bettyTokenInfo);
        setBettyBalance(betToken.balance);
        console.log('BET 토큰 잔액 업데이트됨:', betToken.balance);
      }
    } catch (error) {
      console.error('BET 토큰 잔액 새로고침 실패:', error);
    }
  };

  const handleAction = async () => {
    if (!amount || isNaN(Number(amount))) return;
    
    const numAmount = Number(amount);
    
    try {
      // 지갑 정보 가져오기
      const walletStore = useWalletStore.getState();
      const privateKey = await walletStore.exportPrivateKey();
      
      if (!privateKey) {
        throw new Error("개인키를 가져올 수 없습니다. 지갑에 연결되어 있는지 확인해주세요.");
      }
      
      // 환경 변수 확인
      const contractAddress = import.meta.env.VITE_EXCHANGE_CONTRACT_ADDRESS;
      if (!contractAddress) {
        console.error("컨트랙트 주소 환경 변수가 설정되지 않았습니다.");
        throw new Error("컨트랙트 주소가 설정되지 않았습니다. 관리자에게 문의해주세요.");
      }
      
      console.log("개인키 확인:", privateKey ? "있음" : "없음");
      console.log("컨트랙트 주소 확인:", contractAddress);
      
      // 컨트랙트 인스턴스 생성 (명시적으로 주소 전달)
      const contract = getExchangeContract(privateKey, contractAddress);
      const RPC_URL = import.meta.env.VITE_RPC_URL;
      
      let success = false;
      
      switch (mode) {
        case 'buy': {
          if (!currentTokenInfo) return;
          
          // 1. BET 토큰 컨트랙트 주소 가져오기
          console.log("BET 토큰 컨트랙트 주소 가져오기 시작");
          const betTokenAddress = await contract.betToken();
          console.log("BET 토큰 컨트랙트 주소:", betTokenAddress);
          
          // 2. 지갑 주소 가져오기
          const walletAddress = await walletStore.getAccounts();
          console.log("지갑 주소:", walletAddress);
          
          if (!walletAddress || !ethers.isAddress(walletAddress)) {
            throw new Error("유효한 지갑 주소를 가져올 수 없습니다");
          }
          
          // 3. BET 토큰 컨트랙트 인스턴스 생성
          const erc20ABI = [
            "function allowance(address owner, address spender) view returns (uint256)",
            "function approve(address spender, uint256 amount) returns (bool)"
          ];
          
          const provider = new ethers.JsonRpcProvider(RPC_URL);
          const wallet = new ethers.Wallet(privateKey, provider);
          const betTokenContract = new ethers.Contract(betTokenAddress, erc20ABI, wallet);
          
          // 4. 토큰 총 금액 계산 (BET 토큰 양 = 팬토큰 수량 * 가격)
          const betAmount = (Number(amount) * price).toString();
          console.log(`구매할 팬토큰 수량: ${amount}, 필요한 BET 토큰 양: ${betAmount}`);
          
          // 5. BET 토큰 금액을 Wei 단위로 변환 - 소수점 오류 방지를 위해 문자열 처리
          // 소수점 문제 방지를 위해 toFixed 사용
          const fixedBetAmount = Number(betAmount).toFixed(18);
          console.log(`Wei 변환을 위한 정확한 금액: ${fixedBetAmount}`);
          const amountWei = ethers.parseEther(fixedBetAmount);
          
          // 5-1. 승인량은 10% 더 여유있게 (올림)
          const approveAmount = (Math.ceil(Number(betAmount) * 1.1)).toString();
          const approveWei = ethers.parseEther(approveAmount);
          console.log(`승인 요청할 금액: ${approveAmount} BET (올림 적용)`);
          
          // 6. 승인 상태 확인
          const allowance = await betTokenContract.allowance(walletAddress, contractAddress);
          console.log(`현재 승인량: ${ethers.formatEther(allowance)}, 필요한 승인량: ${betAmount}`);
          
          // 7. 승인량이 부족하면 승인 요청
          if (allowance < amountWei) {
            console.log('BET 토큰 승인이 필요합니다...');
            const approveTx = await betTokenContract.approve(contractAddress, approveWei, { 
              gasPrice: ethers.parseUnits('5', 'gwei') 
            });
            
            console.log('승인 트랜잭션 제출됨, 확인 대기 중...', approveTx.hash);
            const approveReceipt = await approveTx.wait();
            console.log('BET 토큰 승인 완료!', approveReceipt.hash);
            
            // 승인 후 잔액 재확인
            const newAllowance = await betTokenContract.allowance(walletAddress, contractAddress);
            console.log(`승인 후 새로운 승인량: ${ethers.formatEther(newAllowance)}`);
            
            if (newAllowance < amountWei) {
              throw new Error('승인이 제대로 처리되지 않았습니다. 다시 시도해 주세요.');
            }
          }
          
          // 8. 팬토큰 구매 실행
          console.log(`팬토큰 구매 실행: ${currentTokenInfo.tokenName}, ${fixedBetAmount} BET`);
          try {
            await buyFanToken(
              contract,
              currentTokenInfo.tokenName,
              fixedBetAmount
            );
            success = true;
          } catch (buyError: any) {
            console.error("구매 트랜잭션 실패:", buyError);
            if (buyError.message?.includes("missing revert data")) {
              throw new Error("컨트랙트 실행 중 오류가 발생했습니다. 잔액을 확인하거나 네트워크 상태를 확인해주세요.");
            } else {
              throw buyError;
            }
          }
          break;
        }

        case 'sell': {
          if (!selectedTokenInfo) return;
          // 컨트랙트의 sellFanToken 함수를 직접 호출
          await sellFanToken( 
            contract,
            selectedTokenInfo.tokenName,
            amount
          );
          success = true;
          break;
        }

        case 'swap': {
          if (!selectedTokenInfo || !currentTokenInfo) return;
          // 컨트랙트의 swapFanToken 함수를 직접 호출
          await swapFanToken(
            contract,
            selectedTokenInfo.tokenName,
            currentTokenInfo.tokenName,
            amount
          );
          success = true;
          break;
        }
      }

      if (success) {
        // 거래 성공 후 베티코인 잔액 업데이트
        await refreshBettyBalance();
        
        setShowSuccess(true);
        setTimeout(() => {
          setShowSuccess(false);
          setAmount('');
          onClose();
        }, 1500);
      }

    } catch (err) {
      console.error('거래 실패:', err);
      alert(`거래 처리 중 오류가 발생했습니다: ${err instanceof Error ? err.message : '알 수 없는 오류'}`);
    }
  };

  // 총 가격 계산
  const totalPrice = amount ? Number(amount) * price : 0;

  // 모드 변경 시 초기화 및 초기 선택 토큰 설정
  const handleModeChange = (newMode: Mode) => {
    setMode(newMode);
    setAmount('');
    setIsDropdownOpen(false);
    
    if (newMode === 'swap' || newMode === 'sell') {
      // 현재 팀이 아닌 다른 보유 토큰 중 첫 번째를 선택
      const availableToken = userTokens.find(token => token.team !== team);
      setSelectedToken(availableToken?.team || null);
    } else {
      setSelectedToken(null);
    }
  };

  // 컴포넌트 마운트 시 초기 선택 토큰 설정
  useEffect(() => {
    if (isOpen && (mode === 'swap' || mode === 'sell')) {
      const availableToken = userTokens.find(token => token.team !== team);
      setSelectedToken(availableToken?.team || null);
    }
  }, [isOpen, mode, team, userTokens]);

  if (!isOpen) return null;

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 bg-white z-50"
      style={{
        width: '360px',
        height: '743px',
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        overflow: 'hidden'
      }}
    >
      {/* 헤더 */}
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <div className="w-[12px] h-[12px]" />
        <h1 className="text-lg font-['Giants-Bold'] text-gray-800">팬토큰 거래</h1>
        <button 
          onClick={onClose}
          className="w-5 h-5 flex items-center justify-center text-gray-400 hover:text-gray-600"
        >
          <svg
            className="w-full h-full"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>

      {/* 메인 컨텐츠 */}
      <div className="h-[calc(100%-56px)] flex flex-col pt-12">
        <div className="flex-1 p-4">
          {/* 모드 선택 */}
          <div className="flex justify-between mb-4">
            {(['buy', 'swap', 'sell'] as Mode[]).map((tabMode) => (
              <button
                key={tabMode}
                onClick={() => handleModeChange(tabMode)}
                className="relative py-2 text-sm font-['Giants-Bold'] transition-colors flex-1"
              >
                <span className={mode === tabMode ? 'text-blue-500' : 'text-gray-400'}>
                  {tabMode === 'buy' ? '구매하기' : tabMode === 'swap' ? '스왑하기' : '판매하기'}
                </span>
                {mode === tabMode && (
                  <motion.div
                    layoutId="underline"
                    className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-500"
                  />
                )}
              </button>
            ))}
          </div>

          {/* 차트 영역 */}
          <div className="bg-gray-100 rounded-lg p-3 mb-4 overflow-hidden">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <div 
                  className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-['Giants-Bold']"
                  style={{ 
                    backgroundColor: (teamColors as TeamColors)[mode === 'sell' ? (selectedToken || team) : team].bg,
                    color: (teamColors as TeamColors)[mode === 'sell' ? (selectedToken || team) : team].text 
                  }}
                >
                  {formatTeamCode(mode === 'sell' ? (selectedToken || team) : team)}
                </div>
                <span className="text-sm text-gray-800 font-['Giants-Bold']">
                  {currentTokenInfo?.tokenName || formatTeamName(team)} 가격 차트
                </span>
              </div>
              <div className="flex items-center gap-1">
                <span className="text-sm text-gray-800 font-['Giants-Bold']">
                  {price}
                </span>
                <img src={bettyImg} alt="BETTY" className="w-4 h-4" />
              </div>
            </div>
            
            {/* 차트 상태 표시줄 */}
            <div className="mb-2 flex justify-between items-center text-xs px-2 py-1 bg-gray-200 rounded">
              <span className="text-gray-600">
                {chartLoading ? '로딩 중...' : `마지막 업데이트: ${formatLastUpdateTime(lastChartUpdateTime)}`}
              </span>
              <div className="flex items-center">
                {!chartLoading && (
                  <span className="text-gray-600 mr-2">
                    {formatRemainingTime(timeUntilChartRefresh)} 후 갱신
                  </span>
                )}
                <button 
                  onClick={handleRefreshChart}
                  disabled={chartLoading}
                  className={`p-1 rounded ${chartLoading ? 'text-gray-400 bg-gray-300' : 'text-gray-600 hover:bg-gray-300'}`}
                >
                  <svg className={`w-3 h-3 ${chartLoading ? 'animate-spin' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                </button>
              </div>
            </div>
            
            <div className="h-56 bg-white rounded-lg relative overflow-hidden">
              {chartLoading ? (
                <div className="w-full h-full flex flex-col items-center justify-center">
                  <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mb-2"></div>
                  <p className="text-sm text-gray-500">차트 데이터 로딩 중...</p>
                </div>
              ) : (
                <CandleChart 
                  dailyData={dailyData} 
                  hourlyData={hourlyData}
                  initialPeriod={chartPeriod}
                  onPeriodChange={handlePeriodChange}
                  key={`${tokenId}-${lastChartUpdateTime.getTime()}`}
                />
              )}
            </div>
          </div>

          {/* 토큰 선택 (스왑/판매 모드) */}
          {(mode === 'swap' || mode === 'sell') && (
            <div className="relative mb-4">
              <button
                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                className="w-full bg-white rounded-lg p-3 flex items-center justify-between border border-gray-200"
              >
                <div className="flex items-center gap-2">
                  <span className="text-base text-gray-800 font-['Giants-Bold']">
                    {selectedTokenInfo?.tokenName || '토큰 선택'}
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="flex items-center gap-1">
                    <span className="text-base text-gray-800 font-['Giants-Bold']">
                      보유: {selectedTokenInfo?.balance || 0}
                    </span>
                  </div>
                  <svg
                    className={`w-5 h-5 text-gray-500 transition-transform ${isDropdownOpen ? 'rotate-180' : ''}`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </div>
              </button>
              {isDropdownOpen && (
                <div className="absolute top-full left-0 right-0 mt-1 bg-white rounded-lg shadow-lg z-10 max-h-60 overflow-y-auto border border-gray-200">
                  {walletTokens
                    .filter(token => mode === 'sell' ? true : token.tokenName !== currentTokenInfo?.tokenName && token.balance > 0)
                    .map(token => (
                      <button
                        key={token.tokenName}
                        onClick={() => {
                          setSelectedToken(token.tokenName);
                          setIsDropdownOpen(false);
                        }}
                        className={`w-full p-3 flex items-center justify-between hover:bg-gray-50 ${
                          selectedTokenInfo?.tokenName === token.tokenName ? 'bg-blue-50' : ''
                        }`}
                      >
                        <div className="flex items-center gap-2">
                          <span className={`text-sm font-['Giants-Bold'] ${
                            selectedTokenInfo?.tokenName === token.tokenName ? 'text-blue-500' : 'text-gray-800'
                          }`}>
                            {token.tokenName}
                          </span>
                        </div>
                        <div className="flex items-center gap-2">
                          <span className="text-sm text-gray-500">{token.balance}</span>
                        </div>
                      </button>
                    ))}
                </div>
              )}
            </div>
          )}

          {/* 금액 입력 */}
          <div className="bg-gray-100 rounded-lg p-4">
            {/* 보유량 표시 */}
            <div className="flex items-center justify-between mb-4 pb-3 border-b border-gray-200">
              <div className="flex flex-col">
                <span className="text-xs text-gray-500 mb-1">내 보유량</span>
                <span className="text-base font-['Giants-Bold'] text-gray-800">
                  {mode === 'sell' 
                    ? `${selectedTokenInfo?.balance || 0} ${selectedTokenInfo?.tokenName || ''}`
                    : `${bettyInfo?.balance || 0} ${bettyInfo?.tokenName || 'BETTY'}`}
                </span>
              </div>
              <div className="flex flex-col items-end">
                <span className="text-xs text-gray-500 mb-1">현재 토큰 가격</span>
                <div className="flex items-center gap-1">
                  <span className="text-base font-['Giants-Bold'] text-gray-800">
                    {price}
                  </span>
                  <img src={bettyImg} alt="BETTY" className="w-4 h-4" />
                </div>
              </div>
            </div>

            {/* 수량 입력 */}
            <div className="mb-4">
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm text-gray-500">
                  {mode === 'buy' 
                    ? `${currentTokenInfo?.tokenName || ''} 구매 수량`
                    : mode === 'swap' 
                      ? '스왑 수량' 
                      : '판매 수량'}
                </span>
              </div>
              <div className="flex items-center justify-between bg-white rounded-lg p-3">
                <input
                  type="number"
                  value={amount}
                  onChange={(e) => {
                    const value = e.target.value;
                    if (value.length <= 10) {
                      setAmount(value);
                    }
                  }}
                  placeholder="0"
                  className="text-xl font-['Giants-Bold'] text-gray-800 w-full text-right outline-none bg-transparent"
                />
                <span className="text-base font-['Giants-Bold'] text-gray-600 ml-2">
                  {mode === 'buy' 
                    ? currentTokenInfo?.tokenName || ''
                    : mode === 'sell' 
                      ? selectedTokenInfo?.tokenName || ''
                      : 'BETTY'}
                </span>
              </div>
            </div>

            {/* 총 거래액 */}
            {amount && (
              <div className="bg-blue-50 rounded-lg p-3">
                <div className="flex items-center justify-between">
                  <span className="text-sm text-blue-600">
                    총 {mode === 'sell' ? '판매' : '구매'}금액
                  </span>
                  <div className="flex items-center gap-1">
                    <span className="text-lg font-['Giants-Bold'] text-blue-600">
                      {Number(amount) * price}
                    </span>
                    <img src={bettyImg} alt="BETTY" className="w-4 h-4" />
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* 구매/스왑/판매 버튼 */}
          <button
            onClick={handleAction}
            disabled={!amount || (mode === 'sell' 
              ? Number(amount) > (selectedTokenInfo?.balance || 0)
              : Number(amount) * price > (bettyInfo?.balance || 0))}
            className={`w-full h-12 rounded-lg text-white text-base font-['Giants-Bold'] transition-colors mt-4 ${
              !amount || (mode === 'sell'
                ? Number(amount) > (selectedTokenInfo?.balance || 0)
                : Number(amount) * price > (bettyInfo?.balance || 0))
                ? 'bg-gray-300 cursor-not-allowed'
                : 'bg-blue-500 hover:bg-blue-600'
            }`}
          >
            {mode === 'buy' ? '구매하기' : mode === 'swap' ? '스왑하기' : '판매하기'}
          </button>
        </div>
      </div>

      <AnimatePresence>
        {showSuccess && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-white flex items-center justify-center pointer-events-none z-[60]"
          >
            <div className="flex flex-col items-center">
              <svg
                className="w-16 h-16 sm:w-24 sm:h-24 text-green-500"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <motion.path
                  initial={{ pathLength: 0 }}
                  animate={{ pathLength: 1 }}
                  transition={{ duration: 0.5 }}
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M7 13l4 4L21 7"
                />
              </svg>
              <motion.p
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.5 }}
                className="text-sm sm:text-base font-['Giants-Bold'] text-gray-800 mt-2 sm:mt-3"
              >
                {mode === 'buy' ? '구매되었습니다' : mode === 'swap' ? '스왑되었습니다' : '판매되었습니다'}
              </motion.p>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
};

export default BuyFanToken;
