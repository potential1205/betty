import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import MainPage from './pages/MainPage';
import './index.css';
import TeamPage from './pages/TeamPage';
import MyPage from './pages/Mypage';
import LogoScreen from './pages/LogoScreen';
import ArchivePage from './pages/ArchivePage';
import ArchiveDetailPage from './pages/ArchiveDetailPage';
import ExchangePage from './pages/ExchangePage';
import { useEffect } from 'react';
import { useUserStore } from './stores/authStore';
import { web3auth } from './utils/web3auth';

const App: React.FC = () => {
  const { initialize, isAuthenticated, isInitialized, login } = useUserStore();

  // 앱 시작 시 Web3Auth 초기화
  useEffect(() => {
    const initializeAuth = async () => {
      console.log('앱 시작: Web3Auth 초기화 시작');
      await initialize();
      console.log('Web3Auth 초기화 완료, 인증 상태:', isAuthenticated);
    };

    initializeAuth();
  }, [initialize]);

  // Web3Auth 연결 상태 유지 (새로고침 대응)
  useEffect(() => {
    const reconnectWeb3Auth = async () => {
      if (isInitialized && isAuthenticated && !web3auth.connected) {
        try {
          console.log('새로고침 감지: Web3Auth 재연결 시도 중...');
          await login('google');
          console.log('Web3Auth 재연결 성공!');
        } catch (err) {
          console.error('Web3Auth 재연결 실패:', err);
        }
      }
    };

    reconnectWeb3Auth();
  }, [isInitialized, isAuthenticated, login]);

  return (
    <BrowserRouter>
      <div className="flex justify-center items-center min-h-screen w-screen bg-gray-200">
        <div className="relative w-full md:w-[360px] h-screen bg-gray-200 overflow-hidden flex items-center">
          <div
            className="relative w-full h-screen shadow-lg"
            style={{
              aspectRatio: '360/743',
              maxHeight: 'min(743px, 100vh)',
              margin: 'auto',
            }}
          >
            <div className="absolute inset-0">
              <Routes>
                <Route path="/" element={<LogoScreen />} />
                <Route path="/home" element={<Home />} />
                <Route path="/main" element={<MainPage />} />
                <Route path="/team" element={<TeamPage />} />
                <Route path="/my" element={<MyPage />} />
                <Route path="/archive" element={<ArchivePage />} />
                <Route path="/archive/:artId" element={<ArchiveDetailPage />} />
                <Route path="/exchange" element={<ExchangePage />} />
              </Routes>
            </div>
          </div>
        </div>
      </div>
    </BrowserRouter>
  );
};

export default App;