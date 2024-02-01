import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "../../../api/axios";
import { useUserContext } from './UserContext';
import { authenticate } from '../../auth/AuthUtils.js';
import { Alert } from "reactstrap";

function ViewUserInformation(props) {
  const navigate = useNavigate();
  const authResult = authenticate();


  const REDIRECT = 'https://localhost:3000/view-users';
  const { userObject } = useUserContext();
  const { username } = useParams();

  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");

  function onDismiss() {
    setShow(false);
  }

  const handleErrors = (responseErrors) => {
    if (responseErrors.length > 0) {
      const errorString = responseErrors.map((error) => error.errorMessage).join("\n");
      setErrors(errorString);
      setShow(true);
      return true;
    }
    return false;
  };

  const handleDelete = async () => {
    try {
      const response = await axios.delete(`/user/${userObject.username}`);
      console.log(response);
      if (handleErrors(response.data.errors)) {
        return;
      }
      window.location.href = REDIRECT;
    } catch (error) {
      console.error("Error deleting user:", error);
    }
  };
  
  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  return (
    <div className="container">
      <h2>User Information</h2>
      <p><strong>Username:</strong> {userObject.username}</p>
      <p><strong>Full name:</strong> {userObject.fullName}</p>
      <p><strong>Email:</strong> {userObject.email}</p>
      <p><strong>Address:</strong> {userObject.address}</p>
      <p><strong>Profile:</strong> {userObject.profileName}</p>
      <p><strong>Status:</strong> {userObject.status}</p>

      <Alert color="danger" isOpen={show} toggle={onDismiss}>
        <p> {"Error:\n" + errors} </p>
      </Alert>
      {(sessionStorage.getItem("profileType") !== "CUSTOMER" || sessionStorage.getItem("rights").includes("REMOVE_USER")) &&
      <div>
        {userObject.status !== 'REMOVED' && 
        userObject.needsApproval === false && 
        sessionStorage.getItem("rights").includes("REMOVE_USER") &&
        userObject.username !== "admin" &&
        userObject.username !== "mobile" &&
          <button onClick={handleDelete} className="button">Delete</button>}
        
        {userObject.needsApproval === true && sessionStorage.getItem("rights").includes("APPROVE_USER") &&
          <button onClick={() => navigate(`/approve-user/${userObject.username}`)} className="button">Approve/Reject</button>}
      </div>}
    </div>
  );
}

export default ViewUserInformation;
