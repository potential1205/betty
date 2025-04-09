import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useStore } from '../stores/useStore';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import Sidebar from '../components/Sidebar';
import { ColorPicker } from '../components/ColorPicker';
import CollaborativeCanvas from '../components/CollaborativeCanvas';
import { Winner } from '../components/Winner';
import { Game } from '../stores/useStore';
import QuizModal from '../components/QuizModal';
import axiosInstance from '../apis/axios';
import { getGameDetail } from '../apis/gameApi';

type Tab = 'LIVE-PICK' | 'WINNER' | 'DISPLAY';

interface Problem {
  problemId: string;
  gameId: string;
  inning: string;
  attackTeam: string;
  batterName: string;
  batterNumber: string;
  questionCode: string;
  description: string;
  options: string[];
  answer: string | null;
  timestamp: number;
  push: boolean;
}

interface ProblemHistory {
  problemId: string;
  gameId: string;
  inning: string;
  attackTeam: string;
  batterName: string;
  questionCode: string;
  description: string;
  options: string[];
  answer: string | null;
  timestamp: number;
  push: boolean;
  userAnswer: string | null;
}

const MainPage: React.FC = () => {
  const navigate = useNavigate();
  const { gameId } = useParams<{ gameId: string }>();
  const [activeTab, setActiveTab] = useState<Tab>('LIVE-PICK');
  const [displayTeam, setDisplayTeam] = useState<'home' | 'away'>('home');
  const { currentGame, toggleSidebar, setCurrentGame } = useStore();
  const [isLive, setIsLive] = useState(true);
  const [problemData, setProblemData] = useState<Problem | null>(null);
  const [isActive, setIsActive] = useState(false);
  const [selectedAnswer, setSelectedAnswer] = useState<string | null>(null);
  const [timeLeft, setTimeLeft] = useState(0);
  const [problemHistory, setProblemHistory] = useState<ProblemHistory[]>([]);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [pendingAnswer, setPendingAnswer] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // 게임 정보 로드
  useEffect(() => {
    const loadGameData = async () => {
      try {
        // URL 파라미터에서 gameId가 있으면 사용, 아니면 로컬스토리지에서 확인
        const targetGameId = gameId || localStorage.getItem('currentGameId');
        
        if (!targetGameId) {
          // 게임 ID가 없는 경우 홈으로 리다이렉트
          navigate('/home');
          return;
        }
        
        // 이미 currentGame이 있고 ID가 일치하면 로드하지 않음
        if (currentGame && currentGame.gameId.toString() === targetGameId) {
          setIsLoading(false);
          return;
        }
        
        // localStorage에서 게임 정보를 확인
        const savedGame = localStorage.getItem('currentGame');
        if (savedGame) {
          try {
            const parsedGame = JSON.parse(savedGame);
            if (parsedGame && parsedGame.gameId.toString() === targetGameId) {
              setCurrentGame(parsedGame);
              
              // API를 통해 최신 정보도 가져오기 시도
              try {
                const numericGameId = Number(targetGameId);
                const gameData = await getGameDetail(numericGameId);
                console.log('API에서 가져온 게임 데이터:', gameData);
                
                // 형식 변환 - API 응답 형식을 useStore의 Game 형식에 맞게 변환
                const formattedGame: Game = {
                  id: gameData.id,
                  gameId: gameData.id,
                  homeTeamId: gameData.homeTeam.id,
                  awayTeamId: gameData.awayTeam.id,
                  homeTeam: gameData.homeTeam.teamName,
                  awayTeam: gameData.awayTeam.teamName,
                  homeScore: gameData.homeScore || 0,
                  awayScore: gameData.awayScore || 0,
                  status: gameData.status,
                  inning: gameData.inning || 0,
                  schedule: {
                    gameId: gameData.id,
                    homeTeamId: gameData.homeTeam.id,
                    awayTeamId: gameData.awayTeam.id,
                    season: gameData.season,
                    gameDate: gameData.gameDate,
                    startTime: `${gameData.startTime.hour}:${gameData.startTime.minute.toString().padStart(2, '0')}`,
                    stadium: gameData.stadium,
                    homeTeamName: gameData.homeTeam.teamName,
                    awayTeamName: gameData.awayTeam.teamName,
                    status: gameData.status
                  },
                  homeTeamCode: gameData.homeTeam.teamCode,
                  awayTeamCode: gameData.awayTeam.teamCode,
                  homeTeamName: gameData.homeTeam.teamName,
                  awayTeamName: gameData.awayTeam.teamName
                };
                
                setCurrentGame(formattedGame);
                localStorage.setItem('currentGame', JSON.stringify(formattedGame));
              } catch (apiError) {
                console.error('API에서 최신 게임 정보 가져오기 실패:', apiError);
                // API 호출 실패해도 localStorage의 데이터는 이미 설정됨
              }
              
              setIsLoading(false);
              return;
            }
          } catch (e) {
            console.error('저장된 게임 정보 파싱 오류:', e);
            // 파싱 오류 시 API 호출 시도
          }
        }
        
        // localStorage에 정보가 없거나 파싱 오류 발생 시 API 호출
        try {
          const numericGameId = Number(targetGameId);
          const gameData = await getGameDetail(numericGameId);
          console.log('API에서 가져온 게임 데이터:', gameData);
          
          // 형식 변환 - API 응답 형식을 useStore의 Game 형식에 맞게 변환
          const formattedGame: Game = {
            id: gameData.id,
            gameId: gameData.id,
            homeTeamId: gameData.homeTeam.id,
            awayTeamId: gameData.awayTeam.id,
            homeTeam: gameData.homeTeam.teamName,
            awayTeam: gameData.awayTeam.teamName,
            homeScore: gameData.homeScore || 0,
            awayScore: gameData.awayScore || 0,
            status: gameData.status,
            inning: gameData.inning || 0,
            schedule: {
              gameId: gameData.id,
              homeTeamId: gameData.homeTeam.id,
              awayTeamId: gameData.awayTeam.id,
              season: gameData.season,
              gameDate: gameData.gameDate,
              startTime: `${gameData.startTime.hour}:${gameData.startTime.minute.toString().padStart(2, '0')}`,
              stadium: gameData.stadium,
              homeTeamName: gameData.homeTeam.teamName,
              awayTeamName: gameData.awayTeam.teamName,
              status: gameData.status
            },
            homeTeamCode: gameData.homeTeam.teamCode,
            awayTeamCode: gameData.awayTeam.teamCode,
            homeTeamName: gameData.homeTeam.teamName,
            awayTeamName: gameData.awayTeam.teamName
          };
          
          setCurrentGame(formattedGame);
          localStorage.setItem('currentGameId', targetGameId);
          localStorage.setItem('currentGame', JSON.stringify(formattedGame));
          setIsLoading(false);
          return;
        } catch (apiError) {
          console.error('API에서 게임 정보 가져오기 실패:', apiError);
          // API 호출 실패 시 홈으로 리다이렉트
          navigate('/home');
        }
      } catch (error) {
        console.error('게임 정보 로드 실패:', error);
        navigate('/home');
      } finally {
        setIsLoading(false);
      }
    };
    
    loadGameData();
  }, [gameId, navigate, setCurrentGame, currentGame]);

  // SSE 연결 및 문제 수신
  useEffect(() => {
    if (!currentGame || currentGame.status !== 'LIVE' || isLoading) return;

    // 웹소켓으로 변경 예정 - SSE 코드 제거됨
    console.log("[리얼타임 연결] 웹소켓으로 변경 예정. 현재 연결 비활성화됨.");
    
    // 임시 디버깅 데이터
    console.log(`게임 정보: ${currentGame.homeTeam} vs ${currentGame.awayTeam}, ID: ${currentGame.gameId}`);
    
    // 실시간 데이터 처리 함수 - 향후 웹소켓에서 사용할 예정
    const handleRealTimeData = (data: any) => {
      console.log("[리얼타임] 데이터 수신 처리기 호출됨 (현재 비활성화)", data);
      
      // 타입별 처리 로직 (주석 처리)
      /*
      switch (data.type) {
        case 'PROBLEM':
          setProblemData(data.problem);
          setIsActive(true);
          setTimeLeft(10);
          break;
        case 'SCORE':
          setCurrentGame({
            ...currentGame!,
            homeScore: data.homeScore,
            awayScore: data.awayScore
          });
          break;
        case 'STATUS':
          setCurrentGame({
            ...currentGame!,
            status: data.status
          });
          setIsLive(data.status === 'LIVE');
          break;
        case 'END':
          if (data.isGameEnd) {
            setCurrentGame({
              ...currentGame!,
              status: 'ENDED'
            });
            setIsLive(false);
          } else {
            setIsActive(false);
            if (problemData) {
              const newHistory = {
                ...problemData,
                userAnswer: selectedAnswer
              };
              setProblemHistory(prev => [...prev, newHistory]);
            }
            setProblemData(null);
            setSelectedAnswer(null);
          }
          break;
      }
      */
    };
    
    return () => {
      console.log("[리얼타임 연결] 정리 함수 호출됨 (현재 연결 없음)");
    };
  }, [currentGame, problemData, selectedAnswer, isLoading]);

  // 디버깅용 - 상태 변경 시 로깅
  useEffect(() => {
    console.log("[상태 디버깅] problemData 변경:", problemData);
  }, [problemData]);

  useEffect(() => {
    console.log("[상태 디버깅] isActive 변경:", isActive);
  }, [isActive]);

  useEffect(() => {
    console.log("[상태 디버깅] problemHistory 변경:", problemHistory);
  }, [problemHistory]);

  // 타이머 카운트다운
  useEffect(() => {
    if (!isActive || timeLeft <= 0) return;

    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [isActive, timeLeft]);

  const getStatusText = () => {
    if (!currentGame?.status) return '';
    switch(currentGame.status) {
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

  const handleAnswer = async (answer: string) => {
    setPendingAnswer(answer);
    setShowConfirmModal(true);
  };

  const confirmAnswer = async () => {
    if (!currentGame || !problemData || !pendingAnswer) return;
    
    try {
      const response = await axiosInstance.post(`/home/games/${currentGame.gameId}/votes/live`, {
        gameId: currentGame.gameId,
        selectedAnswer: pendingAnswer,
        problemId: problemData.problemId
      });

      if (response.status === 200) {
        setSelectedAnswer(pendingAnswer);
        setShowConfirmModal(false);
        setPendingAnswer(null);
      }
    } catch (error) {
      console.error('답변 제출 실패:', error);
      alert('답변 제출에 실패했습니다. 다시 시도해주세요.');
    }
  };

  // 로딩 중인 경우 로딩 화면 표시
  if (isLoading) {
    return (
      <div className="relative h-full bg-black text-white flex items-center justify-center">
        <div className="text-center">
          <div className="text-xl font-['Giants-Bold'] mb-2">
            게임 정보 로딩 중...
          </div>
        </div>
      </div>
    );
  }
  
  // 게임 정보가 없는 경우도 처리
  if (!currentGame) {
    return (
      <div className="relative h-full bg-black text-white flex items-center justify-center">
        <div className="text-center">
          <div className="text-xl font-['Giants-Bold'] mb-2">
            게임 정보를 찾을 수 없습니다
          </div>
          <div className="text-sm text-gray-400 mt-2 mb-4">
            게임을 선택하거나 다시 시도해주세요
          </div>
          <div className="flex gap-3 justify-center">
            <button 
              onClick={() => navigate('/home')}
              className="px-4 py-2 bg-white text-black rounded-full text-sm"
            >
              홈으로
            </button>
            <button 
              onClick={() => window.location.reload()}
              className="px-4 py-2 bg-gray-800 text-white rounded-full text-sm"
            >
              새로고침
            </button>
          </div>
        </div>
      </div>
    );
  }

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
          <button 
            onClick={() => toggleSidebar(true)}
            className="w-5 h-5"
          >
            <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
          </button>
        </div>
      </div>

      {/* 상단 매치업 타이틀 */}
      <div className="pt-16 pb-4 text-center">
        <div className="text-red-600 text-sm font-['Giants-Bold']">
          {getStatusText()}
        </div>
        <h1 className="text-4xl font-['Giants-Bold'] mt-1">
          {`${currentGame.homeTeam} vs ${currentGame.awayTeam}`}
        </h1>
        <div className="mt-2">
          <div className="text-xl font-['Giants-Bold']">
            {`${currentGame.homeScore} : ${currentGame.awayScore}`}
          </div>
          <div className="text-xs text-gray-400 mt-0.5">
            {`${currentGame.schedule.stadium} | ${currentGame.schedule.startTime}`}
          </div>
        </div>
      </div>

      {/* 토글 탭 */}
      <div className="px-6">
        <div className="flex rounded-full bg-gray-800/50 p-1">
          {(['LIVE-PICK', 'WINNER', 'DISPLAY'] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`flex-1 py-3 rounded-full text-sm font-['Pretendard-Regular'] transition-all duration-200
                ${activeTab === tab ? 'bg-white text-black' : 'text-white hover:bg-gray-700/50'}`}
            >
              {tab}
            </button>
          ))}
        </div>
      </div>

      {/* DISPLAY 탭일 때만 보이는 팀 선택 토글 */}
      {activeTab === 'DISPLAY' && currentGame?.status === 'LIVE' && (
        <div className="px-6 mt-4">
          <div className="flex rounded-full bg-gray-800/50 p-1">
            <button
              onClick={() => setDisplayTeam('home')}
              className={`flex-1 py-2 rounded-full text-xs font-['Giants-Bold'] transition-all duration-200
                ${displayTeam === 'home' ? 'bg-white text-black' : 'text-white hover:bg-gray-700/50'}`}
            >
              {currentGame.homeTeam}
            </button>
            <button
              onClick={() => setDisplayTeam('away')}
              className={`flex-1 py-2 rounded-full text-xs font-['Giants-Bold'] transition-all duration-200
                ${displayTeam === 'away' ? 'bg-white text-black' : 'text-white hover:bg-gray-700/50'}`}
            >
              {currentGame.awayTeam}
            </button>
          </div>
        </div>
      )}

      {/* 컨텐츠 영역 */}
      <div className="mt-6 px-6 overflow-y-auto h-[calc(100vh-320px)] [&::-webkit-scrollbar]:hidden [-ms-overflow-style:'none'] [scrollbar-width:'none']">
        {activeTab === 'LIVE-PICK' && (
          <div className="flex flex-col h-full">
            {currentGame?.status !== 'LIVE' ? (
              <div className="flex-1 flex items-center justify-center">
                <div className="text-gray-400 text-center">
                  <div className="text-xl font-['Giants-Bold'] mb-2">
                    {currentGame?.status === 'SCHEDULED' ? '경기 시작 전입니다' : 
                     currentGame?.status === 'ENDED' ? '경기가 종료되었습니다' :
                     '경기가 취소되었습니다'}
                  </div>
                  <div className="text-xs font-['Pretendard-Regular']">
                    {currentGame?.status === 'SCHEDULED' ? '경기 시작 후 다시 확인해주세요' : 
                     '다음 경기를 기대해주세요'}
                  </div>
                </div>
              </div>
            ) : (
              <>
                {/* 현재 문제 */}
                {problemData && (
                  <QuizModal
                    problem={problemData}
                    isActive={isActive}
                    onAnswer={handleAnswer}
                    currentTime={timeLeft}
                    currentAnswer={selectedAnswer}
                  />
                )}

                {/* 문제 히스토리 */}
                <div className="space-y-3">
                  {problemHistory.map((history, index) => (
                    <div key={history.problemId} className="backdrop-blur-md bg-black/30 rounded-xl p-3 border border-white/10">
                      <div className="flex items-center justify-between mb-1">
                        <div className="flex items-center gap-2">
                          <span className="text-xs text-gray-300 bg-white/10 px-2 py-0.5 rounded-full">
                            {history.inning}
                          </span>
                          <span className="text-xs text-gray-300">
                            {history.attackTeam}
                          </span>
                        </div>
                        <span className={`text-xs px-2 py-0.5 rounded-full ${
                          history.userAnswer 
                            ? 'bg-green-500/20 text-green-300' 
                            : 'bg-red-500/20 text-red-300'
                        }`}>
                          {history.userAnswer || '시간 초과'}
                        </span>
                      </div>
                      <div className="text-sm font-['Pretendard-Regular'] text-gray-300 mb-1">
                        {history.batterName} 타석
                      </div>
                      <div className="text-sm font-['Pretendard-Regular'] text-white">
                        {history.description}
                      </div>
                    </div>
                  ))}
                </div>
              </>
            )}
          </div>
        )}
        {activeTab === 'DISPLAY' && (
          <div className="flex flex-col h-full">
            {currentGame?.status !== 'LIVE' ? (
              <div className="flex-1 flex items-center justify-center">
                <div className="text-gray-400 text-center">
                  <div className="text-xl font-['Giants-Bold'] mb-2">
                    {currentGame?.status === 'SCHEDULED' ? '경기 시작 전입니다' : 
                     currentGame?.status === 'ENDED' ? '경기가 종료되었습니다' :
                     '경기가 취소되었습니다'}
                  </div>
                  <div className="text-xs font-['Pretendard-Regular']">
                    {currentGame?.status === 'SCHEDULED' ? '경기 시작 후 다시 확인해주세요' : 
                     '다음 경기를 기대해주세요'}
                  </div>
                </div>
              </div>
            ) : (
              <div className="flex flex-col h-full">
                <div className="w-full flex items-center mb-4">
                  <div className="flex items-center gap-2">
                    <span className="text-sm text-gray-400">선택된 색상:</span>
                    <ColorPicker />
                  </div>
                </div>
                <div className="w-full">
                  <CollaborativeCanvas team={displayTeam} />
                </div>
              </div>
            )}
          </div>
        )}
        {activeTab === 'WINNER' && (
          <Winner
            homeTeam={currentGame.homeTeam}
            awayTeam={currentGame.awayTeam}/>
        )}
      </div>

      {/* 답변 확인 모달 */}
      {showConfirmModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl p-6 w-[320px]">
            <div className="text-center mb-4">
              <div className="text-lg font-['Giants-Bold'] text-gray-800 mb-2">
                답변을 제출하시겠습니까?
              </div>
              <div className="text-sm text-gray-600">
                {pendingAnswer}
              </div>
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => {
                  setShowConfirmModal(false);
                  setPendingAnswer(null);
                }}
                className="flex-1 py-3 rounded-lg bg-gray-100 text-gray-800 text-sm font-['Pretendard-Regular']"
              >
                취소
              </button>
              <button
                onClick={confirmAnswer}
                className="flex-1 py-3 rounded-lg bg-black text-white text-sm font-['Pretendard-Regular']"
              >
                확인
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Sidebar */}
      <Sidebar />
    </div>
  );
};

export default MainPage;
