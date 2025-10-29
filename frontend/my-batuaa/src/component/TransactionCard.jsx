import { useEffect, useState } from "react";
import axios from "axios";
import BasicCardFourCorners from "./BasicCardFourCorners";
import { Box, IconButton, Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
export default function TransactionCard({ primaryWallet }) {
  const token = sessionStorage.getItem("token");
  const emailId = sessionStorage.getItem("email");
  const [transactions, setTransactions] = useState([]);
const navigate = useNavigate();

//navigate to primary wallet
const goToTranscationFilter = (primarywallet) => {
   if (!primarywallet) return; 
  navigate(`/transactionfilter/${primarywallet}`);
  };


  useEffect(() => {
    const fetchTransactions = async () => {
      if (!primaryWallet) return;
      const walletId = primaryWallet?.walletId || primaryWallet;
      console.log("Transactions primary:", walletId);

      try {
        const res = await axios.get("http://localhost:8086/transaction/api/v2/all-transactions", {
          params: { emailId, walletId },
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
        setTransactions(res.data);
        console.log("Transaction data loaded:", res.data);
      } catch (err) {
        console.error("Error fetching transactions:", err);
      }
    };

    fetchTransactions();
  }, [primaryWallet, emailId, token]);

  return (
    <div>
    
      <Typography variant="h5" sx={{ mb: 2 }} align="left">
        Recent Transactions History
      </Typography>

      <Box sx={{ backgroundColor: "#f5f5f5", borderRadius: 2, p: 4, width: "100%" }}>
        <Box sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}>
         
         <IconButton onClick={()=>goToTranscationFilter(primaryWallet?.walletId|| primaryWallet)}>
View all
         </IconButton>
        </Box>

        <Stack spacing={3} sx={{ mt: 4 }}>
          {transactions.map((transaction, index) => (
            <BasicCardFourCorners key={index} transaction={transaction} />
          ))}
        </Stack>
      </Box>
    </div>
  );
}
