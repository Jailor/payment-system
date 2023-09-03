import React, { useEffect, useRef, useState } from "react";
import { useLocation, Link, useParams } from "react-router-dom";
import "../../styles/Form.css";
import axios from "../../api/axios";
import { useNavigate } from "react-router-dom";
import { AccountContextProvider, useAccountContext } from "./AccountContext";
import { useCustomerContext } from "../Customer/CustomerContext";
import { authenticate } from "../auth/AuthUtils.js";
import { Alert } from "reactstrap";

function AccountForm(props) {
  const navigate = useNavigate();
  const authResult = authenticate();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const CURRENCIES_URL = "/constants/currencies";
  const ACCOUNT_STATUSES_URL = "/constants/account-statuses";

  const { accountObject } = useAccountContext();
  const [accountNumber, setAccountNumber] = useState("");
  const [ownerEmail, setOwnerEmail] = useState("");

  const [ACCOUNT_STATUSES, setAccountStatuses] = useState([]);
  const [CURRENCIES, setCurrencies] = useState([]);

  const [currency, setCurrency] = useState("USD");
  const [accountStatus, setAccountStatus] = useState("OPEN");

  const ACCOUNT_URL = "/account";
  const CUSTOMER_URL = "/customer";
  const [customers, setCustomers] = useState([]);
  const { customerObject } = useCustomerContext();

  const { accountnumber } = useParams();
  const location = useLocation();
  const passedString = location.state;
  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");

  useEffect(() => {
    if(!authResult) return;
    async function fetchConstants() {
      try {
        const response_currencies = await axios.get(CURRENCIES_URL);
        const response_account_statuses = await axios.get(ACCOUNT_STATUSES_URL);

        const currencyNames = response_currencies.data.object.map(
          (currency) => currency.name
        );
        setCurrencies(currencyNames);
        setAccountStatuses(response_account_statuses.data.object);
        setLoading(false); // Data has been fetched, so set loading to false
      } catch (error) {
        console.error("Error fetching  data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }
    fetchConstants();
  }, []);

  useEffect(() => {
    if(!authResult) return;
    if (passedString === "edit") {
      if (accountObject !== null) {
        setAccountNumber(accountObject.accountNumber);
        setOwnerEmail(accountObject.ownerEmail);
        setCurrency(accountObject.currency.name);
        setAccountStatus(accountObject.accountStatus.name);
      } else {
        const fetchAccount = async () => {
          try {
            const response = await axios.get(`${ACCOUNT_URL}/${accountnumber}`);
            const obj = response.data.object;
            setAccountNumber(obj.accountNumber);
            setOwnerEmail(accountObject.ownerEmail);
            setCurrency(obj.currency.name);
            setAccountStatus(obj.accountStatus.name);
          } catch (error) {
            // Handle error state here if needed
          }
        };
        fetchAccount();
      }
    } else {
      // If the passedString is 'create', set fields to empty strings/default options
      setAccountNumber("<auto-generated>");
      if(sessionStorage.getItem("profileType") === "CUSTOMER"){
        setOwnerEmail(sessionStorage.getItem("email"));
      }
      else
      {
        setOwnerEmail("");
      }
      setCurrency("USD");
      setAccountStatus("OPEN");
    }
  }, [passedString, accountnumber]);

  useEffect(() => {
    if(!authResult) return;
    // Fetch profile data when the component mounts
    const fetchCustomers = async () => {
      try {
        const response = await axios.get(CUSTOMER_URL);
        setCustomers(response.data.object);
        if (passedString !== "edit") {
          if (response.data.object.length > 0) {
     
            setOwnerEmail(response.data.object[0].email);
          }
          if(sessionStorage.getItem("profileType") === "CUSTOMER"){ 
            setOwnerEmail(customerObject.email);
          }
        }
      } catch (error) {
        // Handle error state here if needed
      }
    };
    fetchCustomers();
  }, []);

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

  const handleSubmit = async (event) => {
    event.preventDefault();
    // Handle the form submission or save the data as needed
    try {
      if (passedString === "create") {
        console.log("create");
        console.log("accountNumber " + accountNumber);
        console.log("ownerEmail " + ownerEmail);
        console.log("currency " + currency);
        console.log("accountStatus " + accountStatus);
        const response = await axios.post(
          ACCOUNT_URL,
          JSON.stringify({
            accountNumber,
            ownerEmail,
            currency,
            accountStatus,
          }),
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
        if (handleErrors(response.data.errors)) {
          return;
        }
      } else {
        console.log("edit");
        console.log("accountNumber " + accountNumber);
        console.log("ownerEmail " + ownerEmail);
        console.log("currency " + currency);
        console.log("accountStatus " + accountStatus);
        const response = await axios.put(
          ACCOUNT_URL,
          JSON.stringify({
            accountNumber,
            ownerEmail,
            currency,
            accountStatus,
          }),
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
        if (handleErrors(response.data.errors)) {
          return;
        }
      }
      navigate("/redirect/view-accounts");
    } catch (err) {}
  };

  function onDismiss() {
    setShow(false);
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
    <div className="form">
      <form className="form" onSubmit={handleSubmit}>
        <div className="form-body">
          <div className="form-input-container">
            <label className="form__label" htmlFor="accountNumber">
              Account Number{" "}
            </label>
            <input
              className="form__input disabled"
              type="text"
              id="accountNumber"
              placeholder="Account Number"
              required
              value={accountNumber}
              readOnly={passedString}
              onChange={(e) => setAccountNumber(e.target.value)}
            />
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="owner">
              Owner{" "}
            </label>
            <select
              className={
                passedString === "edit" ? "form__input disabled" : "form__input"
              }
              id="owner"
              value={ownerEmail}
              required
              disabled={passedString === "edit" || sessionStorage.getItem("profileType") === "CUSTOMER"}
              onChange={(e) => setOwnerEmail(e.target.value)}
            >
              {customers.map((customer, index) => (
                <option key={index} value={customer.email}>
                  {customer.name + " " + customer.email}
                </option>
              ))}
            </select>
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="currency">
              Currency{" "}
            </label>
            <select
              className="form__input"
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
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="accountStatus">
              Account Status{" "}
            </label>
            <select
              className={passedString === "edit" ? "form__input disabled" : "form__input"}
              id="accountStatus"
              value={accountStatus}
              required
              disabled={passedString === "edit" || sessionStorage.getItem("profileType") === "CUSTOMER"}
              onChange={(e) => setAccountStatus(e.target.value)}
            >
              {ACCOUNT_STATUSES.map((status, index) => (
                <option key={index} value={status}>
                  {status}
                </option>
              ))}
            </select>
          </div>
        </div>
        <Alert color="danger" isOpen={show} toggle={onDismiss}>
          <p> {"Error:\n" + errors} </p>
        </Alert>
        <div className="footer">
          <button type="submit" className="button">
            {" "}
            {passedString === "edit" ? "Edit Account" : "Create Account"}
          </button>
        </div>
      </form>
    </div>
  );
}
export default AccountForm;
