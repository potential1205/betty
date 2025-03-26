import { motion } from 'framer-motion'
import { useNavigate } from 'react-router-dom'

const letters = ['B', 'E', 'T', 'T', 'Y']

function LogoScreen() {
  const navigate = useNavigate()

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

          <motion.button
            onClick={() => navigate('/home')}
            className="bg-blue-950 text-white px-8 py-3 rounded-full font-['Giants-Bold'] text-lg hover:bg-blue-900 transition-colors"
            initial={{ opacity: 0, y: 20, scale: 0.9 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            transition={{
              delay: 2.5,
              duration: 0.5,
              ease: 'easeOut',
            }}
            whileHover={{ 
              scale: 1.05,
              boxShadow: '0 4px 20px rgba(12, 27, 77, 0.3)',
            }}
            whileTap={{ scale: 0.95 }}
            style={{
              boxShadow: '0 4px 15px rgba(12, 27, 77, 0.2)',
            }}
          >
            시작하기
          </motion.button>
        </motion.div>
      </div>
    </motion.div>
  )
}

export default LogoScreen
