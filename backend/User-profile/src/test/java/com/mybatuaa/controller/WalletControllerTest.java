package com.mybatuaa.controller;

import com.mybatuaa.exception.BankAccountAlreadyLinkedException;
import com.mybatuaa.exception.WalletNotFoundException;
import com.mybatuaa.model.BankAccount;
import com.mybatuaa.model.Wallet;
import com.mybatuaa.service.BankService;
import com.mybatuaa.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest
class WalletControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private WalletService walletService;

	// @MockBean
	@Mock
	private BankService bankService;

	private BankAccount bankAccount;

	@InjectMocks
	private WalletController walletController;
	private Wallet wallet;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

		bankAccount = new BankAccount();
		bankAccount.setAccountNumber("1234567890");
		bankAccount.setBankName("SBI");
		bankAccount.setBalance(new BigDecimal(2000));
		wallet = new Wallet();
		wallet.setWalletId("wallet123");
		wallet.setBalance(new BigDecimal(900));
	}

	// --- GET WALLET BALANCE ----
	@Test
	void testGetWalletBalance_Success() throws Exception {

		when(walletService.getWalletBalance("wallet123")).thenReturn(wallet.getBalance());

		mockMvc.perform(get("/wallet/api/v1/get-wallet-balance/wallet123").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().string("900"));

		verify(walletService, times(1)).getWalletBalance("wallet123");
	}

	@Test
	void testGetWalletBalance_WalletNotFound() throws Exception {
		when(walletService.getWalletBalance("invalidWallet"))
				.thenThrow(new WalletNotFoundException("Wallet not found with ID: invalidWallet"));

		mockMvc.perform(get("/wallet/api/v1/get-wallet-balance/invalidWallet").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Wallet not found with ID: invalidWallet"));

		verify(walletService, times(1)).getWalletBalance("invalidWallet");

	}

	// -- LINK BANK ACCOUNT ---
	@Test
	void testLinkBankAccount_Success() throws Exception {

		when(bankService.linkBankAccountToWallet(eq("wallet123"), any(BankAccount.class))).thenReturn(bankAccount);

		mockMvc.perform(post("/wallet/api/v1/link-bank-account").param("walletId", "wallet123")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountNumber\":\"1234567890\",\"bankName\":\"SBI\"}")).andExpect(status().isCreated())
				.andExpect(jsonPath("$.accountNumber").value("1234567890"))
				.andExpect(jsonPath("$.bankName").value("SBI"));

		verify(bankService, times(1)).linkBankAccountToWallet(eq("wallet123"), any(BankAccount.class));

	}

	@Test
	void testLinkBankAccount_AlreadyLinked() throws Exception {

		when(bankService.linkBankAccountToWallet(eq("wallet123"), any(BankAccount.class))).thenThrow(
				new BankAccountAlreadyLinkedException("Bank account 1234567890 already linked to this wallet"));

		mockMvc.perform(post("/wallet/api/v1/link-bank-account").param("walletId", "wallet123")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountNumber\":\"1234567890\",\"bankName\":\"SBI\"}")).andExpect(status().isConflict())
				.andExpect(content().string("Bank account 1234567890 already linked to this wallet"));

		verify(bankService, times(1)).linkBankAccountToWallet(eq("wallet123"), any(BankAccount.class));
	}

}
