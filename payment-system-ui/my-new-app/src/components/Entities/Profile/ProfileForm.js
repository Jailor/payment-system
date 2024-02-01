import React, { useEffect, useRef, useState } from "react";
import { useLocation, Link, useParams } from "react-router-dom";
import "../../../styles/Form.css";
import axios from "../../../api/axios.js";
import { ProfileContextProvider, useProfileContext } from "./ProfileContext.js";
import { useNavigate } from "react-router-dom";
import { authenticate } from "../../auth/AuthUtils.js";
import { Alert } from "reactstrap";

function ProfileForm(props) {
  const navigate = useNavigate();
  const authResult = authenticate();

  const PROFILE_TYPES_URL = "/constants/profile-types";
  const PROFILE_RIGHTS_URL = "/constants/profile-rights";
  const REDIRECT = "https://localhost:3000/view-profiles";

  const [PROFILE_TYPES, setProfileTypes] = useState([]);
  const [PROFILE_RIGHTS, setProfileRights] = useState([]);

  const { profileObject } = useProfileContext();
  const [profileType, setProfileType] = useState("ADMINISTRATOR");
  const [name, setName] = useState("");
  const [rights, setRights] = useState([]);
  const PROFILE_URL = "/profile";
  const { profileName } = useParams();
  const location = useLocation();
  const passedString = location.state;
  const [GENERAL_RIGHTS, setGeneralRights] = useState([]);
  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");

  useEffect(() => {
    if(!authResult) return;
    async function fetchProfileConstants() {
      try {
        const responseProfileTypes = await axios.get(PROFILE_TYPES_URL);
        const responseProfileRights = await axios.get(PROFILE_RIGHTS_URL);

        setProfileTypes(responseProfileTypes.data.object);
        setProfileRights(responseProfileRights.data.object);
        
        setGeneralRights(responseProfileRights.data.object.ADMINISTRATOR);
        
        if (passedString === "edit") {
          if(profileObject.profileType === "ADMINISTRATOR"){
            setGeneralRights(responseProfileRights.data.object.ADMINISTRATOR);
          }
          else if(profileObject.profileType === "EMPLOYEE"){
            setGeneralRights(responseProfileRights.data.object.EMPLOYEE);
          }
          else if(profileObject.profileType === "CUSTOMER"){
            setGeneralRights(responseProfileRights.data.object.CUSTOMER);
          }
        }
      } catch (error) {
        console.error("Error fetching profile constants:", error);
      }
    }

    fetchProfileConstants();
  }, []);

  useEffect(() => {
    if(!authResult) return;
    if (passedString === "edit") {
      if (profileObject !== null) {
        console.log(profileObject);
        setName(profileObject.name);
        setProfileType(profileObject.profileType);
        setRights(profileObject.rights);
      } else {
        const fetchProfile = async () => {
          try {
            const response = await axios.get(`${PROFILE_URL}/${profileName}`);
            let obj = response.data.object;
            setName(obj.name);
            setProfileType(obj.profileType);
            setRights(obj.rights);
          } catch (error) {
            // Handle error state here if needed
          }
        };
        fetchProfile();
      }
    } else {
      // If the passedString is 'create', set fields to empty strings
      setName("");
    }
  }, [passedString, profileName]);

  // handle the errors
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
        const response = await axios.post(
          PROFILE_URL,
          JSON.stringify({ profileType, name, rights }),
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
        if (handleErrors(response.data.errors)) {
          return;
        }
      } else {
        const response = await axios.put(
          PROFILE_URL,
          JSON.stringify({ name, profileType, rights }),
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
        if (handleErrors(response.data.errors)) {
          return;
        }
      }
      window.location.href = REDIRECT;
    } catch (err) {}
    setName("");
    setRights([]);
  };

  function onDismiss() {
    setShow(false);
  }
  const handleFilterToggleProfileRights = (right) => {
    if(rights.includes(right)){
      setRights((prevSelected) => 
      prevSelected.filter((item) => item != right)
      );
    }else{
      setRights((prevSelected) => [...prevSelected, right]);
    }
  }

  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  return (
    <div className="form">
      <form className="form" onSubmit={handleSubmit}>
        <div className="form-body">
          <div className="form-input-container">
            <label className="form__label" htmlFor="name">
              Name{" "}
            </label>
            <input
              type="text"
              name=""
              id="name"
              className={
                passedString === "edit" ? "form__input disabled" : "form__input"
              }
              placeholder="Name"
              required
              value={name}
              readOnly={passedString === "edit"}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="Profile Type">
              Profile Type{" "}
            </label>
            <select
              className="form__input"
              id="profileType"
              value={profileType}
              required
              onChange={(e) => {
                setProfileType(e.target.value);

                if (e.target.value === "ADMINISTRATOR") {
                  setGeneralRights(PROFILE_RIGHTS.ADMINISTRATOR);
                } else if (e.target.value === "EMPLOYEE") {
                  setGeneralRights(PROFILE_RIGHTS.EMPLOYEE);
                } else if (e.target.value === "CUSTOMER") {
                  setGeneralRights(PROFILE_RIGHTS.CUSTOMER);
                } else {
                  setGeneralRights([]);
                }
              }}
            >
              {PROFILE_TYPES.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
          </div>
          <div className="form-input-container">
          <div className="filter-group">
        <label className="filter-label">Filter by Profile Rights:</label>
        <br />
        <div className="form-options">
        {GENERAL_RIGHTS.map((right) => (
              <label
                key={right}
                className={`filter-button ${
                  rights.includes(right) ? "selected" : ""
                }`}
                onClick={() => handleFilterToggleProfileRights(right)}
              >
                {right}
              </label>
            ))}
        </div>
        </div>
          </div>
        </div>
        <Alert color="danger" isOpen={show} toggle={onDismiss}>
          <p> {"Error:\n" + errors} </p>
        </Alert>
        <div className="footer">
          <button type="submit" className="btn">
            {" "}
            {passedString === "edit" ? "Edit Profile" : "Create Profile"}
          </button>
        </div>
      </form>
    </div>
  );
}
export default ProfileForm;
