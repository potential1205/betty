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

// 선수 정보 인터페이스
interface Player {
  name: string;          // 선수 이름
  position: string;      // 포지션
  handedness: string;    // 주력 손(투수/타자)
  imageUrl: string;      // 프로필 이미지 URL
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
}

// 기본 선수 이미지 URL
const DEFAULT_PLAYER_IMAGE = 'https://a609-betty-bucket.s3.ap-northeast-2.amazonaws.com/player/betty_player_default.png';

export const Winner: React.FC<WinnerProps> = ({ homeTeam, awayTeam }) => {
  // Store에서 currentGame 가져오기
  const { currentGame } = useStore();
  
  // 상태 관리
  const [lineup, setLineup] = useState<LineupData | null>(null);          // 라인업 데이터
  const [loading, setLoading] = useState(true);                           // 로딩 상태
  const [error, setError] = useState<string | null>(null);                // 오류 상태 추가
  const [selectedTeam, setSelectedTeam] = useState<string | null>(null);  // 선택된 팀
  const [selectedPlayer, setSelectedPlayer] = useState<Player | null>(null); // 선택된 선수
  const [showWinnerPay, setShowWinnerPay] = useState(false);
  const [showMvpPay, setShowMvpPay] = useState(false);

  // API URL 설정
  const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

  // 라인업 데이터 가져오기
  useEffect(() => {
    const fetchLineup = async () => {
      try {
        // Store에서 가져온 currentGame의 gameId 사용
        const gameId = currentGame?.gameId;
        
        console.log('=== Lineup Request Debug ===');
        console.log('1. Game ID:', gameId, '(타입:', typeof gameId, ')');
        
        // gameId를 number로 변환
        const numericGameId = Number(gameId);
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
        // 선수 이미지 URL 확인을 위한 로깅 추가
        if (data.home) {
          console.log('Home team pitcher:', data.home.starterPitcher);
          console.log('Home team batters:', data.home.starterBatters);
        }
        if (data.away) {
          console.log('Away team pitcher:', data.away.starterPitcher);
          console.log('Away team batters:', data.away.starterBatters);
        }
        setLineup(data);
        setError(null);
      } catch (error: any) {
        console.error('라인업 로딩 에러:', error);
        console.error('Error response:', error.response);
        console.error('Error config:', error.config);
        console.error('Error data:', error.response?.data);
        
        if (error.response?.data?.code === 2001) {
          setError('라인업 정보가 아직 없습니다!');
        } else {
          setError(error.response?.data?.message || '라인업 정보를 불러올 수 없습니다.');
        }
      } finally {
        setLoading(false);
      }
    };

    // currentGame이 있을 때만 API 호출
    if (currentGame?.gameId) {
      fetchLineup();
    } else {
      console.error('Invalid gameId:', currentGame?.gameId);
      setError('게임 정보가 없습니다.');
      setLoading(false);
    }
  }, [currentGame, API_URL]);

  // 로딩 중 표시
  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
      </div>
    );
  }

  // 오류 표시
  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-full">
        <div className="text-center text-gray-400 mb-6">
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
      </div>
    );
  }

  /**
   * 팀 선택 섹션 렌더링
   * - 홈팀과 원정팀 중 승리 예상 팀 선택
   * - 선택된 팀은 시각적으로 강조 표시
   */
  const renderTeamSelection = () => (
    <div className="mb-8">
      <h3 className="text-base font-['Giants-Bold'] mb-4">우승팀 예측</h3>
      <div className="grid grid-cols-2 gap-4">
        {[homeTeam, awayTeam].map((team) => (
          <motion.button
            key={team}
            onClick={() => setSelectedTeam(team)}
            className={`p-4 rounded-xl border-2 transition-all
              ${selectedTeam === team 
                ? 'border-white bg-white/20' 
                : 'border-white/20 hover:border-white/40'}`}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            <span className="font-['Giants-Bold'] text-base">{team}</span>
          </motion.button>
        ))}
      </div>
    </div>
  );

  // 이미지 컴포넌트 수정
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
          target.src = DEFAULT_PLAYER_IMAGE;
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

    return (
      <div>
        <h3 className="text-base font-['Giants-Bold'] mb-4">MVP 예측</h3>
        <div className="grid grid-cols-2 gap-4">
          {/* 홈팀 선수들 */}
          <div className="space-y-4">
            <div className="text-center text-xs text-gray-400 mb-2">{homeTeam}</div>
            {[lineup.home.starterPitcher, ...(lineup.home.starterBatters || [])].filter(Boolean).map((player, index) => (
              <motion.button
                key={index}
                onClick={() => setSelectedPlayer(player)}
                className={`w-full p-3 rounded-xl border-2 transition-all flex items-center
                  ${selectedPlayer === player 
                    ? 'border-white bg-white/20' 
                    : 'border-white/20 hover:border-white/40'}`}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                {renderPlayerImage(player)}
                <div className="ml-3 min-w-0 flex-1">
                  <p className="font-['Giants-Bold'] text-xs truncate">{player.name}</p>
                  <p className="text-[10px] text-gray-400 truncate">{player.position}</p>
                </div>
              </motion.button>
            ))}
          </div>

          {/* 원정팀 선수들 */}
          <div className="space-y-4">
            <div className="text-center text-xs text-gray-400 mb-2">{awayTeam}</div>
            {[lineup.away.starterPitcher, ...(lineup.away.starterBatters || [])].filter(Boolean).map((player, index) => (
              <motion.button
                key={index}
                onClick={() => setSelectedPlayer(player)}
                className={`w-full p-3 rounded-xl border-2 transition-all flex items-center
                  ${selectedPlayer === player 
                    ? 'border-white bg-white/20' 
                    : 'border-white/20 hover:border-white/40'}`}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                {renderPlayerImage(player)}
                <div className="ml-3 min-w-0 flex-1">
                  <p className="font-['Giants-Bold'] text-xs truncate">{player.name}</p>
                  <p className="text-[10px] text-gray-400 truncate">{player.position}</p>
                </div>
              </motion.button>
            ))}
          </div>
        </div>
      </div>
    );
  };

  // 메인 렌더링
  return (
    <div className="space-y-8 pb-24">
      {/* 팀 투표 섹션 */}
      <div>
        {renderTeamSelection()}
        {selectedTeam && (
          <motion.button
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="w-full py-3 bg-white text-black rounded-xl font-['Giants-Bold'] text-base
              hover:bg-white/90 transition-colors mt-4"
            onClick={() => setShowWinnerPay(true)}
          >
            팀 배팅하기
          </motion.button>
        )}
      </div>

      {/* MVP 투표 섹션 */}
      <div>
        {lineup ? renderPlayerSelection() : (
          <div className="text-center text-gray-400">
            <p className="text-lg font-['Giants-Bold'] mb-2">아직 라인업이 공개되지 않았습니다</p>
            <p className="text-sm">경기 시작 전에 다시 확인해주세요</p>
          </div>
        )}
        {selectedPlayer && (
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
        />
      )}
    </div>
  );
};