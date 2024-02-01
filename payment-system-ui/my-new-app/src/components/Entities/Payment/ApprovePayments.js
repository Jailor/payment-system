import React from "react";
import { Link } from "react-router-dom";
import axios from "../../../api/axios";
import "../../../styles/Form.css";
import { authenticate } from "../../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import TransactionModalGeneral from "./TransactionModalGeneral";
import VerifyModal from "../Balance/VerifyModal";
import { Alert } from "reactstrap";

function ApprovePayments(props) {
  const authResult = authenticate();

  const APPROVAL_PAYMENTS_URL = "/payment/requires-approval";
  const CANCEL_PAYMENT_URL = "/payment/cancel";
  const AUTHORIZE_PAYMENT_URL="/payment/authorize";
  const APPROVE_PAYMENT_URL = "payment/approve";
  const UNBLOCK_FRAUD_PAYMENT_URL = "/payment/unblock-fraud";

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isTransactionModalGeneralOpen, setTransactionModalGeneralOpen] = useState(false);
  const [isVerifyModalOpen, setVerifyModalOpen] = useState(false);
  const [approvalPayments, setApprovalPayments] = useState([]);
  const [paymentAmount, setPaymentAmount]=useState();
  const [systemReference, setSystemReference]=useState();
  const REDIRECT = "https://localhost:3000/view-payments";

  
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

  const openTransactionModalGeneral = () => {
    setTransactionModalGeneralOpen(true);
  };

  const closeTransactionModalGeneral = () => {
    setTransactionModalGeneralOpen(false);
  };

  const openVerifyModal = () => {
    setVerifyModalOpen(true);
  };

  const closeVerifyModal = () => {
    setVerifyModalOpen(false);
  };


  useEffect(() => {
    if(!authResult) return;
    async function fetchPayments() {
      try {
        const response = await axios.get(APPROVAL_PAYMENTS_URL);
        setApprovalPayments(response.data.object);
        setLoading(false); 
      } catch (error) {
        console.error("Error fetching account data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }

    fetchPayments();
  }, []);

  
  function formatCurrency(value, fractionDigits) {
    const formattedValue = (value / Math.pow(10, fractionDigits)).toFixed(fractionDigits);
    const parts = formattedValue.split('.');
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ' '); // Add white space for thousands
    return parts.join('.');
}

  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  if (loading) {
    return <div>Loading...</div>; // Show a loading message while waiting for data
  }

  if (error) {
    return <div>Error: {error}</div>; // Show an error message if the API call failed
  }
  return (
    <div className="container">
    <h2>Payments awaiting approval</h2>
    <ul>
        {approvalPayments.map((payment, index) => (
          <ul key={index} className={`info ${payment.status === "BLOCKED_BY_FRAUD" ? "fraud-payment" : ""}`}>
            <div>
              <div className="list-container">
                <p>Reference: {payment.systemReference}</p>
                <p>Message: {payment.userReference}</p>
                <p>Currency : {payment.currency.name} </p>
                <p>Amount : {formatCurrency(payment.amount, payment.currency.fractionDigits)}</p>
                <p>From: {payment.debitAccountNumber} </p>
                <p>To: {payment.creditAccountNumber}</p>
                <p>At: {payment.stringTimeStamp}</p>
              </div>
              {payment.status === "VERIFY" && sessionStorage.getItem("rights").includes("VERIFY_PAYMENT") &&(
                <button
                type="button"
                className="button"
                onClick={() => {
                  setPaymentAmount(payment.amount);
                  setSystemReference(payment.systemReference);
                  openVerifyModal();
                }}
                >
                Verify
              </button>
                )}
                {payment.status === "AUTHORIZE" && sessionStorage.getItem("rights").includes("AUTHORIZE_PAYMENT") &&(
                <button
                type="button"
                className="button"
                onClick={
                  async() => {
                    const response = await axios.post(AUTHORIZE_PAYMENT_URL + "/" + payment.systemReference);
                    if (handleErrors(response.data.errors)) {
                      return;
                    }
                    window.location.reload();
                }}
                >
                Authorize
              </button>
                )}
                {payment.status === "APPROVE" && sessionStorage.getItem("rights").includes("APPROVE_PAYMENT") &&(
                <button
                type="button"
                className="button"
                onClick={
                  async () =>{
                    const response = await axios.post(APPROVE_PAYMENT_URL + "/" + payment.systemReference);
                    if (handleErrors(response.data.errors)) {
                      return;
                    }
                    window.location.reload();
                    }
                }
                >
                Approve
              </button>
                )}
              {payment.status === "BLOCKED_BY_FRAUD" && sessionStorage.getItem("rights").includes("APPROVE_PAYMENT") &&(
                <button
                type="button"
                className="button"
                onClick={
                  async () =>{
                    const response = await axios.post(UNBLOCK_FRAUD_PAYMENT_URL + "/" + payment.systemReference);
                    if (handleErrors(response.data.errors)) {
                      return;
                    }
                    window.location.reload();
                    }
                }
                >
                Unblock Possible Fraud
              </button>
                )}

              {(
               payment.status === "VERIFY" || 
               payment.status === "AUTHORIZE"||
               payment.status === "APPROVE" || 
               payment.status === "BLOCKED_BY_FRAUD")
              && (
                <button
                type="button"
                className="button"
                onClick={ 
                    async () =>{
                    const response = await axios.post(CANCEL_PAYMENT_URL + "/" + payment.systemReference);
                    if (handleErrors(response.data.errors)) {
                      return;
                    }
                    window.location.reload();
                    }
                }>
                Cancel
              </button>
                )}

            </div>
          </ul>
        ))}
      </ul>
      <Alert color="danger" isOpen={show} toggle={onDismiss}>
        <p> {"Error:\n" + errors} </p>
      </Alert>
      
      <h2> Create payment</h2>
      <button
          className="link-button transaction-button"
          onClick={openTransactionModalGeneral}
        >
          Transaction
      </button>
    <VerifyModal
              isOpen={isVerifyModalOpen}
              onRequestClose={closeVerifyModal}
              amount = {paymentAmount}
              systemReference={systemReference}
            />
      <TransactionModalGeneral
        isOpen={isTransactionModalGeneralOpen}
        onRequestClose={closeTransactionModalGeneral}
      />
    </div>
  );
}

export default ApprovePayments;
