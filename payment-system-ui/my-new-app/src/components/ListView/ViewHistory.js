import React from "react";
import { useLocation, Link, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "../../api/axios";
import "../../styles/Form.css";
import { authenticate } from "../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";

function ViewHistory(props) {
  const navigate = useNavigate();
  const authResult = authenticate();

  const [onlyFraud, setOnlyFraud] = useState(false);
  const [history, setHistory] = useState([]);
  const { parameter } = useParams();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const location = useLocation();
  const passedString = location.state;
  const [empty, setEmpty] = useState(false);

  const fetchUserHistory = async (parameter) => {
    try {
      var response = null;
      if (onlyFraud === true) {
        response = parameter
          ? await axios.get(`${passedString}fraud/${parameter}`)
          : await axios.get(`${passedString}/fraud`);
      }
      else {
        response = parameter
          ? await axios.get(`${passedString}${parameter}`)
          : await axios.get(`${passedString}`);
      }

      if (response.data?.object) {
        setHistory(response.data.object);
      } else {
        setHistory(response.data);
      }
      setLoading(false);
    } catch (error) {
      console.error("Error fetching history:", error);
      if(error.response?.status === 404){
        setEmpty(true);
      }
      else{
        setError( "Error fetching data"
        );
      }
      setLoading(false);
    }
  };

  useEffect(() => {
    if(!authResult) return;
    fetchUserHistory(parameter);
  }, [parameter, location.state, onlyFraud]);

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

  if(history.length === 0) {
    return <div> <h2> No history entries found</h2> </div>;
  }

  function formatCurrency(value, fractionDigits) {
    const formattedValue = (value / Math.pow(10, fractionDigits)).toFixed(fractionDigits);
    const parts = formattedValue.split('.');
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ' '); // Add white space for thousands
    return parts.join('.');
}


  return (
    <div className="list">
      {(passedString === "/payment-history" || passedString === "/payment-history/") &&
        (
          <button className="btn padded-button" onClick={async () => {
            setOnlyFraud(!onlyFraud);
          }}>
            {onlyFraud ? "Show All" : "Show Only Fraud"}
          </button>
        )}

      <ul>
        {history.map((object, index) => (
          <ul key={index} style={ulStyle}>
            <div>
              {(passedString === "/user-history" ||
                passedString === "/user-history/") &&
                object?.username && (
                <div className="transaction-item">
                  <p>
                    <span style={spanStyle}>Username: {object.username}</span> <br/>
                    <span style={spanStyle}>Full Name: {object.fullName}</span> <br/>
                    <span style={spanStyle}>Email: {object.email}</span> <br/>
                    <span style={spanStyle}>Address: {object.address}</span> <br/>
                    <span style={spanStyle}>Profile: {object.profileName}</span> <br/>
                    <span style={spanStyle}>Status: {object.status}</span> <br/>
                    <span style={spanStyle}>
                      Timestamp: {object.stringTimeStamp}
                    </span>
                  </p>
                  </div>
                )}
              {(passedString === "/profile-history" ||
                passedString === "/profile-history/") &&
                object?.name && (
                <div className="transaction-item">
                  <p>
                    <span style={spanStyle}>Name: {object.name}</span> <br/>
                    <span style={spanStyle}>
                      Profile Type: {object.profileType}
                    </span> <br/> 
                    <span style={spanStyle}>
                      Rights: {object.rights.join(", ")}
                    </span> <br/>
                    <span style={spanStyle}>Status: {object.status}</span> <br/>
                    <span style={spanStyle}>
                      Timestamp: {object.stringTimeStamp}
                    </span>
                  </p>
                  </div>
                )}
              {(passedString === "/account-history" ||
                passedString === "/account-history/") &&
                object?.accountNumber && (
                  <div className="transaction-item">
                  <p>
                    <span style={spanStyle}> 
                      Account Number: {object.accountNumber}
                    </span> <br/>
                    <span style={spanStyle}>Email: {object.ownerEmail}</span> <br/>
                    <span style={spanStyle}>
                      Currency: {object.currency.name}
                    </span> <br/>
                    <span style={spanStyle}>
                      Account Status: {object.accountStatus}
                    </span> <br/>
                    <span style={spanStyle}>Status: {object.status}</span> <br/>
                    <span style={spanStyle}> 
                      Timestamp: {object.stringTimeStamp}
                    </span>
                  </p>
                  </div>
                )}
              {(passedString === "/customer-history" ||
                passedString === "/customer-history/") &&
                object?.email && (
                  <div className="transaction-item">
                  <p>
                    <span style={spanStyle}>Name: {object.name}</span>
                    <span style={spanStyle}>
                      Phone Number: {object.phoneNumber}
                    </span>
                    <span style={spanStyle}>Email: {object.email}</span>
                    <span style={spanStyle}>Address: {object.address}</span>
                    <span style={spanStyle}>City: {object.city}</span>
                    <span style={spanStyle}>State: {object.state}</span>
                    <span style={spanStyle}>Country: {object.country}</span>
                    <span style={spanStyle}>Status: {object.status}</span>
                    <span style={spanStyle}>
                      Timestamp: {object.stringTimeStamp}
                    </span>
                  </p>
                  </div>
                )}
              {(passedString === "/payment-history" || passedString === "/payment-history/") && object?.creditAccountNumber &&
                (
                  <div className="transaction-item">
                    {parameter !== undefined && (
                    <span className={`transaction-type ${object.creditAccountNumber === parameter ? 'incoming' : 'outgoing'}`}>
                      {object.creditAccountNumber === parameter ? 'INCOMING' : 'OUTGOING'}
                    </span>)}
                    <span className="transaction-info">Reference: {object.systemReference}</span>
                    <span className="transaction-info">Message: {object.userReference}</span>
                    <span className="transaction-info">Currency: {object.currency.name}</span>
                    <span className="transaction-info">Amount: {
                      formatCurrency(object.amount, object.currency.fractionDigits) + " " + object.currency.name
                    }</span>
                    {parameter && (
                      <span className="transaction-info">To:
                        {object.debitAccountNumber !== parameter ? object.debitAccountNumber : object.creditAccountNumber}</span>)}
                    {parameter === undefined && (
                      <div>
                        <span className="transaction-info">From: {object.debitAccountNumber}</span>
                        <span className="transaction-info">To: {object.creditAccountNumber}</span>
                      </div>
                    )}
                    <span className="transaction-info">At: {object.stringTimeStamp}</span>
                    <span className="transaction-info">Status: {object.status}</span>
                    <span className="transaction-info">Timestamp: {object.stringHistoryTimeStamp}</span>
                  </div>
                )}
            </div>
          </ul>
        ))}
      </ul>
    </div>
  );
}
export default ViewHistory;
