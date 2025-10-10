package com.myBatuaa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="wallet_records")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
	
	@Id
	private Integer userId;
	  @OneToOne
	    @MapsId
	    @JoinColumn(name = "user_id")
	    private User user;
	    private LocalDate createdAt;
	    private BigDecimal balance;

}
