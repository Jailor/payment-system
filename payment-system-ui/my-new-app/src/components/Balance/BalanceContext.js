import React, { createContext, useContext, useState } from "react";

const BalanceContext = createContext();

export function useBalanceContext() {
  return useContext(BalanceContext);
}

export function BalanceContextProvider({ children }) {
  const [balanceObject, setBalanceObject] = useState(null);

  return (
    <BalanceContext.Provider value={{ balanceObject, setBalanceObject }}>
      {children}
    </BalanceContext.Provider>
  );
}
