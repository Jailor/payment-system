import React from "react";
import { useLocation, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "../../../api/axios.js";
import "../../../styles/View.css";
import { useCustomerContext } from "./CustomerContext.js";
import { authenticate } from "../../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";

function ViewCustomers() {
  const navigate = useNavigate();
  const authResult = authenticate();

  const { setCustomerObject } = useCustomerContext();
  const VIEW_CUSTOMERS_URL = "/customer";
  const FILTER_CUSTOMER_URL = "/customer/filter";
  const STATUSES_URL = "/constants/statuses";

  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [STATUSES, setStatuses] = useState([]);
  const [selectedStatuses, setSelectedStatuses] = useState(STATUSES);
  const [nameFilter, setNameFilter] = useState("");
  const [addressFilter, setAddressFilter] = useState("");
  const [phoneNumberFilter, setPhoneNumberFilter] = useState("");
  const [emailFilter, setEmailFilter] = useState("");
  const [showFilterNotification, setShowFilterNotification] = useState(false);

  const hideFilterNotification = () => {
    setShowFilterNotification(false);
  };

  useEffect(() => {
    if(!authResult) return;
    async function fetchConstants() {
      try {
        const response_statuses = await axios.get(STATUSES_URL);
        setStatuses(response_statuses.data.object);
        setSelectedStatuses(response_statuses.data.object);

        setLoading(false);
      } catch (error) {
        console.error("Error fetching constants:", error);
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
        const response = await axios.get(VIEW_CUSTOMERS_URL);
        setCustomers(response.data.object);
        setLoading(false); // Data has been fetched, so set loading to false
      } catch (error) {
        console.error("Error fetching customer data:", error);
        setError("Error fetching data"); // Set error state if there's an error
        setLoading(false); // Data fetching is completed (even if there's an error)
      }
    }

    fetchData();
  }, []);

  const handleFilter = async () => {
    try {
      const filterObject = {
        nameFilter,
        phoneNumberFilter,
        emailFilter,
        addressFilter,
        statuses: selectedStatuses,
      };
      console.log(filterObject);

      //TODO: once filter endpoint is up and running, show the
      const response = await axios.post(FILTER_CUSTOMER_URL, filterObject, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      // const response = await axios.get(VIEW_USERS_URL);

      // Update the filtered users state with the response data
      setCustomers(response.data.object);
      setShowFilterNotification(true);
      setTimeout(hideFilterNotification, 2000);
    } catch (error) {
      console.error("Error filtering customers:", error);
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
        <h2>Customers</h2>
        <div className="filter-section">
        {showFilterNotification && (
          <div className="filter-notification">
            Filtering has been done successfully!
          </div>
        )}
        <div className="filter-group">
        <label className="filter-label">Filter by Status:</label><br />
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
        <div className="form-options">
          <label className="form-label">
            Filter by name:
          </label>
          <br/>
          <input
            type="text"
            name=""
            id="name"
            className="form_input_view"
            placeholder="name"
            required
            value={nameFilter}
            onChange={(e) => setNameFilter(e.target.value)}
          />
        </div>
        <div className="form-options">
          <label className="form-label">Filter by email:</label>
          <br/>
          <input
            type="text"
            name=""
            id="email"
            className="form_input_view"
            placeholder="email"
            required
            value={emailFilter}
            onChange={(e) => setEmailFilter(e.target.value)}
          />
        </div>
        <button type="submit" className="button offset-right" onClick={handleFilter}>
          Filter
        </button>
      </div>
      <ul>
        {customers.map((object, index) => (
          <ul key={index} className="info">
            <div>
              <div className="list-container">
                <p>{object.name}</p>
                <p>{object.status}</p>
              </div>
              {object.needsApproval === true && (
                <p className="faded-text">Needs Approval</p>
              )}
              {object.status !== "REMOVED" &&
                object.needsApproval === false && (
                  <Link
                    to={`/edit-customer/${object.email}`}
                    state={"edit"}
                    className="link-button"
                    onClick={() => setCustomerObject(object)}
                  >
                    Edit
                  </Link>
                )}
              <Link
                to={`/view-customer/${object.email}`}
                className="link-button"
                onClick={() => setCustomerObject(object)}
              >
                View
              </Link>
              <Link
                to={`/view-history/${object.email}`}
                state={`/customer-history/`}
                className="link-button"
              >
                History
              </Link>
            </div>
          </ul>
        ))}
      </ul>
    </div>
  );
}
export default ViewCustomers;
