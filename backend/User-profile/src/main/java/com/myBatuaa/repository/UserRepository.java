package com.myBatuaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myBatuaa.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
