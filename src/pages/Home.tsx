import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useUserStore } from '../stores/authStore';

const Home: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useUserStore();

  React.useEffect(() => {
    if (isAuthenticated) {
      navigate('/main');
    }
  }, [isAuthenticated, navigate]);

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="text-center">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">Welcome to Betty</h1>
        <p className="text-xl text-gray-600">Your KBO Fan Platform</p>
      </div>
    </div>
  );
};

export default Home;