import React from "react";
import "../../styles/LoginPage.css";
import { useState } from "react";
import axios from "../../api/axios";
import { Alert } from "reactstrap";
import { useUserContext } from "../Entities/User/UserContext";
import { useCustomerContext } from "../Entities/Customer/CustomerContext";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");
  const PROFILE_URL = "/profile";
  const USER_URL = "/user";
  const CUSTOMER_URL = "/customer";

  const { setUserObject } = useUserContext();
  const {setCustomerObject} = useCustomerContext();

  const success = (position) => {
    const latitude = position.coords.latitude;
    const longitude = position.coords.longitude;
    console.log(`Latitude: ${latitude}, Longitude: ${longitude}`);
  }
  
  const error = () => {
    console.log("Unable to retrieve your location");
  }

  
  const setSessionLocation = (position) => {
    sessionStorage.setItem("latitude", position.coords.latitude);
    sessionStorage.setItem("longitude", position.coords.longitude);
  }

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
  
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(success, error);
  } else {
    console.log("Geolocation not supported");
  }


  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent default form submission

    const loginData = {
      username: username,
      password: password,
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
            sessionStorage.setItem("profileType", responseProfile.data.object.profileType);
            sessionStorage.setItem("rights", responseProfile.data.object.rights);
            // for geolocation
            navigator.geolocation.getCurrentPosition(setSessionLocation, error);

            setUserObject(responseUser.data.object);
         

            if(responseProfile.data.object.profileType === "CUSTOMER"){
              const responseCustomer = await axios.get(`${CUSTOMER_URL}/${responseUser.data.object.email}`);
              if(!handleErrors(responseCustomer.data.errors)){
                sessionStorage.setItem("email",responseUser.data.object.email);
                setCustomerObject(responseCustomer.data.object);
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
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            name="username"
            placeholder="Enter your username"
            required
            value={username}
            onChange={(e) => setUsername(e.target.value)} 
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
            onChange={(e) => setPassword(e.target.value)}
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
