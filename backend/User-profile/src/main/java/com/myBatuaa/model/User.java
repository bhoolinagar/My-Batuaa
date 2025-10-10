package com.myBatuaa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="user-records")

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
/*id <PK> : Integer
String email
name: Name
gender: enum
mobileNumber : int
address: String
anual_Income: String
password: String(min 8 -12 digit)
ADMIN/BUYER : enum(Role)
 
 */

@OneToOne
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer userId;

@Email
@Column(nullable = false, unique = true)
private String emailId;

private Name name;

@Enumerated(EnumType.STRING)
private Gender gender;

@Column(nullable = false, unique = true)
private String mobileNumber;

private String address;

// ⚠️ Store hashed password only, not plain text
@Column(nullable = false)
private String password;

@Enumerated(EnumType.STRING)
private Role role;
	
	
	
}
