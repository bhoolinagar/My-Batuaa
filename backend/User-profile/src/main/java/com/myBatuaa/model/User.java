package com.myBatuaa.model;

import java.time.LocalDate;

import jakarta.persistence.Id;

public class User {

	/*
	 * id <PK> : Integer
name: Name
gender: enum
nationality: String
countryCode : String
mobileNumber : int
address: Address
occupation : String
education : Education
anual_Income: String
document: Document
submissionDate : Date Timestamp
ADMIN/BUYER : enum(Role)*
	 */
	@Id
	private Integer userid;
	private Name name;
	private Gender gender;
	private String nationality;
	private String countryCode;
	private Integer mobileNumber;
	private Address address;
	private String occupation;
	private String annualIncome;
	private LocalDate submmissionDate;
	private Role role;
	
	
	
}
