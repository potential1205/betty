import React, { useState } from 'react';
import { Quiz, QuizHistory } from '../constants/dummy';

interface QuizModalProps {
  quiz: Quiz;
  isActive: boolean;
  onAnswer: (answer: number) => void;
  currentTime: number;
  currentAnswer: number | null;
}

// 실제 퀴즈 모달 컴포넌트
const QuizModal: React.FC<QuizModalProps> = ({
  quiz,
  isActive,
  onAnswer,
  currentTime,
  currentAnswer,
}) => {
  return (
    <div className="bg-gray-800 rounded-lg p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-bold">{quiz.question}</h3>
        <div className="flex items-center gap-1">
          <svg className="w-4 h-4 text-red-500" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.486 2 2 6.486 2 12s4.486 10 10 10 10-4.486 10-10S17.514 2 12 2zm0 18c-4.411 0-8-3.589-8-8s3.589-8 8-8 8 3.589 8 8-3.589 8-8 8z"/>
            <path d="M13 7h-2v5.414l3.293 3.293 1.414-1.414L13 11.586z"/>
          </svg>
          <span className="text-red-500 font-bold">{currentTime}</span>
        </div>
      </div>
      <div className="space-y-2">
        {quiz.options.map((option, index) => {
          const isSelected = currentAnswer === index;
          
          return (
            <button
              key={index}
              onClick={() => onAnswer(index)}
              disabled={!isActive || currentTime === 0}
              className={`w-full p-3 text-left rounded-lg transition-colors
                ${isActive && currentTime > 0
                  ? isSelected
                    ? 'bg-blue-600' // 선택된 답변
                    : 'hover:bg-blue-600 bg-gray-700'
                  : 'bg-gray-700 opacity-50 cursor-not-allowed'
                }`}
            >
              {option}
            </button>
          )}
        )}
      </div>
    </div>
  );
};

// 퀴즈 히스토리 아이템 컴포넌트
export const QuizHistoryItem: React.FC<{
  quiz: Quiz;
  history: QuizHistory;
}> = ({ quiz, history }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const isCorrect = history.userAnswer === quiz.answer;
  
  return (
    <div className="bg-gray-800/50 rounded-lg p-4 mb-4">
      {/* 문제 내용 */}
      <div className="mb-3">
        <h3 className="text-sm text-gray-400">{quiz.question}</h3>
      </div>

      {/* 결과 헤더 */}
      <div className={`flex items-center justify-between ${isExpanded ? 'mb-4' : ''}`}>
        <div className="flex items-center gap-3">
          {history.userAnswer === null ? (
            <div className="text-yellow-500 text-2xl font-bold">시간 초과</div>
          ) : isCorrect ? (
            <div className="text-green-500 text-2xl font-bold">정답</div>
          ) : (
            <div className="text-red-500 text-2xl font-bold">오답</div>
          )}
          {!isExpanded && history.userAnswer !== null && (
            <div className="text-gray-400">
              {/* 선택한 답변 표시 */}
              <span>선택: {quiz.options[history.userAnswer]}</span>
              {!isCorrect && (
                <span className="ml-2">
                  (정답: {quiz.options[quiz.answer]})
                </span>
              )}
            </div>
          )}
        </div>
        <button
          onClick={() => setIsExpanded(!isExpanded)}
          className="text-gray-400 hover:text-white text-sm flex items-center gap-1"
        >
          {isExpanded ? '접기' : '상세보기'}
          <svg 
            className={`w-4 h-4 transition-transform ${isExpanded ? 'rotate-180' : ''}`}
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
          </svg>
        </button>
      </div>

      {/* 상세 내용 - 펼쳤을 때만 표시 */}
      {isExpanded && (
        <div className="space-y-2">
          {quiz.options.map((option, index) => {
            const isSelected = history.userAnswer === index;
            const isCorrect = quiz.answer === index;
            
            return (
              <div key={index} className="relative">
                <div
                  className={`w-full h-10 rounded-lg overflow-hidden ${
                    isSelected 
                      ? isCorrect 
                        ? 'bg-green-600/20'
                        : 'bg-red-600/20'
                      : isCorrect
                        ? 'bg-green-600/20'
                        : 'bg-gray-700/20'
                  }`}
                >
                  <div
                    className={`h-full ${
                      isSelected 
                        ? isCorrect
                          ? 'bg-green-600/40'
                          : 'bg-red-600/40'
                        : isCorrect
                          ? 'bg-green-600/40'
                          : 'bg-gray-700/40'
                    }`}
                    style={{ width: isSelected || isCorrect ? '100%' : '0%' }}
                  />
                </div>
                <div className="absolute top-0 left-0 right-0 h-10 px-3 flex items-center justify-between">
                  <span className="text-sm">{option}</span>
                  {(isSelected || isCorrect) && (
                    <span className={`text-sm ${
                      isCorrect ? 'text-green-400' : 'text-red-400'
                    }`}>
                      {isCorrect ? '정답' : '오답'}
                    </span>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default QuizModal;
