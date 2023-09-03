import axios from "../../api/axios";

const isLoggedInCheck = () => {
  const username = sessionStorage.getItem("username");
  const profileName = sessionStorage.getItem("profileName");
  return username && profileName;
};
const LOGIN_URL = "https://localhost:3000/login";

const authenticate = () => {
  if (!isLoggedInCheck()) {
    window.location.href =LOGIN_URL;
    return false;
    // navigate("/login");
  }
  async function fetchSecureData() {
    try {
      const securityResponse = await axios.get('/secure');
      return true;
    } catch (error) {
      console.error("Error fetching secure data:", error);
      sessionStorage.clear();
      window.location.href = LOGIN_URL;
      return false;
    }
  }
  return fetchSecureData();
};

export { authenticate, isLoggedInCheck };
