import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Alert } from "reactstrap";
import "../../../styles/Form.css";
import axios from "../../../api/axios";
import { authenticate } from "../../auth/AuthUtils.js";
import { useProfileContext } from "./ProfileContext";

function ViewProfileInformation(props) {
  const navigate = useNavigate();
  const authResult = authenticate();

  const REDIRECT = "https://localhost:3000/view-profiles";
  const { profileObject } = useProfileContext();
  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");

  const onDismiss = () => {
    setShow(false);
  };

  const handleErrors = (errors) => {
    if (errors && errors.length > 0) {
      const errorString = errors.map((error) => error.errorMessage).join("\n");
      setErrors(errorString);
      setShow(true);
      return true;
    }
    return false;
  };

  const handleDelete = async () => {
    try {
      const response = await axios.delete(`/profile/${profileObject.name}`);
      console.log(response);
      if (handleErrors(response.data.errors)) {
        return;
      }
      window.location.href = REDIRECT;
    } catch (error) {
      console.error("Error deleting profile:", error);
    }
  };

  const handleApprovalNavigation = () => {
    navigate(`/approve-profile/${profileObject.name}`);
  };

  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  return (
    <div className="container">
      <h2>Profile Information</h2>
      <p>
        <strong>Profile name:</strong> {profileObject.name}
      </p>
      <p>
        <strong>Profile type:</strong> {profileObject.profileType}
      </p>
      <p>
        <strong>Rights:</strong> {profileObject.rights.join(", ")}
      </p>
      <p>
        <strong>Status:</strong> {profileObject.status}
      </p>
      <Alert color="danger" isOpen={show} toggle={onDismiss}>
        <p> {"Error:\n" + errors} </p>
      </Alert>
      {profileObject.status !== "REMOVED" && 
        profileObject.needsApproval === false && sessionStorage.getItem("rights").includes("REMOVE_PROFILE") && (
          <button onClick={handleDelete} className="button">
            Delete
          </button>
        )}
      {profileObject.needsApproval === true && sessionStorage.getItem("rights").includes("APPROVE_PROFILE") &&(
        <button onClick={handleApprovalNavigation} className="button">
          Approve/Reject
        </button>
      )}
    </div>
  );
}
export default ViewProfileInformation;
