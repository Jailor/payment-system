import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import "../../../App.css"
import axios from "../../../api/axios";
import { authenticate } from '../../auth/AuthUtils.js';
import { Alert } from "reactstrap";

function ApproveUsers() {
    const authResult = authenticate();

    const REDIRECT = 'https://localhost:3000/view-users';
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { username } = useParams();

    const [show, setShow] = useState(false);
    const [errors, setErrors] = useState("");
    function onDismiss() {
      setShow(false);
    }
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

    useEffect(() => {
        if(!authResult) return;
        async function fetchData() {
            try {
                const response = await axios.get(`/user/requires-approval/${username}`);
                setUserData(response.data.object);
                console.log(response.data);
                setLoading(false); // Data has been fetched, so set loading to false
            } catch (error) {
                console.error("Error fetching user data:", error);
                setError("Error fetching data"); // Set error state if there's an error
                setLoading(false); // Data fetching is completed (even if there's an error)
            }
        }

        fetchData();
    }, []);
    if (loading) {
        return <div>Loading...</div>; // Show a loading message while waiting for data
    }

    if (error) {
        return <div>Error: {error}</div>; // Show an error message if the API call failed
    }

    const handleApprove = async () => {
        try {
            const response = await axios.post(
                `/user/approve/${userData.username}`
            );
            console.log(response);
            if (handleErrors(response.data.errors)) {
                return;
            }
            window.location.href = REDIRECT;
        } catch (error) {
            console.error("Error approving creation:", error);
        }
    };

    const handleReject = async () => {
        try {
            const response = await axios.post(
                `/user/reject/${userData.username}`
            );
            console.log(response);
            if (handleErrors(response.data.errors)) {
                return;
            }
            window.location.href = REDIRECT;
        } catch (error) {
            console.error("Error approving creation:", error);
        }

        window.location.href = REDIRECT;
    };
    
    if(!authResult){
        return  <div>Redirecting...</div>;
    }

    return (
        <div className="container">
            {(userData.operation === "CREATE" ||
                userData.operation === "REMOVE" ||
                userData.operation === "BLOCK" ||
                userData.operation === "UNBLOCK") &&
                <div>

                    <h2> Created User Information</h2>
                    <p>
                        <strong>Username:</strong> {userData.username}
                    </p>
                    <p>
                        <strong>Full name:</strong> {userData.fullName}
                    </p>
                    <p>
                        <strong>Email:</strong> {userData.email}
                    </p>
                    <p>
                        <strong>Address:</strong> {userData.address}
                    </p>
                    <p>
                        <strong>Profile:</strong> {userData.profileName}
                    </p>
                    <p>
                        <strong>Status:</strong> {userData.status}
                    </p>
                    <Alert color="danger" isOpen={show} toggle={onDismiss}>
                        <p> {"Error:\n" + errors} </p>
                    </Alert>
                    <div>
                        <button onClick={handleApprove} className="button">
                            Approve {userData.operation}
                        </button>
                        <button onClick={handleReject} className="button">
                            Reject {userData.operation}
                        </button>
                    </div>
                </div>}
            {userData.operation === "MODIFY" && (
                <div>
                    <h2>Modified User Information</h2>

                    <div className="row">
                        <div className="column">
                            <h3>Old Information</h3>
                            <p>
                                <strong>Username:</strong> {userData.username}
                            </p>
                            <p>
                                <strong>Full name:</strong> {userData.fullName}
                            </p>
                            <p>
                                <strong>Email:</strong> {userData.email}
                            </p>
                            <p>
                                <strong>Address:</strong> {userData.address}
                            </p>
                            <p>
                                <strong>Profile:</strong> {userData.profileName}
                            </p>
                            <p>
                                <strong>Status:</strong> {userData.status}
                            </p>
                        </div>

                        <div className="column">
                            <h3>New Information</h3>
                            <p>
                                <strong>Username:</strong> {userData.username}
                            </p>
                            <p>
                                <strong>Full name:</strong> {userData.newFullName}
                            </p>
                            <p>
                                <strong>Email:</strong> {userData.newEmail}
                            </p>
                            <p>
                                <strong>Address:</strong> {userData.newAddress}
                            </p>
                            <p>
                                <strong>Profile:</strong> {userData.newProfileName}
                            </p>
                            <p>
                                <strong>Status:</strong> {userData.newStatus}
                            </p>
                        </div>
                    </div>
                    <Alert color="danger" isOpen={show} toggle={onDismiss}>
                        <p> {"Error:\n" + errors} </p>
                    </Alert>
                    {sessionStorage.getItem("rights").includes("APPROVE_USER") &&
                    <div>
                        <button onClick={handleApprove} className="button">
                            Approve Modification
                        </button>
                        <button onClick={handleReject} className="button">
                            Reject Modification
                        </button>
                    </div>}
                </div>
            )}
        </div>

    );
}
export default ApproveUsers;
