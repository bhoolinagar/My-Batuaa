package com.batuaa.transactionservice.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Entity
@Table(name = "transaction_records")// corrected name for proper mapping with db.
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "transactionId")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;  // Auto-generated PK

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_wallet_id", referencedColumnName = "wallet_id")
    //@JsonBackReference
    private Wallet fromWallet;      // FK to source Wallet

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_wallet_id", referencedColumnName = "wallet_id")
   // @JsonBackReference
    private Wallet toWallet;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_email_id", referencedColumnName = "email_id")
   // @JsonBackReference
    private Buyer fromBuyer;      // FK to source Wallet

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_email_id", referencedColumnName = "email_id")
   // @JsonBackReference
    private Buyer toBuyer;
    // FK to destination Wallet
    private BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp; // Full date & time
    @Enumerated(EnumType.STRING)
    private Status status; // SUCCESS, PROCESSING, FAILED

    private String remarks;

    public Transaction(int i, BigDecimal bigDecimal, String receivedRs500FromWalletWal4DA22CC6, String success, LocalDateTime localDateTime, Object o, Wallet wal0DC01EF4, Wallet wal2ED73EBA, String mail, String email, Type received) {

    }


    @Enumerated(EnumType.STRING)
    private Type type;


    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Wallet getFromWallet() {
        return fromWallet;
    }

    public void setFromWallet(Wallet fromWallet) {
        this.fromWallet = fromWallet;
    }

    public Wallet getToWallet() {
        return toWallet;
    }

    public void setToWallet(Wallet toWallet) {
        this.toWallet = toWallet;
    }

    public Buyer getFromBuyer() {
        return fromBuyer;
    }

    public void setFromBuyer(Buyer fromBuyer) {
        this.fromBuyer = fromBuyer;
    }

    public Buyer getToBuyer() {
        return toBuyer;
    }

    public void setToBuyer(Buyer toBuyer) {
        this.toBuyer = toBuyer;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public Transaction(){

    }

    public Transaction(Wallet fromWallet, Wallet toWallet, Buyer fromBuyer, Buyer toBuyer, BigDecimal amount, LocalDateTime timestamp, Status status, String remarks, Type type) {
        this.fromWallet = fromWallet;
        this.toWallet = toWallet;
        this.fromBuyer = fromBuyer;
        this.toBuyer = toBuyer;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
        this.remarks = remarks;
        this.type = type;
    }

    public Transaction(Integer transactionId, Wallet fromWallet, Wallet toWallet, Buyer fromBuyer, Buyer toBuyer, BigDecimal amount, LocalDateTime timestamp, Status status, String remarks, Type type) {

        this.transactionId = transactionId;
        this.fromWallet = fromWallet;
        this.toWallet = toWallet;
        this.fromBuyer = fromBuyer;
        this.toBuyer = toBuyer;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
        this.remarks = remarks;

        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;

    }


    //new Transaction(walletFrom, walletTo, walletFrom.getBuyer(), walletTo.getBuyer(),
    //                    transferDto.getAmount(), LocalDateTime.now(), Status.PROCESSING,
    //                    "Initiating transfer of Rs " + transferDto.getAmount() + " to wallet " +
    //                    transferDto.getToWalletId(),
    //                    Type.WITHDRAWN);
}