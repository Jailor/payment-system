import React from "react";
import "../styles/Home.css";
import { authenticate } from './auth/AuthUtils.js';
function Home() {
  const authResult = authenticate();

  const username =  sessionStorage.getItem("username");
  const DEBUG = true;

  if(!authResult) return;
  
  return (
    <div className="home-container">
      <h1 className="welcome-text">
        Welcome {username}! <br/>
        {DEBUG && (
        <div>
        Longitude: {sessionStorage.getItem("longitude")} <br/>
        Latitude: {sessionStorage.getItem("latitude")} <br/>
        </div>) }
        
      </h1>
    </div>
  );
}

export default Home;
