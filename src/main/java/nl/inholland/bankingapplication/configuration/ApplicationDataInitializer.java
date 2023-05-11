package nl.inholland.bankingapplication.configuration;

import jakarta.transaction.Transactional;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
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
                new BankAccountDTO("NL77ABNA5602795901", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, "Bank"),
                new BankAccountDTO("NL71RABO3667086008", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, "JunFeng"),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, "JohnDoe"),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), BankAccountType.SAVINGS, BankAccountStatus.ACTIVE, 1000.00, 0, "KarenWinter"),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), BankAccountType.SAVINGS, BankAccountStatus.ACTIVE, 1000.00, 0, "SteveWoo"),
                new BankAccountDTO(bankAccountService.GenerateIBAN().toString(), BankAccountType.SAVINGS, BankAccountStatus.ACTIVE, 1000.00, 0, "ale")
        ).forEach(
                dto -> bankAccountService.addBankAccount(dto)
        );

        bankAccountService.getAllBankAccounts().forEach(System.out::println);
    }

    public void loadUserAccounts(){
        List.of(
                new UserAccountDTO("Bank", "Bank", "Bank@gmail.com", "Bank", "secret123", "customer"),
                new UserAccountDTO("Jun", "Feng", "junfeng@gmail.com", "JunFeng", "secret123", "customer"),
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
