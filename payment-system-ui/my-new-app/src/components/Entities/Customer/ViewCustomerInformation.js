import React, { useState } from "react";
import "../../../styles/Form.css";
import axios from "../../../api/axios";
import { authenticate } from "../../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";
import { Alert } from "reactstrap";
import { useCustomerContext } from "./CustomerContext";

function ViewCustomerInformation(props) {
  const navigate = useNavigate();
  const authResult = authenticate();

  const REDIRECT = "https://localhost:3000/view-customers";
  const { customerObject } = useCustomerContext();

  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");

  const onDismiss = () => {
    setShow(false);
  };

  const handleErrors = (errors) => {
    if (errors.length > 0) {
      const errorString = errors.map((error) => error.errorMessage).join("\n");
      setErrors(errorString);
      setShow(true);
      return true;
    }
    return false;
  };

  const handleDelete = async () => {
    try {
      const response = await axios.delete(`/customer/${customerObject.email}`);
      if (handleErrors(response.data.errors)) {
        return;
      }
      navigate(REDIRECT);
    } catch (error) {
      console.error("Error deleting customer:", error);
    }
  };

  const handleApproval = async () => {
    try {
      const response = await axios.post(`/approve-customer/${customerObject.email}`);
      if (handleErrors(response.data.errors)) {
        return;
      }
      navigate(REDIRECT);
    } catch (error) {
      console.error("Error approving customer:", error);
    }
  };

  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  return (
    <div className="container">
      <h2>Customer Information</h2>
      <p><strong>Name:</strong> {customerObject.name}</p>
      <p><strong>Phone Number:</strong> {customerObject.phoneNumber}</p>
      <p><strong>Email:</strong> {customerObject.email}</p>
      <p><strong>Address:</strong> {customerObject.address}</p>
      <p><strong>City:</strong> {customerObject.city} </p>
      <p><strong>State:</strong> {customerObject.state}</p>
      <p><strong>Country:</strong> {customerObject.country}</p>
      <p><strong>Status:</strong> {customerObject.status}</p>
      
      <Alert color="danger" isOpen={show} toggle={onDismiss}>
        {"Error:\n" + errors}
      </Alert>

      <div>
        {customerObject.status !== "REMOVED" && customerObject.needsApproval === false && sessionStorage.getItem("rights").includes("REMOVE_ACCOUNT") &&(
          <button onClick={handleDelete} className="button">
            Delete
          </button>
        )}
        {customerObject.needsApproval === true && sessionStorage.getItem("rights").includes("APPROVE_ACCOUNT") &&
        <button onClick={() => navigate(`/approve-customer/${customerObject.email}`)} className="button">Approve/Reject</button>}
      </div>
    </div>
  );
}

export default ViewCustomerInformation;
