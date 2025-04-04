"use client";

import React from 'react';
import { useCanvasStore } from '../stores/canvasStore';

export function ColorPicker() {
  const { selectedColor, setSelectedColor } = useCanvasStore();

  return (
    <input
      type="color"
      value={selectedColor}
      onChange={(e) => setSelectedColor(e.target.value)}
      style={{ width: '2.5rem', height: '2.5rem' }}
    />
  );
}
