package com.myBatuaa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Id;

public class Transaction {
	@Id
private Integer transactionId;
	private Integer accountNumber;
	private Integer fromWallet;
	private Integer toWallet;
	private  BigDecimal amount;
	private LocalDate timeStamp;
	private Status status; // success, processing, failed
	private String remarks;// please clarify with client what exact meaning of remark
	
}
