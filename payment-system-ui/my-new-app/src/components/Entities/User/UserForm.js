import React, { useEffect, useRef, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import "../../../styles/Form.css"
import axios from "../../../api/axios";
import {useUserContext } from './UserContext';
import { authenticate } from '../../auth/AuthUtils.js';
import { useNavigate } from "react-router-dom";
import { Alert } from "reactstrap";
import Select from "react-select"; 


function UserForm(props) {
  const navigate = useNavigate();
  const authResult = authenticate();

  const REDIRECT = 'https://localhost:3000/view-users';

  const { userObject } = useUserContext();
  const [userName, setUserName] = useState("");
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [address, setAddress] = useState("");
  const [selectedProfile, setSelectedProfile] = useState("");
  const [profiles, setProfiles] = useState([]);
  const [password, setPassword] = useState("");
  const USER_URL = "/user";
  const PROFILE_URL = "/profile/usable";
  const CUSTOMER_URL = "/customer";
  const { username } = useParams();
  const [user, setUser] = useState([]);
  const location = useLocation();
  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");
  const passedString = location.state;
  const [isCustomerSelected, setIsCustomerSelected] = useState(false);
  const [phoneNumber, setPhoneNumber] = useState("");
  const phoneRegex = /^\d{10}$/;

  // State for country and city selections
  const [selectedCountry, setSelectedCountry] = useState(null);
  const [selectedState, setSelectedState] = useState(null);
  const [selectedCity, setSelectedCity] = useState(null);

  const [countries, setCountries] = useState([]);
  const [states, setStates] = useState([]);
  const [cities, setCities] = useState([]);

  const [loadingCountries, setLoadingCountries] = useState(false);
  const [loadingStates, setLoadingStates] = useState(false);
  const [loadingCities, setLoadingCities] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if(!authResult) return;
    const fetchCountries = async () => {
      try {
        setLoadingCountries(true);
        // Check if the data is already cached in localStorage
        const cachedCountries = localStorage.getItem("cachedCountries");
        if (cachedCountries) {
          setCountries(JSON.parse(cachedCountries));
        } else {
          const response = await axios.get("https://countriesnow.space/api/v0.1/countries");
          const formattedCountries = response.data.data.map((country) => ({
            label: country.country,
            value: country.country,
          }));
          formattedCountries.sort((a, b) => a.label.localeCompare(b.label));
          setCountries(formattedCountries);
          // Cache the fetched data in localStorage
          localStorage.setItem("cachedCountries", JSON.stringify(formattedCountries));
        }
        setLoadingCountries(false);
      } catch (error) {
        console.error("Error fetching countries:", error);
        setLoadingCountries(false);
      }
      if (passedString === 'create') {
        setLoading(false);
      }
    };

    fetchCountries();
    console.log(countries);
  }, []);

  useEffect(() => {
    if(!authResult) return;
    const fetchStates = async () => {
      try {
        setLoadingStates(true);
        if (!selectedCountry) {
          setStates([]); // Clear the states list if no country is selected
          setSelectedState(null); // Clear the selected state
          setLoadingStates(false);
          return;
        }

        const response = await axios.post(
          "https://countriesnow.space/api/v0.1/countries/states",
          {
            country: selectedCountry.value
          }
        );

        const statesData = response.data.data.states;

        // Create formatted states array
        const formattedStates = statesData.map((state) => ({
          label: state.name,
          value: state.name,
        }));


        setStates(formattedStates);
        setLoadingStates(false);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching states:", error);
        setLoadingStates(false);
        setLoading(false);
      }
    };

    fetchStates();
  }, [selectedCountry]);

  useEffect(() => {
    if(!authResult) return;
    const fetchCities = async () => {
      try {
        setLoadingCities(true);
        console.log("selected state " + selectedState);
        if (!selectedState) {
          setCities([]); // Clear the cities list if no state is selected
          setLoadingCities(false);
          return;
        }

        console.log("selected state " + selectedState.value);

        const response = await axios.post(
          "https://countriesnow.space/api/v0.1/countries/state/cities",
          {
            country: selectedCountry.value,
            state: selectedState.value
          }
        );


        const citiesData = response.data.data;

        // Create formatted cities array
        const formattedCities = citiesData.map((city) => ({
          label: city,
          value: city,
        }));

        // Sort cities by name
        formattedCities.sort((a, b) => a.label.localeCompare(b.label));

        setCities(formattedCities);
        setLoadingCities(false);
      } catch (error) {
        console.error("Error fetching cities:", error);
        setLoadingCities(false);
      }
    };
    fetchCities();
  }, [selectedState]);


  useEffect(() => {
    if(!authResult) return;
    if (passedString === "edit") {
      if (userObject !== null) {
        setUserName(userObject.username);
        setFullName(userObject.fullName);
        setEmail(userObject.email);
        setAddress(userObject.address);
        setSelectedProfile(userObject.profileName);
        setPassword(userObject.password);
        setLoading(false);
      }
      else {
        const fetchUser = async () => {
          try {
            const response = await axios.get(`${USER_URL}/${username}`);
            const obj = response.data.object;
            setUser(obj);
            setUserName(obj.username);
            setFullName(obj.fullName);
            setEmail(obj.email);
            setAddress(obj.address);
            setSelectedProfile(obj.profileName);
            setPassword(obj.password);
          } catch (error) {
            // Handle error state here if needed
          }
        };
        fetchUser();
      }

    } else {
      // If the passedString is 'create', set fields to empty strings
      setUserName("");
      setFullName("");
      setEmail("");
      setAddress("");
      setSelectedProfile("");
      setPassword("");
      setSelectedCountry(null);
      setSelectedState(null);
      setSelectedCity(null);
    }
  }, [passedString, username]);
  useEffect(() => {
    if(!authResult) return;
    // Fetch profile data when the component mounts
    const fetchProfiles = async () => {
      try {
        const response = await axios.get(PROFILE_URL);
        setProfiles(response.data.object);
        if (passedString !== "edit") {
          if (response.data.object.length > 0) {
            setSelectedProfile(response.data.object[0].name);
          }
        }
      } catch (error) {
        // Handle error state here if needed
      }
    };
    fetchProfiles();
  }, []);
  useEffect(() => {
    if(!authResult) return;
    if (passedString !== "edit") {
      const profile = profiles.find(p => p.name === selectedProfile);
      if (profile && profile.profileType === "CUSTOMER") {
        setIsCustomerSelected(true);
      } else {
        setIsCustomerSelected(false);
      }
    }
  }, [selectedProfile, profiles]);


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
    if (isCustomerSelected && !phoneRegex.test(phoneNumber)) {
      // Handle phone validation error
      setErrors("Invalid phone number provided.");
      setShow(true);
      return;
    }
    try {
      
      if (passedString === "create") {
        if (isCustomerSelected) {
          const userResponse = await axios.get(USER_URL);
          const users = userResponse.data.object;

          const response = await axios.get(CUSTOMER_URL);
          const customers = response.data.object;

          // Filter customers that are NOT associated with a user
          const associatedCustomers = customers.filter((customer) => {
            const associatedUser = users.find((user) => user.email === customer.email);
            return associatedUser;
          });

          // Now, check if any of the non-associated customers have the currently entered phone number
          const customerWithPhoneNumber = associatedCustomers.find((c) => c.phoneNumber === phoneNumber);
          if (customerWithPhoneNumber) {
            // Handle phone validation error
            setErrors("Phone number already exists.");
            setShow(true);
            return;
          }
        }
        const response = await axios.post(
          USER_URL,
          JSON.stringify({
            username: userName,
            fullName,
            email,
            address,
            profileName: selectedProfile,
            password,
          }),
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
        // show alert if there are errors
        if (handleErrors(response.data.errors)) {
          return;
        }
        if (isCustomerSelected) {
          const response = await axios.post(
            CUSTOMER_URL,
            JSON.stringify({
              name: fullName,
              email,
              address,
              phoneNumber,
            }),
            {
              headers: { "Content-Type": "application/json" },
              withCredentials: true,
            }
          );
        }
      }
      else {
        const response = await axios.put(
          USER_URL,
          JSON.stringify({
            username: userName,
            fullName,
            email,
            address,
            //password, // password cannot be changed, and should not be sent for updates.
            // in order to change the password, use the reset feature
            profileName: selectedProfile
          }),
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
        // show alert if there are errors
        if (handleErrors(response.data.errors)) {
          return;
        }
      }
      window.location.href = REDIRECT;
    } catch (err) { }
    setUserName("");
    setFullName("");
    setEmail("");
    setAddress("");
    setSelectedProfile("");
    setPassword("");
  };

  function onDismiss() {
    setShow(false);
  }

  if (loading) {
    return <div>Loading...</div>
  }
  if(!authResult){
    return  <div>Redirecting...</div>;
  }


  return (
    <div className="form">
      <form className="form" onSubmit={handleSubmit}>
        <div className="form-body">
          <div className="form-input-container">
            <label className="form__label" htmlFor="userName">
              Username{" "}
            </label>
            <input
              className={passedString === "edit" ? "form__input disabled" : "form__input"}
              type="text"
              id="userName"
              placeholder="Username"
              required
              value={userName}
              readOnly={passedString === "edit"}
              onChange={(e) => setUserName(e.target.value)}
            />
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="fullName">
              Full Name{" "}
            </label>
            <input
              type="text"
              name=""
              id="fullName"
              className="form__input"
              placeholder="Full Name"
              required
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
            />
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="email">
              Email{" "}
            </label>
            <input
              type="email"
              id="email"
              className="form__input"
              placeholder="Email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="address">
              Address
            </label>
            <input
              className="form__input"
              type="Address"
              id="address"
              placeholder="Address"
              required
              value={address}
              onChange={(e) => setAddress(e.target.value)}
            />
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="Profile">
              Profile{" "}
            </label>
            <select
              className="form__input"
              id="profile"
              value={selectedProfile}
              required
              onChange={(e) => {
                setSelectedProfile(e.target.value)
                console.log(profiles)
              }
              }
            >
              {profiles.map((profile, index) => (
                <option key={index} value={profile.name}>
                  {profile.name}
                </option>
              ))}
            </select>
          </div>
          {isCustomerSelected === true && (
            <div className="form-input-container">
              <label className="form__label" htmlFor="phoneNumber">
                Phone Number{" "}
              </label>
              <input
                type="tel"
                name=""
                id="phoneNumber"
                className="form__input"
                placeholder="Phone Number"
                required
                readOnly={passedString === "edit"}
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
              />
            </div>)}
          {isCustomerSelected === true && (
            <div className="form-input-container">
              <label className="form__label" htmlFor="country">
                Country
              </label>
              <Select
                id="country"
                options={countries} // Options for countries (replace with your data)
                value={selectedCountry}
                onChange={(option) => {
                  setSelectedState(null);
                  setSelectedCity(null);
                  setStates([]);
                  setCities([]);
                  setSelectedCountry(option);
                }
                }
                placeholder="Select a country"
              />
              {loadingCountries && <p>Loading countries...</p>}
            </div>)}
          {isCustomerSelected === true && (
            <div className="form-input-container">
              <label className="form__label" htmlFor="state">
                Province/State
              </label>
              <Select
                id="state"
                options={states} // Options for states (replace with your data)
                value={selectedState}
                onChange={(option) => {
                  setSelectedCity(null);
                  setCities([]);
                  setSelectedState(option);
                }
                }
                placeholder="Select a state"
              />
              {loadingStates && <p>Loading states...</p>}
            </div>)}

          {isCustomerSelected === true && (
            <div className="form-input-container">
              <label className="form__label" htmlFor="city">
                City
              </label>
              <Select
                id="city"
                options={cities} // Options for cities (replace with your data)
                value={selectedCity}
                onChange={(option) => setSelectedCity(option)}
                placeholder="Select a city"
              />
              {loadingCities && <p>Loading cities...</p>}
            </div>)}

          {passedString === "create" && (
            <div className="form-input-container">
              <label className="form__label" htmlFor="password">
                Password
              </label>
              <input
                className="form__input"
                type="password"
                id="password"
                required
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          )}
        </div>
        <Alert color="danger" isOpen={show} toggle={onDismiss}>
          <p> {"Error:\n" + errors} </p>
        </Alert>
        <div className="footer">
          <button type="submit" class="button">
            {" "}
            {passedString === "edit" ? "Edit User" : "Create User"}
          </button>
        </div>

      </form>
    </div>
  );
}
export default UserForm;
