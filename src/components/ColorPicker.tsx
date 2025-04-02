import React from 'react';
import { motion } from 'framer-motion';
import { ChromePicker } from 'react-color';

interface ColorPickerProps {
  isOpen: boolean;
  onClose: () => void;
  onSelectColor: (color: string) => void;
  selectedColor: string;
}

const ColorPicker: React.FC<ColorPickerProps> = ({ 
  isOpen, 
  onClose, 
  onSelectColor, 
  selectedColor 
}) => {
  const variants = {
    open: { x: '0%' },
    closed: { x: '120%' },
  };

  return (
    <motion.div
      initial="closed"
      animate={isOpen ? "open" : "closed"}
      variants={variants}
      transition={{ 
        type: "tween",
        duration: 0.8,
        ease: "easeInOut"
      }}
      className="absolute top-0 right-0 w-full h-full bg-black z-50"
      style={{
        aspectRatio: '360/743',
        maxHeight: 'min(743px, 100vh)',
      }}
    >
      <div className="h-full flex flex-col">
        {/* 헤더 */}
        <div className="flex justify-between items-center p-6">
          <span className="text-white text-xl font-['Giants-Bold']">Color Picker</span>
          <button onClick={onClose} className="text-white text-2xl">×</button>
        </div>

        {/* 컬러 피커 */}
        <div className="flex-1 px-4 flex justify-center items-center">
          <div className="w-full">
            <ChromePicker
              color={selectedColor}
              onChange={(color) => onSelectColor(color.hex)}
              disableAlpha={true}
              styles={{
                default: {
                  picker: {
                    width: '100% !important',
                    height: '320px !important',
                    background: '#1a1a1a',
                    boxShadow: 'none',
                    borderRadius: '12px',
                  },
                  saturation: {
                    borderRadius: '12px 12px 0 0',
                    paddingBottom: '100%',
                    height: '240px !important'
                  },
                  hue: {
                    height: '32px',
                    borderRadius: '0 0 12px 12px',
                    marginTop: '8px'
                  }
                }
              }}
            />
          </div>
        </div>

        {/* 하단 버튼 */}
        <div className="p-6">
          <button
            onClick={onClose}
            className="w-full py-4 bg-white text-black rounded-full font-['Pretendard-Regular']"
          >
            선택 완료
          </button>
        </div>
      </div>
    </motion.div>
  );
};

export default ColorPicker;
