import React from "react";
import { useLocation, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "../../../api/axios.js";
import "../../../styles/View.css"
import { useUserContext } from './UserContext.js';
import { authenticate } from '../../auth/AuthUtils.js';
import { useNavigate } from "react-router-dom";

function ViewUsers() {
  const navigate = useNavigate();
  const authResult = authenticate();

  const { setUserObject } = useUserContext();
  const VIEW_USERS_URL = "/user";
  const FILTER_USERS_URL = "/user/filter";
  const STATUSES_URL = "/constants/statuses";

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [STATUSES, setStatuses] = useState([]);
  const [selectedStatuses, setSelectedStatuses] = useState(["ACTIVE", "REPAIR", "REMOVED", "BLOCKED", "APPROVE"]);
  const [usernameFilter, setUsernameFilter] = useState("");
  const [fullNameFilter, setFullNameFilter] = useState("");
  const [emailFilter, setEmailFilter] = useState("");
  const [addressFilter, setAddressFilter] = useState("");
  const [showFilterNotification, setShowFilterNotification] = useState(false);


  const hideFilterNotification = () => {
    setShowFilterNotification(false);
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
        const response = await axios.get(VIEW_USERS_URL);
        setUsers(response.data.object);
        setLoading(false); // Data has been fetched, so set loading to false
      } catch (error) {
        console.error("Error fetching user data:", error);
        setError("Error fetching data"); // Set error state if there's an error
        setLoading(false); // Data fetching is completed (even if there's an error)
      }
    }

    fetchData();
  }, []);
  

  const handleFilter = async () => {
    try {
      const filterObject = {
         usernameFilter,
        fullNameFilter,
        emailFilter,
        addressFilter,
        statuses: selectedStatuses
      };
      console.log(filterObject);

      //TODO: once filter endpoint is up and running, show the 
      const response = await axios.post(FILTER_USERS_URL, filterObject, {
        headers: {
          "Content-Type": "application/json",
        },
      });
     // const response = await axios.get(VIEW_USERS_URL);

      // Update the filtered users state with the response data
      setUsers(response.data.object);
      setShowFilterNotification(true);
      setTimeout(hideFilterNotification, 2000);
    } catch (error) {
      console.error("Error filtering users:", error);
      // Handle error state here if needed
    }
  };

  const handleUserStatus = async (username, operationType) => {   
    const operationData = {
      operation: operationType,
    };
    const response = await axios.put('/user/' + username, operationData);
    console.log(response);
    if(response.data.errors.length > 0){
      const userError = response.data.errors.map((error) => error.errorMessage).join("\n");
      alert("User operation failed! "  + userError);
    }
    else{
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
      
      <h2>Users</h2>
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
            Filter by username:
          </label>
          <br/>
          <input
            type="text"
            name=""
            id="username"
            className="form_input_view"
            placeholder="username"
            required
            value={usernameFilter}
            onChange={(e) => setUsernameFilter(e.target.value)}
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
        {users.map((object, index) => (
          <ul key={index} className="info">
            <div>
              <div className="list-container">
                <p>{object.username}</p>
                <p>{object.status}</p>
              </div>
              {object.needsApproval === true && <p className="faded-text">Needs Approval</p>}
              {object.status !== 'REMOVED' && object.needsApproval === false && 
              sessionStorage.getItem("rights").includes("MODIFY_USER") &&
              object.username !== "admin" &&
              object.username !== "mobile" &&
              <Link
                to={`/edit-user/${object.username}`}
                state={"edit"}
                className="link-button"
                onClick={() => setUserObject(object)}
              >
                Edit
              </Link>}
              {sessionStorage.getItem("rights").includes("LIST_USER") &&
              <Link
                to={`/view-user/${object.username}`}
                className="link-button"
                onClick={() => setUserObject(object)}
              >
                View
              </Link>}

              {object.status === 'ACTIVE' && object.needsApproval === false && 
              sessionStorage.getItem("rights").includes("BLOCK_USER") &&
              object.username !== "admin" &&
              object.username !== "mobile" &&
              <button type="button" 
              className="link-button"
              onClick={() => handleUserStatus(object.username, "BLOCK")}>
                Block
              </button>}

              {object.status === 'BLOCKED' && object.needsApproval === false && sessionStorage.getItem("rights").includes("UNBLOCK_USER") &&
              <button type="button" 
              className="link-button"
              onClick={() => handleUserStatus(object.username, "UNBLOCK")}>
                Unblock
              </button>}

              <Link
                to={`/view-history/${object.username}`}
                state={`/user-history/`}
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
export default ViewUsers;
