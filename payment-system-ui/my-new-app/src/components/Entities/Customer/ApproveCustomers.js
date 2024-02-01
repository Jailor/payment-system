import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import "../../../App.css";
import axios from "../../../api/axios";
import { authenticate } from "../../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";
import { Alert } from "reactstrap";
import { CustomerContextProvider, useCustomerContext } from "./CustomerContext";

function ApproveCustomers() {
  const navigate = useNavigate();
  const authResult = authenticate();


  const REDIRECT = "https://localhost:3000/view-customers";
  const { customerObject } = useCustomerContext();
  const [customerData, setCustomerData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { email } = useParams();

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
        const response = await axios.get(`/customer/requires-approval/${email}`);
        setCustomerData(response.data.object);
        console.log(response.data.object)
        setLoading(false);
      } catch (error) {
        console.error("Error fetching customer data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }

    fetchData();
  }, []);

  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  const handleApprove = async () => {
    try {
      const response = await axios.post(`/customer/approve/${customerData.email}`);
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
      const response = await axios.post(`/customer/reject/${customerData.email}`);
      console.log(response);
      if (handleErrors(response.data.errors)) {
        return;
      }
      window.location.href = REDIRECT;
    } catch (error) {
      console.error("Error rejecting creation:", error);
    }
  };

  return (
    <div className="container">
      {["CREATE", "REMOVE"].includes(customerData.operation) && (
        <div>
          <h2>Customer Information for operation {customerData.operation}</h2>
          <p>
            <strong>Name:</strong> {customerData.name}
          </p>
          <p>
            <strong>Phone number:</strong> {customerData.phoneNumber}
          </p>
          <p>
            <strong>Email:</strong> {customerData.email}
          </p>
          <p>
            <strong>Address:</strong> {customerData.address}
          </p>
          <p>
            <strong>City:</strong> {customerData.city}
          </p>
          <p>
            <strong>State:</strong> {customerData.state}
          </p>
          <p>
            <strong>Country:</strong> {customerData.country}
          </p>
          <p>
            <strong>Status:</strong> {customerData.status}
          </p>
          <Alert color="danger" isOpen={show} toggle={onDismiss}>
            <p> {"Error:\n" + errors} </p>
          </Alert>
          <div>
            <button onClick={handleApprove} className="button">
              Approve {customerData.operation}
            </button>
            <button onClick={handleReject} className="button">
              Reject {customerData.operation}
            </button>
          </div>
        </div>
      )}

      {customerData.operation === "MODIFY" && (
        <div>
          <h2>Modified Customer Information</h2>
          <div className="row">
            <div className="column">
              <h3>Old Information</h3>
              <p>
                <strong>Name:</strong> {customerData.name}
              </p>
              <p>
                <strong>Phone number:</strong> {customerData.phoneNumber}
              </p>
              <p>
                <strong>Email:</strong> {customerData.email}
              </p>
              <p>
                <strong>Address:</strong> {customerData.address}
              </p>
              <p>
                <strong>City:</strong> {customerData.city}
              </p>
              <p>
                <strong>State:</strong> {customerData.state}
              </p>
              <p>
                <strong>Country:</strong> {customerData.country}
              </p>
              <p>
                <strong>Status:</strong> {customerData.status}
              </p>
              <p>
                <strong>Default account:</strong> {customerData.defaultAccountNumber}
              </p>
            </div>

            <div className="column">
              <h3>New Information</h3>
              <p>
                <strong>Name:</strong> {customerData.newName}
              </p>
              <p>
                <strong>Phone number:</strong> {customerData.newPhoneNumber}
              </p>
              <p>
                <strong>Email:</strong> {customerData.email}
              </p>
              <p>
                <strong>Address:</strong> {customerData.newAddress}
              </p>
              <p>
                <strong>City:</strong> {customerData.newCity}
              </p>
              <p>
                <strong>State:</strong> {customerData.newState}
              </p>
              <p>
                <strong>Country:</strong> {customerData.newCountry}
              </p>
              <p>
                <strong>Status:</strong> {customerData.newStatus}
              </p>
              <p>
                <strong>Default account:</strong> {customerData.newDefaultAccountNumber}
              </p>
            </div>
          </div>
          <Alert color="danger" isOpen={show} toggle={onDismiss}>
            <p> {"Error:\n" + errors} </p>
          </Alert>
          <div>
            <button onClick={handleApprove} className="button">
              Approve Modification
            </button>
            <button onClick={handleReject} className="button">
              Reject Modification
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default ApproveCustomers;