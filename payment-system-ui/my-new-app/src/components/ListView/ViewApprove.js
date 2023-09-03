import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import axios from "../../api/axios";
import "../../styles/View.css";
import { authenticate } from "../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";

function ViewApprove() {
  const navigate = useNavigate();
  const authResult = authenticate();

  const VIEW_USERS_URL = "/user/requires-approval";
  const VIEW_PROFILES_URL = "/profile/requires-approval";
  const VIEW_ACCOUNTS_URL = "/account/requires-approval";
  const VIEW_CUSTOMERS_URL = "/customer/requires-approval";

  const [profiles, setProfiles] = useState([]);
  const [users, setUsers] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if(!authResult) return;
    async function fetchDataUsers() {
      try {
        const response = await axios.get(VIEW_USERS_URL);
        setUsers(response.data.object);

        setLoading(false);
      } catch (error) {
        console.error("Error fetching user data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }

    fetchDataUsers();
  }, []);

  useEffect(() => {
    if(!authResult) return;
    async function fetchDataProfiles() {
      try {
        const response = await axios.get(VIEW_PROFILES_URL);
        setProfiles(response.data.object);

        setLoading(false);
      } catch (error) {
        console.error("Error fetching profile data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }

    fetchDataProfiles();
  }, []);

  useEffect(() => {
    if(!authResult) return;
    async function fetchDataAccounts() {
      try {
        const response = await axios.get(VIEW_ACCOUNTS_URL);
        setAccounts(response.data.object);

        setLoading(false);
      } catch (error) {
        console.error("Error fetching account data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }

    fetchDataAccounts();
  }, []);

  useEffect(() => {
    if(!authResult) return;
    async function fetchDataCustomers() {
      try {
        const response = await axios.get(VIEW_CUSTOMERS_URL);
        setCustomers(response.data.object);

        setLoading(false);
      } catch (error) {
        console.error("Error fetching customer data:", error);
        setError("Error fetching data");
        setLoading(false);
      }
    }

    fetchDataCustomers();
  }, []);



  const spanStyle = {
    marginRight: "10px",
  };

  const ulStyle = {
    marginTop: "10px",
    padding: 0,
    borderBottom: "1px solid #ccc",
    marginBottom: "10px",
  };

    
  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  if(profiles.length === 0 && users.length === 0 && accounts.length === 0 && customers.length === 0) {
    return <div> <h2> No pending approvals </h2></div>;
  }

  return (
    <div className="list">
      <ul>
        {users.map((object) => (
          <ul key={object.id} style={ulStyle}>
            <div>
              <p>User: {object.username}</p>
              <Link
                to={`/approve-user/${object.username}`}
                className="small-link-button"
              >
                Approve/Reject
              </Link>
            </div>
          </ul>
        ))}
        {profiles.map((object) => (
          <ul key={object.id} style={ulStyle}>
            <div>
              <p>Profile: {object.name}</p>
              <Link
                to={`/approve-profile/${object.name}`}
                className="small-link-button"
              >
                Approve/Reject
              </Link>
            </div>
          </ul>
        ))}
        {accounts.map((object) => (
          <ul key={object.id} style={ulStyle}>
            <div>
              <p>Account: {object.accountNumber}</p>
              <Link
                to={`/approve-account/${object.accountNumber}`}
                className="small-link-button"
              >
                Approve/Reject
              </Link>
            </div>
          </ul>
        ))}
        {customers.map((object) => (
          <ul key={object.id} style={ulStyle}>
            <div>
              <p>Customer: {object.email}</p>
              <Link
                to={`/approve-customer/${object.email}`}
                className="small-link-button"
              >
                Approve/Reject
              </Link>
            </div>
          </ul>
        ))}
      </ul>
    </div>
  );
}

export default ViewApprove;
