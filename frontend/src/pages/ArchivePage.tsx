import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import { useStore } from '../stores/useStore';
import Sidebar from '../components/Sidebar';

// axios 인스턴스 (API 호출용)
const axiosInstance = axios.create({
  baseURL: 'https://j12a609.p.ssafy.io/api/v1/display',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  withCredentials: true,
});

const ACCESS_TOKEN_KEY = 'accessToken';

export const setAccessToken = (token: string) => {
  localStorage.setItem(ACCESS_TOKEN_KEY, token);
};

export const removeAccessToken = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
};

export const getAccessToken = () => {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
};

axiosInstance.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    console.log('=== Axios Request Interceptor Debug ===');
    console.log('1. Request URL:', config.url);
    console.log('2. Request method:', config.method);
    console.log('3. Access token:', token);
    console.log('4. Request headers:', config.headers);
    console.log('=== End Debug ===');
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// API에서 반환하는 타입
interface Display {
  id: number;
  gameId: number;
  teamId: number;
  inning: number;
  displayUrl: string;
  createdAt: string;
}

// API 응답 타입
interface DisplayResponse {
  displayList: Display[];
}

// 기존 픽셀 아트 인터페이스 (UI에 맞게 표시)
interface PixelArt {
  id: number;
  title: string;
  image: string;
  description: string;
  creator: string;
  createdAt: string;
  matchTeams: string[];
}

const teamNames = [
  "",
  "롯데 자이언츠",
  "키움 히어로즈",
  "NC 다이노스",
  "LG 트윈스",
  "두산 베어스",
  "KT 위즈",
  "SSG 랜더스",
  "KIA 타이거즈",
  "삼성 라이온즈",
  "한화 이글스"
];

const ArchivePage: React.FC = () => {
  const navigate = useNavigate();
  const { toggleSidebar } = useStore();
  const [searchTerm, setSearchTerm] = useState('');
  const [pixelArts, setPixelArts] = useState<PixelArt[]>([]);

  // API를 호출해서 데이터를 불러오고, 이를 PixelArt 형태로 매핑
  useEffect(() => {
    const fetchDisplays = async () => {
      try {
        // 실제 엔드포인트 주소는 백엔드 문서를 참고하여 수정하세요.
        const response = await axiosInstance.get<DisplayResponse>('');
        const displays = response.data.displayList;
        

        // Display 데이터를 PixelArt 데이터로 매핑
        const mappedArts: PixelArt[] = displays.map((display) => ({
          
          id: display.id,
          title: `${display.gameId} 번째 [${teamNames[display.teamId]}]`,
          image: display.displayUrl,
          description: `Team ${display.teamId} 경기 기록`,
          // creator 및 matchTeams는 백엔드에서 해당 정보가 없으므로 기본값 처리 (필요에 따라 다른 방식으로 매핑)
          creator: 'BETTY', 
          createdAt: new Date(display.createdAt).toLocaleString(),
          matchTeams: [] // 팀 목록 정보가 없으므로 빈 배열로 처리 (필요시 추가 API 호출 등 고려)
        }));

        setPixelArts(mappedArts);
      } catch (error) {
        console.error('픽셀 아트 데이터를 불러오는데 실패했습니다:', error);
      }
    };

    fetchDisplays();
  }, []);

  // 검색어를 기준으로 필터링
  const filteredArts = pixelArts.filter(art =>
    art.title.toLowerCase().includes(searchTerm.toLowerCase())
    // 팀 정보가 있는 경우 matchTeams에서도 검색하도록 할 수 있음.
  );

  // 픽셀 아트 클릭 시 상세 페이지로 이동
  const handleArtClick = (art: PixelArt) => {
    navigate(`/archive/${art.id}`, { state: { art } });
  };

  return (
    <div className="relative h-full bg-black text-white overflow-hidden">
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <button 
          onClick={() => navigate(-1)} 
          className="w-[12px] h-[12px]"
        >
          <img src={backImg} alt="Back" className="w-full h-full" />
        </button>
        <h1 className="text-xl font-['Giants-Bold']">픽셀 아트 아카이브</h1>
        <div className="w-5 h-5 flex items-center justify-center">
          <button 
            onClick={() => toggleSidebar(true)}
            className="w-5 h-5"
          >
            <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
          </button>
        </div>
      </div>

      {/* 검색바 */}
      <div className="pt-20 px-6 pb-3">
        <input
          type="text"
          placeholder="픽셀 아트 또는 게임 검색"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full px-4 py-2 text-sm rounded-full bg-gray-800 text-white placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-red-500"
        />
      </div>

      {/* 픽셀 아트 리스트 */}
      <div className="h-[calc(100%-120px)] overflow-y-auto scrollbar-hide">
        <div className="px-6 py-3">
          <div className="grid grid-cols-2 gap-3">
            {filteredArts.map((art) => (
              <div 
                key={art.id}
                onClick={() => handleArtClick(art)}
                className="bg-gray-900 rounded-lg overflow-hidden border border-gray-800 cursor-pointer hover:border-red-500 transition-colors"
              >
                <div className="aspect-square relative">
                  <img
                    src={art.image}
                    alt={art.title}
                    className="w-full h-full object-cover"
                  />
                  <div className="absolute bottom-1.5 left-1.5 px-1.5 py-0.5 rounded-full text-[10px] text-gray-300 bg-black/70">
                    {art.creator}
                  </div>
                </div>
                <div className="p-2">
                  <h3 className="font-['Giants-Bold'] text-xs mb-0.5 truncate">{art.title}</h3>
                  <p className="text-gray-400 text-[10px] mb-1.5">{art.description}</p>
                  <p className="text-gray-500 text-[10px]">{art.createdAt}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <Sidebar />
    </div>
  );
};

export default ArchivePage;
