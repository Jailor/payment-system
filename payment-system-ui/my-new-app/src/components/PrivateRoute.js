import React from "react";
import { Route, useNavigate } from "react-router-dom";

const isLoggedInCheck = () => {
  const username = sessionStorage.getItem("username");
  const profileName = sessionStorage.getItem("profileName");
  return username && profileName;
};

const PrivateRoute = ({ children }) => {
  const isLoggedIn = isLoggedInCheck();
  const navigate = useNavigate();

  return isLoggedIn ? children : navigate("/login");
};

export default PrivateRoute;
