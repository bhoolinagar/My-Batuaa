package com.myBatuaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myBatuaa.model.BankAccount;

@Repository
public interface BankAccountResposity extends JpaRepository<BankAccount,
String>{

	
}
