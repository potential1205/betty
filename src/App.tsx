import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import MainPage from './pages/MainPage';
import './index.css';
import TeamPage from './pages/TeamPage';
import MyPage from './pages/Mypage';
import LogoScreen from './pages/LogoScreen';
import ArchivePage from './pages/ArchivePage';
import ArchiveDetailPage from './pages/ArchiveDetailPage';

const App: React.FC = () => {
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
                <Route path="/my-page" element={<MyPage />} />
                <Route path="/archive" element={<ArchivePage />} />
                <Route path="/archive/:artId" element={<ArchiveDetailPage />} />
              </Routes>
            </div>
          </div>
        </div>
      </div>
    </BrowserRouter>
  );
};

export default App;