import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useStore } from '../stores/useStore';
import backBlackImg from '../assets/back_black.png';
import hamburgerBlackImg from '../assets/hamburger_black.png';
import { formatTeamCode } from './TeamPage';
import Sidebar from '../components/Sidebar';
import ChargeModal from '../components/charge';

const MyPage: React.FC = () => {
  const navigate = useNavigate();
  const { userTokens, myProposals, toggleSidebar, savedNFTs } = useStore();
  const [showAllNFTs, setShowAllNFTs] = useState(false);

  // 랜덤 문구 배열
  const greetings = [
    "야구 보기 좋은 날이네요!",
    "오늘도 즐거운 경기 되세요!",
    "승리를 기원합니다!",
    "오늘은 어떤 경기를 볼까요?",
    "응원하는 팀의 소식을 확인해보세요!",
    "새로운 제안을 기다리고 있어요!"
  ];

  // 랜덤 문구 선택
  const [randomGreeting] = React.useState(() => 
    greetings[Math.floor(Math.random() * greetings.length)]
  );

  // 충전 모달 상태 추가
  const [isChargeModalOpen, setIsChargeModalOpen] = React.useState(false);

  // 제안 설명 확장 상태 관리를 위한 객체
  const [expandedProposals, setExpandedProposals] = React.useState<{[key: number]: boolean}>({});

  // 충전 처리 함수
  const handleCharge = (amount: number) => {
    console.log('충전 금액:', amount);
    // TODO: 실제 충전 로직 구현
  };

  // 컴포넌트 마운트 시 사이드바를 닫힌 상태로 설정
  React.useEffect(() => {
    toggleSidebar(false);
  }, []);

  // 특정 제안의 더보기/접기 토글 함수
  const toggleProposalExpand = (proposalId: number) => {
    setExpandedProposals(prev => ({
      ...prev,
      [proposalId]: !prev[proposalId]
    }));
  };

  // 표시할 NFT 개수 계산
  const displayedNFTs = showAllNFTs ? savedNFTs : savedNFTs.slice(0, 4);

  return (
    <div className="relative h-full bg-white text-black">
      {/* 헤더 */}
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <button 
          onClick={() => navigate(-1)} 
          className="w-[12px] h-[12px]"
        >
          <img src={backBlackImg} alt="Back" className="w-full h-full" />
        </button>
        <div className="w-5 h-5 flex items-center justify-center">
          <button 
            onClick={() => toggleSidebar(true)}
            className="w-5 h-5"
          >
            <img src={hamburgerBlackImg} alt="Menu" className="w-full h-full" />
          </button>
        </div>
      </div>

      {/* 스크롤 가능한 컨텐츠 영역 */}
      <div 
        className="h-full pt-20 px-6 pb-24 overflow-y-auto"
        style={{
          msOverflowStyle: 'none',
          scrollbarWidth: 'none',
          WebkitOverflowScrolling: 'touch',
        }}
      >
        <style>
          {`
            div::-webkit-scrollbar {
              display: none;
            }
          `}
        </style>
        {/* 인사말 섹션 */}
        <div className="mb-8">
          <h2 className="text-xl font-['Giants-Bold']">안녕하세요,</h2>
          <p className="text-sm text-gray-500 mt-1">{randomGreeting}</p>
        </div>

        {/* BETTY 코인 섹션 */}
        <div className="mb-8">
          <h2 className="text-lg font-['Giants-Bold'] mb-4">BETTY 코인</h2>
          <div className="bg-gray-100 rounded-xl p-6">
            <div className="flex justify-between items-center">
              <div>
                <p className="text-sm text-gray-500 mb-1">보유 코인</p>
                <p className="text-2xl font-['Giants-Bold']">1,234</p>
              </div>
              <button 
                className="bg-black text-white px-6 py-2.5 rounded-full text-sm hover:bg-gray-800 transition-colors"
                onClick={() => setIsChargeModalOpen(true)}
              >
                충전하기
              </button>
            </div>
          </div>
        </div>

        {/* 팬토큰 섹션 */}
        <div>
          <h2 className="text-lg font-['Giants-Bold'] mb-4">보유 팬토큰</h2>
          <div className="space-y-3">
            {userTokens.length > 0 ? (
              userTokens.map((token) => (
                <motion.div
                  key={token.team}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="bg-gray-100 rounded-xl p-6"
                >
                  <div>
                    <p className="text-sm text-gray-500 mb-1">{formatTeamCode(token.team)}</p>
                    <p className="text-xl font-['Giants-Bold']">{token.amount}</p>
                  </div>
                </motion.div>
              ))
            ) : (
              <div className="bg-gray-100 rounded-xl p-6 text-center">
                <p className="text-gray-500">보유중인 팬토큰이 없습니다</p>
                <p className="text-sm text-gray-400 mt-1">
                  거래소에서 팬토큰을 구매해보세요
                </p>
              </div>
            )}
          </div>
        </div>

        {/* 저장한 NFT 섹션 */}
        <div className="mt-8 mb-8">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-['Giants-Bold']">저장한 NFT</h2>
            {savedNFTs.length > 4 && (
              <button
                onClick={() => setShowAllNFTs(!showAllNFTs)}
                className="text-sm text-gray-600 hover:text-black"
              >
                {showAllNFTs ? '접기' : '더보기'}
              </button>
            )}
          </div>
          {savedNFTs.length > 0 ? (
            <div className="grid grid-cols-2 gap-3">
              {displayedNFTs.map((nft) => (
                <motion.div
                  key={nft.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="bg-gray-100 rounded-xl overflow-hidden"
                >
                  <div className="aspect-square relative">
                    <img
                      src={nft.image}
                      alt={nft.name}
                      className="w-full h-full object-cover"
                    />
                  </div>
                  <div className="p-3">
                    <h3 className="text-sm font-['Giants-Bold'] mb-1">{nft.name}</h3>
                    <p className="text-xs text-gray-500">{nft.matchTeams.join(' vs ')}</p>
                    <p className="text-xs text-gray-400 mt-1">{nft.creator}</p>
                  </div>
                </motion.div>
              ))}
            </div>
          ) : (
            <div className="bg-gray-100 rounded-xl p-6 text-center">
              <p className="text-gray-500">저장한 NFT가 없습니다</p>
              <p className="text-sm text-gray-400 mt-1">
                NFT 아카이브에서 마음에 드는 NFT를 저장해보세요
              </p>
            </div>
          )}
        </div>

        {/* 내 제안 섹션 */}
        <div className="mt-8">
          <h2 className="text-lg font-['Giants-Bold'] mb-4">내가 제안한 안건</h2>
          <div className="space-y-3">
            {myProposals.length > 0 ? (
              myProposals.map((proposal) => (
                <motion.div
                  key={proposal.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="bg-gray-100 rounded-xl p-6"
                >
                  <div className="flex justify-between items-start mb-2">
                    <span className="text-sm text-gray-500">{formatTeamCode(proposal.team)}</span>
                    <span className="text-sm text-gray-500">마감: {proposal.deadline}</span>
                  </div>
                  <h3 className="text-lg font-['Giants-Bold'] mb-2">{proposal.title}</h3>
                  <div className="mb-3">
                    <p className={`text-sm text-gray-600 ${expandedProposals[proposal.id] ? "whitespace-pre-wrap" : "line-clamp-3"}`}>
                      {proposal.description}
                    </p>
                    {proposal.description.length > 100 && (
                      <button
                        onClick={() => toggleProposalExpand(proposal.id)}
                        className="text-sm text-gray-800 hover:underline mt-2"
                      >
                        {expandedProposals[proposal.id] ? '접기' : '더보기'}
                      </button>
                    )}
                  </div>
                  <div className="flex justify-between items-center text-sm text-gray-500">
                    <div>
                      진행률: {Math.round((proposal.currentVotes / proposal.targetVotes) * 100)}%
                    </div>
                    <div>
                      {proposal.currentVotes} / {proposal.targetVotes} {formatTeamCode(proposal.team)}
                    </div>
                  </div>
                  {/* 진행 상황 바 */}
                  <div className="w-full h-1.5 bg-gray-200 rounded-full mt-2">
                    <div
                      className="h-full bg-black rounded-full transition-all duration-300"
                      style={{ 
                        width: `${(proposal.currentVotes / proposal.targetVotes) * 100}%` 
                      }}
                    />
                  </div>
                </motion.div>
              ))
            ) : (
              <div className="bg-gray-100 rounded-xl p-6 text-center">
                <p className="text-gray-500">제안한 안건이 없습니다</p>
                <p className="text-sm text-gray-400 mt-1">
                  팀 채널에서 새로운 제안을 해보세요
                </p>
              </div>
            )}
          </div>
        </div>
      </div>

      <Sidebar />

      {/* 충전 모달 추가 */}
      <ChargeModal
        isOpen={isChargeModalOpen}
        onClose={() => setIsChargeModalOpen(false)}
        onCharge={handleCharge}
      />
    </div>
  );
};

export default MyPage;
