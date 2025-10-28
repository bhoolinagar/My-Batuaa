import { Link, useNavigate } from "react-router-dom";
import React, { useState } from "react";
import {
  Box,
  TextField,
  InputAdornment,
  Button,
  Snackbar,
} from "@mui/material";
import MuiAlert from "@mui/material/Alert";
import logo from "../assets/logo.png";
import "../component/AddMoney.css";
import EmailIcon from "@mui/icons-material/Email";
import LockIcon from "@mui/icons-material/Lock";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import axios from "axios";
import Footer from "./Footer";
import PublicAppBar from "../landing/PublicAppBar";
export default function Login() {
  const [formData, setFormData] = useState({ emailId: "", password: "" });
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
  });

  const navigate = useNavigate();

// Input change
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleCloseSnackbar = (_, reason) => {
    if (reason === "clickaway") return;
    setSnackbar({ ...snackbar, open: false });
  };


  const handleLogin = async (e) => {
    e.preventDefault();
    if (!formData.emailId || !formData.password) {
      setSnackbar({
        open: true,
        message: "Please enter email and password",
        severity: "error",
      });
      return;
    }

    try {
        
      const response = await axios.post(
        "http://localhost:8031/buyers/api/v1/login",
        formData,
        { headers: { "Content-Type": "application/json" } }

      ); 
      console.log("login API response:", response);

      const { token, role,email ,name} = response.data.data;

      // Save token and role
      sessionStorage.setItem("email",email)
      sessionStorage.setItem("token",token)
      sessionStorage.setItem("role", role)
      sessionStorage.setItem("name", name)


      // Show success message
      setSnackbar({
        open: true,
        message: response.data.message || "Login successful",
        severity: "success",
      });

      // Redirect based on role
      
      setTimeout(() => {
      if (role === "ADMIN") navigate("/admin/admindashboard");
        // navigate(`/admindashboard`);
      else navigate(`/dashboard`);
    }, 1000); // 

  } catch (error) {
    setSnackbar({
      open: true,
      message: error.response?.data?.message || "Login failed",
      severity: "error",
    });
    }
  };

  return (
    
    <div>
     {/*  Navbar */}
          <PublicAppBar/> 
              
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#f9f9f9",
        gap: 2,
      }}
    >
      
      
      <Box
        sx={{
          width: "400px",
          backgroundColor: "#f3f5f9",
          borderRadius: "20px",
          boxShadow: "0px 0px 20px rgba(0,0,0,0.1)",
          p: 4,
          textAlign: "center",
          border: "2px solid #0F3A6E",
          
        }}
      >
        <h3
          style={{
            marginBottom: "20px",
            color: "#0F3A6E",
            fontFamily: "Poppins, sans-serif",
          }}
        >
          Login
        </h3>

        <p style={{ color: "#555", marginBottom: "20px", fontSize: "0.9rem" }}>
          Don’t have an account?{" "}
          <Link
            to="/register"
            style={{
              color: "#0F3A6E",
              fontWeight: "600",
              textDecoration: "none",
            }}
          >
            Register
          </Link>
        </p>

        <form onSubmit={handleLogin}>
          <TextField
            label="Email ID"
            name="emailId"
            value={formData.emailId}
            onChange={handleChange}
            fullWidth
            margin="normal"
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <EmailIcon sx={{ color: "#0F3A6E" }} />
                </InputAdornment>
              ),
            }}
          />
          <TextField
            label="Password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
            fullWidth
            margin="normal"
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <LockIcon sx={{ color: "#0F3A6E" }} />
                </InputAdornment>
              ),
            }}
          />

          <Button
            type="submit"
            variant="contained"
            endIcon={<ArrowForwardIcon />}
            sx={{
              mt: 2,
              width: "60%",
              borderRadius: "40px",
              height: 45,
              backgroundColor: "#0F3A6E",
              fontSize: 18,
              fontWeight: 600,
              textTransform: "none",
              "&:hover": { backgroundColor: "#0d2e59" },
            }}
          >
            Login
          </Button>
        </form>
      </Box>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={5000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <MuiAlert
          elevation={6}
          variant="filled"
          severity={snackbar.severity}
          onClose={handleCloseSnackbar}
          sx={{
            backgroundColor: snackbar.severity === "success" ? "#4caf50" : "#d32f2f",
            color: "white",
            fontWeight: 600,
          }}
        >
          {snackbar.message}
        </MuiAlert>
      </Snackbar>
    </Box>
    <Footer/>
    </div>
  );
}
