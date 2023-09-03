import axios from 'axios';

axios.defaults.baseURL = 'https://localhost:443/api';
// .create({
//     baseURL: 'http://localhost:8080/api'
// });

axios.interceptors.request.use((config) => {
    const username = sessionStorage.getItem("username");
    const profileName = sessionStorage.getItem("profileName");
    const jsonWebToken = sessionStorage.getItem("jsonWebToken");
    
    // const csrfToken = sessionStorage.getItem("csrfToken");
    // const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
    if (username) {
      config.headers["username"] = username;
    }
    if(profileName) {
        config.headers["profileName"] = profileName;
    }
    if(jsonWebToken){
        config.headers["Authorization"] = `Bearer ${jsonWebToken}`;
    }
    // if(csrfToken){
    //     config.headers["X-XSRF-TOKEN"] = csrfToken;
    //     config.headers["X-CSRF-TOKEN"] = csrfToken;
    //     config._csrf = csrfToken;
    // }
   
    console.log(config.headers + " " + config.url)
    return config;
  });

export default axios;