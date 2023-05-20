package nl.inholland.bankingapplication.configuration;

import jakarta.transaction.Transactional;
import nl.inholland.bankingapplication.models.dto.BankAccountDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
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

    private void loadBackAccounts() {
        List.of(
                new BankAccountDTO("NL77ABNA5602795901", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, 1L),
                new BankAccountDTO("NL71RABO3667086008", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, 2L),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, 3L),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), BankAccountType.SAVINGS, BankAccountStatus.ACTIVE, 1000.00, 0, 4L),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), BankAccountType.SAVINGS, BankAccountStatus.ACTIVE, 1000.00, 0, 5L),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), BankAccountType.SAVINGS, BankAccountStatus.ACTIVE, 1000.00, 0, 6L)
        ).forEach(
                dto -> bankAccountService.addBankAccount(dto)
        );

        bankAccountService.getAllBankAccounts().forEach(System.out::println);
    }

    public void loadUserAccounts(){
        List.of(
                new UserAccountDTO("Bank", "Bank", "Bank@gmail.com", "Bank", "secret123", "employee", "+31111111111", 12345111, 1000.00, 250.00),
                new UserAccountDTO("Jun", "Feng", "junfeng@gmail.com", "JunFeng", "secret123", "customer", "+31222222222", 12345222, 1000.00, 250.00),
                new UserAccountDTO("John", "Doe", "JohnDoe@gmail.com", "JohnDoe", "secret123", "customer", "+31333333333", 12345333, 1000.00, 250.00),
                new UserAccountDTO("Karen", "Winter", "KarenWinter@gmail.com", "KarenWinter", "secret123", "deactivateduser", "+31444444444", 12345444, 1000.00, 250.00),
                new UserAccountDTO("Steve", "Woo", "SteveWoo@gmail.com", "SteveWoo", "secret123", "registereduser", "+31555555555", 12345555, 1000.00, 250.00),
				new UserAccountDTO("Alessandra", "Ribeiro", "ale@gmail.com", "ale", "123", "customer", "+31666666666", 12345666, 1000.00, 250.00)

        ).forEach(
                dto -> userAccountService.addUserAccount(dto)
        );

        userAccountService.getAllUserAccounts().forEach(System.out::println);
    }
}
