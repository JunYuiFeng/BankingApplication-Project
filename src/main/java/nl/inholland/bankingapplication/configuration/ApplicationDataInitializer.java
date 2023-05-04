package nl.inholland.bankingapplication.configuration;

import jakarta.transaction.Transactional;
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
        loadBackAccounts();

        loadUserAccounts();
    }

    private void loadBackAccounts() {
        List.of(
                new BankAccountDTO("NL58RABO4228435912", "current", "active", 1000.00),
                new BankAccountDTO("NL58RABO4228435913", "saving", "active", 1000.00),
                new BankAccountDTO("NL58RABO4228435914", "saving", "active", 1000.00)
        ).forEach(
                dto -> bankAccountService.addBankAccount(dto)
        );

        bankAccountService.getAllBankAccounts().forEach(System.out::println);
    }

    public void loadUserAccounts(){
        List.of(
                new UserAccountDTO("John", "Doe", "JohnDoe@gmail.com", "JohnDoe", "secret123"),
                new UserAccountDTO("Karen", "Winter", "KarenWinter@gmail.com", "KarenWinter", "secret123"),
                new UserAccountDTO("Steve", "Woo", "SteveWoo@gmail.com", "SteveWoo", "secret123")
        ).forEach(
                dto -> userAccountService.addUserAccount(dto)
        );

        userAccountService.getAllUserAccounts().forEach(System.out::println);
    }
}
