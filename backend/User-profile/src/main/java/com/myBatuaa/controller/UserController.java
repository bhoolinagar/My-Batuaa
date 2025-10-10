package com.myBatuaa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myBatuaa.model.User;

@RestController
@RequestMapping("users/api/v1")
public class UserController {

	/*
 Register Account(User user)
Login(String email, String password)+
CreateWallet()
Logout
getAccountByUserId(String userId)
	 */

	
@PostMapping("/register")	
//Register Account(User user)
//CreateWallet()
	public ResponseEntity<?> addUser(User user){
	return null;
}

//Login(String email, String password)	
@GetMapping("/login")
public ResponseEntity<?> loginUser(String emailId, String password){
	return null;
}
	


	//Logout
@PostMapping("/logout")
public ResponseEntity<?> logoutUser(){
	return null;
}
	
@GetMapping("/user-by-userId/{userId}")
	//getAccountByUserId(String userId)
public ResponseEntity<?> getuserByuserId(Integer userId){
	return null;
}
	
}
