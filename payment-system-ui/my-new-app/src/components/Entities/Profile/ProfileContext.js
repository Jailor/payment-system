import React, { createContext, useContext, useState, useEffect } from 'react';

const ProfileContext = createContext();

export function useProfileContext() {
  return useContext(ProfileContext);
}

export function ProfileContextProvider({ children }) {
  const storedProfileObject = JSON.parse(localStorage.getItem('profileObject'));
  const [profileObject, setProfileObject] = useState(storedProfileObject || null);

  // Effect to store profileObject in localStorage whenever it changes
  useEffect(() => {
    if (profileObject) {
      localStorage.setItem('profileObject', JSON.stringify(profileObject));
    } else {
      localStorage.removeItem('profileObject'); // removes the item if profileObject is null or undefined
    }
  }, [profileObject]);

  return (
    <ProfileContext.Provider value={{ profileObject, setProfileObject }}>
      {children}
    </ProfileContext.Provider>
  );
}
