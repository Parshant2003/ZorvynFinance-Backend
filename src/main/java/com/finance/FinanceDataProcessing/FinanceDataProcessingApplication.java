package com.finance.FinanceDataProcessing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Zorvyn Finance Dashboard Backend",
				version = "1.0.0",
				description = "Backend API for a finance dashboard system with role-based access control",
				contact = @Contact(
						name = "Parshant",
						email = "parshantbisht656@gmail.com",
						url = "https://github.com/Parshant2003"
				)
		)
)
@SecurityScheme(
		name = "Bearer Authentication",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT",
		description = "JWT token for authentication"
)
public class FinanceDataProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceDataProcessingApplication.class, args);
		System.out.println("FinanceDataProcessingApplication Started");
	}

}
