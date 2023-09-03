import React, { useEffect } from "react";
import "../styles/Home.css";
import { useState, useRef } from "react";
import axios from "../api/axios";
import { useUserContext } from './User/UserContext';
import { useCustomerContext } from "./Customer/CustomerContext";
import { authenticate } from './auth/AuthUtils.js';
function Home() {
  const authResult = authenticate();

  const USER_URL = "/user"
  const CUSTOMER_URL = "/customer"
  const username =  sessionStorage.getItem("username");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { setUserObject } = useUserContext();
  const {setCustomerObject} = useCustomerContext();
  const [errors, setErrors] = useState(""); 
  const [show, setShow] = useState(false); 
  const [randNum, setRandNum] = useState(0);
  const DEBUG = true;

  const handleErrors = (errors) => {
    if (errors.length > 0) {
      console.log(errors);
      const errorString = errors
        .map((error) => error.errorMessage)
        .join("\n");
      setErrors(errorString);
      setShow(true);
      return true;
    }
    return false;
  };

  useEffect(() => {
    
    async function fetchData() {
      if(!authResult) return;
      if(sessionStorage.getItem("profileType") === "CUSTOMER"){
        try {
          const response = await axios.get(`${USER_URL}/${username}`);
          const responseCustomer = await axios.get( `${CUSTOMER_URL}/${response.data.object.email}`);
          setUserObject(response.data.object);
          if(!handleErrors(responseCustomer.data.errors)){
            console.log(response.data.object);
            setCustomerObject(response.data.object);
        }
          setCustomerObject(responseCustomer.data.object);
          setLoading(false); // Data has been fetched, so set loading to false
        } catch (error) {
          console.error("Error fetching user data:", error);
          setError("Error fetching data"); // Set error state if there's an error
          setLoading(false); // Data fetching is completed (even if there's an error)
        }
    }
    else{
      setLoading(false);
    }
  }
    fetchData();
  }, []);

  useEffect(() => {
    if(!authResult) return;
    async function fetchSecureData() {
        try {
          const securityResponse = await axios.get('/secure');
          console.log(securityResponse);
          setRandNum(securityResponse.data.object);
        } catch (error) {
          console.error("Error fetching secure data:", error);
        }
      }
      fetchSecureData();
  }, []);

  if (loading) {
    return <div>Loading...</div>; // Show a loading message while waiting for data
  }

  if (error) {
    return <div>Error: {error}</div>; // Show an error message if the API call failed
  }
  return (
    <div className="home-container">
      <h1 className="welcome-text">
        Welcome {username}! <br/>
        {DEBUG && (<div>
        Longitude: {sessionStorage.getItem("longitude")} <br/>
        Latitude: {sessionStorage.getItem("latitude")} <br/>
        Random number: {randNum}</div>) }
        
      </h1>
    </div>
  );
}

export default Home;
