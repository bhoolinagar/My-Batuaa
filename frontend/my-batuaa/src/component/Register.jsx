import { Link, useNavigate } from "react-router-dom";
import React, { useState } from "react";
import './AddMoney.css';
import {
  Box,
  TextField,
  InputAdornment,
  Button,
  Snackbar,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormLabel,
} from "@mui/material";
import MuiAlert from "@mui/material/Alert";
import PersonIcon from "@mui/icons-material/Person";
import EmailIcon from "@mui/icons-material/Email";
import LockIcon from "@mui/icons-material/Lock";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import axios from "axios";

import PublicAppBar from "../landing/PublicAppBar";
import Footer from "./Footer";
export default function Register() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    emailId: "",
    password: "",
    role: "",
  });

  const [errors, setErrors] = useState({});
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
  });

  // Validation
  const validate = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = "Name is required.";
    if (!formData.emailId.trim()) newErrors.emailId = "Email is required.";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.emailId))
      newErrors.emailId = "Invalid email format.";
    if (!formData.password.trim()) newErrors.password = "Password is required.";
    else if (formData.password.length < 6)
      newErrors.password = "Password must be at least 6 characters.";
    if (!formData.role) newErrors.role = "Please select a role.";
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Input change
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  // Submit
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      emailId: formData.emailId,
      name: formData.name,
      password: formData.password,
      Role: formData.role,
    };

    try {
      const response = await axios.post(
        "http://localhost:8031/buyers/api/v1/register",
        payload,
        { headers: { "Content-Type": "application/json" } }
      );

      setSnackbar({
        open: true,
        message:
          response.data.message ||
          "Registration successful! You can now log in.",
        severity: "success",
      });

      setFormData({ name: "", emailId: "", password: "", role: "" });
     setTimeout(() => {
    navigate("/login");
  },1500);

    } catch (error) {
      setSnackbar({
        open: true,
        message:
          error.response?.data?.message ||
          "Registration failed. Please try again.",
        severity: "error",
      });
    }
  };

  const handleCloseSnackbar = (_, reason) => {
    if (reason === "clickaway") return;
    setSnackbar({ ...snackbar, open: false });
  };

  return (
    <div>
     {/*  Navbar */}
      <PublicAppBar />
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      {/* REGISTRATION BOX */}
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
          Create New Account
        </h3>

        <p style={{ color: "gray", marginBottom: "20px", fontSize: "0.9rem" }}>
  Already Registered?{" "}
  <Link
    to="/login"
    style={{
      color: "#0F3A6E",
      fontWeight: "600",
      textDecoration: "none",
    }}
  >
    Login
  </Link>
</p>
        
        <form onSubmit={handleSubmit}>
          {/* Name */}
          <TextField
            label="Full Name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            fullWidth
            error={!!errors.name}
            helperText={errors.name}
            margin="normal"
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <PersonIcon sx={{ color: "#0F3A6E" }} />
                </InputAdornment>
              ),
            }}
          />

          {/* Email */}
          <TextField
            label="Email ID"
            name="emailId"
            value={formData.emailId}
            onChange={handleChange}
            fullWidth
            error={!!errors.emailId}
            helperText={errors.emailId}
            margin="normal"
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <EmailIcon sx={{ color: "#0F3A6E" }} />
                </InputAdornment>
              ),
            }}
          />

          {/* Password */}
          <TextField
            label="Password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
            fullWidth
            error={!!errors.password}
            helperText={errors.password}
            margin="normal"
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <LockIcon sx={{ color: "#0F3A6E" }} />
                </InputAdornment>
              ),
            }}
          />

          {/* Role Selection */}
          
          <RadioGroup
            row
            name="role"
            value={formData.role}
            onChange={handleChange}
            sx={{ justifyContent: "center" }}
          >
            <FormControlLabel value="ADMIN" control={<Radio />} label="Admin" />
            <FormControlLabel value="BUYER" control={<Radio />} label="Buyer" />
          </RadioGroup>
          {errors.role && (
            <p style={{ color: "red", fontSize: "0.9rem" }}>{errors.role}</p>
          )}

          {/* Submit Button */}
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
            Register
          </Button>
        </form>
      </Box>

      {/* Snackbar */}
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
            backgroundColor:
              snackbar.severity === "success" ? "#0ebe01ff" : "#d32f2f",
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