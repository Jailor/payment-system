import React, { useEffect, useRef, useState } from "react";
import { authenticate } from "./auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";
import { useLocation, useParams } from "react-router-dom";

const RedirectReq = () => {
  const navigate = useNavigate();
  authenticate(navigate);

  const { redirect } = useParams();

  useEffect(() => {
    window.location.href = "/" + redirect;
  }, []);
  return (
    <div>
      <p> Redirecting...</p>
    </div>
  );
};
export default RedirectReq;
