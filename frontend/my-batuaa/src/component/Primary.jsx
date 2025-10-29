import React from "react";
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import CardActionArea from '@mui/material/CardActionArea';
import CardActions from '@mui/material/CardActions';
import { Box, IconButton } from '@mui/material';
import copyLogo from '../images/copy_logo.png';
import { Add, Padding, Wallet } from '@mui/icons-material';
import AddMoney from './AddMoney.jsx';
import  walletLogo from '../images/wallet.png'
import transferLogo from '../images/transfer_logo.png'
import shoppinyLogo from '../images/shopying.png'
import "./WalletList.css";
import AddWallet from './AddWallet.jsx';
import { use, useEffect, useState } from 'react';
import axios from 'axios';
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import { esES } from "@mui/material/locale";
import { useNavigate } from "react-router-dom";

import { ClipboardWithIcon } from "flowbite-react";

const BASE_URL = "http://localhost:8031/wallet/api/v1";

export default function PrimaryWallet(props) {
  const [primaryWallet, setPrimaryWallet] = useState(null);
  const [walletId, setWalletId] = useState("");

  const buyerEmail = sessionStorage.getItem("email");
  const token = sessionStorage.getItem("token");

  const navigate = useNavigate();

  // Create axios instance once
  const axiosInstance = axios.create({
    baseURL: BASE_URL,
  });

  // Attach token automatically
  axiosInstance.interceptors.request.use(
    (config) => {
      const token = sessionStorage.getItem("token");
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  const goToAddWallet = () => {
  navigate(`/addwallet`);
};
  const goToWallet = (walletId) => {
    navigate(`/addmoney/${walletId}`);
  };

  const goToTransferMoney = (walletId) => {
    if (!walletId || !buyerEmail) {
      console.error("Wallet ID or email missing!");
       alert("Cannot transfer money: wallet or email missing.");
      return;
    }

    navigate("/transferMoney", {
      state: {
        email: buyerEmail,
        primaryWalletId: walletId,
      },
    });
  };

  useEffect(() => {
    if (!token) {
      navigate("/login"); // redirect if no token
      return;
    }

    const fetchPrimaryWallet = async () => {
      try {
        const res = await axiosInstance.get(`/primary`, {
          params: { email: buyerEmail },
        });

        if (res.data?.data) {
          setPrimaryWallet(res.data.data);
          setWalletId(res.data.data.walletId);
          console.log("Primary wallet:", res.data.data);
       props.setPrimaryWallet(res.data.data.walletId)
        }
      } catch (err) {
        console.error(" Failed to fetch primary wallet:", err);
        setPrimaryWallet(null);
      }
    };
    fetchPrimaryWallet();
  }, [buyerEmail, token, navigate]); // dependencies

  // Get the current visible wallets
//const visibleWallets = wallets.slice(start, start + limit);
  
return (
<div >
    <Box className="wallet-list-container">
{primaryWallet?( <Card
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
            <Typography  className="white-text">
              Email : {buyerEmail}
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
                Wallet Id : {primaryWallet?.walletId|| "N/A"}
              </Typography>

              <ClipboardWithIcon valueToCopy={primaryWallet?.walletId||"N/A"} />
            </Box>
            <Typography 
             className="white-text">
              Balance :  ₹ {primaryWallet?.balance||0}
            </Typography>
          </CardContent>
        </CardActionArea>
        <CardActions className="wallet-card-actions">
          <Button
            size="medium"
            color="primary"
            className="add-wallet-btn"

            onClick={() => {
             goToWallet(primaryWallet?.walletId||null)
            }}
          >
            Add Money
          </Button>
        </CardActions>
      </Card>):(
        <Card className="wallet-card"sx={{borderRadius:4 ,width:270, height:190}}>
          <CardContent sx={{ margin:'20%'}}>
            <Typography variant="h5">No Wallet found</Typography>
          </CardContent>
        </Card>

)}

   <Card className="wallet-card" sx={{borderRadius:4 ,width:270, }}>
      <CardActionArea>
        <img src={walletLogo} alt="Wallet" className="wallet-image" />
      </CardActionArea>

      <CardActions className="wallet-card-actions">
        <Button size="medium" color="primary" className="add-wallet-btn"

            onClick={()=>goToAddWallet()}
        >
          Add new Wallet
        </Button>
      </CardActions>
    </Card>  


    {/* Transfer Money Card */}

     <Card className="wallet-card" sx={{borderRadius:4 ,width:270}}>
      <CardActionArea>
        <img src={transferLogo} alt="Wallet" className="wallet-image" />
      </CardActionArea>

      <CardActions className="wallet-card-actions">
      
  <Button size="medium" color="primary" className="add-wallet-btn"
        onClick={() =>
          goToTransferMoney(primaryWallet?.walletId||null)}
          >
          Transfer Money
        </Button>
      </CardActions>
    </Card>  

     <Card className="wallet-card" sx={{borderRadius:4 ,width:270}}>
      <CardActionArea>
        <img src={shoppinyLogo} alt="Wallet" className="wallet-image" />
      </CardActionArea>
      <CardActions className="wallet-card-actions">
        <Button size="medium" color="primary" className="add-wallet-btn"  >
            Shopping
        </Button>
      </CardActions>
    </Card>  
    
  </Box>
  </div>
  );

}
