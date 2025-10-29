import React, { startTransition, useEffect, useState } from "react";
import axios from "axios";
import {
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  CircularProgress,
  Snackbar,
  Alert,
} from "@mui/material";
import CardMedia from '@mui/material/CardMedia';
import CardActionArea from '@mui/material/CardActionArea';
import CardActions from '@mui/material/CardActions';
import { Box, IconButton } from '@mui/material';
import copyLogo from '../images/copy_logo.png';
import { Add, Wallet } from '@mui/icons-material';
import AddMoney from './AddMoney.jsx';
import  walletLogo from '../images/wallet.png'
import transferLogo from '../images/transfer_logo.png'
import shoppinyLogo from '../images/shopying.png'
import "./WalletList.css";
import AddWallet from './AddWallet.jsx';
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import { esES } from "@mui/material/locale";
import { useNavigate } from "react-router-dom";
import { ClipboardListIcon, ClipboardWithIcon } from "flowbite-react";
import Navbar from "../Navbar.jsx";
import Footer from "./Footer.jsx";

const BASE_URL = "http://localhost:8031/wallet/api/v1";
const WalletDashboard = () => {
   let buyerEmail= sessionStorage.getItem("email") // replace with dynamic user email if needed
  let token=sessionStorage.getItem("token");
  console.log(" Session sotrage token: ",token)
   const [wallets, setWallets] = useState([]);
  const [primaryWallet, setPrimaryWallet] = useState(null);
  const [loading, setLoading] = useState(false);
  const [alert, setAlert] = useState({ open: false, message: "", severity: "success" });

  const navigate = useNavigate();
  
  const goToWallet = (walletId) => {
    navigate(`/addmoney/${walletId}`);
  };

  // 🔹 Helper: attach token globally to every axios request
  const axiosInstance = axios.create({
    baseURL: BASE_URL,
    headers: { Authorization: `Bearer ${token}` },
  });
  
  // Fetch all wallets
  const fetchWallets = async () => {
    setLoading(true);
    try {
      const res = await axiosInstance.get(`/wallet-list/${buyerEmail}`
        );
      setWallets(res.data.data || []);
    } catch (err) {
      console.error("Failed to fetch wallets:", err);
      setWallets([]);
      setAlert({ open: true, message: "Failed to fetch wallets", severity: "error" });
    } finally {
      setLoading(false);
    }
  };

  // Fetch primary wallet
  const fetchPrimaryWallet = async () => {
    try {
      const res = await axiosInstance.get(`/primary`, {
        params: { email: buyerEmail }
       
      });
      setPrimaryWallet(res.data.data);
    } catch (err) {
      console.error("Primary wallet not found", err);
      setPrimaryWallet(null);
    }
  };

  // Set a wallet as primary
  const setPrimaryWalletHandler = async (walletId) => {
    try {
      const res = await axiosInstance.put(
        `/set-primary`,
        null,
        { params: { walletId, email: buyerEmail } ,
           }
      );
      setAlert({ open: true, message: res.data.message, severity: "success" });
      // Refresh both wallet list and primary wallet
      fetchWallets();
      fetchPrimaryWallet();
    } catch (err) {
      console.error("Error setting primary wallet:", err);
      setAlert({ open: true, message: "Failed to set primary wallet", severity: "error" });
    }
  };

  useEffect(() => {
     if (!token) {
      navigate("/login"); // Redirect if token missing
      return;
    }
    fetchWallets();
    fetchPrimaryWallet();
  }, []);

  if (loading) return <CircularProgress />;

  return (
    <Box>
      <Navbar></Navbar>
    <div className="p-2">
      <Typography variant="h5" gutterBottom>
        Buyer Profile
      </Typography>
     
     <Box sx={{
    display: "flex",
    flexDirection: "column",
    alignItems: "flex-start", // aligns text to the left
    justifyContent: "flex-start",
    textAlign: "left", // ensures Typography text aligns left
    width: "fit-content", // optional: prevents box from taking full width
    ml: 2, // optional: small left margin for spacing
  }}>
      <Typography variant="h5" gutterBottom sx={{alignContent:'start'}}>
        Primary wallet details
      </Typography>
     </Box>
      {/* Current Primary Wallet */}
      {primaryWallet ? (
         <Card
        sx={{
          alignContent:'center',
          maxWidth: 345,
          color: "#0F3A6E",
          borderRadius: 4,
          boxShadow: 3,
          borderColor: "#9828a2ff",
          borderBlockColor: "#ba357cff",
        }}
      >
        <CardActionArea sx={{ backgroundColor: "#0F3A6E" }}>
          <CardContent>
            <Typography
              gutterBottom
              variant="h5"
              align="right"
              component="div"
              sx={{ color: "white" }}
            >
              Wallet
            </Typography>
            <Typography className="white-text">
              Email: {buyerEmail}
            </Typography>
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                gap: 1,
                flexDirection: "row",
              }}
            >
              <Typography
               className="white-text"
              >
                Wallet Id: {primaryWallet.walletId}
              </Typography>
               <ClipboardWithIcon valueToCopy={primaryWallet.walletId} />
            {/*    <IconButton
                aria-label="copy"
                sx={{ color: "#439b8bff", backgroundColor: "white" }}
              >
        
        <img
                  src={copyLogo}
                  alt="Copy"
                  style={{ width: 20, height: 20 }}
                />
             
              </IconButton> */}
            </Box>
            <Typography
               className="white-text"
              >
                Account Number : {primaryWallet.accountNumber}
              </Typography>
           <Typography
                className="white-text"
              >
                Bank Name : {primaryWallet.bankName}
              </Typography>
            <Typography className="white-text">
              Balance : ₹ {primaryWallet.balance} 
            </Typography>
          </CardContent>
        </CardActionArea>
        
      </Card>
      ) : (
        <Typography variant="subtitle1" sx={{ mb: 4 }}>
          No primary wallet selected
        </Typography>
      )}

<Typography gutterBottom
              variant="h3"
              align="center"
              component="div"
              fontSize={29}
              fontFamily={600}
              padding={4}
 sx={{ color: "black" }}>
          All wallets details 
            </Typography>
      {/* Wallet List */}
      <Grid container spacing={3}>
        {wallets.map((wallet) => (
          <Grid item xs={12} sm={6} md={4} key={wallet.walletId}>
             <Card
        sx={{
          maxWidth: 345,
          color: "#0F3A6E",
          borderRadius: 4,
          boxShadow: 3,
          borderColor: "#9828a2ff",
          borderBlockColor: "#ba357cff",
        }}
      >
        <CardActionArea sx={{ backgroundColor: "#0F3A6E" }}>
          <CardContent>
            <Typography
              gutterBottom
              variant="h5"
              align="right"
              component="div"
              sx={{ color: "white" }}
            >
              Wallet
            </Typography>
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                gap: 1,
                flexDirection: "row",
              }}
            >
              <Typography
               className="white-text"
              >
                Wallet Id: {wallet.walletId}
              </Typography>
             
            {/*    <IconButton
                aria-label="copy"
                sx={{ color: "#439b8bff", backgroundColor: "white" }}
              >
        
        <img
                  src={copyLogo}
                  alt="Copy"
                  style={{ width: 20, height: 20 }}
                />
             
              </IconButton> */}
            </Box>
            <Typography
                className="white-text"
              >
                Account Number: {wallet.accountNumber}
              </Typography>
           <Typography
                className="white-text"
              >
                Bank Name: {wallet.bankName}
              </Typography>
            <Typography 
            className="white-text">
              Balance: ₹ {wallet.balance}
            </Typography>
          </CardContent>
        </CardActionArea>
        <CardActions className="wallet-card-actions">
          <Button
            size="medium"
            color="primary" fontFamily=""
            className="add-wallet-btn"
                onClick={() => setPrimaryWalletHandler(wallet.walletId)}
          >
            Set as Primary
          </Button>
        </CardActions>
      </Card>
          </Grid>
        ))}
      </Grid>
 
      {/* Snackbar for alerts */}
      <Snackbar
        open={alert.open}
        autoHideDuration={3000}
        onClose={() => setAlert({ ...alert, open: false })}
      >
        <Alert
          onClose={() => setAlert({ ...alert, open: false })}
          severity={alert.severity}
          sx={{ width: "100%" ,backgroundColor:alert.severity === "success" ? "#4caf50" : "#d32f2f", 
            color:"#ffff"}}
        >
          {alert.message}
        </Alert>
      </Snackbar>

     
    </div>
    <Footer></Footer>
    </Box>
  );
};

export default WalletDashboard;
