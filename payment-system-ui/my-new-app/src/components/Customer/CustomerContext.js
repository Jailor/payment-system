import React, { createContext, useContext, useState, useEffect } from 'react';

const CustomerContext = createContext();

export function useCustomerContext() {
  return useContext(CustomerContext);
}

export function CustomerContextProvider({ children }) {
  const storedCustomerObject = JSON.parse(localStorage.getItem('customerObject'));
  const [customerObject, setCustomerObject] = useState(storedCustomerObject || null);

  // Effect to store customerObject in localStorage whenever it changes
  useEffect(() => {
    if (customerObject) {
      localStorage.setItem('customerObject', JSON.stringify(customerObject));
    } else {
      localStorage.removeItem('customerObject'); // removes the item if customerObject is null or undefined
    }
  }, [customerObject]);

  return (
    <CustomerContext.Provider value={{ customerObject, setCustomerObject }}>
      {children}
    </CustomerContext.Provider>
  );
}
