package com.mybatuaa.controller;

import com.mybatuaa.exception.UnauthorizedAccessException;
import com.mybatuaa.model.Role;
import com.mybatuaa.model.User;
import com.mybatuaa.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
//Register a new user and create wallet automatically
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        // call service once
        User savedUser = userService.registerUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


/*Login user with email, password, and role
Using GET for local testing; in production, should use POST with @RequestBody for security.*/

    @GetMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String emailId, @RequestParam String password, @RequestParam Role role) {

        // Call the service once and get the User
        try {
            User user = userService.login(emailId, password, role);
            return ResponseEntity.ok(user);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(STR."Login failed: \{e.getMessage()}");
        }
    }

}
