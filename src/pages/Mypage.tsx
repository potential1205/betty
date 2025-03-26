import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useStore } from '../stores/useStore';
import backBlackImg from '../assets/back_black.png';
import hamburgerBlackImg from '../assets/hamburger_black.png';
import Sidebar from '../components/Sidebar';
import ChargeModal from '../components/charge';
import { formatTeamCode } from './TeamPage';

const MyPage: React.FC = () => {
  const navigate = useNavigate();
  const { toggleSidebar, walletInfo, activeTab, setActiveTab, isChargeModalOpen, setIsChargeModalOpen } = useStore();

  // BTC 금액 포맷팅 함수
  const formatBTC = (btc: number) => btc.toFixed(2);

  // 컴포넌트 마운트 시 사이드바를 닫힌 상태로 설정
  React.useEffect(() => {
    toggleSidebar(false);
  }, []);

  const tabs = [
    { id: 'ASSETS', label: '보유자산' },
    { id: 'TRANSACTIONS', label: '거래내역' },
    { id: 'CHARGE', label: '충전' }
  ];

  const renderContent = () => {
    switch (activeTab) {
      case 'ASSETS':
        return (
          <div className="space-y-6">
            {/* 총 보유자산 */}
            <div className="bg-gradient-to-br from-black to-gray-800 rounded-2xl p-6 shadow-lg">
              <p className="text-sm text-gray-400 mb-2">총 보유자산</p>
              <div className="flex items-baseline">
                <p className="text-3xl font-['Giants-Bold'] text-white">{formatBTC(walletInfo.totalBTC)}</p>
                <p className="text-xl text-gray-400 ml-2">BTC</p>
              </div>
            </div>

            {/* 보유 팬토큰 */}
            <div>
              <h3 className="text-lg font-['Giants-Bold'] mb-4">보유 팬토큰</h3>
              <div className="space-y-3">
                {walletInfo.tokens.map((token) => (
                  <motion.div
                    key={token.team}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="bg-white rounded-2xl p-6 shadow-md border border-gray-100"
                  >
                    <div className="flex justify-between items-center">
                      <div>
                        <div className="flex items-center mb-2">
                          <div className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center mr-2">
                            <span className="text-sm font-['Giants-Bold'] text-gray-600">
                              {formatTeamCode(token.team)}
                            </span>
                          </div>
                          <p className="text-sm text-gray-500">{token.team}</p>
                        </div>
                        <p className="text-xl font-['Giants-Bold']">{token.amount}개</p>
                      </div>
                      <div className="text-right">
                        <p className="text-sm text-gray-500 mb-1">BTC 가치</p>
                        <div className="flex items-baseline">
                          <p className="text-xl font-['Giants-Bold'] text-black">{formatBTC(token.btcValue)}</p>
                          <p className="text-sm text-gray-500 ml-1">BTC</p>
                        </div>
                      </div>
                    </div>
                  </motion.div>
                ))}
              </div>
            </div>
          </div>
        );

      case 'TRANSACTIONS':
        return (
          <div className="space-y-4">
            {walletInfo.transactions.map((transaction) => (
              <motion.div
                key={transaction.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="bg-white rounded-2xl p-6 shadow-md border border-gray-100"
              >
                <div className="flex justify-between items-start mb-3">
                  <div className="flex items-center">
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center mr-2 ${
                      transaction.type === 'BUY' ? 'bg-green-100' :
                      transaction.type === 'SELL' ? 'bg-red-100' :
                      'bg-blue-100'
                    }`}>
                      <span className={`text-sm font-['Giants-Bold'] ${
                        transaction.type === 'BUY' ? 'text-green-600' :
                        transaction.type === 'SELL' ? 'text-red-600' :
                        'text-blue-600'
                      }`}>
                        {transaction.type === 'BUY' ? '매수' :
                         transaction.type === 'SELL' ? '매도' : '충전'}
                      </span>
                    </div>
                    <span className="text-sm text-gray-500">{transaction.date}</span>
                  </div>
                </div>
                <div className="space-y-2">
                  <p className="text-lg font-['Giants-Bold']">
                    {transaction.type === 'CHARGE' 
                      ? `${transaction.amount.toLocaleString()}원`
                      : `${formatBTC(transaction.amount)} BTC`}
                  </p>
                  {transaction.tokenName && (
                    <div className="flex items-center text-sm text-gray-500">
                      <div className="w-6 h-6 rounded-full bg-gray-100 flex items-center justify-center mr-2">
                        <span className="text-xs font-['Giants-Bold'] text-gray-600">
                          {formatTeamCode(transaction.tokenName)}
                        </span>
                      </div>
                      <span>{transaction.tokenAmount}개</span>
                      <span className="mx-2">•</span>
                      <span>{formatBTC(transaction.tokenPrice || 0)} BTC/개</span>
                    </div>
                  )}
                </div>
              </motion.div>
            ))}
          </div>
        );

      case 'CHARGE':
        return (
          <div className="space-y-6">
            <div className="bg-gradient-to-br from-black to-gray-800 rounded-2xl p-6 shadow-lg">
              <p className="text-sm text-gray-400 mb-2">현재 보유 BTC</p>
              <div className="flex items-baseline">
                <p className="text-3xl font-['Giants-Bold'] text-white">{formatBTC(walletInfo.totalBTC)}</p>
                <p className="text-xl text-gray-400 ml-2">BTC</p>
              </div>
            </div>
            <button
              onClick={() => setIsChargeModalOpen(true)}
              className="w-full bg-gradient-to-r from-black to-gray-800 text-white py-4 rounded-2xl font-['Giants-Bold'] hover:from-gray-800 hover:to-black transition-all shadow-lg"
            >
              BTC 충전하기
            </button>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className="relative h-full bg-gray-50 text-black">
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

        {/* 지갑 주소 */}
        <div className="mb-8">
          <h2 className="text-2xl font-['Giants-Bold']">안녕하세요!</h2>
          <p className="text-sm text-gray-500 mt-1">{walletInfo.address}</p>
        </div>

        {/* 탭 */}
        <div className="flex space-x-2 mb-6 bg-white p-1 rounded-2xl shadow-sm">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as 'ASSETS' | 'TRANSACTIONS' | 'CHARGE')}
              className={`flex-1 py-3 rounded-xl text-sm font-['Giants-Bold'] transition-all duration-200
                ${activeTab === tab.id
                  ? 'bg-black text-white shadow-md'
                  : 'text-gray-500 hover:text-black hover:bg-gray-50'}`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* 탭 컨텐츠 */}
        {renderContent()}
      </div>

      <Sidebar />
      <ChargeModal
        isOpen={isChargeModalOpen}
        onClose={() => setIsChargeModalOpen(false)}
      />
    </div>
  );
};

export default MyPage;
