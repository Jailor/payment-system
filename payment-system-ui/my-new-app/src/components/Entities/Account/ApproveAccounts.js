import React, { useEffect, useState } from "react";
import {useParams } from "react-router-dom";
import axios from "../../../api/axios";
import "../../../App.css";
import { authenticate } from "../../auth/AuthUtils.js";
import { Alert } from "reactstrap";

function ApproveAccounts() {
  const authResult = authenticate();
  const REDIRECT = "https://localhost:3000/view-accounts";

  const [accountData, setAccountData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { accountNumber } = useParams();

  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");
  function onDismiss() {
    setShow(false);
  }
  const handleErrors = (errors) => {
    if (errors.length > 0) {
      console.log(errors);
      console.log("activated alert");
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
        const response = await axios.get(
          `/account/requires-approval/${accountNumber}`
        );
        setAccountData(response.data.object);
        console.log(response.data);
        setLoading(false); // Data has been fetched, so set loading to false
      } catch (error) {
        console.error("Error fetching account data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }

    fetchData();
  }, [accountNumber]);

  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  if (loading) {
    return <div>Loading...</div>; // Show a loading message while waiting for data
  }

  if (error) {
    return <div>Error: {error}</div>; // Show an error message if the API call failed
  }

  const handleApprove = async () => {
    try {
      const response = await axios.post(
        `/account/approve/${accountData.accountNumber}`
      );
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
      const response = await axios.post(
        `/account/reject/${accountData.accountNumber}`
      );
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
    <div>
      {(accountData.operation === "CREATE" ||
        accountData.operation === "REMOVE") && (
        <div>
          <h2>Created Account Information</h2>
          <p>
            <strong>Account Number:</strong> {accountData.accountNumber}
          </p>
          <p>
            <strong>Owner Email:</strong> {accountData.ownerEmail}
          </p>
          <p>
            <strong>Currency:</strong> {accountData.currency.name}
          </p>
          <p>
            <strong>Account Status:</strong> {accountData.accountStatus}
          </p>
          <p>
            <strong> Status: </strong> {accountData.status}
          </p>
          <div>
          <Alert color="danger" isOpen={show} toggle={onDismiss}>
          <p> {"Error:\n" + errors} </p>
          </Alert>
           <button onClick={handleApprove} className="link-button">
              Approve {accountData.operation}
            </button>
            <button onClick={handleReject} className="link-button">
              Reject {accountData.operation}
            </button>
          </div>
        </div>
      )}
      {accountData.operation === "MODIFY" && (
        <div>
          <h2>Modified Account Information</h2>
          <div className="column">
            <h3>Old Information</h3>
            <p>
              <strong>Account Number:</strong> {accountData.accountNumber}
            </p>
            <p>
              <strong>Owner Email:</strong> {accountData.ownerEmail}
            </p>
            <p>
              <strong>Currency:</strong> {accountData.currency.name}
            </p>
            <p>
              <strong>Account Status:</strong> {accountData.accountStatus}
            </p>
            <p>
              <strong> Status: </strong> {accountData.status}
            </p>
          </div>

          <div className="column">
            <h3>New Information</h3>
            <p>
              <strong>Account Number:</strong> {accountData.accountNumber}
            </p>
            <p>
              <strong>Owner Email:</strong> {accountData.ownerEmail}
            </p>
            <p>
              <strong>Currency:</strong> {accountData.newCurrency.name}
            </p>
            <p>
              <strong>Account Status:</strong>{" "}
              {accountData.newAccountStatus}
            </p>
            <p>
              <strong> Status: </strong> {accountData.newStatus}
            </p>
          </div>
          <Alert color="danger" isOpen={show} toggle={onDismiss}>
          <p> {"Error:\n" + errors} </p>
          </Alert>
          <div>
            <button onClick={handleApprove} className="link-button">
              Approve Modification
            </button>
            <button onClick={handleReject} className="link-button">
              Reject Modification
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default ApproveAccounts;
