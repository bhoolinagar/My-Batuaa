import React, { useState, useEffect } from "react";
import {
  Box,
  TextField,
  InputAdornment,
  Button,
  CircularProgress,
} from "@mui/material";
import CurrencyRupeeIcon from "@mui/icons-material/CurrencyRupee";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import axios from "axios";
import { useLocation, useNavigate } from "react-router-dom";
import "../component/AddMoney.css";
import Footer from "../component/Footer";
import Navbar from "../Navbar";
import { Snackbar, Alert as MuiAlert } from "@mui/material";


export default function TransferMoney() {
  const location = useLocation();
  const navigate = useNavigate();

  const [snackbar, setSnackbar] = useState({
  open: false,
  message: "",
  severity: "success",
});
const handleCloseSnackbar = (_, reason) => {
  if (reason === "clickaway") return;
  setSnackbar({ ...snackbar, open: false });
};

  // Get email and primary wallet from state|session
  const { email, primaryWalletId } = location.state || {};
  const fromBuyerEmail = email || sessionStorage.getItem("email");
  const token = sessionStorage.getItem("token");
  console.log(" token " + token + "  walletId " + primaryWalletId);

  const [loading, setLoading] = useState(true);
  const [wallets, setWallets] = useState([]);
  const [message, setMessage] = useState("");

  const [formData, setFormData] = useState({
    fromWalletId: primaryWalletId || "",
    toWalletId: "",
    toBuyerEmailId: "",
    amount: "",
    remarks: "",
  });

  // Fetching wallets on mount
  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    if (!fromBuyerEmail) {
      setMessage("User email not found. Please login again.");
      setLoading(false);
      return;
    }

    axios
      .get(
        `http://localhost:8031/wallet/api/v1/wallet-list/${encodeURIComponent(fromBuyerEmail)}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      )
      .then((res) => {
        const apiResponse = res.data;
        console.log("Wallet list response:", apiResponse);

        if (apiResponse.status === "success" && apiResponse.data?.length > 0) {
          setWallets(apiResponse.data);

          // Setting default fromWalletId.
          if (!formData.fromWalletId) {
            setFormData((prev) => ({
              ...prev,
              fromWalletId: apiResponse.data[0].walletId,
            }));
          }
        } else {
          setMessage("No wallets found for this user.");
        }
      })
      .catch((err) => {
        console.error("Failed to fetch wallets:", err);
        setMessage(err.response?.data?.message || "Error fetching wallets.");
      })
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (
      !formData.fromWalletId ||
      !formData.toWalletId ||
      !formData.toBuyerEmailId ||
      !formData.amount
    ) {
      setSnackbar({
      open: true,
      message: "Please fill all required fields.",
      severity: "error",
    });
    return;
    }

    setLoading(true);
    try {
      const payload = {
        fromWalletId: formData.fromWalletId,
        toWalletId: formData.toWalletId,
        toBuyerEmailId: formData.toBuyerEmailId,
        amount: parseFloat(formData.amount),
        remarks: formData.remarks,
        fromBuyerEmailId: fromBuyerEmail,
      };

      console.log("Submitting transfer payload:", payload);

      const res = await axios.post(`http://localhost:8086/transaction/api/v2/transfer-wallet`,payload,
        {
          headers: {"Content-Type": "application/json",Authorization: `Bearer ${token}`,},
        }
      );

      setSnackbar({
      open: true,
      message: res.data.message || "Transaction successful!",
      severity: "success",
    });

      setFormData({
      ...formData,
      toWalletId: "",
      toBuyerEmailId: "",
      amount: "",
      remarks: "",
    });
    setTimeout(() => {
      navigate("/dashboard", { state: { email: fromBuyerEmail } });
    }, 2000);
    }  
    catch (err) {
      console.error("Transaction error:", err);
      console.log("Full backend error response:", err.response?.data);

      const backendMessage =
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data?.details ||
        err.message ||
        "Transaction failed. Please try again.";

      if (backendMessage?.toLowerCase().includes("receiver")) {
        setMessage(
          "Receiver email and wallet ID do not match. Please verify the details."
        );
      } else {
       setSnackbar({
      open: true,
      message: backendMessage,
      severity: "error",
    });
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading)
    return <CircularProgress sx={{ mt: 10, color: "#0F3A6E" }} />;

  return (
    <div>
      <Navbar />
      <form onSubmit={handleSubmit} className="add-money-form">
        <Box className="form-title">Transfer Money</Box>

        <div className="input-container">
          <TextField
            label="From Wallet ID"
            name="fromWalletId"
            value={formData.fromWalletId}
            fullWidth
            InputProps={{ readOnly: true }}
          />
        </div>

        <div className="input-container">
          <TextField
            required
            label="Receiver Wallet ID"
            name="toWalletId"
            value={formData.toWalletId}
            onChange={handleChange}
            fullWidth
          />
        </div>

        <div className="input-container">
          <TextField
            required
            label="Receiver Email"
            name="toBuyerEmailId"
            value={formData.toBuyerEmailId}
            onChange={handleChange}
            placeholder="receiver@example.com"
            type="email"
            fullWidth
          />
        </div>

        <div className="input-container">
          <TextField
            required
            label="Amount"
            name="amount"
            value={formData.amount}
            onChange={handleChange}
            placeholder="100.00"
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

        <div className="input-container">
          <TextField
            label="Remarks"
            name="remarks"
            value={formData.remarks}
            onChange={handleChange}
            placeholder="Optional Remarks"
            fullWidth
          />
        </div>

        <Button
          type="submit"
          variant="contained"
          endIcon={<ArrowForwardIcon />}
          sx={{
            width: "50%",
            height: 50,
            mt: 2,
            py: 1.5,
            px: 4,
            borderRadius: "40px",
            backgroundColor: "#0F3A6E",
            textTransform: "none",
            fontSize: 20,
            fontWeight: 600,
            fontFamily: "Roboto Mono, monospace",
            "&:hover": { backgroundColor: "#0d2e59" },
          }}
        >
          Submit
        </Button>

        {message && (
          <Box
            sx={{
              mt: 2,
              color: message.includes("successful") ? "green" : "red",
            }}
          >
            {message}
          </Box>
        )}
      </form>

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
            snackbar.severity === "success" ? "#4caf50" : "#d32f2f",
          color: "white",
          fontWeight: 600,
        }}
      >
        {snackbar.message}
      </MuiAlert>
    </Snackbar>
      <Footer />
    </div>
  );
}