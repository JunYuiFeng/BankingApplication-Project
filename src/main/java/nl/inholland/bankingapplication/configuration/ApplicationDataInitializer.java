package nl.inholland.bankingapplication.configuration;

import jakarta.transaction.Transactional;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.*;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.repositories.TransactionRepository;
import nl.inholland.bankingapplication.services.BankAccountService;
import nl.inholland.bankingapplication.services.TransactionService;
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
    private TransactionService transactionService;
    private TransactionRepository transactionRepository;

    public ApplicationDataInitializer(BankAccountService bankAccountService, UserAccountService userAccountService, TransactionService transactionService, TransactionRepository transactionRepository){
        this.bankAccountService = bankAccountService;
        this.userAccountService = userAccountService;
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadUserAccounts();

        loadBackAccounts();
        loadTransactions();
    }

    private void loadBackAccounts() {
        List.of(
                new BankAccountRegisterDTO(BankAccountType.SAVINGS, 4L),
                new BankAccountRegisterDTO(BankAccountType.SAVINGS, 6L)
        ).forEach(
                dto -> bankAccountService.addBankAccount(dto)
        );

        List.of(
                new BankAccountPredefinedDTO("NL01INHO0000000001", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000000000.00, 0, 1L),
                new BankAccountPredefinedDTO("NL71RABO3667086008", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, 2L),
                new BankAccountPredefinedDTO("NL43ABNA5253446745", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, 3L)
        ).forEach(
                dto -> bankAccountService.addPredefinedBankAccount(dto)
        );

        bankAccountService.getAllBankAccounts().forEach(System.out::println);
    }

    public void loadUserAccounts(){
        List.of(
                new UserAccountPredefinedDTO("Bank", "Bank", "Bank@gmail.com", "Bank", "secret123", UserAccountType.ROLE_EMPLOYEE, UserAccountStatus.ACTIVE, "+31111111111", 12345111, 1000.00,0, 250.00),
                new UserAccountPredefinedDTO("Jun", "Feng", "junfeng@gmail.com", "JunFeng", "secret123", UserAccountType.ROLE_CUSTOMER, UserAccountStatus.ACTIVE, "+31222222222", 12345222, 1000.00,0, 250.00),
                new UserAccountPredefinedDTO("John", "Doe", "JohnDoe@gmail.com", "JohnDoe", "secret123", UserAccountType.ROLE_CUSTOMER, UserAccountStatus.ACTIVE, "+31333333333", 12345333, 1000.00,0, 250.00),
                new UserAccountPredefinedDTO("Karen", "Winter", "KarenWinter@gmail.com", "KarenWinter", "secret123", UserAccountType.ROLE_EMPLOYEE, UserAccountStatus.ACTIVE, "+31444444444", 12345444, 1000.00, 0,250.00),
                new UserAccountPredefinedDTO("Steve", "Woo", "SteveWoo@gmail.com", "SteveWoo", "secret123", UserAccountType.ROLE_USER, UserAccountStatus.ACTIVE, "+31555555555", 12345555, 1000.00, 0,250.00),
				new UserAccountPredefinedDTO("Alessandra", "Ribeiro", "ale@gmail.com", "ale", "123", UserAccountType.ROLE_CUSTOMER, UserAccountStatus.ACTIVE, "+31666666666", 12345666, 1000.00, 0,250.00)

        ).forEach(
                dto -> userAccountService.addPredefinedUserAccount(dto)
        );

        userAccountService.getAllUserAccounts().forEach(System.out::println);
    }
    private void loadTransactions() {

            List<BankAccountResponseDTO> accounts = bankAccountService.getAllBankAccounts();

            List<MakeTransactionDTO> transactions = List.of(
                    //double amount, UserAccount madeBy, BankAccount accountFrom, BankAccount accountTo, String description, Timestamp occuredAt
                    new MakeTransactionDTO(accounts.get(3).getIBAN(), accounts.get(4).getIBAN(), 100, "oi"),
                    new MakeTransactionDTO(accounts.get(4).getIBAN(), accounts.get(2).getIBAN(), 50,"la" ),
                    new MakeTransactionDTO(accounts.get(2).getIBAN(), accounts.get(3).getIBAN(),200,"ta" ),

                    new MakeTransactionDTO(accounts.get(3).getIBAN(), accounts.get(4).getIBAN(),500,"ta" ),

                    new MakeTransactionDTO(accounts.get(1).getIBAN(), accounts.get(2).getIBAN(), 10, null)

            );

            UserAccount bankUserAccount = userAccountService.getUserAccountById(1L);
            transactions.forEach(
                    dto -> {
                        try {
                            transactionService.makeMockTransaction(dto);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

            transactionRepository.findAll().forEach(System.out::println);

    }
}
