import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useStore } from '../stores/useStore';
import { useWalletStore } from '../stores/walletStore';
import { useUserStore } from '../stores/authStore';
import axiosInstance from '../apis/axios';
import { getTeamTokenName } from '../apis/tokenApi';
import { placeBet, getWinningTeamVotingContract, getWinningTeamVotingContractReadOnly, getTeamBets } from '../utils/winningTeamVotingContract';
import { web3auth } from '../utils/web3auth';

interface WinnerPayProps {
  isOpen: boolean;
  onClose: () => void;
  type: 'winner' | 'mvp';
  team?: string;
  player?: string;
  onBetSuccess?: () => void;
}

const WinnerPay: React.FC<WinnerPayProps> = ({ isOpen, onClose, type, team, player, onBetSuccess }) => {
  const [amount, setAmount] = useState<number>(0);
  const [customAmount, setCustomAmount] = useState<string>('');
  const [showSuccess, setShowSuccess] = useState(false);
  const { currentGame, walletInfo } = useStore();
  const { getTokenBalance, tokenBalance, tokenBalanceLoading } = useWalletStore();
  const { login, isAuthenticated } = useUserStore();
  const [tokenName, setTokenName] = useState<string>('');
  const [teamId, setTeamId] = useState<number | null>(null);
  const [isLoadingBalance, setIsLoadingBalance] = useState(false);
  const [isPlacingBet, setIsPlacingBet] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
  
  const amounts = [1, 5, 10, 50];

  // web3auth 로그인 상태 확인
  useEffect(() => {
    const checkLoginStatus = async () => {
      // 1. web3auth.connected 체크
      const isWeb3Connected = web3auth.connected;
      
      // 2. authStore의 isAuthenticated 체크
      const isAuth = isAuthenticated;
      
      // 3. 최종 로그인 상태 설정 (App.tsx에서 web3auth 연결 유지 로직을 처리하므로 자동 재연결 로직 제거)
      setIsLoggedIn(isWeb3Connected || isAuth);
    };
    
    checkLoginStatus();
  }, [isAuthenticated]);

  // 토큰 이름 가져오기 및 teamId 설정
  useEffect(() => {
    const fetchTokenName = async () => {
      if (!currentGame || !team) return;
      
      try {
        // 선택한 팀의 팀 ID 확인
        const currentTeamId = team === currentGame.homeTeam 
          ? currentGame.homeTeamId 
          : currentGame.awayTeamId;
          
        if (!currentTeamId) return;
        
        setTeamId(currentTeamId);
        
        // 토큰 이름 가져오기
        const fetchedTokenName = await getTeamTokenName(currentTeamId);
        setTokenName(fetchedTokenName || 'BET');
      } catch (error) {
        console.error('토큰 이름 가져오기 실패:', error);
        setTokenName('BET'); // 기본값
      }
    };
    
    fetchTokenName();
  }, [currentGame, team]);
  
  // 토큰 잔액 가져오기
  useEffect(() => {
    const fetchTokenBalance = async () => {
      if (!teamId) return;
      
      setIsLoadingBalance(true);
      try {
        await getTokenBalance(teamId);
      } catch (error) {
        console.error('토큰 잔액 가져오기 실패:', error);
      } finally {
        setIsLoadingBalance(false);
      }
    };
    
    fetchTokenBalance();
  }, [teamId, getTokenBalance]);

  const handleCustomAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.replace(/[^0-9]/g, '');
    setCustomAmount(value);
    setAmount(Number(value));
  };

  const handleNumberClick = (num: string) => {
    if (customAmount.length < 10) {
      const newAmount = customAmount + num;
      setCustomAmount(newAmount);
      setAmount(Number(newAmount));
    }
  };

  const handleDelete = () => {
    const newAmount = customAmount.slice(0, -1);
    setCustomAmount(newAmount);
    setAmount(Number(newAmount));
  };

  // 로그인 처리
  const handleLogin = async () => {
    try {
      setIsPlacingBet(true);
      setError(null);
      
      // useUserStore의 login 함수 사용 (web3auth 초기화 + 연결 포함)
      const loginSuccess = await login('google');
      
      if (loginSuccess) {
        setIsLoggedIn(true);
        
        // 로그인 후 토큰 잔액 새로고침
        if (teamId) {
          await getTokenBalance(teamId);
        }
      } else {
        throw new Error('로그인에 실패했습니다');
      }
    } catch (err: any) {
      console.error('로그인 실패:', err);
      setError(err.message || '로그인에 실패했습니다. 다시 시도해주세요.');
    } finally {
      setIsPlacingBet(false);
    }
  };

  const handlePay = async () => {
    // 로그인/연결 상태 재확인
    const isConnected = web3auth.connected;
    const providerExists = !!web3auth.provider;
    
    // 연결되어 있지 않거나 프로바이더가 없는 경우 로그인 필요
    if (!isConnected || !providerExists) {
      setError('베팅을 위해 먼저 지갑에 연결해주세요.');
      return;
    }

    if (amount <= 0 || !currentGame || !teamId) {
      setError('유효한 금액과 팀을 선택해주세요.');
      return;
    }

    setIsPlacingBet(true);
    setError(null);

    try {
      // privateKey를 사용하여 베팅하기
      console.log(`베팅 시작: 게임 ID=${currentGame.gameId}, 팀 ID=${teamId}, 금액=${amount}`);
      
      // 1. 개인키 가져오기
      const privateKey = await useWalletStore.getState().exportPrivateKey();
      if (!privateKey) {
        throw new Error("개인키를 가져올 수 없습니다. 다시 로그인해주세요.");
      }
      
      // 2. 컨트랙트 인스턴스 생성
      const contract = getWinningTeamVotingContract(privateKey);
      
      // 3. 베팅 실행
      const receipt = await placeBet(
        contract,
        currentGame.gameId,
        teamId,
        amount.toString()
      );
      
      console.log('베팅 성공:', receipt);
      
      // 4. 베팅 성공 후 상태 업데이트
      // 베팅 금액 업데이트를 위해 컨트랙트에서 다시 조회
      const readOnlyContract = getWinningTeamVotingContractReadOnly();
      const updatedBets = await getTeamBets(readOnlyContract, currentGame.gameId, teamId);
      console.log('업데이트된 베팅량:', updatedBets);
      
      // 콜백 함수 호출로 부모 컴포넌트에 베팅 성공 알림
      if (onBetSuccess) {
        onBetSuccess();
      }
      
      // 성공 메시지 표시
      setShowSuccess(true);
      setTimeout(() => {
        setShowSuccess(false);
        setAmount(0);
        setCustomAmount('');
        onClose();
      }, 1500);
      
    } catch (err: any) {
      console.error('베팅 실패:', err);
      
      // 연결 관련 오류인 경우 다시 로그인 필요
      if (err.message && (
          err.message.includes('개인키를 가져올 수 없습니다') || 
          err.message.includes('invalid sender')
        )) {
        setError('지갑 연결에 문제가 있습니다. 다시 로그인해주세요.');
      } else {
        setError(err.message || '베팅에 실패했습니다. 다시 시도해주세요.');
      }
    } finally {
      setIsPlacingBet(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div 
        className="bg-white rounded-xl overflow-hidden shadow-xl"
        style={{ width: '100%', maxWidth: '360px' }}
      >
        {/* 헤더 */}
        <div className="flex justify-between items-center p-4 border-b border-gray-100">
          <div className="w-5"></div>
          <h1 className="text-lg font-['Giants-Bold'] text-gray-800">
            {type === 'winner' ? `${team} 우승 예측` : `${player} MVP 예측`}
          </h1>
          <button 
            onClick={onClose}
            className="w-5 h-5 flex items-center justify-center text-gray-400 hover:text-gray-600"
          >
            <svg
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              className="w-5 h-5"
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
        <div className="p-4">
          {/* 현재 보유 금액 */}
          <div className="bg-gradient-to-br from-black to-gray-800 rounded-xl p-3 shadow-lg mb-3">
            <p className="text-xs text-gray-400 mb-1">현재 보유 금액</p>
            <div className="flex items-baseline">
              {tokenBalanceLoading || isLoadingBalance ? (
                <p className="text-sm text-gray-400">로딩 중...</p>
              ) : (
                <>
                  <p className="text-xl font-['Giants-Bold'] text-white">
                    {tokenBalance ? Number(tokenBalance).toLocaleString(undefined, {maximumFractionDigits: 4}) : '0'}
                  </p>
                  <p className="text-sm text-gray-400 ml-1">{tokenName || 'BET'}</p>
                </>
              )}
            </div>
            <div className="flex items-baseline mt-1">
              <p className="text-xs text-gray-400">BET 잔액: {walletInfo.totalBET.toLocaleString()}</p>
            </div>
          </div>

          {!isLoggedIn ? (
            // 로그인 필요 화면
            <div className="py-4 flex flex-col items-center">
              <p className="text-sm text-gray-600 text-center mb-4">
                베팅을 위해 지갑에 연결해주세요.
              </p>
              <button
                onClick={handleLogin}
                disabled={isPlacingBet}
                className="px-6 py-3 rounded-xl bg-black text-white font-['Giants-Bold'] hover:bg-gray-800 transition-colors"
              >
                {isPlacingBet ? '연결 중...' : '지갑 연결하기'}
              </button>
            </div>
          ) : (
            // 로그인 완료 후 베팅 화면
            <>
              {/* 금액 선택 버튼들 */}
              <div className="grid grid-cols-2 gap-2 mb-3">
                {amounts.map((value) => (
                  <button
                    key={value}
                    onClick={() => {
                      setAmount(value);
                      setCustomAmount(value.toString());
                    }}
                    className={`p-3 rounded-xl text-center transition-colors
                      ${amount === value
                        ? 'bg-black text-white' 
                        : 'bg-gray-100 text-black hover:bg-gray-200'}`}
                  >
                    <p className="text-sm mb-1">베팅금액</p>
                    <p className="text-lg font-['Giants-Bold']">
                      {value.toLocaleString()} {tokenName || 'BET'}
                    </p>
                  </button>
                ))}
              </div>

              {/* 직접 입력 필드 */}
              <div className="bg-gray-100 rounded-lg p-3 mb-3">
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm text-gray-500">직접 입력하기</span>
                </div>
                <div className="flex items-center justify-between">
                  <input
                    type="text"
                    value={customAmount}
                    onChange={handleCustomAmountChange}
                    placeholder="0"
                    className="text-xl font-['Giants-Bold'] text-gray-800 w-full outline-none bg-transparent"
                  />
                  <span className="text-gray-500 ml-2">{tokenName || 'BET'}</span>
                </div>
              </div>

              {/* 에러 메시지 */}
              {error && (
                <div className="bg-red-50 text-red-500 p-2 rounded-lg mb-3 text-sm">
                  {error}
                </div>
              )}

              {/* 버튼 */}
              <button
                onClick={handlePay}
                disabled={amount === 0 || isPlacingBet}
                className={`w-full py-3 rounded-xl text-white text-base font-['Giants-Bold'] transition-colors ${
                  amount > 0 && !isPlacingBet
                    ? 'bg-black hover:bg-gray-800'
                    : 'bg-gray-300 cursor-not-allowed'
                }`}
              >
                {isPlacingBet ? '베팅 처리 중...' : '베팅하기'}
              </button>
            </>
          )}
        </div>
      </div>

      {/* 성공 메시지 모달 */}
      {showSuccess && (
        <div className="fixed inset-0 bg-white flex items-center justify-center z-[60]">
          <div className="flex flex-col items-center">
            <svg
              className="w-16 h-16 text-green-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M7 13l4 4L21 7"
              />
            </svg>
            <p className="text-base font-['Giants-Bold'] text-gray-800 mt-2">
              베팅이 완료되었습니다
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default WinnerPay; 