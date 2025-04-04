import { create } from 'zustand';
import { Pixel, CanvasState } from '../types/types';

interface CanvasStore extends CanvasState {
  setSelectedColor: (color: string) => void;
  setIsConnected: (isConnected: boolean) => void;
  updatePixels: (pixels: Pixel[]) => void;
  updateOrAddPixel: (pixel: Pixel) => void;
}

export const useCanvasStore = create<CanvasStore>((set) => ({
  pixels: [],
  selectedColor: '#000000', 
  isConnected: false,
  
  setSelectedColor: (color: string) => set({ selectedColor: color }),
  setIsConnected: (isConnected: boolean) => set({ isConnected }),
  updatePixels: (pixels: Pixel[]) => set({ pixels }),
  updateOrAddPixel: (pixel: Pixel) => set((state) => {
    const newPixels = [...state.pixels];
    const index = newPixels.findIndex(p => p.x === pixel.x && p.y === pixel.y);
    if (index >= 0) {
      newPixels[index] = pixel;
    } else {
      newPixels.push(pixel);
    }
    return { pixels: newPixels };
  }),
})); 