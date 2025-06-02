import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { formatTeamName, formatTeamCode } from '../constants/dummy';
import { ethers } from 'ethers';
import { useWalletStore } from '../stores/walletStore';
import { useProposalStore } from '../stores/proposalStore';
import FanTokenDAOABI from '../../abi/FanTokenDAO.json';
import TokenABI from '../../abi/Token.json';

// 환경 변수 가져오기
const DAO_CONTRACT_ADDRESS = import.meta.env.VITE_DAO_CONTRACT_ADDRESS;
const RPC_URL = import.meta.env.VITE_RPC_URL;
const BLOCK_EXPLORER = import.meta.env.VITE_BLOCK_EXPLORER;

// 트랜잭션 상태 인터페이스
interface TransactionState {
  isProcessing: boolean;
  currentStep: string;
  error: string;
  successMessage: string;
  txHash: string;
}

interface SuggestModalProps {
  isOpen: boolean;
  onClose: () => void;
  team: string;
  onSubmit?: (title: string, content: string, targetCount: number) => Promise<void>;
}

const SuggestModal: React.FC<SuggestModalProps> = ({ 
  isOpen, 
  onClose, 
  team
}) => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [error, setError] = useState('');
  const [walletAddress, setWalletAddress] = useState<string>('');

  // 트랜잭션 상태 관리
  const [txState, setTxState] = useState<TransactionState>({
    isProcessing: false,
    currentStep: '',
    error: '',
    successMessage: '',
    txHash: '',
  });

  // 지갑 및 제안 관련 스토어
  const { getAccounts, exportPrivateKey } = useWalletStore();
  const { 
    createProposal, 
    registerProposalHash, 
    resetMessages,
    error: storeError,
    successMessage: storeSuccessMessage
  } = useProposalStore();

  // 컴포넌트 마운트 시 지갑 주소 가져오기
  useEffect(() => {
    const fetchWalletAddress = async () => {
      try {
        const address = await getAccounts();
        setWalletAddress(address);
      } catch (error) {
        console.error('지갑 주소 가져오기 실패:', error);
      }
    };

    fetchWalletAddress();
  }, [getAccounts]);

  // 스마트 컨트랙트에 안건 등록
  const registerProposalOnChain = async (
    proposalId: number,
    contentHash: string,
    teamId: number
  ): Promise<string> => {
    try {
      setTxState(prev => ({
        ...prev,
        currentStep: '블록체인에 안건 등록 중...',
      }));

      // 안건 마감일 설정 (현재 시간으로부터 7일 후)
      const deadline = Math.floor(Date.now() / 1000) + 7 * 24 * 60 * 60;
      
      // Web3Auth로부터 개인키 가져오기
      const privateKey = await exportPrivateKey();
      if (!privateKey) {
        throw new Error('개인 키를 가져올 수 없습니다.');
      }
      
      const provider = new ethers.JsonRpcProvider(RPC_URL);
      const wallet = new ethers.Wallet(privateKey, provider);
      
      // DAO 컨트랙트 인스턴스 생성
      const daoContract = new ethers.Contract(
        DAO_CONTRACT_ADDRESS,
        FanTokenDAOABI.abi,
        wallet
      );

      // 팀 토큰 주소 가져오기
      const teamTokenAddress = await daoContract.teamTokens(teamId);
      if (teamTokenAddress === "0x0000000000000000000000000000000000000000") {
        throw new Error(`팀 ID ${teamId}에 대한 토큰이 설정되지 않았습니다.`);
      }
      
      // 토큰 컨트랙트 생성
      const tokenContract = new ethers.Contract(
        teamTokenAddress,
        TokenABI.abi,
        wallet
      );
      
      // 제안 비용 확인
      const proposalFee = await daoContract.PROPOSAL_FEE();
      console.log('안건 등록 비용:', ethers.formatEther(proposalFee), '팬토큰');
      
      // 사용자 토큰 잔액 확인
      const balance = await tokenContract.balanceOf(walletAddress);
      console.log('현재 토큰 잔액:', ethers.formatEther(balance), '팬토큰');
      
      if (balance < proposalFee) {
        throw new Error(`안건 등록에 필요한 팬토큰이 부족합니다. 필요: ${ethers.formatEther(proposalFee)} 팬토큰, 보유: ${ethers.formatEther(balance)} 팬토큰`);
      }
      
      // DAO 컨트랙트에 토큰 사용 승인
      setTxState(prev => ({
        ...prev,
        currentStep: '토큰 사용 승인 중...',
      }));
      
      const approveTx = await tokenContract.approve(DAO_CONTRACT_ADDRESS, proposalFee, { gasPrice: 0 });
      console.log('토큰 승인 트랜잭션 전송 완료:', approveTx.hash);
      
      const approveReceipt = await approveTx.wait();
      console.log('토큰 승인 완료:', approveReceipt.hash);
      
      // contentHash가 0x로 시작하지 않으면 추가
      const formattedHash = contentHash.startsWith("0x") ? contentHash : `0x${contentHash}`;

      // 제안 등록 트랜잭션 실행
      setTxState(prev => ({
        ...prev,
        currentStep: '블록체인에 안건 등록 트랜잭션 전송 중...',
      }));
      
      const tx = await daoContract.registerProposal(proposalId, formattedHash, teamId, deadline, { gasPrice: 0 });
      console.log('트랜잭션 전송 완료. 채굴 대기 중...', tx.hash);
      
      // 트랜잭션이 블록에 포함될 때까지 대기하고 영수증 가져오기
      const receipt = await tx.wait();
      console.log('트랜잭션 채굴 완료:', receipt.hash);
      
      // 트랜잭션 해시 반환
      return receipt.hash;
    } catch (error: any) {
      console.error("안건 등록 트랜잭션 실패:", error);
      throw new Error(`스마트 컨트랙트 안건 등록 실패: ${error.message || "알 수 없는 오류"}`);
    }
  };

  const handleSubmit = async () => {
    // 유효성 검사
    if (!title.trim()) {
      setError('제안 제목을 입력해주세요.');
      return;
    }
    
    if (!content.trim()) {
      setError('제안 내용을 입력해주세요.');
      return;
    }
    
    setTxState({
      isProcessing: true,
      currentStep: '안건 등록 중...',
      error: '',
      successMessage: '',
      txHash: '',
    });
    setError('');
    
    try {
      resetMessages();
      
      // 팀 ID를 숫자로 변환
      const teamId = parseInt(team, 10);
      if (isNaN(teamId)) {
        throw new Error('잘못된 팀 ID 입니다.');
      }
      
      // 1단계: 백엔드에 안건 등록 요청
      const proposalData = {
        teamId,
        title,
        content,
        targetCount: 100 // 목표 투표수는 100로 고정
      };
      
      const proposalResponse = await createProposal(proposalData);
      
      if (!proposalResponse) {
        throw new Error('백엔드에 안건 등록에 실패했습니다.');
      }
      
      console.log('안건 백엔드 등록 성공:', proposalResponse);
      
      // 2단계: 스마트 컨트랙트에 안건 등록
      const txHash = await registerProposalOnChain(
        proposalResponse.proposalId,
        proposalResponse.contentHash,
        teamId
      );
      
      console.log('블록체인 트랜잭션 해시:', txHash);
      
      // 트랜잭션 상태 업데이트
      setTxState(prev => ({
        ...prev,
        txHash: txHash,
        currentStep: '트랜잭션 해시 서버에 등록 중...',
      }));
      
      // 3단계: 트랜잭션 해시 즉시 백엔드에 등록
      console.log('백엔드 API에 트랜잭션 해시 등록 중...');
      const registrationSuccess = await registerProposalHash(proposalResponse.proposalId, txHash);
      
      if (registrationSuccess) {
        console.log('트랜잭션 해시 등록 성공!');
        setTxState(prev => ({
          ...prev,
          successMessage: '안건이 성공적으로 등록되었습니다!',
          isProcessing: false,
          currentStep: '완료',
        }));
        
        // 폼 초기화
        setTitle('');
        setContent('');
        
        // 3초 후 모달 닫기
        setTimeout(() => {
          onClose();
        }, 3000);
      } else {
        throw new Error('서버에 트랜잭션 해시 등록에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('안건 등록 프로세스 실패:', error);
      setTxState(prev => ({
        ...prev,
        error: error.message || '안건 등록 중 오류가 발생했습니다.',
        isProcessing: false,
        currentStep: '실패',
      }));
      setError(error.message || '제안 등록 중 오류가 발생했습니다.');
    }
  };

  // 모달이 닫힐 때 폼 초기화
  const handleClose = () => {
    setTitle('');
    setContent('');
    setError('');
    setTxState({
      isProcessing: false,
      currentStep: '',
      error: '',
      successMessage: '',
      txHash: '',
    });
    onClose();
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 z-50 flex items-center justify-center"
          style={{
            maxWidth: '360px',
            maxHeight: '743px',
            margin: 'auto',
            left: '0',
            right: '0'
          }}
        >
          {/* 배경 오버레이 */}
          <div 
            className="fixed inset-0 bg-black/60"
            onClick={handleClose}
          />
          
          {/* 모달 컨텐츠 */}
          <motion.div
            initial={{ scale: 0.9, y: 20 }}
            animate={{ scale: 1, y: 0 }}
            exit={{ scale: 0.9, y: 20 }}
            className="relative mx-4 bg-gray-900 rounded-xl p-6 w-full max-w-[328px] z-10"
          >
            <h2 className="text-xl font-['Giants-Bold'] text-white mb-6">
              {formatTeamName(team)} 팀에 제안하기
            </h2>
            
            {/* 에러 메시지 */}
            {error && (
              <div className="mb-4 p-3 bg-red-900/40 border border-red-500 rounded-md text-red-400 text-sm">
                {error}
              </div>
            )}
            
            {/* 트랜잭션 상태 표시 */}
            {txState.isProcessing && txState.currentStep && (
              <div className="mb-4 p-3 bg-blue-900/30 border border-blue-600 rounded-md text-blue-300 text-sm">
                <div className="flex items-center">
                  <span className="inline-block text-blue-400 font-bold mr-2 animate-pulse">▶</span>
                  <span>{txState.currentStep}</span>
                </div>
              </div>
            )}
            
            {/* 성공 메시지 */}
            {txState.successMessage && (
              <div className="mb-4 p-3 bg-green-900/30 border border-green-600 rounded-md text-green-300 text-sm">
                <strong>성공: </strong>{txState.successMessage}
              </div>
            )}
            
            {/* 트랜잭션 해시 표시 */}
            {txState.txHash && (
              <div className="p-3 bg-gray-800 text-gray-300 rounded mb-4 text-xs">
                <p className="font-medium mb-1">트랜잭션 해시:</p>
                <div className="break-all">{txState.txHash}</div>
                
                {/* 블록 익스플로러 링크 */}
                {BLOCK_EXPLORER && (
                  <a 
                    href={`${BLOCK_EXPLORER}/tx/${txState.txHash}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-blue-400 hover:underline mt-2 inline-block"
                  >
                    블록 익스플로러에서 보기
                  </a>
                )}
              </div>
            )}
            
            {/* 폼 필드 */}
            <div className="mb-4">
              <label className="block text-gray-400 text-sm mb-2">제안 제목</label>
              <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                className="w-full bg-gray-800 border border-gray-700 rounded-md p-3 text-white focus:outline-none focus:ring-1 focus:ring-blue-500"
                placeholder="제안 제목을 입력하세요"
                maxLength={50}
                disabled={txState.isProcessing}
              />
            </div>
            
            <div className="mb-4">
              <label className="block text-gray-400 text-sm mb-2">제안 내용</label>
              <textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                className="w-full bg-gray-800 border border-gray-700 rounded-md p-3 text-white focus:outline-none focus:ring-1 focus:ring-blue-500 min-h-[120px]"
                placeholder="구체적인 제안 내용을 작성해주세요"
                maxLength={500}
                disabled={txState.isProcessing}
              />
            </div>
            
            <div className="text-gray-400 text-sm mb-6">
              <p>- 제안 등록에는 10 팬토큰이 사용됩니다.</p>
              <p>- 목표 투표수 100 에 도달하면 구단에 전달됩니다.</p>
              <p>- 7일 이내에 목표에 도달하지 못하면 자동 소멸됩니다.</p>
            </div>
            
            <div className="flex space-x-3">
              <button
                onClick={handleClose}
                disabled={txState.isProcessing}
                className="flex-1 py-3 rounded-full border border-gray-700 text-white hover:bg-gray-800 transition-colors"
              >
                취소
              </button>
              <button
                onClick={handleSubmit}
                disabled={txState.isProcessing}
                className={`flex-1 py-3 rounded-full font-['Pretendard-Regular'] transition-colors
                  ${txState.isProcessing
                    ? 'bg-gray-600 text-gray-400 cursor-not-allowed'
                    : 'bg-white text-black hover:bg-gray-200'}`}
              >
                {txState.isProcessing ? '제출 중...' : '제안하기'}
              </button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default SuggestModal;