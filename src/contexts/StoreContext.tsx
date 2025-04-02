import React, { createContext, useContext, useState } from 'react';

interface UserToken {
  team: string;
  amount: number;
}

interface StoreContextType {
  userTokens: UserToken[];
  setUserTokens: React.Dispatch<React.SetStateAction<UserToken[]>>;
}

const StoreContext = createContext<StoreContextType | undefined>(undefined);

export const StoreProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [userTokens, setUserTokens] = useState<UserToken[]>([]);

  return (
    <StoreContext.Provider value={{ userTokens, setUserTokens }}>
      {children}
    </StoreContext.Provider>
  );
};

export const useStore = () => {
  const context = useContext(StoreContext);
  if (context === undefined) {
    throw new Error('useStore must be used within a StoreProvider');
  }
  return context;
}; 