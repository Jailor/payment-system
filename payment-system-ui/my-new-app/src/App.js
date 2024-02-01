import React from "react";
import { BrowserRouter as Router, Route, Routes, Navigate  } from "react-router-dom";
import { Col, Row } from "reactstrap";
import Header from "./components/Header";
import SideNavigation from "./components/SideNavigation";
import Home from "./components/Home";
import UserForm from "./components/Entities/User/UserForm";
import ViewUsers from "./components/Entities/User/ViewUsers";
import ViewHistory from "./components/ListView/ViewHistory";
import ViewUserInformation from "./components/Entities/User/ViewUserInformation";
import ViewAudit from "./components/ListView/ViewAudit";
import ViewProfiles from "./components/Entities/Profile/ViewProfiles";
import ProfileForm from "./components/Entities/Profile/ProfileForm";
import LoginPage from "./components/auth/LoginPage.js";
import Logout from "./components/auth/Logout.js";
import AccountForm from "./components/Entities/Account/AccountForm";
import { UserContextProvider } from "./components/Entities/User/UserContext";
import { ProfileContextProvider } from "./components/Entities/Profile/ProfileContext";
import ViewProfileInformation from "./components/Entities/Profile/ViewProfileInformation";
import ApproveUsers from "./components/Entities/User/ApproveUsers";
import ApproveProfiles from "./components/Entities/Profile/ApproveProfiles";
import ViewApprove from "./components/ListView/ViewApprove";
import { AccountContextProvider } from "./components/Entities/Account/AccountContext";
import ViewAccounts from "./components/Entities/Account/ViewAccounts";
import { isLoggedInCheck } from "./components/auth/AuthUtils.js";
import ViewAccountInformation from "./components/Entities/Account/ViewAccountInformation";
import ApproveAccounts from "./components/Entities/Account/ApproveAccounts";
import ChangePassword from "./components/auth/ChangePassword.js";
import ViewCustomers from "./components/Entities/Customer/ViewCustomers";
import { CustomerContextProvider } from "./components/Entities/Customer/CustomerContext";
import CustomerForm from "./components/Entities/Customer/CustomerForm";
import ApproveCustomers from "./components/Entities/Customer/ApproveCustomers";
import ViewCustomerInformation from "./components/Entities/Customer/ViewCustomerInformation";
import { BalanceContextProvider } from "./components/Entities/Balance/BalanceContext";
import ApprovePayments from "./components/Entities/Payment/ApprovePayments";
import ViewPayments from "./components/Entities/Payment/ViewPayments";

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
                      {!isLoggedInCheck() && <Route path="/" element={<LoginPage />} />}
                      {isLoggedInCheck() && <Route path="/" element={<Home />} />}
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
                      <Route path="*" element={<Home />} />
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
