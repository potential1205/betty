import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../stores/useStore';
import { teamColors } from '../constants/colors';
import { formatTeamName, formatTeamCode } from '../constants/dummy';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import Sidebar from '../components/Sidebar';
import SuggestModal from '../components/suggest';
import ConfirmModal from '../components/confirm';

interface Proposal {
  id: number;
  team: string;
  title: string;
  description: string;
  requiredTokens: number;
  currentVotes: number;
  targetVotes: number;
  deadline: string;
}

interface TeamToken {
  team: string;
  amount: number;
}

// 제안 카드 컴포넌트
const ProposalCard: React.FC<{
  proposal: Proposal;
  userTokens: TeamToken[];
  onVote: (proposalId: number) => void;
}> = ({ proposal, userTokens, onVote }) => {
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);  // 더보기 상태 관리
  const userTeamTokens = userTokens.find(token => token.team === proposal.team)?.amount || 0;
  const progress = (proposal.currentVotes / proposal.targetVotes) * 100;
  const teamCode = formatTeamCode(proposal.team);
  const isCompleted = progress >= 100;

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
              {proposal.description}
            </p>
            {proposal.description.length > 100 && (
              <button
                onClick={() => setIsExpanded(!isExpanded)}
                className="text-white hover:underline mt-2"
              >
                {isExpanded ? '접기' : '더보기'}
              </button>
            )}
          </div>
        </div>
        <span className="text-sm text-gray-400">마감: {proposal.deadline}</span>
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
        <span>{proposal.currentVotes} / {proposal.targetVotes} {teamCode}</span>
        <span>진행률: {Math.round(progress)}%</span>
      </div>

      {isCompleted ? (
        <div className="w-full py-3 rounded-full bg-green-400/20 text-green-400 text-center text-sm">
          구단 검토 예정인 제안입니다
        </div>
      ) : (
        <button
          onClick={() => setIsConfirmOpen(true)}
          disabled={userTeamTokens < 1}
          className={`w-full py-3 rounded-full font-['Pretendard-Regular'] transition-colors
            ${userTeamTokens >= 1 
              ? 'bg-white text-black hover:bg-gray-200' 
              : 'bg-gray-700 text-gray-400 cursor-not-allowed'}`}
        >
          {userTeamTokens >= 1 
            ? `1 ${teamCode}로 투표하기`
            : `${teamCode}가 부족합니다`}
        </button>
      )}

      <ConfirmModal
        isOpen={isConfirmOpen}
        onClose={() => setIsConfirmOpen(false)}
        onConfirm={() => onVote(proposal.id)}
        team={proposal.team}
        requiredTokens={1}
      />
    </motion.div>
  );
};

const TeamPage: React.FC = () => {
  const navigate = useNavigate();
  const {
    selectedTeam,
    userTokens,
    proposals,
    isSuggestModalOpen,
    toggleSuggestModal,
    toggleSidebar,
    updateProposal,
    getVisibleTeams,
    handlePrevTeam,
    handleNextTeam
  } = useStore();

  // 컴포넌트 마운트 시 사이드바를 닫힌 상태로 설정
  React.useEffect(() => {
    toggleSidebar(false);
  }, []);

  const teams = Object.keys(teamColors);
  const currentIndex = teams.indexOf(selectedTeam);

  // 투표 핸들러
  const handleVote = (proposalId: number) => {
    const proposal = proposals.find(p => p.id === proposalId);
    if (proposal) {
      updateProposal(proposalId, {
        currentVotes: proposal.currentVotes + 1
      });
    }
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
              {getVisibleTeams().map((team) => (
                <button
                  key={team}
                  onClick={() => setSelectedTeam(team)}
                  className={`px-4 py-2 rounded-full transition-all duration-300
                    ${team === selectedTeam 
                      ? 'bg-white text-black scale-110' 
                      : 'bg-gray-800 text-white opacity-50 scale-90'}`}
                >
                  {formatTeamName(team)}
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
                  {userTokens.find(token => token.team === selectedTeam)?.amount || 0}
                </span>
                <span className="ml-2 text-gray-400">{formatTeamCode(selectedTeam)}</span>
              </div>
            </div>

            {/* 제안하기 버튼 - 텍스트만 있는 버전 */}
            {(userTokens.find(token => token.team === selectedTeam)?.amount || 0) > 0 && (
              <div className="pl-6">
                <button
                  onClick={() => toggleSuggestModal(true)}
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
          {(userTokens.find(token => token.team === selectedTeam)?.amount || 0) > 0 ? (
            // 제안 목록
            proposals
              .filter(proposal => proposal.team === selectedTeam)
              .map(proposal => (
                <ProposalCard
                  key={proposal.id}
                  proposal={proposal}
                  userTokens={userTokens}
                  onVote={(id) => handleVote(id)}
                />
              ))
          ) : (
            // 토큰이 없는 경우 접근 권한 없음 메시지 표시
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-gray-800/50 rounded-xl p-6 flex flex-col items-center justify-center text-center"
            >
              <h3 className="text-xl font-['Giants-Bold'] text-white mb-4">접근 권한이 없습니다</h3>
              <p className="text-gray-400 text-sm">
                {formatTeamName(selectedTeam)} 팀의 제안을 보려면<br/>
                {formatTeamCode(selectedTeam)} 팬토큰이 필요합니다
              </p>
            </motion.div>
          )}
        </div>
      </div>

      <Sidebar />
      
      {/* 제안하기 모달 */}
      <SuggestModal
        isOpen={isSuggestModalOpen}
        onClose={() => toggleSuggestModal(false)}
        team={selectedTeam}
      />
    </div>
  );
};

export default TeamPage;
