package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountDTO;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.repositories.BankAccountRepository;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserAccountService userAccountService;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserAccountService userAccountService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userAccountService = userAccountService;
    }

    public List<BankAccount> getAllBankAccounts() {
        return (List<BankAccount>) bankAccountRepository.findAll();
    }

    public BankAccount getBankAccountByIBAN(String IBAN) {
        return bankAccountRepository.findBankAccountByIBAN(IBAN).orElseThrow(
                () -> new EntityNotFoundException("Bank account not found")
        );
    }

    public BankAccount getBankAccountByFirstname(String firstname) {
        return bankAccountRepository.findBankAccountByUserAccountFirstName(firstname).orElseThrow(
                () -> new EntityNotFoundException("Bank account with firstname " + firstname + " not found")
        );
    }

    public BankAccount addBankAccount(BankAccountDTO dto) {
        return bankAccountRepository.save(this.mapDtoToBankAccount(dto));
    }

    public BankAccount deactivateBankAccount(Long id) {
        BankAccount bankAccountToUpdate = bankAccountRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bank Account not found"));
        bankAccountToUpdate.setStatus(BankAccountStatus.ACTIVE);

        return bankAccountRepository.save(bankAccountToUpdate);
    }

    public BankAccount updateAmount(String IBAN, double change){
        BankAccount bankAccountToUpdate = bankAccountRepository
                .findBankAccountByIBAN(IBAN)
                .orElseThrow(() -> new EntityNotFoundException("Bank Account not found"));
        double currentBalance = bankAccountToUpdate.getBalance();
        double newBalance = currentBalance + change;
        bankAccountToUpdate.setBalance(newBalance);
        return  bankAccountRepository.save(bankAccountToUpdate);
    }

    private BankAccount mapDtoToBankAccount(BankAccountDTO dto) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setType(dto.getType());
        bankAccount.setStatus(BankAccountStatus.ACTIVE);
        bankAccount.setBalance(0);
        bankAccount.setAbsoluteLimit(0);
        bankAccount.setIBAN(this.GenerateIBAN());
        bankAccount.setUserAccount(userAccountService.getUserAccountById(dto.getId()));

        return bankAccount;
    }

    public String GenerateIBAN(){
        Iban iban = Iban.random(CountryCode.NL);
        iban = Iban.random();
        iban = new Iban.Builder()
                .countryCode(CountryCode.NL)
                .bankCode("INHO")
                .buildRandom();
        return iban.toString();
    }
}
