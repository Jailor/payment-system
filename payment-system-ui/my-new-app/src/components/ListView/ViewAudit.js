import React from "react";
import { useLocation, Link, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "../../api/axios";
import "../../styles/Form.css";

import { authenticate } from "../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";

function ViewAudit() {
  const navigate = useNavigate();
  const authResult = authenticate();

  const [audit, setAudit] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const fetchAudit = async () => {
    try {
      const response = await axios.get("/audit");
      if (response.data?.object) {
        setAudit(response.data.object);
      } else {
        setAudit(response.data);
      }
      console.log(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching audit:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    if(!authResult) return;
    fetchAudit();
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
  const spanStyle = {
    marginRight: "10px", // Add spacing between each piece of information
  };
  const ulStyle = {
    marginTop: "10px",
    padding: 0,
    borderBottom: "1px solid #ccc", // Add a fine line (border) between history entries
    marginBottom: "10px",
  };
  const formatDate = (timestamp) => {
    const date = new Date(...timestamp);
    return date.toLocaleString(); // Format the timestamp into a readable date string
  };

  if(audit.length === 0) {
    return <div> <h2> No audit entries found</h2> </div>;
  }

  return (
    <div className="list">
      <ul>
        {audit.map((object) => (
          <ul key={object.id} style={ulStyle}>
            <div>
              <p>
                <span style={spanStyle}>Username: {object.username}</span>
                <span style={spanStyle}>
                  Operation: {object.operation}
                </span>
                <span style={spanStyle}>
                  Timestamp: {formatDate(object.timeStamp)}
                </span>
                <span style={spanStyle}>Entity: {object.className}</span>
              </p>
            </div>
          </ul>
        ))}
      </ul>
    </div>
  );
}

export default ViewAudit;
