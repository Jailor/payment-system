import React from "react";
import "../../styles/LoginPage.css";
import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";

function Logout() {
  sessionStorage.clear();
  console.log("Logout");
  window.location.href = "/login";
}
export default Logout;
