import { useState } from 'react'

import './App.css'
import Dashboard from './dashboard/Dashbaord.jsx'
import { Wallet } from '@mui/icons-material'
import WalletList from './dashboard/WalletList.jsx'
import { Box } from '@mui/material'
import AddWallet from './dashboard/AddWallet.jsx'
import TransactionCard from './dashboard/TransactionCard.jsx'

function App() {
  const [count, setCount] = useState(0)

  return (
    <div>
      <Dashboard />
      <Box sx={{ height: 30 }}></Box>
      <WalletList />
      <TransactionCard />
    </div>
  )
}

export default App
