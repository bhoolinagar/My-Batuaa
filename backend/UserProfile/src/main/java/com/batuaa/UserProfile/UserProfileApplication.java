package com.batuaa.UserProfile;

import com.batuaa.userprofile.filter.BuyerFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProfileApplication.class, args);
    }

//    @Bean
//    public FilterRegistrationBean getFilter()
//    {
//        FilterRegistrationBean filterbean=new FilterRegistrationBean();
//        filterbean.setFilter(new BuyerFilter());
//        filterbean.addUrlPatterns("/wallet/api/v1/*");
//        return filterbean;
//    }

}
