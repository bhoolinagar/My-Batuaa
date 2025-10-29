package com.batuaa.transactionservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name="buyer_records")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "emailId")
public class Buyer {
    /*
     * id <PK> : Integer String email name: Name gender: enum mobileNumber : int
     * address: String anual_Income: String password: String(min 8 -12 digit)
     * ADMIN/BUYER : enum(Role)
     *
     */

    @Id
    @Email
    @Column(name = "email_id", nullable = false, unique = true)
    private String emailId;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // Store hashed password only, not plain text
    @Size(min = 8, max = 12, message = "Password must be between 8 and 12 characters")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    //  @JsonBackReference  // to avoid bi-cyclic process during db calling
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Wallet> walletList;

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

    public List<Wallet> getWalletList() {
        return walletList;
    }

    public void setWalletList(List<Wallet> walletList) {
        this.walletList = walletList;
    }
}