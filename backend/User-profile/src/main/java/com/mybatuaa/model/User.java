package com.mybatuaa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity

public class User {
	/*
	 * id <PK> : Integer String email name: Name gender: enum mobileNumber : int
	 * address: String anual_Income: String password: String(min 8 -12 digit)
	 * ADMIN/BUYER : enum(Role)
	 * 
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@Email
	@Column(nullable = false, unique = true)
	private String emailId;

	@NotBlank
	private String name;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
	private String mobileNumber;

	private String address;

	// Store hashed password only, not plain text
	@Size(min = 8, max = 12, message = "Password must be between 8 and 12 characters")
	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User(Integer userId, String emailId, String name, Gender gender, String mobileNumber, String address, String password, Role role) {
        this.userId = userId;
        this.emailId = emailId;
        this.name = name;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.password = password;
        this.role = role;
    }
    public User(){

    }
}
