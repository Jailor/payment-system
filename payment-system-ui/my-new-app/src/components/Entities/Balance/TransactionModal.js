import Modal from "react-modal";
import axios from "../../../api/axios";
import "../../../styles/TransactionModal.css"; // You can define modal styles in this file
import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { Alert } from "reactstrap";

function TransactionModal({ isOpen, onRequestClose }) {
  const [formData, setFormData] = useState({
    accountNumber: "",
    message: "",
    currency: "",
    amount: "",
  });

  const CURRENCIES_URL = "/constants/currencies";
  const [currency, setCurrency] = useState("USD");
  const [CURRENCIES, setCurrencies] = useState([]);
  const PAYMENT_URL = "/payment";

  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");
  // get last part of url
  const { accountNumber } = useParams();

  useEffect(() => {
    async function fetchConstants() {
      try {
        const response_currencies = await axios.get(CURRENCIES_URL);

        const currencyNames = response_currencies.data.object.map(
          (currency) => currency.name
        );
        setCurrencies(currencyNames);
      } catch (error) {
        console.error("Error fetching  data:", error);
      }
    }
    fetchConstants();
  }, []);

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
  function onDismiss() {
    setShow(false);
  }

  const handleTransfer = async (event) => {
    event.preventDefault();
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

    try {
      const transferData = {
        userReference: formData.message,
        creditAccountNumber: formData.accountNumber,
        debitAccountNumber: accountNumber,
        amount: parsedAmount,
        currency: currency,
        latitude: sessionStorage.getItem("latitude"),
        longitude: sessionStorage.getItem("longitude")
      };
      try {
        const response = await axios.post(PAYMENT_URL, transferData);
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
      }
    } catch (error) {
      console.error("Error performing transfer:", error);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onRequestClose}
      contentLabel="Transaction Modal"
      className="transaction-modal"
    >
      <h2>Transaction</h2>
      <form>
        <label>Account Number:</label>
        <input
          type="text"
          name="accountNumber"
          value={formData.accountNumber}
          onChange={handleInputChange}
        />

        <label>Message:</label>
        <input
          type="text"
          name="message"
          value={formData.message}
          onChange={handleInputChange}
        />

        <label>Currency:</label>
        <select
          id="currency"
          value={currency}
          required
          onChange={(e) => setCurrency(e.target.value)}
        >
          {CURRENCIES.map((curr, index) => (
            <option key={index} value={curr}>
              {curr}
            </option>
          ))}
        </select>
        <label>Amount:</label>
        <input
          type="text"
          name="amount"
          value={formData.amount}
          onChange={handleInputChange}
        />

        <button className="transaction-button-modal" onClick={handleTransfer}>
          Transfer
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

export default TransactionModal;
