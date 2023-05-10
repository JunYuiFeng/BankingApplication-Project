package nl.inholland.bankingapplication.configuration;

import jakarta.transaction.Transactional;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.services.BankAccountService;
import nl.inholland.bankingapplication.services.UserAccountService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Transactional
public class ApplicationDataInitializer implements ApplicationRunner {
    private BankAccountService bankAccountService;
    private UserAccountService userAccountService;

    public ApplicationDataInitializer(BankAccountService bankAccountService, UserAccountService userAccountService) {
        this.bankAccountService = bankAccountService;
        this.userAccountService = userAccountService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadUserAccounts();

        loadBackAccounts();
    }

    //TODO: Need to somehow save the generated IBANs to the database -Jason
    private void loadBackAccounts() {
        List.of(
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), "current", "active", 1000.00, "JohnDoe"),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), "saving", "active", 1000.00, "KarenWinter"),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), "saving", "active", 1000.00, "SteveWoo"),
        new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), "saving", "active", 1000.00, "ale")
        ).forEach(
                dto -> bankAccountService.addBankAccount(dto)
        );

        bankAccountService.getAllBankAccounts().forEach(System.out::println);
    }

    public void loadUserAccounts(){
        List.of(

                new UserAccountDTO("John", "Doe", "JohnDoe@gmail.com", "JohnDoe", "secret123", "customer"),
                new UserAccountDTO("Karen", "Winter", "KarenWinter@gmail.com", "KarenWinter", "secret123", "employee"),
                new UserAccountDTO("Steve", "Woo", "SteveWoo@gmail.com", "SteveWoo", "secret123", "registeredUser"),
				new UserAccountDTO("Alessandra", "Ribeiro", "ale@gmail.com", "ale", "123", "customer")

        ).forEach(
                dto -> userAccountService.addUserAccount(dto)
        );

        userAccountService.getAllUserAccounts().forEach(System.out::println);
    }
}
