import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../stores/useStore';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import pencilImg from '../assets/pencil.png';
import Sidebar from '../components/Sidebar';
import ColorPicker from '../components/ColorPicker';
import QuizModal, { QuizHistoryItem } from '../components/QuizModal';
import { Quiz, quizData } from '../constants/dummy';

type Tab = 'LIVE-PICK' | 'WINNER' | 'PIXEL';
type Color = string;

const MainPage: React.FC = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<Tab>('LIVE-PICK');
  const [isPaletteOpen, setIsPaletteOpen] = useState(false);
  const [selectedColor, setSelectedColor] = useState<Color>('#FFFFFF');
  const { currentIndex, games, toggleSidebar } = useStore();
  const currentGame = games[currentIndex];
  const teamNames = Object.keys(currentGame).filter(key => 
    key !== 'inning' && key !== 'status' && key !== 'id'
  );

  const colors: Color[] = [
    '#FFFFFF', '#000000', '#FF0000', '#00FF00', '#0000FF',
    '#FFFF00', '#FF00FF', '#00FFFF', '#808080', '#FFA500'
  ];

  // 퀴즈 관련 상태 수정
  const [activeQuiz, setActiveQuiz] = useState<Quiz | null>(null);
  const [quizHistory, setQuizHistory] = useState<QuizHistory[]>([]);
  const [currentTime, setCurrentTime] = useState(10);

  // 현재 경기 중인 팀들의 퀴즈만 필터링하는 부분 제거
  const availableQuizzes = quizData;

  // 컴포넌트 마운트시 랜덤 퀴즈 설정
  useEffect(() => {
    if (availableQuizzes.length > 0) {
      const randomIndex = Math.floor(Math.random() * availableQuizzes.length);
      setActiveQuiz(availableQuizzes[randomIndex]);
    }
  }, []);

  // 현재 선택한 답변을 저장하는 상태 추가
  const [currentAnswer, setCurrentAnswer] = useState<number | null>(null);

  // 퀴즈 답변 제출 핸들러 수정
  const handleQuizAnswer = (answer: number) => {
    if (!activeQuiz) return;
    setCurrentAnswer(answer);
  };

  // 타임아웃 처리 함수 수정
  const handleQuizTimeout = () => {
    if (!activeQuiz) return;

    const newHistory: QuizHistory = {
      quizId: activeQuiz.id,
      userAnswer: currentAnswer,
      answeredAt: new Date().toISOString(),
      totalVotes: 1,
      optionVotes: activeQuiz.options.map((_, index) => 
        index === currentAnswer ? 1 : 0
      ),
    };

    setQuizHistory(prev => [newHistory, ...prev]);
    setCurrentAnswer(null);
    
    // 새로운 퀴즈 설정
    const remainingQuizzes = availableQuizzes.filter(q => q.id !== activeQuiz.id);
    if (remainingQuizzes.length > 0) {
      const randomIndex = Math.floor(Math.random() * remainingQuizzes.length);
      setActiveQuiz(remainingQuizzes[randomIndex]);
      setCurrentTime(10);
    } else {
      setActiveQuiz(null);
    }
  };

  // useEffect에서 타이머 처리
  useEffect(() => {
    if (currentTime <= 0) {
      handleQuizTimeout();
    }
    
    const timer = currentTime > 0 && setInterval(() => {
      setCurrentTime(current => current - 1);
    }, 1000);

    return () => {
      if (timer) clearInterval(timer);
    };
  }, [currentTime]);

  // 팀 이름 포맷팅 함수
  const formatTeamName = (team: string) => {
    const teamNames: { [key: string]: string } = {
      'bears': '두산',
      'giants': '롯데',
      'heroes': '키움',
      'tigers': 'KIA',
      'twins': 'LG',
      'eagles': '한화',
      'landers': 'SSG',
      'lions': '삼성',
      'dinos': 'NC',
      'wiz': 'KT'
    };
    return teamNames[team] || team;
  };

  return (
    <div className="relative h-full bg-black text-white overflow-hidden">
      {/* 헤더 */}
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <button 
          onClick={() => navigate(-1)} 
          className="w-[12px] h-[12px]"
        >
          <img src={backImg} alt="Back" className="w-full h-full" />
        </button>
        <div className="w-5 h-5 flex items-center justify-center">
          {activeTab === 'PIXEL' ? (
            <button 
              onClick={() => setIsPaletteOpen(true)}
              className="w-[14px] h-[14px]"
            >
              <img src={pencilImg} alt="Pencil" className="w-full h-full" />
            </button>
          ) : (
            <button 
              onClick={() => toggleSidebar(true)}
              className="w-5 h-5"
            >
              <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
            </button>
          )}
        </div>
      </div>

      {/* ColorPicker 컴포넌트 */}
      <ColorPicker 
        isOpen={isPaletteOpen}
        onClose={() => setIsPaletteOpen(false)}
        onSelectColor={setSelectedColor}
        selectedColor={selectedColor}
      />

      {/* 상단 매치업 타이틀 */}
      <div className="pt-16 pb-4 text-center">
        <div className="text-red-600 text-sm font-['Giants-Bold']">LIVE</div>
        <h1 className="text-4xl font-['Giants-Bold'] mt-1">
          {`${teamNames[0]} vs ${teamNames[1]}`}
        </h1>
        <div className="mt-2">
          <div className="text-xl font-['Giants-Bold']">
            {currentGame[teamNames[0]]} : {currentGame[teamNames[1]]}
          </div>
          <div className="text-xs text-gray-400 mt-0.5">
            {currentGame.inning}회 {currentGame.status}
          </div>
        </div>
      </div>

      {/* 토글 탭 */}
      <div className="px-6">
        <div className="flex rounded-full bg-gray-800/50 p-1">
          <button
            onClick={() => setActiveTab('LIVE-PICK')}
            className={`flex-1 py-3 rounded-full text-sm font-['Pretendard-Regular'] transition-colors
              ${activeTab === 'LIVE-PICK' ? 'bg-white text-black' : 'text-white'}`}
          >
            LIVE-PICK
          </button>
          <button
            onClick={() => setActiveTab('WINNER')}
            className={`flex-1 py-3 rounded-full text-sm font-['Pretendard-Regular'] transition-colors
              ${activeTab === 'WINNER' ? 'bg-white text-black' : 'text-white'}`}
          >
            WINNER
          </button>
          <button
            onClick={() => setActiveTab('PIXEL')}
            className={`flex-1 py-3 rounded-full text-sm font-['Pretendard-Regular'] transition-colors
              ${activeTab === 'PIXEL' ? 'bg-white text-black' : 'text-white'}`}
          >
            PIXEL
          </button>
        </div>
      </div>

      {/* 컨텐츠 영역 */}
      <div className="mt-6 px-6 overflow-y-auto h-[calc(100vh-280px)] [&::-webkit-scrollbar]:hidden [-ms-overflow-style:'none'] [scrollbar-width:'none']">
        {activeTab === 'LIVE-PICK' && (
          <div className="space-y-4 pb-6"> {/* 하단 여백 추가 */}
            {/* 활성화된 퀴즈 */}
            {activeQuiz && (
              <div className="mb-6">
                <h2 className="text-lg font-bold mb-3">현재 진행중인 퀴즈</h2>
                <QuizModal
                  quiz={activeQuiz}
                  isActive={true}
                  onAnswer={handleQuizAnswer}
                  currentTime={currentTime}
                  currentAnswer={currentAnswer}
                />
              </div>
            )}

            {/* 퀴즈 히스토리 */}
            {quizHistory.length > 0 && (
              <div>
                <h2 className="text-lg font-bold mb-3">이전 퀴즈 결과</h2>
                {quizHistory.map((history) => {
                  const quiz = quizData.find(q => q.id === history.quizId);
                  if (!quiz) return null;
                  return (
                    <QuizHistoryItem
                      key={history.quizId}
                      quiz={quiz}
                      history={history}
                    />
                  );
                })}
              </div>
            )}
          </div>
        )}
        {activeTab === 'WINNER' && (
          <div>
            {/* WINNER 컨텐츠 */}
          </div>
        )}
        {activeTab === 'PIXEL' && (
          <div>
            {/* PIXEL 컨텐츠 */}
            <div className="text-sm text-gray-400 mb-2">
              선택된 색상: <span className="inline-block w-4 h-4 rounded-full align-middle ml-2" style={{ backgroundColor: selectedColor }} />
            </div>
            {/* 여기에 픽셀 그리기 캔버스 추가 예정 */}
          </div>
        )}
      </div>

      {/* Sidebar - PIXEL 탭이 아닐 때만 표시 */}
      {activeTab !== 'PIXEL' && <Sidebar />}
    </div>
  );
};

export default MainPage;
