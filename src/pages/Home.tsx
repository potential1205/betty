import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useDrag } from '@use-gesture/react';
import Sidebar from '../components/Sidebar';
import profileImg from '../assets/profile.png';
import hamburgerImg from '../assets/hamburger.png';
import { useStore } from '../stores/useStore';
import axiosInstance from '../apis/axios';
import { getAccessToken } from '../apis/axios';

// 팀 색상 정의
const teamColors: Record<string, string> = {
  '두산': '#131230',
  '롯데': '#002955',
  '키움': '#820024',
  'KIA': '#EA0029',
  '한화': '#FF6600',
  'LG': '#C30452',
  '삼성': '#0066B3',
  'SSG': '#CE0E2D',
  'NC': '#315288',
  'KT': '#000000',
  // 기본 색상
  'default1': '#4A90E2',
  'default2': '#50E3C2'
};

// API 응답 타입 정의
interface GameSchedule {
  gameId: string;
  season: number;
  gameDate: string;
  startTime: string;
  stadium: string;
  homeTeam: string;
  awayTeam: string;
  status: string;
}

interface GamesData {
  schedules: GameSchedule[];
}

// 화면에 표시할 게임 데이터 타입
interface Game {
  id: number;
  gameId: string;
  homeTeam: string;
  awayTeam: string;
  homeScore: number;
  awayScore: number;
  inning: number;
  status: string;
  schedule: GameSchedule;
}

const Home: React.FC = () => {
  const navigate = useNavigate();
  const { toggleSidebar, setTodayGames } = useStore();
  
  // 게임 데이터 상태
  const [games, setGames] = useState<Game[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // 환경 변수에서 API URL 가져오기기
  const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

  // API에서 데이터 가져오기
  useEffect(() => {
    const fetchGamesData = async () => {
      setLoading(true);
      try {
        // 디버깅용 로그 추가
        const token = getAccessToken();
        console.log('Current access token:', token);
        const response = await axiosInstance.get('/home/games');
        console.log('API Response:', response);
        const data: GamesData = response.data;
        console.log('API 응답 전체:', data); // 전체 응답 로깅
        
        if (!data.schedules || data.schedules.length === 0) {
          setGames([]);
          setTodayGames([]);
          return;
        }
        
        const formattedGames = data.schedules.map((schedule, index) => {
          console.log(`Schedule ${index} 원본:`, JSON.stringify(schedule, null, 2)); // 각 schedule 상세 로깅
          const game = {
            id: index, // 인덱스 ID (내부 식별용)
            gameId: schedule.gameId, // 원본 gameId 문자열 그대로 보존
            homeTeam: schedule.homeTeam,
            awayTeam: schedule.awayTeam,
            homeScore: 0,
            awayScore: 0,
            inning: 0,
            status: schedule.status,
            schedule
          };
          console.log(`Game ${index} 변환 결과:`, game); // 변환된 game 객체 로깅
          return game;
        });
        
        setGames(formattedGames);
        setTodayGames(formattedGames);
        setError(null);
      } catch (err) {
        console.error('API 요청 에러:', err);
        setError('경기 데이터를 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };
    
    fetchGamesData();
  }, []);

  // 현재 선택된 게임의 상태 로깅
  useEffect(() => {
    const currentGame = games[currentIndex];
    if (currentGame) {
      console.log('Current Game Status:', currentGame.status);
      console.log('Status Text:', getStatusText(currentGame.status));
      console.log('Status Color:', getStatusColor(currentGame.status));
    }
  }, [currentIndex, games]);

  // 캐러셀 제어 함수
  const handleNext = () => {
    if (games.length > 0) {
      setCurrentIndex((prevIndex) => (prevIndex + 1) % games.length);
    }
  };

  const handlePrev = () => {
    if (games.length > 0) {
      setCurrentIndex((prevIndex) => (prevIndex - 1 + games.length) % games.length);
    }
  };

  // 스와이프 제스처 바인딩
  const bind = useDrag(({ down, movement: [mx], direction: [xDir], velocity }) => {
    if (games.length === 0) return;
    
    const trigger = Math.abs(velocity[0]) > 0.2;
    const dir = xDir < 0 ? 1 : -1;
    if (!down && trigger) {
      dir > 0 ? handlePrev() : handleNext();
    }
  });

  // 상태에 따른 텍스트 및 색상 반환 함수
  const getStatusText = (status: string | undefined) => {
    switch(status) {
      case 'SCHEDULED':
        return '경기예정';
      case 'LIVE':
        return 'LIVE';
      case 'ENDED':
        return '경기종료';
      case 'CANCELED':
        return '경기취소';
      default:
        return '';
    }
  };

  const getStatusColor = (status: string | undefined) => {
    switch(status) {
      case 'LIVE':
        return 'text-red-600';
      case 'ENDED':
        return 'text-gray-500';
      case 'SCHEDULED':
        return 'text-blue-500';
      case 'CANCELED':
        return 'text-yellow-500';
      default:
        return 'text-gray-500';
    }
  };

  // 배경 그라데이션 스타일 계산
  const getBackgroundStyle = (homeTeam: string, awayTeam: string) => {
    const color1 = teamColors[homeTeam] || teamColors.default1;
    const color2 = teamColors[awayTeam] || teamColors.default2;
    
    return {
      background: `linear-gradient(135deg, 
        ${color1} 0%,
        ${color2} 35%,
        #ffffff 65%,
        #ffffff 100%)`
    };
  };

  // 카드 스타일 계산
  const getCardStyle = (index: number) => {
    const position = (index - currentIndex + games.length) % games.length;
    const translateX = position === 0 ? 0 : position === 1 ? 240 : position === games.length - 1 ? -240 : -1000;
    const scale = position === 0 ? 1 : 0.8;
    const opacity = position === 0 ? 1 : 0.5;
    const zIndex = position === 0 ? 2 : 1;

    return {
      x: translateX,
      scale,
      opacity,
      zIndex,
      transition: {
        type: "spring",
        stiffness: 300,
        damping: 30
      }
    };
  };

  // 현재 선택된 게임
  const currentGame = games[currentIndex];
  
  // 로딩 중 표시
  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center h-full" style={{
        background: 'black'
      }}>
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
          className="flex space-x-2"
        >
          {[0, 1, 2].map((i) => (
            <motion.div
              key={i}
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{
                duration: 0.3,
                delay: i * 0.2,
                repeat: Infinity,
                repeatType: "reverse"
              }}
              className="w-2 h-2 bg-white rounded-full"
            />
          ))}
        </motion.div>

        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.5 }}
          className="mt-4 text-white text-sm font-['Giants-Bold']"
        >
          경기 정보를 불러오는 중...
        </motion.p>
      </div>
    );
  }

  // 경기 데이터가 없는 경우 표시
  if (games.length === 0) {
    return (
      <div 
        className="relative h-full overflow-hidden"
        style={{
          background: 'linear-gradient(135deg, #2B2B2B 0%, #1A1A1A 100%)'
        }}
      >
        {/* 헤더는 유지 */}
        <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
          <button 
            onClick={() => navigate('/my')} 
            className="w-6 h-6 rounded-full overflow-hidden"
          >
            <img src={profileImg} alt="Profile" className="w-full h-full object-cover" />
          </button>
          <div className="w-5 h-5 flex items-center justify-center">
            <button 
              onClick={() => toggleSidebar(true)}
              className="w-5 h-5"
            >
              <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
            </button>
          </div>
        </div>

        {/* 경기 없음 메시지 */}
        <div className="h-full flex flex-col items-center justify-center px-6">
          <div className="text-center">
            <h2 className="text-2xl font-['Giants-Bold'] text-white mb-3">
              오늘은 경기가 없습니다
            </h2>
            <p className="text-gray-400 text-sm">
              다음 경기를 기다려주세요
            </p>
          </div>
        </div>

        <Sidebar />
      </div>
    );
  }

  // 오류 발생 시 표시
  if (error) {
    return (
      <div 
        className="relative h-full overflow-hidden"
        style={{
          background: 'linear-gradient(135deg, #2B2B2B 0%, #1A1A1A 100%)'
        }}
      >
        {/* 헤더는 유지 */}
        <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
          <button 
            onClick={() => navigate('/my')} 
            className="w-6 h-6 rounded-full overflow-hidden"
          >
            <img src={profileImg} alt="Profile" className="w-full h-full object-cover" />
          </button>
          <div className="w-5 h-5 flex items-center justify-center">
            <button 
              onClick={() => toggleSidebar(true)}
              className="w-5 h-5"
            >
              <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
            </button>
          </div>
        </div>

        {/* 에러 메시지 */}
        <div className="h-full flex flex-col items-center justify-center px-6">
          <div className="text-center">
            <h2 className="text-2xl font-['Giants-Bold'] text-white mb-3">
              {error}
            </h2>
            <button 
              onClick={() => window.location.reload()} 
              className="mt-4 px-6 py-2 bg-white/10 backdrop-blur-xl rounded-lg text-white hover:bg-white/20 transition-colors"
            >
              새로고침
            </button>
          </div>
        </div>

        <Sidebar />
      </div>
    );
  }
  
  return (
    <div 
      className="relative h-full overflow-hidden" 
      style={getBackgroundStyle(currentGame.homeTeam, currentGame.awayTeam)}
    >
      {/* 헤더 */}
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <button 
          onClick={() => navigate('/my')} 
          className="w-6 h-6 rounded-full overflow-hidden"
        >
          <img src={profileImg} alt="Profile" className="w-full h-full object-cover" />
        </button>
        <div className="w-5 h-5 flex items-center justify-center">
          <button 
            onClick={() => toggleSidebar(true)}
            className="w-5 h-5"
          >
            <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
          </button>
        </div>
      </div>

      {/* 매치업 정보 */}
      <div className="absolute top-16 left-0 right-0 z-10 text-center">
        <div className={`text-sm font-bold font-['Giants-Bold'] ${getStatusColor(currentGame?.status)}`}>
          {getStatusText(currentGame?.status)}
        </div>
        <h1 className="text-3xl font-['Giants-Bold'] mt-1 text-white">
          {currentGame ? `${currentGame.homeTeam} vs ${currentGame.awayTeam}` : ''}
        </h1>
        <div className="mt-2">
          <div className="text-xl font-['Giants-Bold'] text-white">
            {currentGame ? `${currentGame.homeScore} : ${currentGame.awayScore}` : ''}
          </div>
          <div className="text-xs mt-0.5 text-gray-200">
            {currentGame ? `${currentGame.schedule.stadium} | ${currentGame.schedule.startTime}` : ''}
          </div>
        </div>
      </div>

      {/* 캐러셀 */}
      <div className="relative h-full flex items-center justify-center translate-y-[10%]">
        {games.map((game, index) => (
          <motion.div
            key={game.id}
            className="absolute w-[80%] h-[65%] rounded-2xl"
            animate={getCardStyle(index)}
            drag="x"
            dragConstraints={{ left: 0, right: 0 }}
            dragElastic={1}
            onDragEnd={(event, info) => {
              const swipe = Math.abs(info.offset.x) * info.velocity.x;
              const threshold = 10000;
              if (swipe < -threshold) {
                handleNext();
              } else if (swipe > threshold) {
                handlePrev();
              }
            }}
          >
            <div className="w-full h-full rounded-2xl bg-white/10 backdrop-blur-xl 
              border border-white/20 shadow-lg">
            </div>
          </motion.div>
        ))}
      </div>

      {/* 입장하기 버튼 */}
      <div className="absolute bottom-12 left-0 right-0 flex justify-center px-12 z-10">
        <button
          onClick={() => {
            // currentGame을 useStore에 저장하고 페이지 이동
            useStore.setState({ currentGame });
            navigate('/main');
          }}
          className="w-full h-12 bg-black rounded-[20px] text-white text-lg font-medium"
        >
          입장하기
        </button>
      </div>

      <Sidebar />
    </div>
  );
};

export default Home;