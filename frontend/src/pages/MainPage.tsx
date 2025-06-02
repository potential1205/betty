import React, { useState, useEffect, useRef } from 'react';
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
import { GameState, Problem as WebSocketProblem } from '../services/LiveVotingWebsocketService';
import { useLiveSocketStore } from '../stores/liveSocketStore';

type Tab = 'LIVE-PICK' | 'WINNER' | 'DISPLAY';

interface ProblemHistory extends WebSocketProblem {
  userAnswer: string | null;
}

const MainPage: React.FC = () => {
  const navigate = useNavigate();
  const { gameId } = useParams<{ gameId: string }>();
  const [activeTab, setActiveTab] = useState<Tab>('LIVE-PICK');
  const [displayTeam, setDisplayTeam] = useState<'home' | 'away'>('home');
  const { currentGame, toggleSidebar, setCurrentGame } = useStore();
  const [isLive, setIsLive] = useState(true);
  const [isLoading, setIsLoading] = useState(true);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [pendingAnswer, setPendingAnswer] = useState<string | null>(null);
  
  // liveSocketStore에서 상태와 액션을 가져옴
  const {
    connect,
    disconnect,
    submitAnswer,
    setCallbacks,
    currentProblem,
    problemHistory,
    isActive,
    timeLeft,
    selectedAnswer,
    setSelectedAnswer: setSocketAnswer,
    addToHistory,
    isDataLoading,
    resetState
  } = useLiveSocketStore();
  
  // 게임 상태 업데이트 콜백 함수
  const handleGameUpdate = (gameState: GameState) => {
    console.log("[MainPage] 게임 상태 업데이트:", JSON.stringify(gameState, null, 2));
    
    if (currentGame) {
      const updatedGame = {
        ...currentGame,
        inning: gameState.inning,
        homeScore: gameState.score.homeScore,
        awayScore: gameState.score.awayScore,
        status: gameState.status
      };
      
      console.log("[MainPage] 게임 정보 업데이트:", {
        이전점수: `${currentGame.homeScore}:${currentGame.awayScore}`,
        새점수: `${updatedGame.homeScore}:${updatedGame.awayScore}`,
        이전이닝: currentGame.inning,
        새이닝: updatedGame.inning,
        상태: updatedGame.status
      });
      
      setCurrentGame(updatedGame);
    }
  };
  
  // 문제 수신 콜백 함수
  const handleProblemReceived = (problem: WebSocketProblem) => {
    console.log("[MainPage] 문제 수신:", JSON.stringify(problem, null, 2));
    
    // 디버깅: 문제 세부 정보
    console.log(`[MainPage] 문제 세부 정보:
      ID: ${problem.problemId}
      게임ID: ${problem.gameId}
      이닝: ${problem.inning}
      공격팀: ${problem.attackTeam}
      타자: ${problem.batterName}
      선택지 수: ${problem.options.length}
      답변: ${problem.answer || '미공개'}
    `);
    
    console.log("[MainPage] 새 문제 활성화: 타이머 시작 (15초)");
  };
  
  // 웹소켓 콜백 설정
  useEffect(() => {
    setCallbacks({
      onConnect: () => {
        console.log("[MainPage] 라이브 투표 웹소켓 연결 성공");
      },
      onDisconnect: () => {
        console.log("[MainPage] 라이브 투표 웹소켓 연결 종료");
      },
      onGameUpdate: handleGameUpdate,
      onProblemReceived: handleProblemReceived
    });
  }, [currentGame]);
  
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

  // 웹소켓 연결 - 게임 ID와 게임 상태에 따라 연결 관리
  useEffect(() => {
    if (!currentGame || isLoading) return;
    
    // 게임이 라이브 상태가 아니면 연결 해제
    if (currentGame.status !== 'LIVE') {
      console.log("[MainPage] 게임이 LIVE 상태가 아님 - 연결 필요 없음");
      disconnect();
      return;
    }
    
    // 게임이 라이브 상태이면 연결 시도
    console.log(`[MainPage] 게임 ID ${currentGame.gameId}에 웹소켓 연결 시도...`);
    connect(currentGame.gameId);
    
    // 컴포넌트 언마운트 시 연결 해제 및 상태 초기화
    return () => {
      console.log("[MainPage] 페이지 이동으로 웹소켓 연결 종료 및 상태 초기화");
      disconnect();
      resetState();
    };
  }, [currentGame?.gameId, currentGame?.status, isLoading]);

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
    if (!currentGame || !currentProblem || !pendingAnswer) return;
    
    console.log("[MainPage] 답변 제출 시도:", {
      문제ID: currentProblem.problemId,
      게임ID: currentGame.gameId,
      선택답변: pendingAnswer
    });
    
    try {
      // 이미 답변했거나 시간이 초과된 경우 처리
      if (!isActive || selectedAnswer !== null) {
        console.log('[MainPage] 이미 답변했거나 시간이 초과되었습니다.');
        setShowConfirmModal(false);
        setPendingAnswer(null);
        return;
      }
      
      // 웹소켓으로 답변 제출 (내부적으로 REST API 폴백 포함)
      const success = await submitAnswer(
        currentGame.gameId,
        currentProblem.problemId,
        pendingAnswer
      );
      
      if (success) {
        console.log("[MainPage] 답변 제출 성공");
        // 히스토리에 추가
        addToHistory(currentProblem, pendingAnswer);
        setShowConfirmModal(false);
        setPendingAnswer(null);
      } else {
        console.error("[MainPage] 답변 제출 실패");
        alert('답변 제출에 실패했습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('답변 제출 실패:', error);
      alert('답변 제출에 실패했습니다. 다시 시도해주세요.');
    } finally {
      setShowConfirmModal(false);
      setPendingAnswer(null);
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
    <div className="relative h-full bg-black text-white overflow-hidden flex flex-col">
      {/* 헤더 */}
      <div className="z-10 flex justify-between items-center p-6 px-8">
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
      <div className="pb-4 text-center">
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
          <div className="text-xs text-gray-400 mt-0.5 flex items-center justify-center gap-2">
            <span>{currentGame.schedule.stadium}</span>
            <span>|</span>
            <span>{currentGame.schedule.startTime}</span>
            {currentGame.status === 'LIVE' && (
              <>
                <span>|</span>
                <span>
                  {currentGame.inning || '시작 전'}
                </span>
              </>
            )}
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
      <div className="flex-1 px-6 mt-6 overflow-y-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:'none'] [scrollbar-width:'none']">
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
            ) : isDataLoading ? (
              <div className="flex-1 flex items-center justify-center">
                <div className="text-gray-400 text-center">
                  <div className="text-xl font-['Giants-Bold'] mb-2">
                    데이터를 불러오는 중입니다
                  </div>
                  <div className="text-xs font-['Pretendard-Regular']">
                    잠시만 기다려주세요
                  </div>
                </div>
              </div>
            ) : (
              <>
                {/* 현재 문제 */}
                {currentProblem && (
                  <QuizModal
                    problem={currentProblem}
                    isActive={isActive}
                    onAnswer={handleAnswer}
                    currentTime={timeLeft}
                    currentAnswer={selectedAnswer}
                  />
                )}

                {/* 문제 히스토리 */}
                <div className="space-y-3">
                  {problemHistory.map((history) => (
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
                  <CollaborativeCanvas 
                    team={displayTeam}
                    gameId={currentGame.gameId.toString()} 
                    teamId={displayTeam === 'home' ? currentGame.homeTeamId.toString() : currentGame.awayTeamId.toString()}
                  />
                </div>
              </div>
            )}
          </div>
        )}
        {activeTab === 'WINNER' && (
          <Winner
            homeTeam={currentGame.homeTeam}
            awayTeam={currentGame.awayTeam}
            gameId={currentGame.gameId.toString()}
          />
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
