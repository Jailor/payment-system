import React from "react";
import { Link } from "react-router-dom";
import axios from "../../../api/axios";
import "../../../styles/Form.css";
import { authenticate } from "../../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";
import { useAccountContext } from "./AccountContext";
import { useState, useEffect } from "react";
import TransactionModal from "../Balance/TransactionModal";
import { useParams } from "react-router-dom";
import { Button } from "reactstrap";
import VerifyModal from "../Balance/VerifyModal";
import { Alert } from "reactstrap";

function ViewAccountInformation(props) {
  const navigate = useNavigate();
  const authResult = authenticate();

  const { accountObject } = useAccountContext();
  const REDIRECT = "https://localhost:3000/view-accounts";

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

  const handleDelete = async () => {
    try {
      const response = await axios.delete(`/account/${accountObject.accountNumber}`);
      if (handleErrors(response.data.errors)) {
        return;
      }
      window.location.href = REDIRECT;
    } catch (error) {
      console.error("Error deleting account:", error);
    }
  };

  const VIEW_BALANCE_URL = "/balance/last";
  const AUTHORIZE_PAYMENT_URL="/payment/authorize"
  const APPROVAL_PAYMENTS_URL = "/payment/requires-approval/account";
  const CANCEL_PAYMENT_URL = "/payment/cancel";
  const APPROVE_PAYMENT_URL = "/payment/approve";
  const UNBLOCK_FRAUD_PAYMENT_URL = "/payment/unblock-fraud";

  const [balance, setBalance] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isTransactionModalOpen, setTransactionModalOpen] = useState(false);
  const [isVerifyModalOpen, setVerifyModalOpen] = useState(false);
  const [approvalPayments, setApprovalPayments] = useState([]);
  const {accountNumber} = useParams();
  const [paymentAmount, setPaymentAmount]=useState();
  const [systemReference, setSystemReference]=useState();


  
  const openTransactionModal = () => {
    setTransactionModalOpen(true);
  };

  const closeTransactionModal = () => {
    setTransactionModalOpen(false);
  };
  const openVerifyModal = () => {
    setVerifyModalOpen(true);
  };

  const closeVerifyModal = () => {
    setVerifyModalOpen(false);
  };

  useEffect(() => {
    if(!authResult) return;

    async function fetchData() {
      if(accountObject === null) window.location.href = "/view-accounts";

      try {
        const response = await axios.get(
          `${VIEW_BALANCE_URL}/${accountObject.accountNumber}`
        );
        setBalance(response.data.object);
        setLoading(false); // Data has been fetched, so set loading to false
      } catch (error) {
        console.error("Error fetching account data:", error);
        setError("Error fetching data");
      }
    }

    fetchData();

    async function fetchPayments() {
      try {
        const response = await axios.get(APPROVAL_PAYMENTS_URL + "/" + accountObject.accountNumber);
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

  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  if (loading) {
    return <div>Loading...</div>; // Show a loading message while waiting for data
  }

  if (error) {
    return <div>Error: {error}</div>; // Show an error message if the API call failed
  }

  function formatCurrency(value, fractionDigits) {
    const formattedValue = (value / Math.pow(10, fractionDigits)).toFixed(fractionDigits);
    const parts = formattedValue.split('.');
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ' '); // Add white space for thousands
    return parts.join('.');
}

  return (
    <div className="container">
      <h2>Account Information</h2>
      <p>
        <strong>Account Number:</strong> {accountObject.accountNumber}
      </p>
      <p>
        <strong>Owner email:</strong> {accountObject.ownerEmail}
      </p>
      <p>
        <strong>Account Currency:</strong> {accountObject.currency.name}
      </p>
      <p>
        <strong>Account Status:</strong> {accountObject.accountStatus}
      </p>
      <p>
        <strong> Available Balance:</strong> {formatCurrency(balance.availableBalance, accountObject.currency.fractionDigits) + " " + accountObject.currency.name}
      </p>
      <p>
        <strong> Pending Balance:</strong> {formatCurrency(balance.pendingBalance, accountObject.currency.fractionDigits) + " "+ accountObject.currency.name}
      </p>
      <p>
        <strong> Projected Balance:</strong> {formatCurrency(balance.projectedBalance, accountObject.currency.fractionDigits) + " "+ accountObject.currency.name}
      </p>
      <div className="action-buttons">
        {/* Button for Transaction */}
        <button
          className="link-button transaction-button"
          onClick={openTransactionModal}
          disabled={
            accountObject.status === "CLOSED" ||
            accountObject.status === "REMOVED" ||
            accountObject.accountStatus === "CLOSED" ||
            accountObject.accountStatus === "BLOCKED" || 
            accountObject.accountStatus === "BLOCKED_DEBIT"
          }
        >
          Transaction
        </button>

        {/* Button for Transaction History */}
        <Link
          to={`/view-history/${accountObject.accountNumber}`}
          state={`/payment-history/`}
          className="link-button transaction-button"
        >
          Transaction History
        </Link>
      </div>
      <Alert color="danger" isOpen={show} toggle={onDismiss}>
        <p> {"Error:\n" + errors} </p>
      </Alert>
      
      <div>
        {accountObject.status !== "REMOVED" &&
        accountObject.accountStatus === "CLOSED" &&
        accountObject.needsApproval === false && sessionStorage.getItem("rights").includes("REMOVE_ACCOUNT") &&
          <button onClick={handleDelete} className="button">Delete</button>}
        
        {accountObject.needsApproval === true && sessionStorage.getItem("rights").includes("APPROVE_ACCOUNT") &&
          <button onClick={() => navigate(`/approve-account/${accountObject.accountNumber}`)} className="button">Approve/Reject</button>}
      </div>
    <br/>
    <h2>Payments awaiting approval</h2>
    <ul>
        {approvalPayments && approvalPayments.map((payment, index) => (
          <ul key={index} className={`info ${payment.status === "BLOCKED_BY_FRAUD" ? "fraud-payment" : ""}`}>
            <div>
              <div className="list-container">
                <p>{payment.creditAccountNumber === accountNumber ? 'INCOMING' : 'OUTGOING'}</p>
                <p>Reference: {payment.systemReference}</p>
                <p>Message: {payment.userReference}</p>
                <p>Currency : {payment.currency.name} </p>
                <p>Amount : {formatCurrency(payment.amount, payment.currency.fractionDigits)}</p>
                <p>To: {payment.debitAccountNumber !== accountNumber ? payment.debitAccountNumber : payment.creditAccountNumber}</p>
                <p>At: {payment.stringTimeStamp}</p>
              </div>
              <Alert color="danger" isOpen={show} toggle={onDismiss}>
              <p> {"Error:\n" + errors} </p>
              </Alert>
              {payment.status=== "VERIFY" && sessionStorage.getItem("rights").includes("VERIFY_PAYMENT") &&(
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
      <VerifyModal
                  isOpen={isVerifyModalOpen}
                  onRequestClose={closeVerifyModal}
                  amount = {paymentAmount}
                  systemReference={systemReference}
                />
      <TransactionModal
        isOpen={isTransactionModalOpen}
        onRequestClose={closeTransactionModal}
      />
    </div>
  );
}

export default ViewAccountInformation;
