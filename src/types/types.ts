export interface Pixel {
    x: number;
    y: number;
    color: string;
    walletAddress: string;
  }
  
  export interface CanvasState {
    pixels: Pixel[];
    selectedColor: string;
    isConnected: boolean;
  } 