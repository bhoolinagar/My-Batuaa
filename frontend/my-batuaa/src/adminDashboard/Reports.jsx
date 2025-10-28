import axios from "axios";
import { Box, Typography, Button, Paper } from "@mui/material";
import DownloadIcon from "@mui/icons-material/Download";
import * as XLSX from "xlsx"; 
import Footer from "../component/Footer";

export function Reports() {
  const email = sessionStorage.getItem("email");
  const role = sessionStorage.getItem("role");
  const token = sessionStorage.getItem("token");

  const handleDownload = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8086/transaction/api/v2/admin-transactions",
        {
          params: { email, role },
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );
 
      // Extract transactions array safely
      const transactions = Array.isArray(response.data)
        ? response.data
        : response.data.data || [];

        
console.log(response.data);
      if (!transactions.length) {
        alert("No data available to download.");
        return;
      }

      // Convert JSON to worksheet
      const worksheet = XLSX.utils.json_to_sheet(transactions);

      // Create a workbook and append the worksheet
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, "Transactions");

      // Generate Excel file and trigger download
      XLSX.writeFile(workbook, "transactions_report.xlsx");
    } catch (error) {
      console.error("Error downloading report:", error);
      alert("Failed to download report. Check console for details.");
    }
  };

  return (
    <div>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom sx={{ fontWeight: "bold" }}>
          Reports
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          Download complete transaction details as an Excel file for analysis or audit.
        </Typography>

        <Box sx={{ mt: 3 }}>
          <Button
            className="submit-button contained"
            variant="contained"
            color="primary"
            startIcon={<DownloadIcon />}
            onClick={handleDownload}
          >
            Download 
          </Button>
        </Box>
      </Paper>
      <Footer />
    </div>
  );
}
