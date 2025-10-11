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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
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


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer userId;

@Email
@Column(nullable = false, unique = true)
private String emailId;

@NotBlank
private String  name;

@Enumerated(EnumType.STRING)
private Gender gender;

@Column(nullable = false, unique = true)
@NotBlank(message = "Mobile number is required")
@Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
private String mobileNumber;

private String address;

// Store hashed password only, not plain text
@Size(min = 8, max = 12, message = "Password must be between 8 and 12 characters")
@Column(nullable = false)
private String password;

@Enumerated(EnumType.STRING)
private Role role;
	
	
	
}
