package nl.inholland.bankingapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class BankingapplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingapplicationApplication.class, args);
	}

	@Bean
	public Random randomizer() {
		return new Random();
	}
}
