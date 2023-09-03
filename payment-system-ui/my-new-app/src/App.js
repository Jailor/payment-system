import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { Col, Row } from "reactstrap";
import Header from "./components/Header";
import SideNavigation from "./components/SideNavigation";
import Home from "./components/Home";
import UserForm from "./components/User/UserForm";
import ViewUsers from "./components/User/ViewUsers";
import ViewHistory from "./components/ListView/ViewHistory";
import ViewUserInformation from "./components/User/ViewUserInformation";
import ViewAudit from "./components/ListView/ViewAudit";
import ViewProfiles from "./components/Profile/ViewProfiles";
import ProfileForm from "./components/Profile/ProfileForm";
import LoginPage from "./components/Login/LoginPage";
import Logout from "./components/Login/Logout";
import AccountForm from "./components/Account/AccountForm";
import { UserContextProvider } from "./components/User/UserContext";
import { ProfileContextProvider } from "./components/Profile/ProfileContext";
import ViewProfileInformation from "./components/Profile/ViewProfileInformation";
import ApproveUsers from "./components/User/ApproveUsers";
import ApproveProfiles from "./components/Profile/ApproveProfiles";
import ViewApprove from "./components/ListView/ViewApprove";
import { useNavigate } from "react-router-dom";
import { AccountContextProvider } from "./components/Account/AccountContext";
import PrivateRoute from "./components/PrivateRoute";
import { Outlet } from "react-router-dom";
import ViewAccounts from "./components/Account/ViewAccounts";
import { isLoggedInCheck } from "./components/auth/AuthUtils.js";
import ViewAccountInformation from "./components/Account/ViewAccountInformation";
import ApproveAccounts from "./components/Account/ApproveAccounts";
import ChangePassword from "./components/Login/ChangePassword";
import RedirectReq from "./components/RedirectReq";
import ViewCustomers from "./components/Customer/ViewCustomers";
import { CustomerContextProvider } from "./components/Customer/CustomerContext";
import CustomerForm from "./components/Customer/CustomerForm";
import ApproveCustomers from "./components/Customer/ApproveCustomers";
import ViewCustomerInformation from "./components/Customer/ViewCustomerInformation";
import { BalanceContextProvider } from "./components/Balance/BalanceContext";
import ApprovePayments from "./components/Payment/ApprovePayments";
import ViewPayments from "./components/Payment/ViewPayments";

function App() {
  const styles = {
    contentDiv: {
      display: "flex",
    },
    contentMargin: {
      marginLeft: "10px",
      width: "100%",
    },
  };
  return (
    <div>
      <UserContextProvider>
        <ProfileContextProvider>
          <AccountContextProvider>
            <CustomerContextProvider>
              <BalanceContextProvider>
                <Row>
                  <Col>
                    <Header />
                  </Col>
                </Row>
                <div style={styles.contentDiv}>
                  <Router>
                    {isLoggedInCheck() && <SideNavigation />}
                    <Routes>
                      <Route path="/" element={<LoginPage />} />
                      <Route path="/welcome-user" element={<Home />} />
                      <Route path="/login" element={<LoginPage />} />
                      <Route path="/logout" element={<Logout />} />

                      <Route path="/create-user" element={<UserForm />} />
                      <Route path="/create-profile" element={<ProfileForm />} />
                      <Route path="/create-account" element={<AccountForm />} />
                      <Route
                        path="/create-customer"
                        element={<CustomerForm />}
                      />

                      <Route
                        path="/edit-user/:username"
                        element={<UserForm />}
                      />
                      <Route
                        path="/edit-account/:accountnumber"
                        element={<AccountForm />}
                      />
                      <Route
                        path="/edit-profile/:profileName"
                        element={<ProfileForm />}
                      />
                      <Route
                        path="/edit-customer/:name"
                        element={<CustomerForm />}
                      />

                      <Route path="/view-users" element={<ViewUsers />} />
                      <Route path="/view-accounts" element={<ViewAccounts />} />
                      <Route path="/view-profiles" element={<ViewProfiles />} />
                      <Route
                        path="/view-customers"
                        element={<ViewCustomers />}
                      />

                      <Route path="/view-history" element={<ViewHistory />} />
                      <Route
                        path="/view-history/:parameter"
                        element={<ViewHistory />}
                      />

                      <Route
                        path="/view-user/:username"
                        element={<ViewUserInformation />}
                      />
                      <Route
                        path="/view-profile/:name"
                        element={<ViewProfileInformation />}
                      />
                      <Route
                        path="/view-account/:accountNumber"
                        element={<ViewAccountInformation />}
                      />
                      <Route
                        path="/view-customer/:name"
                        element={<ViewCustomerInformation />}
                      />

                      <Route
                        path="/approve-user/:username"
                        element={<ApproveUsers />}
                      />
                      <Route
                        path="/approve-profile/:name"
                        element={<ApproveProfiles />}
                      />
                      <Route
                        path="/approve-account/:accountNumber"
                        element={<ApproveAccounts />}
                      />
                      <Route
                        path="/approve-customer/:email"
                        element={<ApproveCustomers />}
                      />

                      <Route path="/view-approve" element={<ViewApprove />} />
                      <Route path="/view-audit" element={<ViewAudit />} />
                      <Route
                        path="/change-password"
                        element={<ChangePassword />}
                      />
                      <Route path="/approve-payments" element={<ApprovePayments/>} />
                      <Route path="/view-payments" element={<ViewPayments/>} />
                      <Route
                        path="/redirect/:redirect"
                        element={<RedirectReq />}
                      />
                    </Routes>
                  </Router>
                </div>
              </BalanceContextProvider>
            </CustomerContextProvider>
          </AccountContextProvider>
        </ProfileContextProvider>
      </UserContextProvider>
    </div>
  );
}

export default App;
