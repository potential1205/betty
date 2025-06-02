/**
 * Winner 컴포넌트: 경기 승패 및 MVP 예측을 위한 베팅 인터페이스
 * 
 * 주요 기능:
 * 1. 승리 팀 예측
 * 2. MVP 선수 예측
 * 3. 베팅 처리
 */

import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useStore } from '../stores/useStore';
import axiosInstance from '../apis/axios';
import WinnerPay from './WinnerPay';
import { getWinningTeamVotingContractReadOnly, getTeamBets, getUserBet } from '../utils/winningTeamVotingContract';
import { getTeamTokenName } from '../apis/tokenApi';
import { useWalletStore } from '../stores/walletStore';
import { getMVPVotingContractReadOnly, getTotalVotes, getUserMVPVote } from '../utils/mvpVotingContract';
import { useUserStore } from '../stores/authStore';

// 선수 정보 인터페이스
interface Player {
  name: string;          // 선수 이름
  position: string;      // 포지션
  handedness: string;    // 주력 손(투수/타자)
  imageUrl: string;      // 프로필 이미지 URL
  id?: number;           // 선수 ID (MVP 투표용)
  voteAmount?: string;   // 선수 베팅량
  tokenName?: string;    // 토큰 이름
}

// 팀 라인업 인터페이스
interface TeamLineup {
  starterPitcher: Player;    // 선발 투수
  starterBatters: Player[];  // 선발 타자 명단
}

// 전체 라인업 데이터 인터페이스
interface LineupData {
  home: TeamLineup;  // 홈팀 라인업
  away: TeamLineup;  // 원정팀 라인업
}

// Winner 컴포넌트 Props 인터페이스
interface WinnerProps {
  homeTeam: string;   // 홈팀 이름
  awayTeam: string;   // 원정팀 이름
  gameId?: string;    // 게임 ID (선택적)
}

// 기본 선수 이미지 URL
const DEFAULT_PLAYER_IMAGE = 'https://a609-betty-bucket.s3.ap-northeast-2.amazonaws.com/player/player_default_image.png';

export const Winner: React.FC<WinnerProps> = ({ homeTeam, awayTeam, gameId }) => {
  // Store에서 currentGame 가져오기
  const { currentGame } = useStore();
  const { getAccounts } = useWalletStore();
  const { isAuthenticated } = useUserStore();
  
  // 상태 관리
  const [lineup, setLineup] = useState<LineupData | null>(null);          // 라인업 데이터
  const [lineupLoading, setLineupLoading] = useState(true);               // 라인업 로딩 상태
  const [teamVotingLoading, setTeamVotingLoading] = useState(true);       // 팀 투표 로딩 상태
  const [mvpVotingLoading, setMvpVotingLoading] = useState(true);         // MVP 투표 로딩 상태
  const [error, setError] = useState<string | null>(null);                // 오류 상태 추가
  const [selectedTeam, setSelectedTeam] = useState<string | null>(null);  // 선택된 팀
  const [selectedPlayer, setSelectedPlayer] = useState<Player | null>(null); // 선택된 선수
  const [showWinnerPay, setShowWinnerPay] = useState(false);
  const [showMvpPay, setShowMvpPay] = useState(false);
  const [homeTeamBets, setHomeTeamBets] = useState<string>('0');
  const [awayTeamBets, setAwayTeamBets] = useState<string>('0');
  const [homeTeamId, setHomeTeamId] = useState<number | null>(null);
  const [awayTeamId, setAwayTeamId] = useState<number | null>(null);
  const [homeTeamTokenName, setHomeTeamTokenName] = useState<string>('');
  const [awayTeamTokenName, setAwayTeamTokenName] = useState<string>('');
  const [userBetTeamId, setUserBetTeamId] = useState<number | null>(null); // 사용자가 베팅한 팀 ID
  const [userBetAmount, setUserBetAmount] = useState<string>('0');        // 사용자 베팅 금액
  const [loadingUserBet, setLoadingUserBet] = useState<boolean>(false);   // 사용자 베팅 정보 로딩 상태
  const [playerVotes, setPlayerVotes] = useState<{[key: string]: string}>({});  // 선수별 베팅량
  const [userMVPBet, setUserMVPBet] = useState<{playerId: number, amount: string} | null>(null);
  const [loadingMVPBet, setLoadingMVPBet] = useState(false);

  // API URL 설정
  const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

  // localStorage에서 팀 ID 가져오기
  useEffect(() => {
    setTeamVotingLoading(true);
    // localStorage에서 현재 게임 정보 읽기
    try {
      const currentGameStr = localStorage.getItem('currentGame');
      if (currentGameStr) {
        const currentGame = JSON.parse(currentGameStr);
        
        if (currentGame) {
          setHomeTeamId(currentGame.homeTeamId);
          setAwayTeamId(currentGame.awayTeamId);
          
          console.log('localStorage에서 팀 ID 가져옴:', {
            homeTeamId: currentGame.homeTeamId,
            awayTeamId: currentGame.awayTeamId,
            homeTeam,
            awayTeam
          });
        }
      } else {
        console.log('localStorage에 currentGame 정보가 없음');
        
        // 백업: 이전 방식으로 팀 ID 읽기
        const savedHomeTeamId = localStorage.getItem(`homeTeamId_${homeTeam}`);
        const savedAwayTeamId = localStorage.getItem(`awayTeamId_${awayTeam}`);
        
        console.log('백업: localStorage에서 팀 ID 읽기:', { savedHomeTeamId, savedAwayTeamId });
        
        if (savedHomeTeamId) {
          const parsedId = parseInt(savedHomeTeamId);
          if (!isNaN(parsedId)) {
            setHomeTeamId(parsedId);
            console.log('백업: homeTeamId 설정:', parsedId);
          }
        }
        
        if (savedAwayTeamId) {
          const parsedId = parseInt(savedAwayTeamId);
          if (!isNaN(parsedId)) {
            setAwayTeamId(parsedId);
            console.log('백업: awayTeamId 설정:', parsedId);
          }
        }
      }
    } catch (error) {
      console.error('localStorage에서 게임 정보 가져오기 오류:', error);
    } finally {
      // 팀 ID 로딩이 완료되면 팀 투표 로딩 상태 업데이트
      setTeamVotingLoading(false);
    }
  }, [homeTeam, awayTeam]);

  // 라인업 데이터 가져오기
  useEffect(() => {
    const fetchLineup = async () => {
      setLineupLoading(true);
      try {
        // Store에서 가져온 currentGame의 gameId 사용 또는 props로 전달받은 gameId 사용
        const targetGameId = gameId || currentGame?.gameId;
        
        console.log('=== Lineup Request Debug ===');
        console.log('1. Game ID:', targetGameId, '(타입:', typeof targetGameId, ')');
        
        // gameId를 number로 변환
        const numericGameId = Number(targetGameId);
        if (isNaN(numericGameId)) {
          throw new Error('유효하지 않은 게임 ID입니다.');
        }
        
        console.log('2. Numeric Game ID:', numericGameId, '(타입:', typeof numericGameId, ')');
        console.log('3. Current access token:', localStorage.getItem('accessToken'));
        console.log('4. Current game status:', currentGame?.status);
        console.log('5. Request URL:', `/home/games/${numericGameId}/lineup`);
        
        const response = await axiosInstance.get(`/home/games/${numericGameId}/lineup`);
        
        console.log('6. Response status:', response.status);
        console.log('7. Response headers:', response.headers);
        console.log('8. Response data:', response.data);
        console.log('=== End Debug ===');
        
        const data = response.data;
        console.log('Received lineup data:', data);
        
        // 팀 ID 설정 및 localStorage에 저장
        const newHomeTeamId = data.home?.teamId || null;
        const newAwayTeamId = data.away?.teamId || null;
        
        console.log('API에서 가져온 팀 ID:', { homeTeamId: newHomeTeamId, awayTeamId: newAwayTeamId });
        
        if (newHomeTeamId) {
          setHomeTeamId(newHomeTeamId);
          localStorage.setItem(`homeTeamId_${homeTeam}`, newHomeTeamId.toString());
        }
        
        if (newAwayTeamId) {
          setAwayTeamId(newAwayTeamId);
          localStorage.setItem(`awayTeamId_${awayTeam}`, newAwayTeamId.toString());
        }
        
        // API가 제공하는 ID를 사용하므로 수동으로 ID를 설정할 필요가 없음
        // 각 선수는 API 응답에서 이미 고유한 ID를 가지고 있음
        
        // API에서 선수 ID가 없는 경우 검증
        let hasInvalidPlayerId = false;
        
        if (data.home && data.away) {
          // 홈팀 선수 ID 확인
          if (data.home.starterPitcher && (!data.home.starterPitcher.id || data.home.starterPitcher.id === 0)) {
            console.error('홈팀 선발 투수 ID가 유효하지 않습니다:', data.home.starterPitcher);
            hasInvalidPlayerId = true;
          }
          
          if (data.home.starterBatters) {
            data.home.starterBatters.forEach((player: any) => {
              if (!player.id || player.id === 0) {
                console.error(`홈팀 타자 ${player.name} ID가 유효하지 않습니다:`, player);
                hasInvalidPlayerId = true;
              }
            });
          }
          
          // 원정팀 선수 ID 확인
          if (data.away.starterPitcher && (!data.away.starterPitcher.id || data.away.starterPitcher.id === 0)) {
            console.error('원정팀 선발 투수 ID가 유효하지 않습니다:', data.away.starterPitcher);
            hasInvalidPlayerId = true;
          }
          
          if (data.away.starterBatters) {
            data.away.starterBatters.forEach((player: any) => {
              if (!player.id || player.id === 0) {
                console.error(`원정팀 타자 ${player.name} ID가 유효하지 않습니다:`, player);
                hasInvalidPlayerId = true;
              }
            });
          }
        }
        
        if (hasInvalidPlayerId) {
          throw new Error('일부 선수의 ID가 유효하지 않습니다. MVP 베팅이 불가능할 수 있습니다.');
        }
        
        setLineup(data);
        setError(null);
        
        // 라인업 로드 후 선수별 베팅량 조회 시작
        fetchPlayerVotes(numericGameId, data);
      } catch (error: any) {
        console.error('라인업 로딩 에러:', error);
          setError(error.response?.data?.message || '라인업 정보를 불러올 수 없습니다.');
      } finally {
        setLineupLoading(false);
      }
    };

    if (currentGame?.gameId || gameId) {
      fetchLineup();
    } else {
      setLineupLoading(false); // 게임 ID가 없는 경우 로딩 상태 해제
    }
  }, [currentGame?.gameId, homeTeam, awayTeam, gameId]);

  // 선수별 베팅량 조회 함수
  const fetchPlayerVotes = async (gameId: number, lineupData: LineupData) => {
    setMvpVotingLoading(true);
    try {
      const contract = getMVPVotingContractReadOnly();
      const votes: {[key: string]: string} = {};
      
      // 홈팀 선수들의 베팅량 조회
      if (lineupData.home) {
        if (lineupData.home.starterPitcher?.id) {
          const playerId = lineupData.home.starterPitcher.id;
          try {
            const voteAmount = await getTotalVotes(contract, gameId, playerId);
            votes[playerId.toString()] = voteAmount;
          } catch (error) {
            console.error(`선수 ${playerId}의 베팅량 조회 실패:`, error);
            votes[playerId.toString()] = '0';
          }
        }
        
        if (lineupData.home.starterBatters) {
          for (const player of lineupData.home.starterBatters) {
            if (player.id) {
              try {
                const voteAmount = await getTotalVotes(contract, gameId, player.id);
                votes[player.id.toString()] = voteAmount;
              } catch (error) {
                console.error(`선수 ${player.id}의 베팅량 조회 실패:`, error);
                votes[player.id.toString()] = '0';
              }
            }
          }
        }
      }
      
      // 원정팀 선수들의 베팅량 조회
      if (lineupData.away) {
        if (lineupData.away.starterPitcher?.id) {
          const playerId = lineupData.away.starterPitcher.id;
          try {
            const voteAmount = await getTotalVotes(contract, gameId, playerId);
            votes[playerId.toString()] = voteAmount;
          } catch (error) {
            console.error(`선수 ${playerId}의 베팅량 조회 실패:`, error);
            votes[playerId.toString()] = '0';
          }
        }
        
        if (lineupData.away.starterBatters) {
          for (const player of lineupData.away.starterBatters) {
            if (player.id) {
              try {
                const voteAmount = await getTotalVotes(contract, gameId, player.id);
                votes[player.id.toString()] = voteAmount;
              } catch (error) {
                console.error(`선수 ${player.id}의 베팅량 조회 실패:`, error);
                votes[player.id.toString()] = '0';
              }
            }
          }
        }
      }
      
      console.log('선수별 베팅량 조회 결과:', votes);
      setPlayerVotes(votes);
    } catch (error) {
      console.error('선수별 베팅량 조회 실패:', error);
    } finally {
      setMvpVotingLoading(false);
    }
  };

  // MVP 베팅량 새로고침 함수
  const refreshMvpVotes = async () => {
    if (!lineup) return;
    
    // 사용 가능한 gameId 찾기
    const targetGameId = gameId || currentGame?.gameId;
    if (!targetGameId) return;
    
    try {
      await fetchPlayerVotes(Number(targetGameId), lineup);
    } catch (error) {
      console.error('MVP 베팅량 새로고침 실패:', error);
    }
  };
  
  // 베팅 성공 시 호출할 함수
  const onBetSuccess = async () => {
    // 팀 베팅량 새로고침
    await refreshTeamBets();
    // MVP 베팅량 새로고침
    await refreshMvpVotes();
  };

  // 베팅 금액 가져오기
  useEffect(() => {
    const fetchTeamBets = async () => {
      // Store에서 가져온 currentGame의 gameId 사용 또는 props로 전달받은 gameId 사용
      const targetGameId = gameId || currentGame?.gameId;
      
      if (!targetGameId) {
        setTeamVotingLoading(false);
        return;
      }

      setTeamVotingLoading(true);
      
      try {
        // 팀 ID가 없어도 진행하되, 있으면 해당 값 사용
        const homeId = homeTeamId || (currentGame?.homeTeamId ? Number(currentGame.homeTeamId) : null);
        const awayId = awayTeamId || (currentGame?.awayTeamId ? Number(currentGame.awayTeamId) : null);
        
        if (!homeId || !awayId) {
          console.log('팀 ID 정보 없음, 베팅 정보 조회 건너뜀:', { homeId, awayId });
          return;
        }
        
        const contract = getWinningTeamVotingContractReadOnly();
        const homeBets = await getTeamBets(contract, Number(targetGameId), homeId);
        const awayBets = await getTeamBets(contract, Number(targetGameId), awayId);
        
        setHomeTeamBets(homeBets);
        setAwayTeamBets(awayBets);
      } catch (error) {
        console.error('팀 베팅액 조회 실패:', error);
      } finally {
        setTeamVotingLoading(false);
      }
    };

    fetchTeamBets();
  }, [currentGame?.gameId, homeTeamId, awayTeamId, currentGame?.homeTeamId, currentGame?.awayTeamId, gameId]);

  // 토큰 이름 가져오기
  useEffect(() => {
    const fetchTokenNames = async () => {
      // 팀 ID가 없어도 진행하되, 있으면 해당 값 사용
      const homeId = homeTeamId || (currentGame?.homeTeamId ? Number(currentGame.homeTeamId) : null);
      const awayId = awayTeamId || (currentGame?.awayTeamId ? Number(currentGame.awayTeamId) : null);
      
      if (!homeId || !awayId) {
        console.log('팀 ID가 없어 토큰 이름 조회를 건너뜁니다:', { homeId, awayId });
        return;
      }
      
      console.log('토큰 이름 조회 API 호출 시작:', { homeId, awayId });
      
      try {
        const homeTokenName = await getTeamTokenName(homeId);
        const awayTokenName = await getTeamTokenName(awayId);
        
        console.log('토큰 이름 조회 결과:', { homeTokenName, awayTokenName });
        
        setHomeTeamTokenName(homeTokenName);
        setAwayTeamTokenName(awayTokenName);
      } catch (error) {
        console.error('토큰 이름 조회 실패:', error);
      }
    };

    fetchTokenNames();
  }, [homeTeamId, awayTeamId, currentGame?.homeTeamId, currentGame?.awayTeamId]);

  // 사용자의 베팅 정보 조회
  useEffect(() => {
    const fetchUserBet = async () => {
      // Store에서 가져온 currentGame의 gameId 사용 또는 props로 전달받은 gameId 사용
      const targetGameId = gameId || currentGame?.gameId;
      
      if (!targetGameId) return;
      
      setLoadingUserBet(true);
      try {
        // 사용자 지갑 주소 가져오기
        const walletAddress = await getAccounts();
        if (!walletAddress) {
          console.log('지갑 주소를 가져올 수 없습니다.');
          return;
        }
        
        // 컨트랙트 인스턴스 생성
        const contract = getWinningTeamVotingContractReadOnly();
        
        // 사용자 베팅 정보 조회
        const betInfo = await getUserBet(contract, Number(targetGameId), walletAddress);
        console.log('사용자 베팅 정보:', betInfo);
        
        // 베팅 정보가 있으면 상태 업데이트
        if (Number(betInfo.amount) > 0) {
          setUserBetTeamId(betInfo.teamId);
          setUserBetAmount(betInfo.amount);
          
          // 이미 베팅한 팀이면 자동 선택
          const betTeamName = betInfo.teamId === homeTeamId ? homeTeam : 
                             betInfo.teamId === awayTeamId ? awayTeam : null;
          if (betTeamName) {
            setSelectedTeam(betTeamName);
          }
        }
      } catch (error) {
        console.error('사용자 베팅 정보 조회 실패:', error);
      } finally {
        setLoadingUserBet(false);
      }
    };

    fetchUserBet();
  }, [currentGame?.gameId, homeTeamId, awayTeamId, homeTeam, awayTeam, getAccounts, gameId]);

  // 팀 베팅량 새로고침 함수
  const refreshTeamBets = async () => {
    // 사용 가능한 gameId 찾기
    const targetGameId = gameId || currentGame?.gameId;
    if (!targetGameId) return;
    
    setTeamVotingLoading(true);
    
    try {
      // 팀 ID가 없어도 진행하되, 있으면 해당 값 사용
      const homeId = homeTeamId || (currentGame?.homeTeamId ? Number(currentGame.homeTeamId) : null);
      const awayId = awayTeamId || (currentGame?.awayTeamId ? Number(currentGame.awayTeamId) : null);
      
      if (!homeId || !awayId) {
        console.log('팀 ID 정보 없음, 베팅 정보 조회 건너뜀:', { homeId, awayId });
        return;
      }
      
      const contract = getWinningTeamVotingContractReadOnly();
      const homeBets = await getTeamBets(contract, Number(targetGameId), homeId);
      const awayBets = await getTeamBets(contract, Number(targetGameId), awayId);
      
      console.log('새로고침된 베팅량:', { homeBets, awayBets });
      
      setHomeTeamBets(homeBets);
      setAwayTeamBets(awayBets);
      
      // 베팅 정보 갱신 후 사용자 베팅 정보도 다시 조회
      const walletAddress = await getAccounts();
      if (walletAddress) {
        const betInfo = await getUserBet(contract, Number(targetGameId), walletAddress);
        setUserBetTeamId(betInfo.teamId);
        setUserBetAmount(betInfo.amount);
      }
    } catch (error) {
      console.error('팀 베팅액 조회 실패:', error);
    } finally {
      setTeamVotingLoading(false);
    }
  };

  // MVP 베팅 정보 조회
  useEffect(() => {
    const fetchUserMVPBet = async () => {
      const targetGameId = gameId || currentGame?.gameId;
      if (!targetGameId || !isAuthenticated) return;

      setLoadingMVPBet(true);
      try {
        const walletAddress = await getAccounts();
        if (!walletAddress) {
          console.log('지갑 주소를 가져올 수 없습니다.');
          return;
        }

        const contract = getMVPVotingContractReadOnly();
        const mvpBetInfo = await getUserMVPVote(contract, Number(targetGameId), walletAddress);
        
        if (Number(mvpBetInfo.amount) > 0) {
          setUserMVPBet({
            playerId: mvpBetInfo.playerId,
            amount: mvpBetInfo.amount
          });
          console.log('사용자 MVP 베팅 정보:', mvpBetInfo);
        }
      } catch (error) {
        console.error('사용자 MVP 베팅 정보 조회 실패:', error);
      } finally {
        setLoadingMVPBet(false);
      }
    };

    fetchUserMVPBet();
  }, [currentGame?.gameId, gameId, getAccounts, isAuthenticated]);

  // 오류 표시 - 전체 컴포넌트를 반환하는 대신 변수로 선언
  const errorDisplay = (
    <div className="text-center text-gray-400 py-4">
          {error === '라인업 정보가 아직 없습니다!' ? (
            <>
              <p className="text-lg font-['Giants-Bold'] mb-2">아직 라인업이 공개되지 않았습니다</p>
              <p className="text-sm">경기 시작 전에 다시 확인해주세요</p>
            </>
          ) : (
            <>
              <p className="text-lg font-['Giants-Bold'] mb-2">라인업 정보를 불러올 수 없습니다</p>
              <p className="text-sm">서버와의 연결에 문제가 있습니다</p>
              <p className="text-xs mt-2">에러 코드: 404</p>
            </>
          )}
      </div>
    );

  /**
   * 팀 선택 섹션 렌더링
   * - 홈팀과 원정팀 중 승리 예상 팀 선택
   * - 선택된 팀은 시각적으로 강조 표시
   * - 이미 베팅한 팀은 노란색으로 표시
   */
  const renderTeamSelection = () => {
    // 팀 투표 로딩 중일 때 로딩 표시
    if (teamVotingLoading) {
      return (
        <div className="flex items-center justify-center py-10">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
        </div>
      );
    }
    
    return (
    <div className="mb-8">
      <h3 className="text-base font-['Giants-Bold'] mb-4">우승팀 예측</h3>
      <div className="grid grid-cols-2 gap-4">
          {[homeTeam, awayTeam].map((team) => {
            // 현재 팀 ID 결정
            const teamId = team === homeTeam ? homeTeamId : awayTeamId;
            // 사용자가 이 팀에 베팅했는지 확인
            const hasUserBet = teamId === userBetTeamId && Number(userBetAmount) > 0;
            
            return (
          <motion.button
            key={team}
                onClick={() => !hasUserBet && setSelectedTeam(team)}
            className={`p-4 rounded-xl border-2 transition-all
                  ${hasUserBet 
                    ? 'border-yellow-400 bg-yellow-400/20 cursor-default' 
                    : selectedTeam === team 
                ? 'border-white bg-white/20' 
                : 'border-white/20 hover:border-white/40'}`}
                whileHover={{ scale: hasUserBet ? 1 : 1.02 }}
                whileTap={{ scale: hasUserBet ? 1 : 0.98 }}
          >
            <span className="font-['Giants-Bold'] text-base">{team}</span>
                <div className="mt-1 text-xs text-gray-400">
                  💰 : {team === homeTeam 
                    ? `${homeTeamBets} ${homeTeamTokenName}` 
                    : `${awayTeamBets} ${awayTeamTokenName}`}
                </div>
                {hasUserBet && (
                  <div className="mt-2 text-xs text-yellow-400 font-bold">
                    내 배팅: {userBetAmount} {team === homeTeam ? homeTeamTokenName : awayTeamTokenName}
                  </div>
                )}
          </motion.button>
            );
          })}
      </div>
    </div>
  );
  };

  // 이미지 컴포넌트는 그대로 유지
  const renderPlayerImage = (player: Player) => {
    const proxyUrl = 'https://images.weserv.nl/?url=';
    const imageUrl = player.imageUrl ? proxyUrl + encodeURIComponent(player.imageUrl) : DEFAULT_PLAYER_IMAGE;
    
    return (
      <img
        src={imageUrl}
        alt={player.name}
        className="w-12 h-12 rounded-full object-cover"
        onError={(e) => {
          const target = e.target as HTMLImageElement;
          // 무한 루프 방지를 위해 이미 기본 이미지로 설정된 경우 재시도하지 않음
          if (target.src !== DEFAULT_PLAYER_IMAGE) {
          target.src = DEFAULT_PLAYER_IMAGE;
          } else {
            // 기본 이미지도 로드 실패 시 onerror 이벤트 제거하고 투명 이미지 사용
            target.onerror = null;  // 더 이상 오류 이벤트 발생하지 않도록 함
            target.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'; // 투명 이미지
          }
        }}
      />
    );
  };

  /**
   * 선수 선택 섹션 렌더링
   * - 양 팀의 모든 선수 목록 표시
   * - 선수 프로필 이미지, 이름, 포지션 표시
   * - 이미지 로드 실패 시 기본 이미지로 대체
   */
  const renderPlayerSelection = () => {
    if (!lineup) return null;

    // 토큰 이름 결정
    const homeTokenName = homeTeamTokenName || 'BET';
    const awayTokenName = awayTeamTokenName || 'BET';

    return (
      <>
        <div className="grid grid-cols-2 gap-4">
          {/* 홈팀 선수들 */}
          <div>
            <div className="text-center text-xs text-gray-400 mb-4">{homeTeam}</div>
            {[lineup.home.starterPitcher, ...(lineup.home.starterBatters || [])].filter(Boolean).map((player, index) => {
              const playerId = player.id?.toString();
              const voteAmount = playerId && playerVotes[playerId] ? playerVotes[playerId] : '0';
              const hasUserBet = userMVPBet && userMVPBet.playerId === player.id;
              
              return (
              <motion.button
                key={index}
                onClick={() => !hasUserBet && setSelectedPlayer({...player, voteAmount, tokenName: homeTokenName})}
                className={`w-full p-4 rounded-xl border-2 transition-all mb-4 h-[108px] flex items-center justify-center
                    ${hasUserBet 
                      ? 'border-yellow-400 bg-yellow-400/20 cursor-default'
                      : selectedPlayer?.name === player.name 
                    ? 'border-white bg-white/20' 
                    : 'border-white/20 hover:border-white/40'}`}
                whileHover={{ scale: hasUserBet ? 1 : 1.02 }}
                whileTap={{ scale: hasUserBet ? 1 : 0.98 }}
              >
                <div className="flex items-center gap-3 w-full">
                {renderPlayerImage(player)}
                  <div className="min-w-0 flex-1 flex flex-col justify-center">
                    <p className="font-['Giants-Bold'] text-base truncate">{player.name}</p>
                    <p className="text-xs text-gray-400 truncate">{player.position}</p>
                    {!mvpVotingLoading && (
                      <p className="text-xs text-gray-400">
                        💰: {voteAmount} {homeTokenName}
                      </p>
                    )}
                    {hasUserBet && (
                      <p className="text-xs text-yellow-400 font-bold">
                        내 배팅: {userMVPBet.amount} {homeTokenName}
                      </p>
                    )}
                  </div>
                </div>
              </motion.button>
              );
            })}
          </div>

          {/* 원정팀 선수들 */}
          <div>
            <div className="text-center text-xs text-gray-400 mb-4">{awayTeam}</div>
            {[lineup.away.starterPitcher, ...(lineup.away.starterBatters || [])].filter(Boolean).map((player, index) => {
              const playerId = player.id?.toString();
              const voteAmount = playerId && playerVotes[playerId] ? playerVotes[playerId] : '0';
              const hasUserBet = userMVPBet && userMVPBet.playerId === player.id;
              
              return (
              <motion.button
                key={index}
                onClick={() => !hasUserBet && setSelectedPlayer({...player, voteAmount, tokenName: awayTokenName})}
                className={`w-full p-4 rounded-xl border-2 transition-all mb-4 h-[108px] flex items-center justify-center
                    ${hasUserBet 
                      ? 'border-yellow-400 bg-yellow-400/20 cursor-default'
                      : selectedPlayer?.name === player.name 
                    ? 'border-white bg-white/20' 
                    : 'border-white/20 hover:border-white/40'}`}
                whileHover={{ scale: hasUserBet ? 1 : 1.02 }}
                whileTap={{ scale: hasUserBet ? 1 : 0.98 }}
              >
                <div className="flex items-center gap-3 w-full">
                {renderPlayerImage(player)}
                  <div className="min-w-0 flex-1 flex flex-col justify-center">
                    <p className="font-['Giants-Bold'] text-base truncate">{player.name}</p>
                    <p className="text-xs text-gray-400 truncate">{player.position}</p>
                    {!mvpVotingLoading && (
                      <p className="text-xs text-gray-400">
                        💰: {voteAmount} {awayTokenName}
                      </p>
                    )}
                    {hasUserBet && (
                      <p className="text-xs text-yellow-400 font-bold">
                        내 배팅: {userMVPBet.amount} {awayTokenName}
                      </p>
                    )}
                  </div>
                </div>
              </motion.button>
              );
            })}
          </div>
        </div>
      </>
    );
  };

  // 메인 렌더링
  return (
    <div className="space-y-8 pb-24 h-full overflow-y-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:'none'] [scrollbar-width:'none']">
      {/* 팀 투표 섹션 - 항상 표시 */}
      <div>
        {renderTeamSelection()}
        {selectedTeam && !teamVotingLoading && (
          <motion.button
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className={`w-full py-3 text-base font-['Giants-Bold'] rounded-xl mt-4
              ${userBetTeamId 
                ? 'bg-gray-400 cursor-not-allowed text-gray-200' 
                : 'bg-white text-black hover:bg-white/90 transition-colors'}`}
            onClick={() => !userBetTeamId && setShowWinnerPay(true)}
            disabled={!!userBetTeamId}
          >
            {userBetTeamId ? '이미 배팅했습니다' : '팀 배팅하기'}
          </motion.button>
        )}
      </div>

      {/* MVP 투표 섹션 */}
      <div className="pb-4">
        <h3 className="text-base font-['Giants-Bold'] mb-4">MVP 예측</h3>
        {lineupLoading || mvpVotingLoading || loadingMVPBet ? (
          <div className="flex items-center justify-center py-10">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
          </div>
        ) : error ? (
          errorDisplay
        ) : lineup ? (
          <>
            {renderPlayerSelection()}
            {userMVPBet ? (
              <motion.button
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="w-full py-3 bg-gray-400 text-gray-200 rounded-xl font-['Giants-Bold'] text-base cursor-not-allowed mt-4"
                disabled
              >
                이미 배팅했습니다
              </motion.button>
            ) : selectedPlayer && !lineupLoading && !mvpVotingLoading && (
          <motion.button
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="w-full py-3 bg-white text-black rounded-xl font-['Giants-Bold'] text-base
              hover:bg-white/90 transition-colors mt-4"
            onClick={() => setShowMvpPay(true)}
          >
            MVP 배팅하기
          </motion.button>
            )}
          </>
        ) : (
          <div className="text-center text-gray-400 py-4">
            <p className="text-lg font-['Giants-Bold'] mb-2">아직 라인업이 공개되지 않았습니다</p>
            <p className="text-sm">경기 시작 전에 다시 확인해주세요</p>
          </div>
        )}
      </div>

      {/* 우승 예측 결제 모달 */}
      {showWinnerPay && selectedTeam && (
        <WinnerPay
          isOpen={showWinnerPay}
          onClose={() => {
            setShowWinnerPay(false);
            setSelectedTeam(null);
          }}
          type="winner"
          team={selectedTeam}
          onBetSuccess={onBetSuccess}
        />
      )}

      {/* MVP 예측 결제 모달 */}
      {showMvpPay && selectedPlayer && (
        <WinnerPay
          isOpen={showMvpPay}
          onClose={() => {
            setShowMvpPay(false);
            setSelectedPlayer(null);
          }}
          type="mvp"
          player={selectedPlayer.name}
          playerId={selectedPlayer.id}
          voteAmount={selectedPlayer.voteAmount}
          tokenName={selectedPlayer.tokenName}
          onBetSuccess={onBetSuccess}
        />
      )}
    </div>
  );
};