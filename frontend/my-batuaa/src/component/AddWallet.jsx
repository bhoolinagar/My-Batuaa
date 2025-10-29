import React, { useState } from "react";
import {
  Box,
  TextField,
  InputAdornment,
  Button, Snackbar,
  Typography,
} from "@mui/material";
import AccountBalanceIcon from "@mui/icons-material/AccountBalance";
import CurrencyRupeeIcon from "@mui/icons-material/CurrencyRupee";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import './AddMoney.css';
import personLogo from "../assets/person.png"; 
import MuiAlert from "@mui/material/Alert";
import { useParams } from "react-router-dom";
import axios from "axios";
import Navbar from "../Navbar";
import Footer from "./Footer";

export default function AddWallet() {
  let emailId = sessionStorage.getItem("email")
  //console.log("Buyer email:",emailId)
  // useParams();
  let token =sessionStorage.getItem("token")

const [formData, setFormData] = useState({
    accountNumber: "",
    bankName: "",
    balance: "",
  });

  const [errors, setErrors] = useState({});
  const
   [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success", // success | error
  });


  // Validation logic
  const validate = () => {
    const newErrors = {};

    if (!formData.accountNumber.trim()) {
      newErrors.accountNumber = "Account number is required.";
    } else if (!/^\d{11}$/.test(formData.accountNumber)) {
      newErrors.accountNumber = "Enter a valid account number (11 digits).";
    }

    if (!formData.bankName.trim()) {
      newErrors.bankName = "Bank name is required.";
    } else if (!/^[A-Za-z\s]+$/.test(formData.bankName)) {
      newErrors.bankName = "Bank name must contain only letters.";
    }

    if (!formData.balance.trim()) {
      newErrors.balance = "Amount is required.";
    } else if (isNaN(formData.balance) || parseFloat(formData.balance) <= 0) {
      newErrors.balance = "Enter a valid positive amount.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async(event) => {

    event.preventDefault();
    if (!validate()) return; // stop if invalid
    console.log("Form submitted:", formData);

    const payload = {
    emailId, // use constant email
    balance: parseFloat(formData.balance),
    bankName: formData.bankName,
    accountNumber: formData.accountNumber,
  };
    try {
      const response = await axios.post(
        `http://localhost:8031/wallet/api/v1/link-bank-account`,
        payload, 
         { 
          headers: { "Content-Type": "application/json" , 
            Authorization: `Bearer ${token}`,} }
      );

      console.log("Message :"+ response.data.message)
      setSnackbar({
        open: true,
        message: response.data.data.message || "New wallet generated successfully!",
        severity: "success",
      });
     setFormData({ accountNumber: "", bankName: "", balance: "" });
    } catch (error) {
     console.error("Error to generate wallet:", error.response?.data.data || error.message);
      setSnackbar({
        open: true,
        message:
          error.response?.data?.message ||
          "Failed to generate wallet. Please try again.",
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
      <Navbar></Navbar>  
    <form onSubmit={handleSubmit} className="add-money-form">
      <Box className="form-title">Add New Wallet</Box>

      <div className="input-container">
        <TextField
          required
          label="Bank Account Number"
          name="accountNumber"
          error={!!errors.accountNumber}
           helperText={errors.accountNumber}
          placeholder="Enter your bank account number"
          value={formData.accountNumber}
          onChange={handleChange}
          variant="outlined"
          fullWidth
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <AccountBalanceIcon sx={{ color: "#0F3A6E", fontSize: 30 }} />
              </InputAdornment>
            ),
          }}
        />
      </div>

      <div className="input-container">
        <TextField
          required
          label="Bank Name"
          name="bankName"
         error={!!errors.bankName}
          helperText={errors.bankName}
            placeholder="Enter your bank name"
          value={formData.bankName}
          onChange={handleChange}
          variant="outlined"
          fullWidth
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <AccountBalanceIcon
                  sx={{ color: "#0F3A6E", fontSize: 30 }}
                />
              </InputAdornment>
            ),
          }}
        />
      </div>

      <div className="input-container">
        <TextField
          required
          label="Amount"
          name="balance"
            placeholder="100.00"
          error={!!errors.balance}
           helperText={errors.balance}
          value={formData.balance}
          onChange={handleChange}
          variant="outlined"
          fullWidth
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <CurrencyRupeeIcon sx={{ color: "#0F3A6E", fontSize: 30 }} />
              </InputAdornment>
            ),
          }}
        />
      </div>

      <Button type="submit" className="submit-button contained"
      
      endIcon={<ArrowForwardIcon />}
      >
        Submit
      </Button>

       {/* Snackbar Notification */}
            <Snackbar
              open={snackbar.open}
              autoHideDuration={5000} // 5 seconds
              onClose={handleCloseSnackbar}
              anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
            >
              <MuiAlert
                elevation={6}
                variant="filled"
                onClose={handleCloseSnackbar}
                severity={snackbar.severity}
                sx={{ width: "100%" }}
              >
                {snackbar.message}
              </MuiAlert>
            </Snackbar>
    </form>
    <Footer></Footer>
    </div>
  );
}