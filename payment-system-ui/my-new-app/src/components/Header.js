import React from "react";
import { AiOutlineUser } from "react-icons/ai";
import { isLoggedInCheck } from "./auth/AuthUtils";

import {
  Nav,
  Navbar,
  NavbarBrand,
  NavbarText,
  NavItem,
  NavLink,
} from "reactstrap";

const Header = () => {
  const isLoggedIn = isLoggedInCheck();
  const username = sessionStorage.getItem("username");
  return (
    <div>
      <Navbar color="primary" light expand="md">
        <NavbarBrand href={isLoggedIn ? "/welcome-user" : "/login"} style={{ color: "#f0f0f0" }}>
          Payment System
        </NavbarBrand>
        <Nav className="mr-auto" navbar>
          <NavItem>
            <NavLink style={{ color: "#f0f0f0" }}>
              {isLoggedIn ? `Hello, ${username}!` : "Login to see more details"}
            </NavLink>
          </NavItem>
        </Nav>
        <NavbarText>
          <div>
            <AiOutlineUser></AiOutlineUser>
          </div>
        </NavbarText>
      </Navbar>
    </div>
  );
};
export default Header;
