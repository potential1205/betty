import { motion } from 'framer-motion'
import { useNavigate } from 'react-router-dom'
import { useStore } from '../stores/useStore'
import { useState } from 'react'

const letters = ['B', 'E', 'T', 'T', 'Y']

function LogoScreen() {
  const navigate = useNavigate()
  const { nickname, setNickname } = useStore()
  const [showNicknameForm, setShowNicknameForm] = useState(false)

  const handleStart = () => {
    setShowNicknameForm(true)
  }

  const handleConfirm = () => {
    if (nickname.trim()) {
      navigate('/home')
    }
  }

  return (
    <motion.div
      className="flex flex-col items-center justify-center h-full relative overflow-hidden"
      initial={{ backgroundColor: '#0c1b4d' }}
      style={{
        background: 'linear-gradient(145deg, #0c1b4d 0%, #1a237e 100%)',
      }}
    >
      {/* 원형 배경 전환 애니메이션 */}
      <motion.div
        className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-20 h-20 rounded-full"
        style={{
          background: 'linear-gradient(145deg, #f8f9fa 0%, #e5e7eb 100%)',
          boxShadow: '0 4px 30px rgba(0, 0, 0, 0.1)',
        }}
        initial={{ scale: 0 }}
        animate={{
          scale: 20,
          transition: {
            delay: 2,
            duration: 2,
            ease: 'easeInOut',
          },
        }}
      />

      <div className="p-6 text-center relative z-10">
        <h1 className="text-6xl font-bold mb-6 flex justify-center relative overflow-hidden tracking-wider">
          {letters.map((letter, index) => (
            <motion.span
              key={index}
              initial={{
                x: (index % 2 === 0 ? -120 : 120),
                y: (index % 2 === 0 ? -60 : 60),
                opacity: 0,
                rotate: (index % 2 === 0 ? -90 : 90),
                marginRight: '25px',
                scale: 0.5,
                color: '#ffffff',
              }}
              animate={{
                x: 0,
                y: 0,
                opacity: 1,
                rotate: 0,
                marginRight: letter === ' ' ? '12px' : '1px',
                scale: 1,
                color: '#0c1b4d',
              }}
              transition={{
                duration: 0.6,
                delay: index * 0.15,
                type: 'spring',
                stiffness: 80,
                damping: 9,
                marginRight: {
                  delay: 1.2,
                  duration: 0.5,
                  ease: 'easeOut',
                },
                color: {
                  delay: 2.2,
                  duration: 1.8,
                  ease: 'easeInOut',
                },
              }}
              style={{
                fontFamily: 'Giants-Bold',
                textShadow: '2px 4px 8px rgba(0, 0, 0, 0.15)',
                WebkitTextStroke: '1px rgba(12, 27, 77, 0.1)',
              }}
            >
              {letter}
            </motion.span>
          ))}
        </h1>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{
            delay: 2.3,
            duration: 0.8,
            ease: 'easeOut',
          }}
          className="flex flex-col items-center justify-center mt-12"
        >
          <p 
            className="text-gray-700 mb-8 text-xl font-medium font-['Giants-Bold'] tracking-tight"
            style={{
              textShadow: '0 2px 4px rgba(0, 0, 0, 0.05)',
            }}
          >
            블록체인으로 만나는 <br /> 
            <span className="text-2xl mt-2 inline-block bg-gradient-to-r from-blue-950 to-blue-900 bg-clip-text text-transparent">
              새로운 야구 팬 경험
            </span>
          </p>

          {showNicknameForm ? (
            <motion.form 
              onSubmit={(e) => {
                e.preventDefault()
                handleConfirm()
              }}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="flex flex-col items-center gap-4"
            >
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
                className="flex items-center gap-2"
              >
                <input
                  type="text"
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value.slice(0, 10))}
                  placeholder="닉네임을 입력하세요"
                  maxLength={10}
                  className="w-48 px-3 py-2 rounded-lg bg-white border border-gray-200 text-gray-800 font-['Giants-Bold'] text-center focus:outline-none focus:border-blue-500 transition-colors"
                  style={{
                    boxShadow: '0 2px 10px rgba(0, 0, 0, 0.05)',
                  }}
                  autoFocus
                />
                <button
                  type="submit"
                  disabled={!nickname.trim()}
                  className={`bg-blue-950 text-white px-4 py-2 rounded-lg font-['Giants-Bold'] text-sm transition-colors ${
                    nickname.trim() ? 'hover:bg-blue-900' : 'opacity-50 cursor-not-allowed'
                  }`}
                >
                  확인
                </button>
              </motion.div>
            </motion.form>
          ) : (
            <button
              onClick={handleStart}
              className="bg-blue-950 text-white px-8 py-3 rounded-full font-['Giants-Bold'] text-lg hover:bg-blue-900 transition-colors"
            >
              시작하기
            </button>
          )}
        </motion.div>
      </div>
    </motion.div>
  )
}

export default LogoScreen
