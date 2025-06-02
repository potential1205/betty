import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useStore } from '../stores/useStore';
import { useUserStore } from '../stores/authStore';
import { 
  formatTeamCode, 
  formatTeamName, 
  teamColors, 
  teamTokenPrices, 
  teamColorsByTokenId, 
  tokenIdToTeamName,
  teamColorsByTokenName,
  tokenNameToTeamName
} from '../constants/dummy';
import { getWalletInfo } from '../apis/axios';
import backImg from '../assets/back_black.png';
import hamburgerImg from '../assets/hamburger_black.png';
import Sidebar from '../components/Sidebar';
import ChargeModal from '../components/charge';
import RegisterWalletModal from '../components/RegisterWalletModal';
import WithdrawModal from '../components/WithdrawModal';
import { Transaction } from '../stores/useStore';
import axios from 'axios';
import { getTransactions, Transaction as ApiTransaction } from '../apis/exchangeApi';

type TeamColor = {
  bg: string;
  text: string;
};

type TeamColors = {
  [key: string]: TeamColor;
};

interface WalletInfo {
  address?: string;
  nickname?: string;
  totalBET?: number;
  transactions: Transaction[];
  tokens?: Array<{
    team: string;
    amount: number;
    betValue?: number;
  }>;
}

// 토큰 ID로부터 team 이름을 가져오는 헬퍼 함수
const getTeamNameFromTokenId = (tokenId: string) => {
  return tokenIdToTeamName[tokenId as keyof typeof tokenIdToTeamName] || tokenId;
};

// 토큰 ID로부터 팀 색상을 가져오는 헬퍼 함수
const getTeamColorFromTokenId = (tokenId: string) => {
  return teamColorsByTokenId[tokenId as keyof typeof teamColorsByTokenId] || { bg: '#eee', text: '#333' };
};

// 토큰 이름(약어)로부터 팀 이름을 가져오는 헬퍼 함수
const getTeamNameFromTokenName = (tokenName: string) => {
  return tokenNameToTeamName[tokenName as keyof typeof tokenNameToTeamName] || tokenName;
};

// 토큰 이름(약어)로부터 팀 색상을 가져오는 헬퍼 함수
const getTeamColorFromTokenName = (tokenName: string) => {
  return teamColorsByTokenName[tokenName as keyof typeof teamColorsByTokenName] || { bg: '#eee', text: '#333' };
};

const MyPage: React.FC = () => {
  const navigate = useNavigate();
  const { toggleSidebar, walletInfo, activeTab, setActiveTab, isChargeModalOpen, setIsChargeModalOpen, nickname, bettyPrice, updateWalletInfo } = useStore();
  const [showAllCharge, setShowAllCharge] = useState(false);
  const [showAllBuy, setShowAllBuy] = useState(false);
  const [showAllSell, setShowAllSell] = useState(false);
  const [showAllSwap, setShowAllSwap] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);
  const [isWithdrawModalOpen, setIsWithdrawModalOpen] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [apiTransactions, setApiTransactions] = useState<ApiTransaction[]>([]);
  const [transactionsLoading, setTransactionsLoading] = useState<boolean>(false);
  const [assetsLoading, setAssetsLoading] = useState<boolean>(false);
  const [lastRefreshTime, setLastRefreshTime] = useState<Date>(new Date());

  // 거래 유형별 스타일 정의
  const transactionStyles = {
    CHARGE: {
      bg: 'bg-blue-100',
      text: 'text-blue-600',
      label: '충전'
    },
    BUY: {
      bg: 'bg-green-100',
      text: 'text-green-600',
      label: '매수'
    },
    SELL: {
      bg: 'bg-red-100',
      text: 'text-red-600',
      label: '매도'
    },
    SWAP: {
      bg: 'bg-purple-100',
      text: 'text-purple-600',
      label: '스왑'
    }
  };

  // 트랜잭션 유형 결정 함수
  const getTransactionType = (transaction: ApiTransaction): 'BUY' | 'SELL' | 'CHARGE' | 'SWAP' | 'UNKNOWN' => {
    // tokenFrom이 BET이고 tokenTo가 있으면 BUY
    if (transaction.tokenFrom?.tokenName === 'BET' && transaction.tokenTo) {
      return 'BUY';
    }
    // tokenFrom이 있고 tokenTo가 BET이면 SELL
    else if (transaction.tokenFrom && transaction.tokenTo?.tokenName === 'BET') {
      return 'SELL';
    }
    // tokenFrom이 있고 tokenTo가 있고, 둘 다 BET가 아니면 SWAP
    else if (transaction.tokenFrom && transaction.tokenTo && 
             transaction.tokenFrom.tokenName !== 'BET' && 
             transaction.tokenTo.tokenName !== 'BET') {
      return 'SWAP';
    }
    // tokenFrom이 없고 tokenTo가 BET이면 CHARGE (입금)
    else if (!transaction.tokenFrom && transaction.tokenTo?.tokenName === 'BET') {
      return 'CHARGE';
    }
    // 기타 경우
    return 'UNKNOWN';
  };

  // BET 금액 포맷팅 함수
  const formatBET = (bet: number | undefined) => {
    if (bet === undefined || bet === null) return '0.00';
    return bet.toFixed(2);
  };

  // 날짜 포맷팅 함수
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  };

  // 데이터 로드 함수들을 useCallback으로 메모이제이션
  const fetchWalletInfo = useCallback(async () => {
    try {
      setAssetsLoading(true);
      await useStore.getState().fetchWalletInfo();
      console.log('지갑 정보 업데이트 완료:', new Date().toISOString());
    } catch (error) {
      console.error('지갑 정보 조회 실패:', error);
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        setIsRegisterModalOpen(true);
      } else {
        setError('지갑 정보를 불러오는데 실패했습니다.');
      }
    } finally {
      setAssetsLoading(false);
    }
  }, []);

  const fetchTransactions = useCallback(async () => {
    try {
      setTransactionsLoading(true);
      const transactions = await getTransactions();
      setApiTransactions(transactions);
      console.log('트랜잭션 데이터 업데이트 완료:', new Date().toISOString(), transactions.length + '건');
      setLastRefreshTime(new Date());
    } catch (error) {
      console.error('트랜잭션 조회 실패:', error);
      setError('트랜잭션 정보를 불러오는데 실패했습니다.');
    } finally {
      setTransactionsLoading(false);
    }
  }, []);

  // 각 탭의 데이터를 새로고침하는 함수
  const refreshCurrentTabData = useCallback(() => {
    console.log('현재 탭 데이터 새로고침:', activeTab);
    switch (activeTab) {
      case 'ASSETS':
        fetchWalletInfo();
        break;
      case 'TRANSACTIONS':
        fetchTransactions();
        break;
      case 'DEPOSIT_WITHDRAW':
        fetchWalletInfo();
        break;
      default:
        break;
    }
    setLastRefreshTime(new Date());
  }, [activeTab, fetchWalletInfo, fetchTransactions]);

  // 컴포넌트 마운트 시 사이드바를 닫힌 상태로 설정
  useEffect(() => {
    toggleSidebar(false);
    
    // 초기 데이터 로드
    setIsLoading(true);
    Promise.all([fetchWalletInfo()])
      .finally(() => {
        setIsLoading(false);
      });
      
    // 30초마다 자동 새로고침
    const refreshInterval = setInterval(() => {
      refreshCurrentTabData();
    }, 30000);
    
    return () => clearInterval(refreshInterval);
  }, [fetchWalletInfo, refreshCurrentTabData]);

  // 탭 변경 시 해당 탭의 데이터 로드
  useEffect(() => {
    refreshCurrentTabData();
  }, [activeTab, refreshCurrentTabData]);

  const tabs = [
    { id: 'ASSETS', label: '보유자산' },
    { id: 'TRANSACTIONS', label: '거래내역' },
    { id: 'DEPOSIT_WITHDRAW', label: '입출금' }
  ];

  // API 트랜잭션 데이터로 거래내역 렌더링
  const renderApiTransactionList = (type: 'BUY' | 'SELL' | 'CHARGE' | 'SWAP', showAll: boolean, setShowAll: (show: boolean) => void) => {
    // 트랜잭션 목록 필터링
    const filteredTransactions = apiTransactions
      .filter(t => getTransactionType(t) === type && t.transactionStatus === 'SUCCESS')
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
    
    const displayedTransactions = showAll ? filteredTransactions : filteredTransactions.slice(0, 3);

    if (filteredTransactions.length === 0) {
      return (
        <div className="text-center py-5 text-gray-400">
          거래 내역이 없습니다.
        </div>
      );
    }

    return (
      <div className="space-y-4">
        <h3 className="text-lg font-['Giants-Bold'] mb-4">
          {type === 'BUY' ? '매수 내역' : 
           type === 'SELL' ? '매도 내역' : 
           type === 'SWAP' ? '스왑 내역' : '충전 내역'}
        </h3>
        {transactionsLoading ? (
          <div className="text-center py-5">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-500 mx-auto"></div>
            <p className="text-gray-500 mt-2">거래 내역을 불러오는 중...</p>
          </div>
        ) : (
          <>
            {displayedTransactions.map((transaction) => (
              <motion.div
                key={transaction.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="bg-white rounded-2xl p-6 shadow-md border border-gray-100"
              >
                <div className="flex justify-between items-start mb-3">
                  <div className="flex items-center">
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center mr-2 ${
                      transactionStyles[type].bg
                    }`}>
                      <span className={`text-sm font-['Giants-Bold'] ${
                        transactionStyles[type].text
                      }`}>
                        {transactionStyles[type].label}
                      </span>
                    </div>
                    <span className="text-sm text-gray-500">{formatDate(transaction.createdAt)}</span>
                  </div>
                </div>
                <div className="space-y-2">
                  {type === 'CHARGE' ? (
                    <p className="text-lg font-['Giants-Bold']">
                      {transaction.amountIn.toLocaleString()}원
                    </p>
                  ) : type === 'BUY' ? (
                    <>
                      <p className="text-lg font-['Giants-Bold']">
                        {formatBET(transaction.amountIn)} BET
                      </p>
                      {transaction.tokenTo && (
                        <div className="flex items-center text-sm text-gray-500">
                          <div className="w-6 h-6 rounded-full bg-gray-100 flex items-center justify-center mr-2">
                            <span className="text-xs font-['Giants-Bold'] text-gray-600">
                              {transaction.tokenTo.tokenName}
                            </span>
                          </div>
                          <span>{transaction.amountOut || 0}개</span>
                          {transaction.amountOut && transaction.amountIn && (
                            <>
                              <span className="mx-2">•</span>
                              <span>{formatBET(transaction.amountIn / transaction.amountOut)} BET/개</span>
                            </>
                          )}
                        </div>
                      )}
                    </>
                  ) : type === 'SELL' ? (
                    <>
                      <p className="text-lg font-['Giants-Bold']">
                        {formatBET(transaction.amountOut || 0)} BET 획득
                      </p>
                      {transaction.tokenFrom && (
                        <div className="flex items-center text-sm text-gray-500">
                          <div className="w-6 h-6 rounded-full bg-gray-100 flex items-center justify-center mr-2">
                            <span className="text-xs font-['Giants-Bold'] text-gray-600">
                              {transaction.tokenFrom.tokenName}
                            </span>
                          </div>
                          <span>{transaction.amountIn}개 판매</span>
                          {transaction.amountOut && transaction.amountIn && (
                            <>
                              <span className="mx-2">•</span>
                              <span>{formatBET(transaction.amountOut / transaction.amountIn)} BET/개</span>
                            </>
                          )}
                        </div>
                      )}
                    </>
                  ) : (
                    // SWAP 타입
                    <>
                      <p className="text-lg font-['Giants-Bold']">
                        토큰 스왑
                      </p>
                      <div className="flex items-center justify-between text-sm text-gray-500">
                        <div className="flex items-center">
                          <div className="w-6 h-6 rounded-full bg-gray-100 flex items-center justify-center mr-2">
                            <span className="text-xs font-['Giants-Bold'] text-gray-600">
                              {transaction.tokenFrom?.tokenName}
                            </span>
                          </div>
                          <span>{transaction.amountIn}개</span>
                        </div>
                        <span className="mx-2">→</span>
                        <div className="flex items-center">
                          <div className="w-6 h-6 rounded-full bg-gray-100 flex items-center justify-center mr-2">
                            <span className="text-xs font-['Giants-Bold'] text-gray-600">
                              {transaction.tokenTo?.tokenName}
                            </span>
                          </div>
                          <span>{transaction.amountOut || 0}개</span>
                        </div>
                      </div>
                    </>
                  )}
                </div>
              </motion.div>
            ))}
            {filteredTransactions.length > 3 && (
              <button
                onClick={() => setShowAll(!showAll)}
                className="w-full py-3 text-sm font-['Giants-Bold'] text-gray-500 hover:text-black transition-colors"
              >
                {showAll ? '접기' : '더보기'}
              </button>
            )}
          </>
        )}
      </div>
    );
  };

  const renderTransactionList = (transactions: Transaction[], type: 'BUY' | 'SELL' | 'CHARGE', showAll: boolean, setShowAll: (show: boolean) => void) => {
    const filteredTransactions = transactions
      .filter(t => t.type === type && t.date)
      .sort((a, b) => new Date(b.date!).getTime() - new Date(a.date!).getTime());
    
    const displayedTransactions = showAll ? filteredTransactions : filteredTransactions.slice(0, 3);

    if (filteredTransactions.length === 0) return null;

    return (
      <div className="space-y-4">
        <h3 className="text-lg font-['Giants-Bold'] mb-4">
          {type === 'BUY' ? '매수 내역' : type === 'SELL' ? '매도 내역' : '충전 내역'}
        </h3>
        {displayedTransactions.map((transaction) => (
          <motion.div
            key={transaction.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-white rounded-2xl p-6 shadow-md border border-gray-100"
          >
            <div className="flex justify-between items-start mb-3">
              <div className="flex items-center">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center mr-2 ${
                  type === 'BUY' ? 'bg-green-100' :
                  type === 'SELL' ? 'bg-red-100' :
                  'bg-blue-100'
                }`}>
                  <span className={`text-sm font-['Giants-Bold'] ${
                    type === 'BUY' ? 'text-green-600' :
                    type === 'SELL' ? 'text-red-600' :
                    'text-blue-600'
                  }`}>
                    {type === 'BUY' ? '매수' :
                    type === 'SELL' ? '매도' : '충전'}
                  </span>
                </div>
                <span className="text-sm text-gray-500">{transaction.date}</span>
              </div>
            </div>
            <div className="space-y-2">
              <p className="text-lg font-['Giants-Bold']">
                {type === 'CHARGE' 
                  ? `${transaction.amount.toLocaleString()}원`
                  : `${formatBET(transaction.amount)} BET`}
              </p>
              {transaction.tokenName && (
                <div className="flex items-center text-sm text-gray-500">
                  <div className="w-6 h-6 rounded-full bg-gray-100 flex items-center justify-center mr-2">
                    <span className="text-xs font-['Giants-Bold'] text-gray-600">
                      {formatTeamCode(transaction.tokenName)}
                    </span>
                  </div>
                  <span>{transaction.tokenAmount}개</span>
                  <span className="mx-2">•</span>
                  <span>{formatBET(transaction.tokenPrice || 0)} BET/개</span>
                </div>
              )}
            </div>
          </motion.div>
        ))}
        {filteredTransactions.length > 3 && (
          <button
            onClick={() => setShowAll(!showAll)}
            className="w-full py-3 text-sm font-['Giants-Bold'] text-gray-500 hover:text-black transition-colors"
          >
            {showAll ? '접기' : '더보기'}
          </button>
        )}
      </div>
    );
  };

  const renderContent = () => {
    switch (activeTab) {
      case 'ASSETS':
        return (
          <div className="space-y-6">
            {/* 총 보유자산 */}
            <div className="bg-gradient-to-br from-black to-gray-800 rounded-2xl p-6 shadow-lg">
              <p className="text-sm text-gray-400 mb-2">총 보유자산</p>
              <div className="flex items-baseline">
                <p className="text-3xl font-['Giants-Bold'] text-white">{formatBET(walletInfo?.totalBET)}</p>
                <p className="text-xl text-gray-400 ml-2">BET</p>
              </div>
            </div>

            {/* 보유 팬토큰 */}
            <div>
              <h3 className="text-base font-['Giants-Bold'] mb-4">보유 팬토큰</h3>
              <div className="space-y-3">
                {walletInfo.tokens?.map((token) => {
                  // token.team을 토큰 ID로 간주하고 토큰 약어로 변환
                  const tokenId = token.team;
                  const teamName = getTeamNameFromTokenId(tokenId);
                  const tokenCode = formatTeamCode(teamName); // 토큰 약어 추출 (DSB, KWH, SSL 등)
                  const teamColor = getTeamColorFromTokenName(tokenCode); // 토큰 약어로 색상 가져오기
                  
                  return (
                    <motion.div
                      key={tokenId}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      className="bg-white rounded-2xl p-6 shadow-md border border-gray-100"
                    >
                      <div className="flex justify-between items-center">
                        <div>
                          <div className="flex items-center mb-2">
                            <div 
                              className="w-8 h-8 rounded-full flex items-center justify-center mr-2"
                              style={{ backgroundColor: teamColor.bg }}
                            >
                              <span className="text-sm font-['Giants-Bold']" style={{ color: teamColor.text }}>
                                {tokenCode}
                              </span>
                            </div>
                            <p className="text-sm text-black">
                              {formatTeamName(teamName)}
                            </p>
                          </div>
                          <p className="text-base text-black">
                            {token.amount}개
                          </p>
                        </div>
                        <div className="text-right">
                          <p className="text-xs text-gray-500 mb-1">BET 가치</p>
                          <div className="flex items-baseline">
                            <p className="text-base font-['Giants-Bold'] text-black">
                              {formatBET(token.betValue)}
                            </p>
                            <p className="text-xs text-gray-500 ml-1">BET</p>
                          </div>
                        </div>
                      </div>
                    </motion.div>
                  );
                })}
              </div>
            </div>
          </div>
        );

      case 'TRANSACTIONS':
        return (
          <div className="space-y-6">
            {/* API로 가져온 트랜잭션 렌더링 */}
            {/* 충전 내역 */}
            {renderApiTransactionList('CHARGE', showAllCharge, setShowAllCharge)}

            {/* 매수 내역 */}
            {renderApiTransactionList('BUY', showAllBuy, setShowAllBuy)}

            {/* 매도 내역 */}
            {renderApiTransactionList('SELL', showAllSell, setShowAllSell)}

            {/* 스왑 내역 */}
            {renderApiTransactionList('SWAP', showAllSwap, setShowAllSwap)}
          </div>
        );

      case 'DEPOSIT_WITHDRAW':
        return (
          <div className="space-y-6">
            <div className="bg-gradient-to-br from-black to-gray-800 rounded-2xl p-6 shadow-lg">
              <p className="text-sm text-gray-400 mb-2">현재 보유 BET</p>
              <div className="flex items-baseline">
                <p className="text-3xl font-['Giants-Bold'] text-white">{formatBET(walletInfo.totalBET)}</p>
                <p className="text-xl text-gray-400 ml-2">BET</p>
              </div>
            </div>
            <div className="space-y-4">
              <div className="flex space-x-4">
                <button
                  onClick={() => setIsChargeModalOpen(true)}
                  className="flex-1 bg-gradient-to-r from-black to-gray-800 text-white py-4 rounded-2xl font-['Giants-Bold'] hover:from-gray-800 hover:to-black transition-all shadow-lg"
                >
                  입금하기
                </button>
                <button
                  onClick={() => setIsWithdrawModalOpen(true)}
                  className="flex-1 bg-white text-black py-4 rounded-2xl font-['Giants-Bold'] hover:bg-gray-100 transition-all shadow-lg border border-gray-200"
                >
                  출금하기
                </button>
              </div>
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className="relative h-full bg-gray-50 text-black">
      {/* 헤더 */}
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <button 
          onClick={() => navigate(-1)} 
          className="w-[12px] h-[12px]"
        >
          <img src={backImg} alt="Back" className="w-full h-full" />
        </button>
        <button 
          onClick={() => toggleSidebar(true)}
          className="w-5 h-5"
        >
          <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
        </button>
      </div>

      {/* 스크롤 가능한 컨텐츠 영역 */}
      <div 
        className="h-full pt-20 px-6 pb-24 overflow-y-auto"
        style={{
          msOverflowStyle: 'none',
          scrollbarWidth: 'none',
          WebkitOverflowScrolling: 'touch',
        }}
      >
        <style>
          {`
            div::-webkit-scrollbar {
              display: none;
            }
          `}
        </style>

        {isLoading ? (
          <div className="flex items-center justify-center h-full">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-black"></div>
          </div>
        ) : error ? (
          <div className="flex flex-col items-center justify-center h-full">
            <p className="text-red-500 mb-4">{error}</p>
            <button
              onClick={() => {
                setError(null);
                refreshCurrentTabData();
              }}
              className="px-4 py-2 bg-black text-white rounded-lg"
            >
              다시 시도
            </button>
          </div>
        ) : (
          <>
            {/* 지갑 주소 */}
            <div className="mb-8">
              <div className="flex items-center gap-2">
                <h2 className="text-xl font-['Giants-Bold']">
                  {walletInfo?.nickname ? `${walletInfo.nickname}님의 지갑` : '내 지갑'}
                </h2>
                <button
                  onClick={() => {
                    useUserStore.getState().logout();
                    navigate('/');
                  }}
                  className="text-xs text-gray-400 hover:text-red-500 transition-colors px-2 py-1 rounded-full border border-gray-200 hover:border-red-200"
                >
                  로그아웃
                </button>
              </div>
              <div className="flex justify-between items-center">
                <p className="text-[10px] text-gray-400 mt-1">지갑 주소: {useUserStore.getState().walletAddress || '지갑 주소 없음'}</p>
                <button 
                  onClick={refreshCurrentTabData}
                  className="text-xs text-gray-400 hover:text-black transition-colors flex items-center gap-1 px-2 py-1 rounded-full border border-gray-200"
                >
                  <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                  새로고침
                </button>
              </div>
              <p className="text-[10px] text-gray-400">마지막 업데이트: {lastRefreshTime.toLocaleTimeString()}</p>
            </div>

            {/* 탭 */}
            <div className="flex space-x-2 mb-6 bg-white p-1 rounded-2xl shadow-sm">
              {tabs.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as 'ASSETS' | 'TRANSACTIONS' | 'DEPOSIT_WITHDRAW')}
                  className={`flex-1 py-3 rounded-xl text-sm font-['Giants-Bold'] transition-all duration-200
                    ${activeTab === tab.id
                      ? 'bg-black text-white shadow-md'
                      : 'text-gray-500 hover:text-black hover:bg-gray-50'}`}
                >
                  {tab.label}
                  {(activeTab === tab.id && 
                    ((tab.id === 'ASSETS' && assetsLoading) || 
                     (tab.id === 'TRANSACTIONS' && transactionsLoading))) && (
                    <span className="ml-1 inline-block w-2 h-2 bg-white rounded-full animate-pulse"></span>
                  )}
                </button>
              ))}
            </div>

            {/* 탭 컨텐츠 */}
            {renderContent()}
          </>
        )}
      </div>

      <Sidebar />
      <ChargeModal
        isOpen={isChargeModalOpen}
        onClose={() => {
          setIsChargeModalOpen(false);
          // 모달 닫을 때 자산 정보 새로고침
          if (activeTab === 'ASSETS' || activeTab === 'DEPOSIT_WITHDRAW') {
            fetchWalletInfo();
          }
        }}
      />
      <WithdrawModal
        isOpen={isWithdrawModalOpen}
        onClose={() => {
          setIsWithdrawModalOpen(false);
          // 모달 닫을 때 자산 정보 새로고침
          if (activeTab === 'ASSETS' || activeTab === 'DEPOSIT_WITHDRAW') {
            fetchWalletInfo();
          }
        }}
      />
      <RegisterWalletModal
        isOpen={isRegisterModalOpen}
        onClose={() => {
          setIsRegisterModalOpen(false);
          fetchWalletInfo();
        }}
      />
    </div>
  );
};

export default MyPage;