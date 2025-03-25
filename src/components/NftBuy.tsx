import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../stores/useStore';
import { NFT } from '../constants/dummy';
import { formatTeamCode } from '../pages/TeamPage';

interface NftBuyProps {
  isOpen: boolean;
  onClose: () => void;
  nft: NFT;
}

const NftBuy: React.FC<NftBuyProps> = ({ isOpen, onClose, nft }) => {
  const navigate = useNavigate();
  const { purchaseNFT, addUserPixelNFT } = useStore();

  const getTokenCode = (token: string) => {
    const tokenCodes: { [key: string]: string } = {
      'SAMSUNG': 'SAL',
      'DOOSAN': 'DSB',
      'LOTTE': 'LTG',
      'KIA': 'KIA',
      'SSG': 'SSG',
      'LG': 'LGT',
      'KT': 'KTW',
      'NC': 'NCD',
      'HANWHA': 'HHE',
      'KIWOOM': 'KWH'
    };
    return tokenCodes[token] || token;
  };

  const handleConfirmPurchase = () => {
    // 구매 가능한 상태에서만 모달이 열리므로, 바로 구매 처리
    purchaseNFT(nft.id);
    
    // NFT를 사용자의 NFT 목록에 추가
    addUserPixelNFT({
      ...nft,
      isListed: false,
      isPurchased: true,
      purchasedAt: new Date().toISOString()
    });
    
    // 모달 닫고 마이페이지로 이동
    onClose();
    navigate('/my-page');
  };

  if (!isOpen) return null;

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-80"
      style={{
        position: 'absolute',
        width: '100%',
        height: '100%'
      }}
    >
      {/* 모달 컨텐츠 */}
      <div 
        className="relative bg-black border border-gray-800 rounded-xl w-[300px] mx-auto"
        style={{
          maxHeight: 'calc(100vh - 40%)',
        }}
      >
        {/* 헤더 */}
        <div className="p-4 border-b border-gray-800">
          <h3 className="text-lg font-['Giants-Bold'] text-center text-white">구매 확인</h3>
        </div>

        {/* 컨텐츠 */}
        <div className="p-6">
          <div className="space-y-4">
            <div className="text-center">
              <p className="text-white font-['Giants-Bold'] text-lg">{nft.name}</p>
            </div>
            
            <div className="bg-gray-900 rounded-lg p-4 text-center">
              <p className="text-2xl font-['Giants-Bold'] text-red-500">
                {nft.price} {getTokenCode(nft.paymentToken)}
              </p>
            </div>
          </div>
        </div>

        {/* 버튼 영역 */}
        <div className="p-4 border-t border-gray-800">
          <div className="flex gap-3">
            <button
              onClick={onClose}
              className="flex-1 py-3 rounded-lg bg-gray-800 text-gray-300 hover:bg-gray-700 font-['Giants-Bold']"
            >
              취소
            </button>
            <button
              onClick={handleConfirmPurchase}
              className="flex-1 py-3 rounded-lg bg-red-500 text-white hover:bg-red-600 font-['Giants-Bold']"
            >
              구매하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NftBuy;
