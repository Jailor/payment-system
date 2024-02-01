package com.team1.paymentsystem;

import com.team1.paymentsystem.states.ApplicationConstants;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Log
public class PaymentSystemApplication extends SpringBootServletInitializer {
	@Autowired
	ApplicationConstants applicationConstants;
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(PaymentSystemApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(PaymentSystemApplication.class, args);
	}

	@PostConstruct
	public void init() {
		applicationConstants.loadConstants();
		log.info("Constants loaded!");
	}
}
