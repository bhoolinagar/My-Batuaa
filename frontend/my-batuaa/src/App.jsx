import { useState } from 'react'
import './App.css'
import Dashboard from './component/Dashbaord.jsx'
import { BrowserRouter, Route, Routes , Navigate} from 'react-router-dom'
import RegisterPage from './component/Register.jsx'
import Login from './component/Login.jsx'
import TransferMoney from './transferMoney/TransferMoney.jsx'
import WalletList from './component/WalletList.jsx'
import AddMoney from './component/AddMoney.jsx'
import AddWallet from './component/AddWallet.jsx'
import Navbar from './Navbar.jsx'
import Welcomepage from './landing/WelcomePage.jsx'
import { AdminDashboard } from './adminDashboard/AdminDashboard.jsx'
import { AdminLayout } from './adminDashboard/AdminLayout.jsx'
import { Reports } from './adminDashboard/Reports.jsx'
import Transactions from './component/TransactionHistory.jsx'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<Welcomepage />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/walletlist" element={<WalletList />} />
         <Route path="/transactionfilter/:walletId" element={<Transactions />} />
        <Route path="/addmoney/:walletId" element={<AddMoney />} />
        <Route path="/addwallet" element={<AddWallet />} />
        <Route path="/transferMoney" element={<TransferMoney />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<Login />} />

        {/* Admin routes (nested under /admin) */}
        <Route path="/admin" element={<AdminLayout />}>
       {/*  <Route index element={<AdminDashboard />} /> */}
           <Route index element={<Navigate to="admindashboard" replace />} />
          <Route path="admindashboard" element={<AdminDashboard />} />
          <Route path="reports" element={<Reports />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
