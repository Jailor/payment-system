import React from "react";
import { useLocation, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "../../../api/axios.js";
import "../../../styles/View.css";
import { authenticate } from "../../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";
import { useAccountContext } from "./AccountContext.js";
import { useCustomerContext } from "../Customer/CustomerContext.js";

function ViewAccounts() {
  const navigate = useNavigate();
  const authResult = authenticate();

  const { setAccountObject } = useAccountContext();
  const STATUSES_URL = "/constants/statuses";
  const CURRENCIES_URL = "/constants/currencies";
  const ACCOUNT_STATUSES_URL = "/constants/account-statuses";

  const VIEW_ACCOUNTS_URL = "/account";
  const FILTER_ACCOUNTS_URL = "/account/filter";
  
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [ACCOUNT_STATUSES, setAccountStatuses] = useState([]);
  const [STATUSES, setStatuses] = useState([]);
  const [CURRENCIES, setCurrencies] = useState([]);

  const [selectedStatuses, setSelectedStatuses] = useState(STATUSES);
  const [selectedCurrencies, setSelectedCurrencies] = useState(CURRENCIES);
  const [selectedAccountStatuses, setSelectedAccountStatuses] =
    useState(ACCOUNT_STATUSES);

  const [showFilterNotification, setShowFilterNotification] = useState(false);
  const [response,setResponse] = useState([]);
  const { customerObject } = useCustomerContext();


  const hideFilterNotification = () => {
    setShowFilterNotification(false);
  };

  useEffect(() => {
    if(!authResult) return;

    async function fetchConstants() {
      try {
        const response_statuses = await axios.get(STATUSES_URL);
        const response_currencies = await axios.get(CURRENCIES_URL);
        const response_account_statuses = await axios.get(ACCOUNT_STATUSES_URL);

        setStatuses(response_statuses.data.object);
        const currencyNames = response_currencies.data.object.map(
          (currency) => currency.name
        );
        setCurrencies(currencyNames);
        setAccountStatuses(response_account_statuses.data.object);

        setSelectedStatuses(response_statuses.data.object);
        setSelectedCurrencies(currencyNames);
        setSelectedAccountStatuses(response_account_statuses.data.object);

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

    async function fetchData() {
      try {
        if(sessionStorage.getItem("profileType")!=="CUSTOMER"){
          const response = await axios.get(VIEW_ACCOUNTS_URL);
          setAccounts(response.data.object);
          setResponse(response);
        }
        else{
          const response = await axios.get(`${VIEW_ACCOUNTS_URL}/find-by-email/${customerObject.email}`);
          setAccounts(response.data.object);
        }
        console.log(accounts);
        setLoading(false); // Data has been fetched, so set loading to false
        
      } catch (error) {
        console.error("Error fetching account data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }

    fetchData();
  }, []);

  const handleFilter = async () => {
    try {
      const filterObject = {
        statuses: selectedStatuses,
        currencyFilter: selectedCurrencies,
        accountStatusFilter: selectedAccountStatuses,
        ownerEmail: "",
      };
      console.log(filterObject);

      const response = await axios.post(FILTER_ACCOUNTS_URL, filterObject, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      console.log(response);

      if (response.data.errors.length > 0) {
        const accountError = response.data.errors
          .map((error) => error.errorMessage)
          .join("\n");
        alert("Filtering failed! " + accountError);
      } else {
        setAccounts(response.data.object);
        setShowFilterNotification(true);
        setTimeout(hideFilterNotification, 2000);
      }
    } catch (error) {
      console.error("Error filtering users:", error);
      // Handle error state here if needed
    }
  };
  
  const handleFilterToggleStatuses = (status) => {
    if (selectedStatuses.includes(status)) {
      setSelectedStatuses((prevSelected) =>
        prevSelected.filter((item) => item !== status)
      );
    } else {
      setSelectedStatuses((prevSelected) => [...prevSelected, status]);
    }
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
  const handleFilterToggleAccountStatuses = (accountStatus) => {
    if (selectedAccountStatuses.includes(accountStatus)) {
      setSelectedAccountStatuses((prevSelected) =>
        prevSelected.filter((item) => item !== accountStatus)
      );
    } else {
      setSelectedAccountStatuses((prevSelected) => [...prevSelected, accountStatus]);
    }
  };
  const handleAccountStatus = async (accountNumber, operationType) => {
    const operationData = {
      operation: operationType,
    };
    const response = await axios.put(
      "/account/" + accountNumber,
      operationData
    );
    console.log(response);
    if (response.data.errors.length > 0) {
      const accountError = response.data.errors
        .map((error) => error.errorMessage)
        .join("\n");
      alert("Account peration failed! " + accountError);
    } else {
      window.location.reload();
    }
  };

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
    <div className="view-container">
      <h2>Accounts</h2>
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

        <div className="filter-group">
          <label className="filter-label">Filter by Account Status:</label>
          <div className="filter-options">
            {ACCOUNT_STATUSES.map((accountStatus, index) => (
              <label
                key={index}
                className={`filter-button ${
                  selectedAccountStatuses.includes(accountStatus)
                    ? "selected"
                    : ""
                }`}
                onClick={() => handleFilterToggleAccountStatuses(accountStatus)}
              >
                {accountStatus}
              </label>
            ))}
          </div>
        </div>

        <button
          type="submit"
          className="filter-button button offset-right"
          onClick={handleFilter}
        >
          Filter
        </button>
      </div>
      <div >
      <ul>
        {accounts.map((account, index) => (
          <ul key={index} className="info">
            <div>
              <div className="list-container">
                <p>Account Number: {account.accountNumber}</p>
                <p>Status: {account.status}</p>
                <p>Account status: {account.accountStatus} </p>
                <p>Owner: {account.ownerEmail} </p>
                <p>Currency: {account.currency.name} </p>
              </div>
              {account.needsApproval === true && (
                <p className="faded-text">Needs Approval</p>
              )}
              {account.status !== "REMOVED" &&
                account.needsApproval === false && sessionStorage.getItem("rights").includes("MODIFY_ACCOUNT") && (
                  <Link
                    to={`/edit-account/${account.accountNumber}`}
                    state={"edit"}
                    className="link-button"
                    onClick={() => setAccountObject(account)}
                  >
                    Edit
                  </Link>
                )}
              <Link
                to={`/view-account/${account.accountNumber}`}
                className="link-button"
                onClick={() => setAccountObject(account)}
              >
                View
              </Link>
              {sessionStorage.getItem("profileType") !== "CUSTOMER" &&
              <div>
              {account.accountStatus !== "CLOSED" &&
                account.needsApproval === false && (
                  <button
                    className="link-button"
                    onClick={() =>
                      handleAccountStatus(account.accountNumber, "CLOSE")
                    }
                  >
                    Close
                  </button>
                )}

              {account.accountStatus === "OPEN" &&
                account.needsApproval === false && (
                  <button
                    className="link-button"
                    onClick={() =>
                      handleAccountStatus(account.accountNumber, "BLOCK")
                    }
                  >
                    Block
                  </button>
                )}

              {(account.accountStatus === "OPEN" ||
                account.accountStatus === "BLOCKED_DEBIT") &&
                account.needsApproval === false && sessionStorage.getItem("rights").includes("MODIFY_ACCOUNT") &&(
                  <button
                    className="link-button"
                    onClick={() =>
                      handleAccountStatus(account.accountNumber, "BLOCK_CREDIT")
                    }
                  >
                    Block Credit
                  </button>
                )}

              {(account.accountStatus === "OPEN" ||
                account.accountStatus === "BLOCKED_CREDIT") && sessionStorage.getItem("rights").includes("MODIFY_ACCOUNT") &&
                account.needsApproval === false && (
                  <button
                    className="link-button"
                    onClick={() =>
                      handleAccountStatus(account.accountNumber, "BLOCK_DEBIT")
                    }
                  >
                    Block Debit
                  </button>
                )}

              {account.accountStatus === "BLOCKED" && sessionStorage.getItem("rights").includes("MODIFY_ACCOUNT") &&
                account.needsApproval === false && (
                  <button
                    className="link-button"
                    onClick={() =>
                      handleAccountStatus(account.accountNumber, "UNBLOCK")
                    }
                  >
                    Unblock
                  </button>
                )}

              {(account.accountStatus === "BLOCKED" ||
                account.accountStatus === "BLOCKED_CREDIT") &&
                account.needsApproval === false && sessionStorage.getItem("rights").includes("MODIFY_ACCOUNT") &&(
                  <button
                    className="link-button"
                    onClick={() =>
                      handleAccountStatus(
                        account.accountNumber,
                        "UNBLOCK_CREDIT"
                      )
                    }
                  >
                    Unblock Credit
                  </button>
                )}

              {(account.accountStatus === "BLOCKED" ||
                account.accountStatus === "BLOCKED_DEBIT") &&
                account.needsApproval === false && sessionStorage.getItem("rights").includes("MODIFY_ACCOUNT") && (
                  <button
                    className="link-button"
                    onClick={() =>
                      handleAccountStatus(
                        account.accountNumber,
                        "UNBLOCK_DEBIT"
                      )
                    }
                  >
                    Unblock Debit
                  </button>
                )}

              <Link
                to={`/view-history/${account.accountNumber}`}
                state={`/account-history/`}
                className="link-button"
              >
                History
              </Link>
              </div>}
            </div>
          </ul>
        ))}
      </ul>
      </div>
      
    </div>
  );
}
export default ViewAccounts;
