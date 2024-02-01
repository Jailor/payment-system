import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import "../../../App.css";
import axios from "../../../api/axios";
import { authenticate } from "../../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";
import { Alert } from "reactstrap";
import { ProfileContextProvider, useProfileContext } from "./ProfileContext";

function ApproveProfiles() {
  const navigate = useNavigate();
  const authResult = authenticate();

  const REDIRECT = "https://localhost:3000/view-profiles";
  const { profileObject } = useProfileContext();
  const [profileData, setProfileData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { name } = useParams();

  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");

  function onDismiss() {
    setShow(false);
  }

  const handleErrors = (errors) => {
    if (errors.length > 0) {
      console.log(errors);
      const errorString = errors.map((error) => error.errorMessage).join("\n");
      setErrors(errorString);
      setShow(true);
      return true;
    }
    return false;
  };

  useEffect(() => {
    if(!authResult) return;
    async function fetchData() {
      try {
        const response = await axios.get(`/profile/requires-approval/${name}`);
        setProfileData(response.data.object);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching profile data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }

    fetchData();
  }, []);


  const handleApprove = async () => {
    try {
      const response = await axios.post(`/profile/approve/${profileData.name}`);
      console.log(response);
      if (handleErrors(response.data.errors)) {
        return;
      }
      window.location.href = REDIRECT;
    } catch (error) {
      console.error("Error approving creation:", error);
    }
  };

  const handleReject = async () => {
    try {
      const response = await axios.post(`/profile/reject/${profileData.name}`);
      console.log(response);
      if (handleErrors(response.data.errors)) {
        return;
      }
      window.location.href = REDIRECT;
    } catch (error) {
      console.error("Error rejecting creation:", error);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }
  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  return (
    <div className="container">
      {["CREATE", "REMOVE"].includes(profileData.operation) && (
        <div>
          <h2>Profile Information for {profileData.operation}</h2>
          <p>
            <strong>Profile name:</strong> {profileData.name}
          </p>
          <p>
            <strong>Profile type:</strong> {profileData.profileType}
          </p>
          <p>
            <strong>Rights:</strong> {profileData.rights.join(", ")}
          </p>
          <p>
            <strong>Status:</strong> {profileData.status}
          </p>
          <Alert color="danger" isOpen={show} toggle={onDismiss}>
            <p> {"Error:\n" + errors} </p>
          </Alert>
          <div>
            <button onClick={handleApprove} className="button">
              Approve {profileData.operation}
            </button>
            <button onClick={handleReject} className="button">
              Reject {profileData.operation}
            </button>
          </div>
        </div>
      )}

      {profileData.operation === "MODIFY" && (
        <div>
          <h2>Modified Profile Information</h2>
          <div className="row">
            <div className="column">
              <h3>Old Information</h3>
              <p>
                <strong>Profile name:</strong> {profileData.name}
              </p>
              <p>
                <strong>Profile type:</strong> {profileData.profileType}
              </p>
              <p>
                <strong>Rights:</strong> {profileData.rights.join(", ")}
              </p>
              <p>
                <strong>Status:</strong> {profileData.status}
              </p>
            </div>

            <div className="column">
              <h3>New Information</h3>
              <p>
                <strong>Profile name:</strong> {profileData.newName}
              </p>
              <p>
                <strong>Profile type:</strong> {profileData.newProfileType}
              </p>
              <p>
                <strong>Rights:</strong> {profileData.newRights.join(", ")}
              </p>
              <p>
                <strong>Status:</strong> {profileData.newStatus}
              </p>
            </div>
          </div>
          <Alert color="danger" isOpen={show} toggle={onDismiss}>
            <p> {"Error:\n" + errors} </p>
          </Alert>
          {sessionStorage.getItem("rights").includes("APPROVE_PROFILE") &&
          <div>
            <button onClick={handleApprove} className="button">
              Approve Modification
            </button>
            <button onClick={handleReject} className="button">
              Reject Modification
            </button>
          </div>}
        </div>
      )}
    </div>
  );
}

export default ApproveProfiles;