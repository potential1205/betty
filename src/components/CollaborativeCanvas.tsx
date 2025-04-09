"use client";

import React, { useEffect, useRef, useState, forwardRef } from 'react';
import { useWebSocketService } from '../services/websocketService';
import { Pixel } from '../types/types';
import { useCanvasStore } from '../stores/canvasStore';
import { useUserStore } from '../stores/authStore';
import { useDisplayStore } from '../stores/displayStore';
import { ColorPicker } from './ColorPicker';
import { ethers } from 'ethers';
import { useWalletStore } from '../stores/walletStore';
import { getTeamTokenName, getTeamTokenAddress } from '../apis/tokenApi';
import DisplayAccessABI from '../../abi/DisplayAccess.json';
import TokenABI from '../../abi/Token.json';

// 환경 변수에서 컨트랙트 주소 가져오기
const CONTRACT_ADDRESS = import.meta.env.VITE_DISPLAY_ACCESS_CONTRACT_ADDRESS;
const RPC_URL = import.meta.env.VITE_RPC_URL;
const BLOCK_EXPLORER = import.meta.env.VITE_BLOCK_EXPLORER;

// PixelUpdateMessage 인터페이스 정의 (websocketService.ts에서 가져오거나 여기서 다시 정의)
interface PixelUpdateMessage {
  gameId: number;
  teamId: number;
  r: number;
  c: number;
  color: string;
  walletAddress?: string;
}

const GRID_SIZE = 64;
const PIXEL_SIZE = 5;
const CANVAS_SIZE = GRID_SIZE * PIXEL_SIZE;

// 컴포넌트 외부에서 초기화 상태 관리
const isCanvasReset = { current: false };

interface CollaborativeCanvasProps {
  team: 'home' | 'away';
  gameId?: string; // 문자열로 받는 게임 ID
  teamId?: string; // 문자열로 받는 팀 ID
}

const CollaborativeCanvas: React.FC<CollaborativeCanvasProps> = ({ team, gameId = '1', teamId }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const gridCanvasRef = useRef<HTMLCanvasElement>(null);
  const pixelCanvasRef = useRef<OffscreenCanvas | null>(null);
  const isDrawingRef = useRef(false);
  const [scale, setScale] = useState(1);
  const [showPurchaseModal, setShowPurchaseModal] = useState(false);
  const [tokenName, setTokenName] = useState<string>('');
  const [tokenAddress, setTokenAddress] = useState<string>('');
  const [tokenBalance, setTokenBalance] = useState<string>('0');
  const [isLoadingBalance, setIsLoadingBalance] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [txHash, setTxHash] = useState<string | null>(null);
  const [connectionAttempted, setConnectionAttempted] = useState(false);
  
  // 사용자 정보 가져오기
  const { walletAddress, isAuthenticated } = useUserStore();
  const { getAccounts, exportPrivateKey } = useWalletStore();
  
  // 캔버스 스토어 사용
  const { 
    selectedColor, 
    isConnected, 
    setIsConnected, 
    updatePixels, 
    updateOrAddPixel 
  } = useCanvasStore();

  // 디스플레이 스토어 사용
  const {
    hasAccessPermission,
    currentGameId,
    currentTeamId,
    setCurrentIds,
    registerAccess
  } = useDisplayStore();

  // 색상 데이터 맵 (x,y -> 색상)
  const pixelColorMapRef = useRef(new Map<string, string>());

  const { connect, disconnect, sendPixel, getConnectionStatus } = useWebSocketService({
    onConnect: () => {
      console.log('WebSocket 연결 성공');
      setIsConnected(true);
    },
    onDisconnect: () => {
      console.log('WebSocket 연결 해제');
      setIsConnected(false);
      setConnectionAttempted(false); // 연결 시도 상태 리셋
    },
    onInitialCanvas: (pixels) => {
      console.log('초기 캔버스 데이터 수신:', pixels);
      pixelColorMapRef.current.clear();
      updatePixels(pixels);
      loadInitialPixels(pixels);
      renderPixelCanvas();
    },
    onPixelUpdate: (pixelData: Pixel | PixelUpdateMessage) => {
      console.log('픽셀 업데이트 수신:', pixelData);
      
      if ('r' in pixelData && 'c' in pixelData) {
        const pixel: Pixel = {
          x: pixelData.c,
          y: pixelData.r,
          color: pixelData.color.startsWith('#') ? pixelData.color : `#${pixelData.color}`,
          walletAddress: pixelData.walletAddress || ""
        };
        updatePixelInMap(pixel);
        updateOrAddPixel(pixel);
      } else {
        updatePixelInMap(pixelData as Pixel);
        updateOrAddPixel(pixelData as Pixel);
      }
      renderPixelCanvas();
    },
  });

  // 게임 ID와 팀 ID 설정 및 접근 권한 확인 (팀 선택 시 1회만 실행)
  useEffect(() => {
    if (!teamId || !gameId) return;
    
    // gameId와 teamId를 문자열로 보장
    const gameIdStr = gameId.toString();
    const teamIdStr = teamId.toString();
    
    console.log(`[CollaborativeCanvas] 초기 설정 - 게임ID: ${gameIdStr}, 팀ID: ${teamIdStr}`);
    setCurrentIds(gameIdStr, teamIdStr);
    
    // 접근 권한 확인
    const checkAccessPermission = async () => {
      try {
        console.log(`[CollaborativeCanvas] 접근 권한 확인 시작 - 게임ID: ${gameIdStr}, 팀ID: ${teamIdStr}`);
        const hasAccess = await useDisplayStore.getState().checkAccess(gameIdStr, teamIdStr);
        console.log(`[CollaborativeCanvas] 접근 권한 확인 결과: ${hasAccess ? '권한 있음' : '권한 없음'}`);
      } catch (error) {
        console.error('[CollaborativeCanvas] 접근 권한 확인 중 오류:', error);
      }
    };
    
    // 토큰 정보 가져오기
    const fetchTokenInfo = async () => {
      try {
        const numericTeamId = parseInt(teamIdStr, 10);
        
        console.log(`[CollaborativeCanvas] 토큰 정보 조회 시작 - 팀ID: ${numericTeamId}`);
        
        const tokenName = await getTeamTokenName(numericTeamId);
        const tokenAddress = await getTeamTokenAddress(numericTeamId);
        
        console.log(`[CollaborativeCanvas] 토큰 정보 조회 결과 - 이름: ${tokenName}, 주소: ${tokenAddress}`);
        
        setTokenName(tokenName || '팬토큰');
        setTokenAddress(tokenAddress);
        
        // 지갑 주소가 있으면 토큰 잔액 가져오기
        if (walletAddress && tokenAddress) {
          await fetchTokenBalance(walletAddress, tokenAddress);
        }
      } catch (error) {
        console.error('[CollaborativeCanvas] 토큰 정보 가져오기 실패:', error);
      }
    };
    
    // 접근 권한 확인 및 토큰 정보 가져오기 실행
    checkAccessPermission();
    fetchTokenInfo();
  }, [gameId, teamId, setCurrentIds, walletAddress]);

  // 접근 권한에 따른 캔버스 초기화 및 웹소켓 연결
  useEffect(() => {
    // 컴포넌트 마운트 시 실행되는 정리 함수
    let isMounted = true;
    
    // 접근 권한 상태 로깅
    console.log(`[CollaborativeCanvas] 접근 권한 상태: ${hasAccessPermission ? '있음' : '없음'}`);
    
    // 게임ID와 팀ID 확인
    if (!currentGameId || !currentTeamId) {
      console.log('[CollaborativeCanvas] 게임ID 또는 팀ID가 없어 웹소켓 연결 불가');
      return;
    }
    
    // 접근 권한이 없으면 웹소켓 연결 해제 및 함수 종료
    if (!hasAccessPermission) {
      console.log('[CollaborativeCanvas] 접근 권한 없음 - 웹소켓 연결 해제');
      disconnect();
      return;
    }
    
    // 캔버스 참조 확인
    if (!canvasRef.current || !gridCanvasRef.current) {
      console.log('[CollaborativeCanvas] 캔버스 참조가 없어 초기화 불가');
      return;
    }
    
    console.log('[CollaborativeCanvas] 접근 권한 있음 - 캔버스 초기화 및 웹소켓 연결 시작');
    
    // 메인 캔버스 설정
    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    
    // 그리드 캔버스 설정
    const gridCanvas = gridCanvasRef.current;
    const gridCtx = gridCanvas.getContext('2d');
    if (!gridCtx) return;
    
    // OffscreenCanvas 생성 (픽셀 데이터용)
    pixelCanvasRef.current = new OffscreenCanvas(GRID_SIZE, GRID_SIZE);
    
    // 캔버스 크기 설정
    canvas.width = CANVAS_SIZE;
    canvas.height = CANVAS_SIZE;
    gridCanvas.width = CANVAS_SIZE;
    gridCanvas.height = CANVAS_SIZE;
    
    // 그리드 그리기
    drawGrid(gridCtx);
    
    // 크기 조정 함수 수정
    const resizeCanvas = () => {
      if (!canvasRef.current) return;
      
      const container = canvasRef.current.parentElement;
      if (!container) return;
      
      // 부모 컨테이너의 크기를 기준으로 설정
      const containerWidth = container.clientWidth;
      const newScale = containerWidth / CANVAS_SIZE;
      setScale(newScale);
      
      canvas.style.width = `${containerWidth}px`;
      canvas.style.height = `${containerWidth}px`;
      gridCanvas.style.width = `${containerWidth}px`;
      gridCanvas.style.height = `${containerWidth}px`;
    };

    resizeCanvas();
    
    // ResizeObserver를 사용하여 부모 컨테이너 크기 변경 감지
    const resizeObserver = new ResizeObserver(() => {
      resizeCanvas();
    });
    
    if (canvasRef.current.parentElement) {
      resizeObserver.observe(canvasRef.current.parentElement);
    }

    window.addEventListener('resize', resizeCanvas);
    
    // 이벤트 리스너 등록
    canvas.addEventListener('mousedown', handlePointerDown);
    canvas.addEventListener('mousemove', handlePointerMove);
    canvas.addEventListener('mouseup', handlePointerUp);
    canvas.addEventListener('mouseleave', handlePointerUp);
    canvas.addEventListener('touchstart', handleTouchStart, { passive: false });
    canvas.addEventListener('touchmove', handleTouchMove, { passive: false });
    canvas.addEventListener('touchend', handlePointerUp);
    
    // 이미 연결 시도가 있었는지 확인
    if (!connectionAttempted) {
      setConnectionAttempted(true);
      
      // 새 연결 시도 전에 먼저 기존 연결 해제
      disconnect();
      
      // 약간의 지연 후 새 연결 시도 (연결 해제가 완료되도록)
      setTimeout(() => {
        if (!isMounted) return; // 컴포넌트가 언마운트된 경우 연결 시도 중단
        
        try {
          const gameIdNum = Number(currentGameId);
          const teamIdNum = Number(currentTeamId);
          
          if (isNaN(gameIdNum) || isNaN(teamIdNum)) {
            console.error(`[CollaborativeCanvas] 웹소켓 연결 실패: 잘못된 gameId(${currentGameId}) 또는 teamId(${currentTeamId})`);
            return;
          }
          
          console.log(`[CollaborativeCanvas] 웹소켓 연결: gameId=${gameIdNum}, teamId=${teamIdNum}`);
          connect(gameIdNum, teamIdNum);
        } catch (error) {
          console.error('[CollaborativeCanvas] 웹소켓 연결 중 오류:', error);
        }
      }, 300);
    }
    
    // 정리 함수
    return () => {
      isMounted = false;
      console.log('[CollaborativeCanvas] 컴포넌트 정리');
      window.removeEventListener('resize', resizeCanvas);
      resizeObserver.disconnect();
      
      // 이벤트 리스너 제거
      canvas.removeEventListener('mousedown', handlePointerDown);
      canvas.removeEventListener('mousemove', handlePointerMove);
      canvas.removeEventListener('mouseup', handlePointerUp);
      canvas.removeEventListener('mouseleave', handlePointerUp);
      canvas.removeEventListener('touchstart', handleTouchStart);
      canvas.removeEventListener('touchmove', handleTouchMove);
      canvas.removeEventListener('touchend', handlePointerUp);
      
      // 웹소켓 연결 해제 (안전하게)
      try {
        const { isConnected, isConnecting } = getConnectionStatus();
        if (isConnected || isConnecting) {
          console.log('[CollaborativeCanvas] 웹소켓 연결 해제 시도');
          disconnect();
        } else {
          console.log('[CollaborativeCanvas] 이미 연결이 해제되었거나 연결 시도 중이 아님');
        }
      } catch (e) {
        console.error('[CollaborativeCanvas] 웹소켓 연결 해제 중 오류:', e);
      }
      
      // 초기화 상태 초기화
      isCanvasReset.current = false;
    };
  }, [hasAccessPermission, currentGameId, currentTeamId]);
  
  // 그리드 그리기 함수
  const drawGrid = (ctx: CanvasRenderingContext2D) => {
    ctx.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
    ctx.strokeStyle = '#ddd';
    ctx.lineWidth = 1;
    
    // 수직선
    for (let i = 0; i <= GRID_SIZE; i++) {
      const pos = i * PIXEL_SIZE;
      ctx.beginPath();
      ctx.moveTo(pos, 0);
      ctx.lineTo(pos, CANVAS_SIZE);
      ctx.stroke();
    }
    
    // 수평선
    for (let i = 0; i <= GRID_SIZE; i++) {
      const pos = i * PIXEL_SIZE;
      ctx.beginPath();
      ctx.moveTo(0, pos);
      ctx.lineTo(CANVAS_SIZE, pos);
      ctx.stroke();
    }
  };
  
  // 초기 픽셀 로드
  const loadInitialPixels = (pixels: Pixel[]) => {
    console.log('초기 픽셀 로드 시작:', pixels.length);
    pixelColorMapRef.current.clear();
    pixels.forEach(pixel => {
      updatePixelInMap(pixel);
    });
    console.log('초기 픽셀 로드 완료, 현재 픽셀 맵 크기:', pixelColorMapRef.current.size);
  };
  
  // 픽셀 맵 업데이트
  const updatePixelInMap = (pixel: Pixel) => {
    const key = `${pixel.x},${pixel.y}`;
    pixelColorMapRef.current.set(key, pixel.color);
  };
  
  // 픽셀 캔버스 렌더링
  const renderPixelCanvas = () => {
    console.log('픽셀 캔버스 렌더링 시작');
    console.log('현재 픽셀 맵 크기:', pixelColorMapRef.current.size);
    
    if (!pixelCanvasRef.current || !canvasRef.current) {
      console.log('캔버스 참조가 없음');
      return;
    }
    
    const pixelCtx = pixelCanvasRef.current.getContext('2d');
    if (!pixelCtx) return;
    
    const mainCtx = canvasRef.current.getContext('2d');
    if (!mainCtx) return;
    
    // 픽셀 크기의 캔버스 클리어
    pixelCtx.clearRect(0, 0, GRID_SIZE, GRID_SIZE);
    
    // 각 픽셀 그리기
    pixelColorMapRef.current.forEach((color, key) => {
      const [x, y] = key.split(',').map(Number);
      
      pixelCtx.fillStyle = color;
      pixelCtx.fillRect(x, y, 1, 1);
    });
    
    // 메인 캔버스 클리어
    mainCtx.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
    
    // 픽셀 캔버스를 확대하여 메인 캔버스에 그리기
    mainCtx.imageSmoothingEnabled = false; // 픽셀화된 이미지를 부드럽게 처리하지 않음
    mainCtx.drawImage(
      pixelCanvasRef.current,
      0, 0, GRID_SIZE, GRID_SIZE,
      0, 0, CANVAS_SIZE, CANVAS_SIZE
    );
  };
  
  // 이벤트 핸들러
  const handlePointerDown = (e: MouseEvent) => {
    isDrawingRef.current = true;
    handlePointerEvent(e.offsetX, e.offsetY);
  };
  
  const handlePointerMove = (e: MouseEvent) => {
    if (!isDrawingRef.current) return;
    handlePointerEvent(e.offsetX, e.offsetY);
  };
  
  const handlePointerUp = () => {
    isDrawingRef.current = false;
  };
  
  const handleTouchStart = (e: TouchEvent) => {
    e.preventDefault();
    if (e.touches.length === 0 || !canvasRef.current) return;
    
    isDrawingRef.current = true;
    const rect = canvasRef.current.getBoundingClientRect();
    const touch = e.touches[0];
    const offsetX = touch.clientX - rect.left;
    const offsetY = touch.clientY - rect.top;
    
    handlePointerEvent(offsetX, offsetY);
  };
  
  const handleTouchMove = (e: TouchEvent) => {
    e.preventDefault();
    if (!isDrawingRef.current || e.touches.length === 0 || !canvasRef.current) return;
    
    const rect = canvasRef.current.getBoundingClientRect();
    const touch = e.touches[0];
    const offsetX = touch.clientX - rect.left;
    const offsetY = touch.clientY - rect.top;
    
    handlePointerEvent(offsetX, offsetY);
  };
  
  // 포인터 이벤트 처리
  const handlePointerEvent = (offsetX: number, offsetY: number) => {
    const x = Math.floor(offsetX / (PIXEL_SIZE * scale));
    const y = Math.floor(offsetY / (PIXEL_SIZE * scale));
    
    console.log('포인터 이벤트 발생:', { x, y, scale, offsetX, offsetY });
    
    if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
      const currentColor = useCanvasStore.getState().selectedColor;
      console.log('픽셀 그리기:', { x, y, color: currentColor });
      
      const pixel: Pixel = {
        x,
        y,
        color: currentColor,
        walletAddress: walletAddress || "anonymous",
      };
      
      sendPixel(pixel);
    }
  };

  // 토큰 잔액 가져오기 함수
  const fetchTokenBalance = async (address: string, tokenAddr: string) => {
    if (!address || !tokenAddr) return;
    
    setIsLoadingBalance(true);
    try {
      const provider = new ethers.JsonRpcProvider(RPC_URL);
      const tokenContract = new ethers.Contract(tokenAddr, TokenABI.abi, provider);
      const balance = await tokenContract.balanceOf(address);
      setTokenBalance(ethers.formatEther(balance));
    } catch (error) {
      console.error('토큰 잔액 가져오기 실패:', error);
    } finally {
      setIsLoadingBalance(false);
    }
  };
  
  // 접근 권한 구매 함수
  const purchaseAccess = async () => {
    console.log(`[CollaborativeCanvas] 구매 시작 - 게임ID: ${currentGameId}, 팀ID: ${currentTeamId}, 토큰 주소: ${tokenAddress}`);
    
    if (!walletAddress) {
      setError('지갑 주소가 없습니다. 먼저 로그인해주세요.');
      return;
    }
    
    if (!currentGameId || !currentTeamId) {
      setError(`게임 또는 팀 정보가 없습니다. (게임ID: ${currentGameId || '없음'}, 팀ID: ${currentTeamId || '없음'})`);
      return;
    }
    
    if (!tokenAddress) {
      setError('토큰 주소를 찾을 수 없습니다. 다시 시도해주세요.');
      return;
    }
    
    if (!CONTRACT_ADDRESS) {
      setError('컨트랙트 주소가 설정되어 있지 않습니다.');
      return;
    }
    
    setIsProcessing(true);
    setError(null);
    setSuccess(null);
    setTxHash(null);
    
    try {
      // 개인키 가져오기
      console.log('[CollaborativeCanvas] 개인키 가져오기 시도');
      const privateKey = await exportPrivateKey();
      if (!privateKey) {
        throw new Error('개인 키를 가져올 수 없습니다.');
      }
      
      console.log('[CollaborativeCanvas] 컨트랙트 인스턴스 생성');
      const provider = new ethers.JsonRpcProvider(RPC_URL);
      const wallet = new ethers.Wallet(privateKey, provider);
      
      // 토큰 컨트랙트 인스턴스 생성
      const tokenContract = new ethers.Contract(tokenAddress, TokenABI.abi, wallet);
      const accessContract = new ethers.Contract(CONTRACT_ADDRESS, DisplayAccessABI.abi, wallet);
      
      // 잔액 확인
      console.log('[CollaborativeCanvas] 토큰 잔액 확인');
      const balance = await tokenContract.balanceOf(walletAddress);
      const amount = ethers.parseEther('1'); // 1 팬토큰 고정 가격
      
      console.log(`[CollaborativeCanvas] 현재 잔액: ${ethers.formatEther(balance)} ${tokenName}`);
      
      if (balance < amount) {
        throw new Error(`토큰 잔액이 부족합니다. 필요: 1.0, 보유: ${ethers.formatEther(balance)}`);
      }
      
      // 토큰 승인 확인
      console.log('[CollaborativeCanvas] 토큰 승인 확인');
      const allowance = await tokenContract.allowance(walletAddress, CONTRACT_ADDRESS);
      
      if (allowance < amount) {
        console.log('[CollaborativeCanvas] 토큰 승인이 필요합니다. 승인 트랜잭션 시작...');
        const approveTx = await tokenContract.approve(CONTRACT_ADDRESS, amount, { gasPrice: 0 });
        
        // 승인 트랜잭션이 블록에 포함될 때까지 대기
        const approveReceipt = await approveTx.wait();
        console.log('[CollaborativeCanvas] 토큰 승인 완료! 트랜잭션 해시:', approveReceipt.hash);
      }
      
      // 액세스 구매 트랜잭션
      console.log(`[CollaborativeCanvas] 액세스 구매 트랜잭션 시작 - 게임ID: ${currentGameId}, 팀ID: ${currentTeamId}`);
      const purchaseTx = await accessContract.purchaseAccess(
        tokenAddress,
        parseInt(currentTeamId),
        parseInt(currentGameId),
        { gasPrice: 0 }
      );
      
      console.log('[CollaborativeCanvas] 트랜잭션 전송 완료. 채굴 대기 중...');
      
      // 트랜잭션이 블록에 포함될 때까지 대기하고 영수증 가져오기
      const receipt = await purchaseTx.wait();
      
      // 트랜잭션 해시 저장
      const hash = receipt.hash;
      setTxHash(hash);
      console.log('[CollaborativeCanvas] 트랜잭션 채굴 완료! 해시:', hash);
      
      // 백엔드 API 호출하여 트랜잭션 해시 등록
      console.log('[CollaborativeCanvas] 백엔드 API 호출하여 트랜잭션 해시 등록');
      const registerSuccess = await registerAccess(currentGameId, currentTeamId, hash);
      
      if (registerSuccess) {
        console.log('[CollaborativeCanvas] 백엔드 등록 성공!');
        setSuccess('전광판 접근 권한을 성공적으로 구매했습니다!');
        
        // 3초 후 모달 닫기
        setTimeout(() => {
          setShowPurchaseModal(false);
        }, 3000);
      } else {
        throw new Error('백엔드에 접근 권한 등록 실패');
      }
    } catch (error: any) {
      console.error('[CollaborativeCanvas] 액세스 구매 실패:', error);
      setError(error.message || '접근 권한 구매 중 오류가 발생했습니다.');
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="w-full flex flex-col">
      {hasAccessPermission ? (
        <div className="relative w-full">
          <canvas 
            ref={canvasRef} 
            width={CANVAS_SIZE}
            height={CANVAS_SIZE}
            className="w-full h-full bg-white"
          />
          <canvas 
            ref={gridCanvasRef} 
            width={CANVAS_SIZE}
            height={CANVAS_SIZE}
            className="absolute top-0 left-0 w-full h-full pointer-events-none"
          />
        </div>
      ) : (
        <div className="w-full h-64 flex flex-col items-center justify-center bg-gradient-to-br from-gray-800 to-black rounded-lg border border-gray-700">
          <div className="text-center px-4">
            <p className="text-white text-lg font-['Giants-Bold'] mb-3">전광판 접근 권한이 필요합니다</p>
            <p className="text-gray-400 text-sm mb-4">이 전광판에 그림을 그리려면 접근 권한을 구매해야 합니다</p>
            <button
              onClick={() => setShowPurchaseModal(true)}
              className="bg-white text-black px-6 py-2 rounded-full text-sm font-['Giants-Bold'] hover:bg-gray-200 transition-colors"
            >
              접근 권한 구매하기
            </button>
          </div>
        </div>
      )}
      
      {/* 구매 모달 */}
      {showPurchaseModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div 
            className="bg-white rounded-xl overflow-hidden shadow-xl"
            style={{ width: '100%', maxWidth: '360px' }}
          >
            {/* 헤더 */}
            <div className="flex justify-between items-center p-4 border-b border-gray-100">
              <div className="w-5"></div>
              <h1 className="text-lg font-['Giants-Bold'] text-gray-800">
                {team === 'home' ? '홈팀' : '어웨이팀'} 전광판 접근 권한 구매
              </h1>
              <button 
                onClick={() => setShowPurchaseModal(false)}
                className="w-5 h-5 flex items-center justify-center text-gray-400 hover:text-gray-600"
              >
                <svg
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  className="w-5 h-5"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>
            
            {/* 메인 컨텐츠 */}
            <div className="p-4">
              {/* 현재 보유 금액 */}
              <div className="bg-gradient-to-br from-black to-gray-800 rounded-xl p-3 shadow-lg mb-3">
                <p className="text-xs text-gray-400 mb-1">현재 보유 금액</p>
                <div className="flex items-baseline">
                  {isLoadingBalance ? (
                    <p className="text-sm text-gray-400">로딩 중...</p>
                  ) : (
                    <>
                      <p className="text-xl font-['Giants-Bold'] text-white">
                        {Number(tokenBalance).toLocaleString(undefined, {maximumFractionDigits: 4})}
                      </p>
                      <p className="text-sm text-gray-400 ml-1">{tokenName}</p>
                    </>
                  )}
                </div>
                {walletAddress && (
                  <div className="flex items-baseline mt-1">
                    <p className="text-xs text-gray-400">
                      지갑 주소: {walletAddress.slice(0, 6)}...{walletAddress.slice(-4)}
                    </p>
                  </div>
                )}
              </div>

              {/* 정보 표시 */}
              <div className="bg-blue-50 rounded-lg p-3 mb-3">
                <div className="flex justify-between items-center mb-2">
                  <span className="text-sm font-medium text-blue-700">필요한 {tokenName}</span>
                  <span className="text-base font-['Giants-Bold'] text-blue-700">1.0</span>
                </div>
                <p className="text-xs text-blue-600">
                  접근 권한을 구매하면 이 게임의 {team === 'home' ? '홈팀' : '어웨이팀'} 전광판에 그림을 그릴 수 있습니다.
                </p>

              </div>

              {/* 에러 메시지 */}
              {error && (
                <div className="bg-red-50 text-red-500 p-2 rounded-lg mb-3 text-sm">
                  {error}
                </div>
              )}

              {/* 성공 메시지 */}
              {success && (
                <div className="bg-green-50 text-green-600 p-2 rounded-lg mb-3 text-sm">
                  {success}
                </div>
              )}

              {/* 트랜잭션 해시 표시 */}
              {txHash && (
                <div className="bg-gray-50 rounded-lg p-2 mb-3">
                  <p className="text-xs font-medium text-gray-600 mb-1">트랜잭션 해시:</p>
                  <p className="text-xs text-gray-500 break-all">{txHash}</p>
                  {BLOCK_EXPLORER && (
                    <a 
                      href={`${BLOCK_EXPLORER}/tx/${txHash}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-blue-500 hover:underline text-xs mt-1 inline-block"
                    >
                      블록 익스플로러에서 보기
                    </a>
                  )}
                </div>
              )}

              {/* 버튼 */}
              {!isAuthenticated ? (
                <button
                  onClick={async () => {
                    try {
                      // useUserStore에서 로그인 함수를 호출
                      const login = useUserStore.getState().login;
                      await login('google');
                      
                      // 로그인 성공 후 토큰 잔액 새로고침
                      if (tokenAddress) {
                        const address = await getAccounts();
                        if (address) {
                          await fetchTokenBalance(address, tokenAddress);
                        }
                      }
                    } catch (err) {
                      console.error('로그인 실패:', err);
                      setError('로그인에 실패했습니다. 다시 시도해주세요.');
                    }
                  }}
                  className="w-full py-3 rounded-xl bg-black text-white text-base font-['Giants-Bold'] hover:bg-gray-800 transition-colors"
                >
                  지갑 연결하기
                </button>
              ) : (
                <button
                  onClick={purchaseAccess}
                  disabled={isProcessing || success !== null}
                  className={`w-full py-3 rounded-xl text-white text-base font-['Giants-Bold'] transition-colors ${
                    isProcessing || success !== null
                      ? 'bg-gray-300 cursor-not-allowed'
                      : 'bg-black hover:bg-gray-800'
                  }`}
                >
                  {isProcessing ? '처리 중...' : success ? '구매 완료' : '접근 권한 구매하기'}
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default CollaborativeCanvas;