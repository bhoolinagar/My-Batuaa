package com.mybatuaa.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mybatuaa.model.Role;
import com.mybatuaa.model.User;
import com.mybatuaa.service.UserService;

import jakarta.validation.Valid;

@RestController
//@RequiredArgsConstructor
@RequestMapping("users/api/v1")
public class UserController {

	/*
	 * Register Account(User user) Login(String email, String password)+
	 * CreateWallet() Logout getAccountByUserId(String userId)
	 */
	@Autowired
	private UserService userService;

	@PostMapping("/register")
//Register Account(User user)
//CreateWallet()
	public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {

		return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
	}

	// Login(String email, String password)
	@GetMapping("/login")
	public ResponseEntity<?> loginUser(@RequestParam String emailId, @RequestParam String password,
			@RequestParam Role role) {
		userService.login(emailId, password, role);// 🔁 Convert string to enum
		return new ResponseEntity<>(userService.login(emailId, password, role), HttpStatus.OK);
	}

}
