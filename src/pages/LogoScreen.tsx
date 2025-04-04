import { motion } from 'framer-motion'
import { useNavigate } from 'react-router-dom'
import { useStore } from '../stores/useStore'
import { useUserStore } from '../stores/authStore'
import { useState } from 'react'

const letters = ['B', 'E', 'T', 'T', 'Y']

function LogoScreen() {
  const navigate = useNavigate()
  const { nickname, setNickname } = useStore()
  const {
    login,
    isLoading,
    error,
    needsNickname,
    registerNickname,
    checkNickname,
  } = useUserStore();

  const [step, setStep] = useState<'LOGIN' | 'NICKNAME'>('LOGIN');

  const handleLogin = async () => {
    try {
      const loginSuccess = await login('google');
      if (!loginSuccess) return;

      const hasNickname = await checkNickname();
      if (hasNickname) {
        navigate('/home');
      } else {
        setStep('NICKNAME');
      }
    } catch (error) {
      console.error('로그인 오류: ', error);
    }
  };

  const handleNicknameSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!nickname.trim()) return;

    const success = await registerNickname(nickname.trim());
    if (success) {
      navigate('/home');
    }
  };

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center h-full" style={{
        background: 'linear-gradient(145deg, #0c1b4d 0%, #1a237e 100%)'
      }}>
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
          className="flex space-x-2"
        >
          {[0, 1, 2].map((i) => (
            <motion.div
              key={i}
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{
                duration: 0.3,
                delay: i * 0.2,
                repeat: Infinity,
                repeatType: "reverse"
              }}
              className="w-2 h-2 bg-white rounded-full"
            />
          ))}
        </motion.div>

        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.5 }}
          className="mt-4 text-white text-sm font-['Giants-Bold']"
        >
          로딩 중...
        </motion.p>
      </div>
    )
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

          {step === 'LOGIN' ? (
            <button
              onClick={handleLogin}
              disabled={isLoading}
              className="bg-blue-950 text-white px-4 py-2 rounded-full font-['Giants-Bold'] text-sm hover:bg-blue-900 transition-all duration-300 hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? '로그인 중...' : 'Google로 로그인'}
            </button>
          ) : (
            <form 
              onSubmit={handleNicknameSubmit}
              className="w-full max-w-[240px] flex gap-2 items-center"
            >
              <input
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value.slice(0, 10))}
                placeholder="닉네임을 입력하세요"
                maxLength={10}
                className="flex-1 px-2 py-2 rounded-full bg-white/90 backdrop-blur-sm border-2 border-gray-100 text-gray-800 font-['Giants-Bold'] text-center focus:outline-none focus:border-blue-500 transition-all duration-300 text-sm"
                style={{ boxShadow: '0 4px 20px rgba(0, 0, 0, 0.1)' }}
                autoFocus
              />
              <button
                type="submit"
                disabled={!nickname.trim()}
                className={`px-3 py-2 rounded-full font-['Giants-Bold'] text-xs transition-all duration-300 whitespace-nowrap min-w-[50px] ${
                  nickname.trim()
                    ? 'bg-blue-950 text-white hover:bg-blue-900 hover:scale-105'
                    : 'bg-gray-100 text-gray-400 cursor-not-allowed'
                }`}
              >
                시작
              </button>
            </form>
         )}
          
          {error && (
            <motion.p 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="text-red-500 mt-4 text-sm"
            >
              {error}
            </motion.p>
          )}
        </motion.div>
      </div>
    </motion.div>
  )
}

export default LogoScreen