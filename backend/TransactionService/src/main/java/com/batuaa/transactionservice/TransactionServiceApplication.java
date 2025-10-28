package com.batuaa.transactionservice;

import com.batuaa.transactionservice.filter.TransactionFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TransactionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionServiceApplication.class, args);
	}

//    @Bean
//    public FilterRegistrationBean getFilter()
//    {
//
//        FilterRegistrationBean filterbean=new FilterRegistrationBean();
//        filterbean.setFilter(new TransactionFilter());
//        filterbean.addUrlPatterns("/transaction/api/v2/*");
//        return filterbean;
//
//    }
}
