import axios from 'axios';

axios.defaults.baseURL = 'https://localhost:443/api';
// .create({
//     baseURL: 'http://localhost:8080/api'
// });

axios.interceptors.request.use((config) => {
    const username = sessionStorage.getItem("username");
    const profileName = sessionStorage.getItem("profileName");
    const jsonWebToken = sessionStorage.getItem("jsonWebToken");
    
    if (username) {
      config.headers["username"] = username;
    }
    if(profileName) {
        config.headers["profileName"] = profileName;
    }
    if(jsonWebToken){
        config.headers["Authorization"] = `Bearer ${jsonWebToken}`;
    }
   
    console.log(config.headers + " " + config.url)
    return config;
  });

export default axios;
