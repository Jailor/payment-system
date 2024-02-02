import Modal from "react-modal";
import axios from "../../../api/axios";
import "../../../styles/TransactionModal.css"; // You can define modal styles in this file
import React, { useEffect, useRef, useState } from "react";
import { Alert } from "reactstrap";

function VerifyModal({ isOpen, onRequestClose, amount, systemReference}) {
    console.log(amount);
  const [formData, setFormData] = useState({
    amount: "",
  });

const VERIFY_PAYMENT_URL = "/payment/verify";

  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");
  // get last part of url

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleErrors = (errors) => {
    if (errors.length > 0) {
      console.log(errors);
      const errorString = errors
        .map((error) => error.errorMessage)
        .join("\n");
      setErrors(errorString);
      setShow(true);
      return true;
    }
    return false;
  };
  const handleVerify =async (event)=>{
    event.preventDefault();

    if (formData.amount === "") {
      const errorString = "Please enter an amount";
      setErrors(errorString);
      setShow(true);
      return;
    }

    const amountRegex = /^\s*((?:\d{1,3}(?:\s\d{3})*|\d+)(?:\.\d{1,2})?)\s*$/;
    if (!amountRegex.test(formData.amount)) {
      setErrors("Invalid amount format. Valid format <number>.<number> like 123.42, max 2 decimal digits.");
      setShow(true);
      return;
    }
  
    let parsedAmount;
    const strippedAmount = formData.amount.replace(/\s+/g, "").replace(",", "");
    if (strippedAmount.includes(".")) {
      const arr =  strippedAmount.split(".");
      const decimalDigits = arr[1].length;
      console.log(decimalDigits);
      if (decimalDigits === 0) {
        parsedAmount = parseFloat(arr[0]) * 100;
      } else if (decimalDigits === 1) {
        parsedAmount = parseFloat(arr[0] + arr[1]) * 10;
      } else if (decimalDigits === 2) {
        parsedAmount = parseFloat(arr[0] + arr[1]);
      } else {
        setErrors("Invalid decimal format. Valid format <number>.<number> like 123.42, max 2 decimal digits.");
        setShow(true);
        return;
      }
    } else {
      parsedAmount = parseFloat(strippedAmount) * 100;
    }
  
    if (isNaN(parsedAmount)) {
      setErrors("Invalid amount format. Valid format <number>.<number> like 123.42, max 2 decimal digits.");
      setShow(true);
      return;
    }


    if(parsedAmount !== amount){
      const errorString = "Amount does not match";
      setErrors(errorString);
      setShow(true);
      return;
    }


    try {
        const response = await axios.post(VERIFY_PAYMENT_URL + "/" + systemReference);
        if (!handleErrors(response.data.errors)) {
          // Log the response data to the console
          console.log("Transfer response:", response.data);
          console.log("Transfer successful!");
          window.location.reload();

        }
      } catch (error) {
        // Handle errors here if needed
        console.error("Transfer error:", error);
        const errorString = "Transaction request unsuccessful";
        setErrors(errorString);
        setShow(true);
        return;
      }
    }
  function onDismiss() {
    setShow(false);
  }

  return (
    <Modal
    isOpen={isOpen}
    onRequestClose={onRequestClose}
    contentLabel="Verify Modal"
    className="verify-modal"

    >
      <h2>Verify</h2>
      <form>
        <label>Verify Amount:</label>
        <input
          type="text"
          name="amount"
          value={formData.amount}
          onChange={handleInputChange}
        />

        <button className="transaction-button-modal" onClick={handleVerify}>
          Verify
        </button>
        <button className="transaction-button-modal" onClick={onRequestClose}>
          Cancel
        </button>
        <Alert color="danger" isOpen={show} toggle={onDismiss}>
          <p> {"Error:\n" + errors} </p>
        </Alert>
      </form>
    </Modal>
  );
}
export default VerifyModal