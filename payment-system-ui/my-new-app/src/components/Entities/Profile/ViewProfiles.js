import React from "react";
import { useLocation, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "../../../api/axios";
import "../../../styles/Form.css";
import { useProfileContext } from "./ProfileContext";

import { authenticate } from "../../auth/AuthUtils.js";
import { useNavigate } from "react-router-dom";

function ViewProfiles() {
  const navigate = useNavigate();
  const authResult = authenticate();

  const { setProfileObject } = useProfileContext();
  const VIEW_PROFILES_URL = "/profile";
  const STATUSES_URL = "/constants/statuses";
  const PROFILE_TYPES_URL = "/constants/profile-types";
  const PROFILE_RIGHTS_URL = "/constants/profile-rights";

  const [PROFILE_RIGHTS, setProfileRights] = useState([]);
  const [PROFILE_TYPES, setProfileTypes] = useState([]);
  const [STATUSES, setStatuses] = useState([]);

  const FILTER_PROFILES_URL = "/profile/filter";
  const [profiles, setProfiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [passedString, setPassedString] = useState("");
  //const STATUSES = ["ACTIVE", "REPAIR", "REMOVED", "BLOCKED", "APPROVE"];
  const [selectedStatuses, setSelectedStatuses] = useState(STATUSES);
  const [nameFilter, setNameFilter] = useState("");
  const [selectedProfileTypes, setSelectedProfileTypes] = useState([]);
  const [selectedProfileRights, setSelectedProfileRights] = useState([]);
  const [rights, setRights] = useState([]);
  const [showFilterNotification, setShowFilterNotification] = useState(false);

  const hideFilterNotification = () => {
    setShowFilterNotification(false);
  };

  useEffect(() => {
    if(!authResult) return;
    async function fetchConstants() {
      try {
        const response_statuses = await axios.get(STATUSES_URL);
        const response_profile_types = await axios.get(PROFILE_TYPES_URL);
        const response_profile_rights = await axios.get(PROFILE_RIGHTS_URL);

        setStatuses(response_statuses.data.object);
        setProfileTypes(response_profile_types.data.object);
        setProfileRights(response_profile_rights.data.object);

        setSelectedProfileTypes(response_profile_types.data.object);
        setSelectedProfileRights(
          response_profile_rights.data.object.ADMINISTRATOR
        );
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
        const response = await axios.get(VIEW_PROFILES_URL);
        setProfiles(response.data.object);
        setLoading(false); // Data has been fetched, so set loading to false
      } catch (error) {
        console.error("Error fetching user data:", error);
        setError("Error fetching data"); // Set error state if there's an error
        setLoading(false); // Data fetching is completed (even if there's an error)
      }
    }

    fetchData();
  }, []);

  useEffect(() => {
    if(!authResult) return;
    if (selectedProfileTypes.includes("ADMINISTRATOR")) {
      setRights(PROFILE_RIGHTS.ADMINISTRATOR);
    } else if (selectedProfileTypes.includes("EMPLOYEE")) {
      setRights(PROFILE_RIGHTS.EMPLOYEE);
    } else if (selectedProfileTypes.includes("CUSTOMER")) {
      setRights(PROFILE_RIGHTS.CUSTOMER);
    } else {
      console.log("no rights");
      setRights([]);
    }
  }, [selectedProfileTypes]);
  
  const handleFilter = async () => {
    try {
      const filterObject = {
        statuses: selectedStatuses,
        types: selectedProfileTypes,
        nameFilter: nameFilter,
        rights: selectedProfileRights,
      };
      console.log(filterObject);

      // TODO: once filter endpoint is up and running, show the
      const response = await axios.post(FILTER_PROFILES_URL, filterObject, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      setProfiles(response.data.object);

      setShowFilterNotification(true);
      setTimeout(hideFilterNotification, 2000);
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
  const handleFilterToggleProfileTypes = (profileType) =>{
    if (selectedProfileTypes.includes(profileType)) {
      setSelectedProfileTypes((prevSelected) =>
        prevSelected.filter((item) => item !== profileType)
      );
    } else {
      setSelectedProfileTypes((prevSelected) => [...prevSelected, profileType]);
    }
  };
  const handleFilterToggleProfileRights = (right) => {
    if(selectedProfileRights.includes(right)){
      setSelectedProfileRights((prevSelected) => 
      prevSelected.filter((item) => item != right)
      );
    }else{
      setSelectedProfileRights((prevSelected) => [...prevSelected, right]);
    }
  }

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
    <div className="view-container">
      
        <h2>Profiles</h2>
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
          <br />
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
        <div className="filter-group">
        <label className="form-label">Filter by Profile Types:</label>
        <br />
        <div className="form-options">
        {PROFILE_TYPES.map((profileType) => (
              <label
                key={profileType}
                className={`filter-button ${
                  selectedProfileTypes.includes(profileType) ? "selected" : ""
                }`}
                onClick={() => handleFilterToggleProfileTypes(profileType)}
              >
                {profileType}
              </label>
            ))}
        </div>
        </div>
        <div className="filter-group">
        <label className="filter-label">Filter by Profile Rights:</label>
        <br />
        <div className="form-options">
        {rights.map((right) => (
              <label
                key={right}
                className={`filter-button ${
                  selectedProfileRights.includes(right) ? "selected" : ""
                }`}
                onClick={() => handleFilterToggleProfileRights(right)}
              >
                {right}
              </label>
            ))}
        </div>
        </div>
        <button
          type="submit"
          className="button offset-right"
          onClick={handleFilter}
        >
          Filter
        </button>
      </div>
      <ul>
        {profiles.map((object, index) => (
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
                object.needsApproval === false && sessionStorage.getItem("rights").includes("MODIFY_PROFILE") &&(
                  <Link
                    to={`/edit-profile/${object.name}`}
                    state={"edit"}
                    onClick={() => setProfileObject(object)}
                    className="link-button"
                  >
                    Edit
                  </Link>
                )}
              {sessionStorage.getItem("rights").includes("LIST_PROFILE") &&
              <Link
                to={`/view-profile/${object.name}`}
                onClick={() => setProfileObject(object)}
                className="link-button"
              >
                View
              </Link>}
              <Link
                to={`/view-history/${object.name}`}
                state={"/profile-history/"}
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
export default ViewProfiles;
