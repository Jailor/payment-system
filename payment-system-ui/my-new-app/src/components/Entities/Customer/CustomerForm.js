import React, { useEffect, useRef, useState } from "react";
import { useLocation, Link, useParams } from "react-router-dom";
import "../../../styles/Form.css";
import axios from "../../../api/axios";
import { CustomerContextProvider, useCustomerContext } from "./CustomerContext";
import { authenticate } from "../../auth/AuthUtils.js";
import { Alert } from "reactstrap";
import Select from "react-select"; // Import react-select

function CustomerForm() {
  const authResult = authenticate();
  const REDIRECT = "https://localhost:3000/view-customers";

  const { customerObject } = useCustomerContext();
  const [name, setName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [email, setEmail] = useState("");
  const [address, setAddress] = useState("");
  const CUSTOMER_URL = "/customer";
  const { nameParam } = useParams();
  const location = useLocation();
  const [show, setShow] = useState(false);
  const [errors, setErrors] = useState("");
  const passedString = location.state;

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
      if(passedString === 'create'){
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
      if (customerObject !== null) {
        setName(customerObject.name);
        setPhoneNumber(customerObject.phoneNumber);
        setEmail(customerObject.email);
        setAddress(customerObject.address);
        setSelectedCountry({label: customerObject.country, value: customerObject.country});
        setSelectedState({label: customerObject.state, value: customerObject.state});
        setSelectedCity({label: customerObject.city, value: customerObject.city});
      } 
      else 
      {
        const fetchCustomer = async () => {
          try {
            const response = await axios.get(
              `${CUSTOMER_URL}/${customerObject.email}`
            );
            const obj = response.data.object;
            setName(obj.name);
            setPhoneNumber(obj.phoneNumber);
            setEmail(obj.email);
            setAddress(obj.address);
            setSelectedCountry({label: obj.country, value: obj.country});
            setSelectedState({label: obj.state, value: obj.state});
            setSelectedCity({label: obj.city, value: obj.city});
          } catch (error) {
            // Handle error state here if needed
          }
        };
        fetchCustomer();
      }
    } else {
      // If the passedString is 'create', set fields to empty strings
      setName("");
      setPhoneNumber("");
      setEmail("");
      setAddress("");
      setSelectedCountry(null);
      setSelectedState(null);
      setSelectedCity(null);
    }
  }, [passedString, nameParam]);

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
      console.log(passedString);
      console.log("name " + name);
      console.log("phoneNumber " + phoneNumber);
      console.log("email " + email);
      console.log("address " + address);
      if (passedString === "create") {
        const response = await axios.post(
          CUSTOMER_URL,
          JSON.stringify({
            name,
            phoneNumber,
            email,
            address,
            country: selectedCountry?.value,
            state: selectedState?.value,
            city: selectedCity?.value,
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
      } else {
        const response = await axios.put(
          CUSTOMER_URL,
          JSON.stringify({
            name,
            phoneNumber,
            email,
            address,
            country: selectedCountry?.value,
            state: selectedState?.value,
            city: selectedCity?.value,
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
    } catch (err) {console.log(err);}
    
    setName("");
    setPhoneNumber("");
    setEmail("");
    setAddress("");
    setSelectedCountry(null);
    setSelectedState(null);
    setSelectedCity(null);
  };

  function onDismiss() {
    setShow(false);
  }

  if(!authResult){
    return  <div>Redirecting...</div>;
  }

  if(loading){
    return <div>Loading...</div>
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
              className="form__input"
              type="text"
              id="name"
              placeholder="Name"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="phoneNumber">
              Phone Number{" "}
            </label>
            <input
              type="tel"
              name=""
              id="phoneNumber"
              className={
                passedString === "edit" ? "form__input disabled" : "form__input"
              }
              placeholder="Phone Number"
              required
              value={phoneNumber}
              readOnly={passedString === "edit"}
              onChange={(e) => setPhoneNumber(e.target.value)}
            />
          </div>
          <div className="form-input-container">
            <label className="form__label" htmlFor="email">
              Email{" "}
            </label>
            <input
              type="email"
              className={
                passedString === "edit" ? "form__input disabled" : "form__input"
              }
              id="email"
              placeholder="Email"
              required
              value={email}
              readOnly={passedString === "edit"}
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
              setSelectedCountry(option);}
            }
            placeholder="Select a country"
          />
          {loadingCountries && <p>Loading countries...</p>}
        </div>

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
        </div>

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
        </div>

        </div>
        <Alert color="danger" isOpen={show} toggle={onDismiss}>
          <p> {"Error:\n" + errors} </p>
        </Alert>
        <div className="footer">
          <button type="submit" class="button">
            {" "}
            {passedString === "edit" ? "Edit Customer" : "Create Customer"}
          </button>
        </div>
      </form>
    </div>
  );
}
export default CustomerForm;
