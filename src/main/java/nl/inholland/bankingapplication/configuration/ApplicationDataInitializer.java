package nl.inholland.bankingapplication.configuration;

import jakarta.transaction.Transactional;
import nl.inholland.bankingapplication.models.dto.BankAccountDTO;
import nl.inholland.bankingapplication.services.BankAccountService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Transactional
public class ApplicationDataInitializer implements ApplicationRunner {
    private BankAccountService bankAccountService;

    public ApplicationDataInitializer(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List.of(
                new BankAccountDTO("NL58RABO4228435912", "current", "active", 1000.00),
                new BankAccountDTO("NL58RABO4228435913", "saving", "active", 1000.00),
                new BankAccountDTO("NL58RABO4228435914", "saving", "active", 1000.00)
        ).forEach(
                dto -> bankAccountService.addBankAccount(dto)
        );

        bankAccountService.getAllBankAccounts().forEach(System.out::println);
    }
}
