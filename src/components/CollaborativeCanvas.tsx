"use client";

import React, { useEffect, useRef, useState, forwardRef } from 'react';
import { useWebSocketService } from '../services/websocketService';
import { Pixel } from '../types/types';
import { useCanvasStore } from '../stores/canvasStore';
import { useUserStore } from '../stores/authStore';
import { ColorPicker } from './ColorPicker';

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
}

const CollaborativeCanvas: React.FC<CollaborativeCanvasProps> = ({ team }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const gridCanvasRef = useRef<HTMLCanvasElement>(null);
  const pixelCanvasRef = useRef<OffscreenCanvas | null>(null);
  const isDrawingRef = useRef(false);
  const [scale, setScale] = useState(1);
  
  // 사용자 정보 가져오기
  const { walletAddress } = useUserStore();
  
  // 캔버스 스토어 사용
  const { 
    selectedColor, 
    isConnected, 
    setIsConnected, 
    updatePixels, 
    updateOrAddPixel 
  } = useCanvasStore();

  // 색상 데이터 맵 (x,y -> 색상)
  const pixelColorMapRef = useRef(new Map<string, string>());

  const { connect, disconnect, sendPixel } = useWebSocketService({
    onConnect: () => {
      console.log('WebSocket 연결 성공');
      setIsConnected(true);
    },
    onDisconnect: () => {
      console.log('WebSocket 연결 해제');
      setIsConnected(false);
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

  useEffect(() => {
    console.log('컴포넌트 마운트, walletAddress:', walletAddress);
    if (!canvasRef.current || !gridCanvasRef.current) {
      console.log('캔버스 참조가 없음');
      return;
    }

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
    
    // 이벤트 리스너
    canvas.addEventListener('mousedown', handlePointerDown);
    canvas.addEventListener('mousemove', handlePointerMove);
    canvas.addEventListener('mouseup', handlePointerUp);
    canvas.addEventListener('mouseleave', handlePointerUp);
    canvas.addEventListener('touchstart', handleTouchStart, { passive: false });
    canvas.addEventListener('touchmove', handleTouchMove, { passive: false });
    canvas.addEventListener('touchend', handlePointerUp);
    
    // 연결
    connect(walletAddress || "anonymous");
    
    return () => {
      window.removeEventListener('resize', resizeCanvas);
      resizeObserver.disconnect();
      canvas.removeEventListener('mousedown', handlePointerDown);
      canvas.removeEventListener('mousemove', handlePointerMove);
      canvas.removeEventListener('mouseup', handlePointerUp);
      canvas.removeEventListener('mouseleave', handlePointerUp);
      canvas.removeEventListener('touchstart', handleTouchStart);
      canvas.removeEventListener('touchmove', handleTouchMove);
      canvas.removeEventListener('touchend', handlePointerUp);
      disconnect();
      // 컴포넌트 언마운트 시 초기화 상태 초기화
      isCanvasReset.current = false;
    };
  }, []); // walletAddress 의존성 제거
  
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

  return (
    <div className="w-full flex flex-col">
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
    </div>
  );
}

export default CollaborativeCanvas;