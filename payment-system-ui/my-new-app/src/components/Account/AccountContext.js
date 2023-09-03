import React, { createContext, useContext, useState, useEffect } from 'react';

const AccountContext = createContext();

export function useAccountContext() {
  return useContext(AccountContext);
}

export function AccountContextProvider({ children }) {
  const storedAccountObject = JSON.parse(localStorage.getItem('accountObject'));
  const [accountObject, setAccountObject] = useState(storedAccountObject || null);

  // Effect to store accountObject in localStorage whenever it changes
  useEffect(() => {
    if (accountObject) {
      localStorage.setItem('accountObject', JSON.stringify(accountObject));
    } else {
      localStorage.removeItem('accountObject'); // removes the item if accountObject is null or undefined
    }
  }, [accountObject]);

  return (
    <AccountContext.Provider value={{ accountObject, setAccountObject }}>
      {children}
    </AccountContext.Provider>
  );
}
