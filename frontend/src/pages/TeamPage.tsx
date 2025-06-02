import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useStore, TEAMS, getTeamById } from '../stores/useStore';
import { colors } from '../constants/colors';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import Sidebar from '../components/Sidebar';
import SuggestModal from '../components/suggest';
import ConfirmModal from '../components/confirm';
import axiosInstance from '../apis/axios';
import { voteOnProposal, checkVoted } from '../utils/proposalContract';
import { web3auth } from '../utils/web3auth';
import { ethers } from 'ethers';

// 팀 이름/코드 포맷팅 함수
const formatTeamName = (teamId: string) => {
  const team = getTeamById(teamId);
  return team?.name || '알 수 없음';
};

const formatTeamCode = (teamId: string) => {
  const team = getTeamById(teamId);
  return team?.code || '???';
};

interface Proposal {
  id: number;
  walletId: number;
  teamId: number;
  title: string;
  content: string;
  targetCount: number;
  currentCount: number;
  createdAt: string;
  closedAt: string;
}

interface Token {
  tokenName: string;
  balance: number;
}

interface WalletBalance {
  walletAddress: string;
  nickname: string;
  totalBet: number;
  tokens: Token[];
}

// 제안 카드 컴포넌트
const ProposalCard: React.FC<{
  proposal: Proposal;
  tokenCount: number;
  onVote: (proposalId: number) => Promise<void>;
  isApproved: boolean;
}> = ({ proposal, tokenCount, onVote, isApproved }) => {
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);
  const [hasVoted, setHasVoted] = useState(false);
  const [isVoting, setIsVoting] = useState(false);
  const [votingError, setVotingError] = useState<string | null>(null);
  const progress = (proposal.currentCount / proposal.targetCount) * 100;
  const teamCode = formatTeamCode(String(proposal.teamId));
  const isCompleted = progress >= 100;
  
  // 날짜 포맷팅 함수
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  // 투표 여부 확인
  useEffect(() => {
    const checkVoteStatus = async () => {
      try {
        if (!web3auth.provider) return;
        
        const provider = new ethers.BrowserProvider(web3auth.provider);
        const signer = await provider.getSigner();
        const address = await signer.getAddress();
        
        const voted = await checkVoted(proposal.id, address);
        setHasVoted(voted);
      } catch (error) {
        console.error('투표 여부 확인 실패:', error);
      }
    };
    
    checkVoteStatus();
  }, [proposal.id]);

  const handleVoteClick = async () => {
    try {
      setIsVoting(true);
      setVotingError(null);
      setIsConfirmOpen(false);

      // 웹쓰리 인증 확인
      if (!web3auth.provider) {
        setVotingError('블록체인 지갑 연결이 필요합니다. 로그인 상태를 확인해주세요.');
        return;
      }
      
      // 웹쓰리 연결 상태 확인
      if (!web3auth.connected) {
        setVotingError('블록체인 연결이 끊어졌습니다. 재로그인이 필요합니다.');
        return;
      }
      
      // 이미 투표했는지 확인
      if (hasVoted) {
        setVotingError('이미 투표하셨습니다.');
        return;
      }

      await onVote(proposal.id);
      setHasVoted(true);
    } catch (error: any) {
      console.error('투표 처리 실패:', error);
      setVotingError(error.message || '투표 처리 중 오류가 발생했습니다.');
    } finally {
      setIsVoting(false);
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-gray-800/50 rounded-xl p-6 mb-4"
    >
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-xl font-['Giants-Bold'] text-white mb-2">{proposal.title}</h3>
          <div className="text-gray-400 text-sm mb-4">
            <p className={isExpanded ? "whitespace-pre-wrap" : "line-clamp-3"}>
              {proposal.content}
            </p>
            {proposal.content.length > 100 && (
              <button
                onClick={() => setIsExpanded(!isExpanded)}
                className="text-white hover:underline mt-2"
              >
                {isExpanded ? '접기' : '더보기'}
              </button>
            )}
          </div>
        </div>
        <span className="text-sm text-gray-400">마감: {formatDate(proposal.closedAt)}</span>
      </div>

      {/* 진행 상황 바 */}
      <div className="w-full h-2 bg-gray-700 rounded-full mb-2">
        <div
          className={`h-full rounded-full transition-all duration-300 ${
            isCompleted ? 'bg-green-400' : 'bg-white'
          }`}
          style={{ width: `${Math.min(progress, 100)}%` }}
        />
      </div>

      <div className="flex justify-between items-center text-sm text-gray-400 mb-4">
        <span>{proposal.currentCount} / {proposal.targetCount} {teamCode}</span>
        <span>진행률: {Math.round(progress)}%</span>
      </div>

      {/* 투표 버튼 또는 상태 표시 */}
      {isApproved || isCompleted ? (
        <div className="w-full py-3 rounded-full bg-green-400/20 text-green-400 text-center text-sm">
          구단 검토 예정인 제안입니다
        </div>
      ) : hasVoted ? (
        <div className="w-full py-3 rounded-full bg-blue-400/20 text-blue-400 text-center text-sm">
          이미 투표한 제안입니다
        </div>
      ) : isVoting ? (
        <div className="w-full py-3 rounded-full bg-gray-700 text-gray-400 text-center text-sm">
          투표 처리 중...
        </div>
      ) : votingError ? (
        <div className="w-full py-3 rounded-full bg-red-400/20 text-red-400 text-center text-sm">
          {votingError}
        </div>
      ) : (
        <button
          onClick={() => setIsConfirmOpen(true)}
          disabled={tokenCount < 1}
          className={`w-full py-3 rounded-full font-['Pretendard-Regular'] transition-colors
            ${tokenCount >= 1 
              ? 'bg-white text-black hover:bg-gray-200' 
              : 'bg-gray-700 text-gray-400 cursor-not-allowed'}`}
        >
          {tokenCount >= 1 
            ? `1 ${teamCode}로 투표하기`
            : `${teamCode}가 부족합니다`}
        </button>
      )}

      <ConfirmModal
        isOpen={isConfirmOpen}
        onClose={() => setIsConfirmOpen(false)}
        onConfirm={handleVoteClick}
        team={String(proposal.teamId)}
        requiredTokens={1}
      />
    </motion.div>
  );
};

const TeamPage: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [proposals, setProposals] = useState<Proposal[]>([]);
  const [walletBalance, setWalletBalance] = useState<WalletBalance | null>(null);
  const [selectedTeamId, setSelectedTeamId] = useState(1); // 기본 팀 ID (두산)
  const [isSuggestModalOpen, setIsSuggestModalOpen] = useState(false);
  const { toggleSidebar } = useStore();
  const [teamsData, setTeamsData] = useState(Object.values(TEAMS).map(team => Number(team.id)));
  const [visibleTeams, setVisibleTeams] = useState<number[]>([]);

  // 현재 선택된 팀의 토큰 수량 계산
  const getSelectedTeamTokenCount = () => {
    if (!walletBalance) return 0;
    const selectedTeamCode = formatTeamCode(String(selectedTeamId));
    const teamToken = walletBalance.tokens.find(token => token.tokenName === selectedTeamCode);
    return teamToken?.balance || 0;
  };

  // 컴포넌트 마운트 시 지갑 잔액 조회
  useEffect(() => {
    const loadWalletBalance = async () => {
      try {
        const walletData = await axiosInstance.get('/wallet/balances');
        setWalletBalance(walletData.data);
      } catch (error) {
        console.error('지갑 잔액 조회 실패:', error);
        setWalletBalance(null);
      }
    };
    
    loadWalletBalance();
  }, []);

  // 팀 ID 변경 시 제안 목록만 로드
  useEffect(() => {
    const loadProposals = async () => {
      setLoading(true);
      try {
        const proposalsData = await axiosInstance.get(`/proposals/team/${selectedTeamId}`);
        setProposals(proposalsData.data.proposalList || []);
      } catch (error: any) {
        console.error('제안 목록 로딩 실패:', error);
        if (error.response) {
          console.error('Error response:', {
            data: error.response.data,
            status: error.response.status,
            headers: error.response.headers
          });
        }
        setProposals([]);
      } finally {
        setLoading(false);
      }
    };
    
    loadProposals();
  }, [selectedTeamId]);

  // 보이는 팀 목록 업데이트
  useEffect(() => {
    updateVisibleTeams();
  }, [selectedTeamId, teamsData]);

  // 투표 핸들러
  const handleVote = async (proposalId: number) => {
    try {
      // 1. 블록체인에 투표 트랜잭션 전송
      console.log('안건 ID로 투표 요청:', proposalId);
      const txHash = await voteOnProposal(proposalId);
      console.log('투표 트랜잭션 해시:', txHash);
      
      // 2. 백엔드 API로 투표 등록
      await axiosInstance.post('/proposals/vote', {
        teamId: selectedTeamId,
        proposalId
      });
      
      // 3. 데이터 다시 불러오기
      const [proposalsData, walletData] = await Promise.all([
        axiosInstance.get(`/proposals/team/${selectedTeamId}`),
        axiosInstance.get('/wallet/balances')
      ]);
      
      setProposals(proposalsData.data.proposalList || []);
      setWalletBalance(walletData.data);
    } catch (error: any) {
      console.error('투표 실패:', error);
      
      // 오류 메시지 사용자 친화적으로 변환
      let errorMsg = error.message || '알 수 없는 오류';
      
      if (errorMsg.includes('invalid sender')) {
        errorMsg = '지갑 인증에 문제가 있습니다. 다시 로그인한 후 시도해주세요.';
      } else if (errorMsg.includes('insufficient funds')) {
        errorMsg = '트랜잭션 수수료를 지불할 토큰이 부족합니다.';
      }
      
      throw new Error(errorMsg);
    }
  };

  // 제안 생성 핸들러
  const handleCreateProposal = async (title: string, content: string, targetCount: number) => {
    try {
      await axiosInstance.post('/proposals', {
        teamId: selectedTeamId,
        title,
        content,
        targetCount
      });
      
      // 제안 생성 후 데이터 다시 불러오기
      const [proposalsData, walletData] = await Promise.all([
        axiosInstance.get(`/proposals/team/${selectedTeamId}`),
        axiosInstance.get('/wallet/balances')
      ]);
      
      setProposals(proposalsData.data.proposalList || []);
      setWalletBalance(walletData.data);
    } catch (error) {
      console.error('제안 생성 실패:', error);
    }
  };

  // 팀 변경 함수들
  const updateVisibleTeams = () => {
    const currentIndex = teamsData.indexOf(selectedTeamId);
    if (currentIndex === -1) {
      setVisibleTeams([teamsData[0], teamsData[1], teamsData[2]]);
      return;
    }
    
    const prevIndex = currentIndex === 0 ? teamsData.length - 1 : currentIndex - 1;
    const nextIndex = currentIndex === teamsData.length - 1 ? 0 : currentIndex + 1;
    
    setVisibleTeams([teamsData[prevIndex], teamsData[currentIndex], teamsData[nextIndex]]);
  };

  const handlePrevTeam = () => {
    const currentIndex = teamsData.indexOf(selectedTeamId);
    const newIndex = currentIndex > 0 ? currentIndex - 1 : teamsData.length - 1;
    setSelectedTeamId(teamsData[newIndex]);
  };

  const handleNextTeam = () => {
    const currentIndex = teamsData.indexOf(selectedTeamId);
    const newIndex = currentIndex < teamsData.length - 1 ? currentIndex + 1 : 0;
    setSelectedTeamId(teamsData[newIndex]);
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
          <button 
            onClick={() => toggleSidebar(true)}
            className="w-5 h-5"
          >
            <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
          </button>
        </div>
      </div>

      {/* 스크롤 가능한 컨텐츠 영역 */}
      <div className="h-full overflow-y-auto no-scrollbar">
        <style>{`
          .no-scrollbar {
            -ms-overflow-style: none;
            scrollbar-width: none;
          }
          .no-scrollbar::-webkit-scrollbar {
            display: none;
          }
        `}</style>

        {/* 팀 선택 탭 */}
        <div className="pt-20 px-6">
          <div className="flex items-center justify-between mb-4">
            <button 
              onClick={handlePrevTeam}
              className="text-white text-xl px-4 opacity-50 hover:opacity-100 transition-opacity"
            >
              &#10094;
            </button>
            <div className="flex gap-4 items-center">
              {visibleTeams.map((team) => (
                <button
                  key={team}
                  onClick={() => setSelectedTeamId(team)}
                  className={`px-4 py-2 rounded-full transition-all duration-300 font-['Giants-Bold']
                    ${team === selectedTeamId 
                      ? 'bg-white text-black scale-110' 
                      : 'bg-gray-800 text-white opacity-50 scale-90'}`}
                >
                  {formatTeamName(String(team))}
                </button>
              ))}
            </div>
            <button 
              onClick={handleNextTeam}
              className="text-white text-xl px-4 opacity-50 hover:opacity-100 transition-opacity"
            >
              &#10095;
            </button>
          </div>
        </div>

        {/* 보유 토큰과 제안하기 버튼 */}
        <div className="px-6 py-4">
          <div className="flex items-center">
            {/* 보유 토큰 표시 */}
            <div className="flex-1 bg-gray-800/50 rounded-xl p-4">
              <h2 className="text-lg font-['Giants-Bold'] mb-2">보유 토큰</h2>
              <div className="flex items-center">
                <span className="text-2xl font-['Giants-Bold']">
                  {getSelectedTeamTokenCount()}
                </span>
                <span className="ml-2 text-gray-400 font-['Giants-Bold']">{formatTeamCode(String(selectedTeamId))}</span>
              </div>
            </div>

            {/* 제안하기 버튼 - 텍스트만 있는 버전 */}
            {getSelectedTeamTokenCount() >= 10 && (
              <div className="pl-6">
                <button
                  onClick={() => setIsSuggestModalOpen(true)}
                  className="text-white hover:text-gray-300 transition-colors font-['Giants-Bold'] pr-6"
                >
                  제안하기
                </button>
              </div>
            )}
          </div>
        </div>

        {/* 제안 목록 또는 접근 권한 없음 메시지 */}
        <div className="px-6 pb-6">
          {loading ? (
            // 로딩 상태 표시
            <div className="flex justify-center items-center py-12">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
            </div>
          ) : getSelectedTeamTokenCount() > 0 ? (
            // 제안 목록
            proposals.length > 0 ? (
              proposals.map(proposal => (
                <ProposalCard
                  key={proposal.id}
                  proposal={proposal}
                  tokenCount={getSelectedTeamTokenCount()}
                  onVote={handleVote}
                  isApproved={proposal.currentCount >= proposal.targetCount}
                />
              ))
            ) : (
              // 제안이 없는 경우
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="bg-gray-800/50 rounded-xl p-6 flex flex-col items-center justify-center text-center"
              >
                <h3 className="text-xl font-['Giants-Bold'] text-white mb-4">아직 제안이 없습니다</h3>
                <p className="text-gray-400 text-sm">
                  {formatTeamName(String(selectedTeamId))} 팀을 위한<br/>
                  첫 번째 제안을 작성해보세요!
                </p>
              </motion.div>
            )
          ) : (
            // 토큰이 없는 경우 접근 권한 없음 메시지 표시
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-gray-800/50 rounded-xl p-6 flex flex-col items-center justify-center text-center"
            >
              <h3 className="text-xl font-['Giants-Bold'] text-white mb-4">접근 권한이 없습니다</h3>
              <p className="text-gray-400 text-sm">
                {formatTeamName(String(selectedTeamId))} 팀의 제안을 보려면<br/>
                {formatTeamCode(String(selectedTeamId))} 팬토큰이 필요합니다
              </p>
            </motion.div>
          )}
        </div>
      </div>

      {/* 사이드바 */}
      <Sidebar />
      
      {/* 제안하기 모달 */}
      <SuggestModal
        isOpen={isSuggestModalOpen}
        onClose={() => setIsSuggestModalOpen(false)}
        team={String(selectedTeamId)}
        onSubmit={handleCreateProposal}
      />
    </div>
  );
};

export default TeamPage;