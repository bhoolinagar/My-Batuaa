package com.mybatuaa.service;

import com.mybatuaa.model.Role;
import com.mybatuaa.model.User;

public interface UserService {
    User registerUser(User user);

    User login(String emailId, String password, Role role);
}