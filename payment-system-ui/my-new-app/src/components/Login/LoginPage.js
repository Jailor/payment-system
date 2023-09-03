import React from "react";
import "../../styles/LoginPage.css";
import { useState, useRef,useEffect } from "react";
import axios from "../../api/axios";
import { useNavigate } from "react-router-dom";
import { Alert } from "reactstrap";
import { useUserContext } from '../User/UserContext';

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");
  const [profile, setProfile] = useState([]);
  const PROFILE_URL = "/profile";
  const USER_URL = "/user";
  const CUSTOMER_URL = "/customer";
  const { setUserObject } = useUserContext();

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(success, error);
  } else {
    console.log("Geolocation not supported");
  }
  
  function success(position) {
    const latitude = position.coords.latitude;
    const longitude = position.coords.longitude;
    console.log(`Latitude: ${latitude}, Longitude: ${longitude}`);
  }
  
  function error() {
    console.log("Unable to retrieve your location");
  }

  function setSessionLocation(position){
    sessionStorage.setItem("latitude", position.coords.latitude);
    sessionStorage.setItem("longitude", position.coords.longitude);
  }


    // useEffect(() => {
    //     // Make an initial request to get the CSRF token
    //     fetch('https://localhost/api/csrf-token')
    //         .then(response => response.json())
    //         .then(data => {
    //             const receivedCsrfToken = data.csrfToken;
    //             sessionStorage.setItem("csrfToken", receivedCsrfToken);
    //             console.log("CSRF token received:", receivedCsrfToken);
    //         })
    //         .catch(error => console.error('Error:', error));
    // }, []);

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent default form submission

    // Create the LoginDTO object
    const loginData = {
      username: username,
      password: password,
    };

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

    try {
      // Make the POST request to "/login"
      const response = await axios.post("/login", loginData);
      if (!handleErrors(response.data.errors)) {
        // Log the response data to the console
        let obj = response.data.object;
        console.log("Login response:", response.data);
        let username = obj.username;
        let profileName = obj.profileName;
        let jsonWebToken = obj.jsonWebToken;

        sessionStorage.setItem("username", username);
        sessionStorage.setItem("profileName", profileName);
        //sessionStorage.setItem("jsonWebToken", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY5MzMxODM1MCwiZXhwIjoxNjk1MTMyNzUwfQ.1t4_YkugaA18I-rZVbKsFw3h6ZuN0suajo1yCcsIqMk ");
        sessionStorage.setItem("jsonWebToken", jsonWebToken);
        
        const responseUser = await axios.get(`${USER_URL}/${username}`)
        console.log("USER" + responseUser)
        const responseProfile = await axios.get(`${PROFILE_URL}/${profileName}`);
        if(!handleErrors(responseProfile.data.errors)&&!handleErrors(responseUser.data.errors)){
            sessionStorage.setItem("profileType", responseProfile.data.object.profileType.name);
            sessionStorage.setItem("rights", responseProfile.data.object.rights);
            // for geolocation
            navigator.geolocation.getCurrentPosition(setSessionLocation, error);

            if(responseProfile.data.object.profileType.name === "CUSTOMER"){
              const responseCustomer = await axios.get(`${CUSTOMER_URL}/${responseUser.data.object.email}`);
              if(!handleErrors(responseCustomer.data.errors)){
                sessionStorage.setItem("email",responseUser.data.object.email);
              }
              else return;
            }
            window.location.href = "/welcome-user";
            // Redirect to the home page
        }

      }
    } catch (error) {
      // Handle errors here if needed
      console.error("Login error:", error);
      const errorString = "Could not reach server or server offline";
      setErrors(errorString);
      setShow(true);
    }
  };

  function onDismiss() {
    setShow(false);
  }

  return (
    <div className="login-container">
      <h1>Login</h1>
      <form onSubmit={handleSubmit}>
        {" "}
        {/* Add onSubmit handler */}
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            name="username"
            placeholder="Enter your username"
            required
            value={username}
            onChange={(e) => setUsername(e.target.value)} // Update the state with the input value
          />
        </div>
        <div>
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            name="password"
            placeholder="Enter your password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)} // Update the state with the input value
          />
        </div>
        <Alert color="danger" isOpen={show} toggle={onDismiss}>
          <p> {"Error:\n" + errors} </p>
        </Alert>
        <button type="submit" className="login-button">
          Login
        </button>
      </form>
    </div>
  );
}
export default LoginPage;
