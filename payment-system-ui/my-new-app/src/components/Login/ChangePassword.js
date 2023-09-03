import React from "react";
import "../../styles/LoginPage.css";
import { useState, useRef } from "react";
import axios from "../../api/axios";
import { useNavigate } from "react-router-dom";
import { authenticate } from '../auth/AuthUtils.js';

function ChangePassword() {
  const authResult = authenticate();

  const [username, setUsername] = useState("");
  const [oldPassword, setOldPassword] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent default form submission

    // Create the LoginDTO object
    const loginData = {
      username: username,
      oldPassword: oldPassword,
      newPassword: password,
    };

    console.log(loginData);

    if (password !== repeatPassword) {
      alert("Passwords do not match");
      return;
    }

    try {
      // Make the POST request to
      const response = await axios.post("/user/change-password", loginData);
      if (response.data.errors.length > 0) {
        alert("Login error: " + response.data.errors[0].errorMessage);
        return;
      }
      else{
        alert("Password changed successfully!");
        navigate("/logout");
      }
      
    } catch (error) {
      // Handle errors here if needed
      console.error("Login error:", error);
      alert("Login error: " + error.message);
    }
  };

  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  return (
    <div className="login-container">
      <h1>Change password</h1>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            name="username"
            placeholder="Enter your username"
            required
            className="form__input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label htmlFor="oldPassword">Current password:</label>
          <input
            type="password"
            id="oldPassword"
            name="oldPassword"
            className="form__input"
            placeholder="Enter your current password"
            required
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">New password:</label>
          <input
            type="password"
            id="password"
            name="password"
            className="form__input"
            placeholder="Enter your new password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label htmlFor="repeatPassword">Repeat new password:</label>
          <input
            type="password"
            id="repeatPassword"
            name="repeatPassword"
            className="form__input"
            placeholder="Repeat your new password"
            required
            value={repeatPassword}
            onChange={(e) => setRepeatPassword(e.target.value)}
          />
        </div>
        <button type="submit" className="login-button">
          Change password
        </button>
      </form>
    </div>
  );
}
export default ChangePassword;
