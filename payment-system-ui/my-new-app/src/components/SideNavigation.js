import { useState } from "react";
import {
  AiOutlineMenu,
  AiOutlineAudit,
  AiOutlineHistory,
  AiOutlineProfile,
  AiFillProfile,
  AiFillCheckCircle,
  AiFillAccountBook,
  AiFillMoneyCollect,
  AiFillDollarCircle,
  AiFillCustomerService,
  AiOutlineLogout,
  AiOutlineLock,
} from "react-icons/ai";
import { FaGem, FaHeart, FaUser } from "react-icons/fa";
import { BrowserRouter as Router, Route, Routes, Link } from "react-router-dom";
import ViewUsers from "./User/ViewUsers";
import { UserContextProvider, useUserContext } from './User/UserContext';
import { useCustomerContext } from "./Customer/CustomerContext";
import {
  Menu,
  MenuItem,
  ProSidebar,
  SidebarHeader,
  SubMenu,
} from "react-pro-sidebar";
import "react-pro-sidebar/dist/css/styles.css";
const SideNavigation = () => {
  const [collapsed, setCollapsed] = useState(false);
  const { customerObject } = useCustomerContext();
  const profileType = sessionStorage.getItem("profileType");
  const rights = sessionStorage.getItem("rights");
  const username = sessionStorage.getItem("username");
  const email = sessionStorage.getItem("email");
  // added styles
  const styles = {
    sideBarHeight: {
      height: "130vh",
    },
    menuIcon: {
      float: "right",
      margin: "10px",
    },
  };
  const onClickMenuIcon = () => {
    setCollapsed(!collapsed);
  };

  return (
    <ProSidebar style={styles.sideBarHeight} collapsed={collapsed}>
      <SidebarHeader>
        <div style={styles.menuIcon} onClick={onClickMenuIcon}>
          <AiOutlineMenu />
        </div>
      </SidebarHeader> 
    <Menu iconShape="square">
      {profileType !== "CUSTOMER" && (
       <div>
          <SubMenu title="User" icon={<FaUser />}>
            {rights.includes("LIST_USER") && (
              <MenuItem>
                <Link to="/view-users">View Users</Link>
              </MenuItem>
            )}
            {rights.includes("CREATE_USER") && (
              <MenuItem>
                <Link to="/create-user" state="create">
                  Create User
                </Link>
              </MenuItem>
            )}
            {profileType !== "CUSTOMER" && (
              <MenuItem>
                <Link to="/view-history" state="/user-history">
                  History
                </Link>
              </MenuItem>
            )}
          </SubMenu>

          <SubMenu title="Profile" icon={<AiFillProfile />}>
            {rights.includes("LIST_PROFILE") && (
              <MenuItem>
                <Link to="/view-profiles"> View Profiles </Link>
              </MenuItem>
            )}
            {rights.includes("CREATE_PROFILE") && (
              <MenuItem>
                <Link to="/create-profile" state="create">
                  Create Profile
                </Link>
              </MenuItem>
            )}
              <MenuItem>
                <Link to="/view-history" state="/profile-history">
                  History
                </Link>
              </MenuItem>
          </SubMenu>

          <SubMenu title="Account" icon={<AiFillDollarCircle />}>
            {rights.includes("LIST_ACCOUNT") && (
              <MenuItem>
                <Link to="/view-accounts"> View Accounts </Link>
              </MenuItem>
            )}
            {rights.includes("CREATE_ACCOUNT") && (
              <MenuItem>
                <Link to="/create-account" state="create">
                  Create Account
                </Link>
              </MenuItem>
            )}
            <MenuItem>
              <Link to="/view-history" state="/account-history">
                History
              </Link>
            </MenuItem>
          </SubMenu>

          <SubMenu title="Customer" icon={<AiFillCustomerService />}>
            {rights.includes("LIST_ACCOUNT") && (
              <MenuItem>
                <Link to="/view-customers"> View Customers </Link>
              </MenuItem>
            )}
            {rights.includes("CREATE_ACCOUNT") && (
              <MenuItem>
                <Link to="/create-customer" state="create">
                  Create Customer
                </Link>
              </MenuItem>
            )}
            <MenuItem>
              <Link to="/view-history" state="/customer-history">
                History
              </Link>
            </MenuItem>
          </SubMenu>

          <SubMenu title="Payments" icon={<AiFillMoneyCollect />}>
            {rights.includes("APPROVE_PAYMENT") &&
              rights.includes("CREATE_PAYMENT") && (
                <MenuItem>
                  <Link to="/approve-payments">Approve/Create Payments</Link>
                </MenuItem>
              )}
            {rights.includes("LIST_PAYMENT") && (
              <MenuItem>
                <Link to="/view-payments">View Payments</Link>
              </MenuItem>
            )}
            <MenuItem>
              <Link to="/view-history" state="/payment-history">
                Payment history
              </Link>
            </MenuItem>
          </SubMenu>

          <MenuItem icon={<AiOutlineAudit />}>
            <Link to="/view-audit">Audit</Link>
          </MenuItem>
          <MenuItem icon={<AiFillCheckCircle />}>
            <Link to="/view-approve">Approve</Link>
          </MenuItem>
          </div>
      )}
      {profileType === "CUSTOMER" && (
        <div>
          <SubMenu title="User" icon={<FaUser />}>
              <MenuItem>
                <Link to={`/view-user/${username}`}>View My Information</Link>
              </MenuItem>
          </SubMenu>
          <SubMenu title="Account" icon={<AiFillDollarCircle />}>
            {rights.includes("LIST_ACCOUNT") && (
              <MenuItem>
                <Link to="/view-accounts"> View My Accounts </Link>
              </MenuItem>
            )}
            {rights.includes("CREATE_ACCOUNT") && (
              <MenuItem>
                <Link to="/create-account" state="create">
                  Create Account
                </Link>
              </MenuItem>
            )}
            <MenuItem>
              <Link to="/view-history" state="/account-history">
                History
              </Link>
            </MenuItem>
          </SubMenu>
          <SubMenu title="Customer" icon={<AiFillCustomerService />}>
              <MenuItem>
                <Link to={`/view-customer/${email}`}>View My Information</Link>
              </MenuItem>
          </SubMenu>
          </div>
       
      )}
      <MenuItem icon={<AiOutlineLogout />}>
        <Link to="/logout"> Logout</Link>
      </MenuItem>
      <MenuItem icon={<AiOutlineLock />}>
        <Link to="/change-password">Change password</Link>
      </MenuItem>
      </Menu>
    </ProSidebar>
  );
};
export default SideNavigation;
