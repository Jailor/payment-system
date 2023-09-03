import React, { createContext, useContext, useState, useEffect } from 'react';

const UserContext = createContext();

export function useUserContext() {
  return useContext(UserContext);
}

export function UserContextProvider({ children }) {
  const storedUserObject = JSON.parse(localStorage.getItem('userObject'));
  const [userObject, setUserObject] = useState(storedUserObject || null);

  // Effect to store userObject in localStorage whenever it changes
  useEffect(() => {
    if (userObject) {
      localStorage.setItem('userObject', JSON.stringify(userObject));
    } else {
      localStorage.removeItem('userObject'); // removes the item if userObject is null or undefined
    }
  }, [userObject]);

  return (
    <UserContext.Provider value={{ userObject, setUserObject }}>
      {children}
    </UserContext.Provider>
  );
}
