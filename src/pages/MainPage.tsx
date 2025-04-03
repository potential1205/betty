import React from 'react';
import { motion } from 'framer-motion';
import Sidebar from '../components/Sidebar';
import walletImg from '../assets/wallet.png';
import hamburgerImg from '../assets/hamburger.png';
import { colors } from '../constants/colors';
import { useStore } from '../stores/useStore';
import { useNavigate } from 'react-router-dom';

const MainPage: React.FC = () => {
  const { currentIndex, games, handleNext, handlePrev, toggleSidebar } = useStore();
  const navigate = useNavigate();
  const currentGame = games[currentIndex];

  const teamNames = Object.keys(currentGame).filter(key => 
    key !== 'inning' && key !== 'status' && key !== 'id'
  );

  const getBackgroundStyle = () => {
    const teamA = teamNames[0] as keyof typeof colors;
    const teamB = teamNames[1] as keyof typeof colors;
    return {
      background: `linear-gradient(135deg, 
        ${colors[teamA]} 0%,
        ${colors[teamB]} 35%,
        #ffffff 65%,
        #ffffff 100%)`
    };
  };

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

  return (
    <div className="relative h-full overflow-hidden" style={getBackgroundStyle()}>
      {/* 헤더 */}
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <button onClick={() => navigate('/my')} className="w-8 h-7">
          <img src={walletImg} alt="wallet" className="w-full h-full" />
        </button>
        <button onClick={() => toggleSidebar(true)} className="w-5 h-5">
          <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
        </button>
      </div>

      {/* 매치업 정보 */}
      <div className="absolute top-13 left-0 right-0 z-10 text-center">
        <div className="text-red-600 text-sm font-['Giants-Bold']">LIVE</div>
        <h1 className="text-4xl font-['Giants-Bold'] text-white mt-1">
          {`${teamNames[0]} vs ${teamNames[1]}`}
        </h1>
        <div className="mt-2 text-white">
          <span className="text-xl font-['Giants-Bold']">
            {currentGame[teamNames[0]]} : {currentGame[teamNames[1]]}
          </span>
          <div className="text-xs mt-0.5 text-gray-300">
            {currentGame.inning}회 {currentGame.status}
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
              border border-white/20 shadow-lg p-6">
              {/* 카드 내용 필요 시 여기에 추가 */}
            </div>
          </motion.div>
        ))}
      </div>

      {/* 입장 버튼 */}
      <div className="absolute bottom-12 left-0 right-0 flex justify-center px-12 z-10">
        <button
          onClick={() => navigate('/main')}
          className="w-full h-12 bg-black rounded-[20px] text-white text-lg font-['Pretendard-Regular']"
        >
          입장하기
        </button>
      </div>

      <Sidebar />
    </div>
  );
};

export default MainPage;
