import React from "react";
import { useLocation, Link, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "../../../api/axios.js";
import "../../../styles/View.css";
import { authenticate } from "../../auth/AuthUtils.js";

function ViewPayments(props) {
  const authResult = authenticate();

  const PAYMENT = "/payment";
  const COMPLETED_PAYMENT = "/payment/completed";
  const [onlyCompleted, setOnlyCompleted] = useState(false);
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const STATUSES_URL = "/constants/payment-statuses";
  const FILTER_ACCOUNTS_URL = "/payment/filter";
  const CURRENCIES_URL = "/constants/currencies";

  const [STATUSES, setStatuses] = useState([]);
  const [selectedStatuses, setSelectedStatuses] = useState(STATUSES);
  const [systemReferenceFilter, setSystemReferenceFilter] = useState("");
  const [CURRENCIES, setCurrencies] = useState([]);
  const [selectedCurrencies, setSelectedCurrencies] = useState(CURRENCIES);

  const [showFilterNotification, setShowFilterNotification] = useState(false);

  const hideFilterNotification = () => {
    setShowFilterNotification(false);
  };
  
  useEffect(() => {
    if(!authResult) return;
    async function fetchConstants() {
      try {
        const response_statuses = await axios.get(STATUSES_URL);
        const response_currencies = await axios.get(CURRENCIES_URL);

        setStatuses(response_statuses.data.object);
        setSelectedStatuses(response_statuses.data.object);

        const currencyNames = response_currencies.data.object.map(
            (currency) => currency.name
          );
        setCurrencies(currencyNames);
        setSelectedCurrencies(currencyNames);

        setLoading(false);
      } catch (error) {
        console.error("Error fetching constants:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }
    fetchConstants();
  }, []);

  const fetchPayments = async () => {
    if(!authResult) return;
    try {
      const response = await axios.get(PAYMENT)
      console.log(response);
      setPayments(response.data.object)
      setLoading(false);
    } catch (error) {
      console.error("Error fetching payments:", error);
      setError(
        error.response?.status === 404
          ? "Payments not found"
          : "Error fetching data"
      );
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPayments();
  }, []);

  const handleFilter = async () => {
    try {
      const filterObject = {
        statuses: selectedStatuses,
        currencies: selectedCurrencies,
        systemReferenceFilter: systemReferenceFilter,
      };
      console.log(filterObject);

      const response = await axios.post(FILTER_ACCOUNTS_URL, filterObject, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      console.log(response);

      if (response.data.errors.length > 0) {
        const paymentError = response.data.errors
          .map((error) => error.errorMessage)
          .join("\n");
        alert("Filtering failed! " + paymentError);
      } else {
        setPayments(response.data.object);
        setShowFilterNotification(true);
        setTimeout(hideFilterNotification, 2000);
      }
    } catch (error) {
      console.error("Error filtering users:", error);
      // Handle error state here if needed
    }
  };



  const ulStyle = {
    marginTop: "10px",
    padding: 0,
    borderBottom: "1px solid #ccc", // Add a fine line (border) between history entries
    marginBottom: "10px",
  };

  const handleFilterToggleCurrencies = (currency) => {
    if (selectedCurrencies.includes(currency)) {
      setSelectedCurrencies((prevSelected) =>
        prevSelected.filter((item) => item !== currency)
      );
    } else {
      setSelectedCurrencies((prevSelected) => [...prevSelected, currency]);
    }
  };
  const handleFilterToggleStatuses = (paymentStatus) => {
    if (selectedStatuses.includes(paymentStatus)) {
      setSelectedStatuses((prevSelected) =>
        prevSelected.filter((item) => item !== paymentStatus)
      );
    } else {
        setSelectedStatuses((prevSelected) => [...prevSelected, paymentStatus]);
    }
  };

  if (loading) {
    return <div>Loading...</div>; // Show a loading message while waiting for data
  }

  if (error) {
    return <div>Error: {error}</div>; // Show an error message if the API call failed
  }

  if(!authResult){
    return  <div>Redirecting...</div>;
  }
  
  return (
    <div className="list">
          <h2>Payments</h2>
        <div className="filter-section">
        {showFilterNotification && (
          <div className="filter-notification">
            Filtering has been done successfully!
          </div>
        )}

        <div className="filter-group">
          <label className="filter-label">Filter by Status:</label>
          <div className="filter-options">
            {STATUSES.map((status) => (
              <label
                key={status}
                className={`filter-button ${
                  selectedStatuses.includes(status) ? "selected" : ""
                }`}
                onClick={() => handleFilterToggleStatuses(status)}
              >
                {status}
              </label>
            ))}
          </div>
        </div>

        <div className="filter-group">
          <label className="filter-label">Filter by Currency:</label>
          <div className="filter-options">
            {CURRENCIES.map((currency, index) => (
              <label
                key={currency}
                className={`filter-button ${
                  selectedCurrencies.includes(currency) ? "selected" : ""
                }`}
                onClick={() => handleFilterToggleCurrencies(currency)}
              >
                {currency}
              </label>
            ))}
          </div>
        </div>

        <div className="form-options">
          <label className="form-label">
            Filter by system reference:
          </label>
          <br/>
          <input
            type="text"
            name=""
            id="system_reference"
            className="form_input_view"
            placeholder="system reference"
            required
            value={systemReferenceFilter}
            onChange={(e) => setSystemReferenceFilter(e.target.value)}
          />
        </div>

        <button
          type="submit"
          className="filter-button button offset-right"
          onClick={handleFilter}
        >
          Filter
        </button>
      </div>
      <ul>
        {payments.map((object, index) => (
          <ul key={index} style={ulStyle}>
            <div>
              {object?.creditAccountNumber &&
                (
                  <div className="transaction-item">
                    <span className="transaction-info">Reference: {object.systemReference}</span>
                    <span className="transaction-info">Message: {object.userReference}</span>
                    <span className="transaction-info">Currency: {object.currency.name}</span>
                    <span className="transaction-info">Amount: {object.amount}</span>
                    <span className="transaction-info">From: {object.debitAccountNumber}</span>
                    <span className="transaction-info">To: {object.creditAccountNumber}</span>
                    <span className="transaction-info">At: {object.stringTimeStamp}</span>
                    <span className="transaction-info">Status: {object.status.name}</span>
                    <span className="transaction-info">Timestamp: {object.stringTimeStamp}</span>
                    <span className="transaction-info">Latitude: {object.latitude}</span>
                    <span className="transaction-info">Logitude: {object.longitude}</span>
                    <span className="transaction-info">Needed approval: {String(object.neededApproval)}</span>
                  </div>
                )}
            </div>
          </ul>
        ))}
      </ul>
    </div>
  );
}
export default ViewPayments;
