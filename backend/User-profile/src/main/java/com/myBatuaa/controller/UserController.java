package com.myBatuaa.controller;

import com.myBatuaa.model.Role;
import com.myBatuaa.repository.UserRepository;
import com.myBatuaa.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myBatuaa.model.User;

@RestController
//@RequiredArgsConstructor
@RequestMapping("users/api/v1")
public class UserController {

    /*
 Register Account(User user)
Login(String email, String password)+
CreateWallet()
Logout
getAccountByUserId(String userId)
	 */
    @Autowired
    private UserService userService;


    @PostMapping("/register")
//Register Account(User user)
//CreateWallet()
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
         // userService.registerUser(user);
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
    }


    //Login(String email, String password)
    @GetMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String emailId, @RequestParam String password, @RequestParam Role role) {
        userService.login(emailId, password, role);//🔁 Convert string to enum
        return new ResponseEntity<>(userService.login(emailId, password, role), HttpStatus.OK);
    }



    //Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return null;
    }

    @GetMapping("/user-by-userId/{userId}")
    //getAccountByUserId(String userId)
    public ResponseEntity<?> getuserByuserId(Integer userId) {
        return null;
    }
}

