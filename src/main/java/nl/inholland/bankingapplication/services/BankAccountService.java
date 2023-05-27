package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccounResponseDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountPredefinedDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountUpdateDTO;
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

    public List<BankAccount> getBankAccountByUserAccountId(Long id) {
        List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByUserAccountId(id);
        if (bankAccounts.isEmpty()) {
            throw new EntityNotFoundException("Bank account not found");
        }
        return bankAccounts;
    }

    public List<BankAccount> getBankAccountByStatus(String status) {
        List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByStatus(BankAccountStatus.valueOf(status.toUpperCase()));
        if (bankAccounts.isEmpty()) {
            throw new EntityNotFoundException("Bank account not found");
        }
        return bankAccounts;
    }

    public List<BankAccount> getBankAccountByName(String name) {
        List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByUserAccountFirstNameContainingIgnoreCaseOrUserAccountLastNameContainingIgnoreCase(name, name);
        if (bankAccounts.isEmpty()) {
            throw new EntityNotFoundException("Bank account not found");
        }
        return bankAccounts;
    }

    public BankAccounResponseDTO addBankAccount(BankAccountRegisterDTO dto) {
        BankAccount bankAccount = bankAccountRepository.save(this.mapDtoToBankAccount(dto));
        return mapBankAccountToDto(bankAccount);
    }

    public BankAccounResponseDTO addPredefinedBankAccount(BankAccountPredefinedDTO dto) {
        BankAccount bankAccount = bankAccountRepository.save(this.mapPreDtoToBankAccount(dto));
        return mapBankAccountToDto(bankAccount);
    }


    public BankAccount updateBankAccount(BankAccountUpdateDTO dto, String IBAN) {
        BankAccount bankAccount = bankAccountRepository
                .findBankAccountByIBAN(IBAN)
                .orElseThrow(() -> new EntityNotFoundException("Bank Account not found"));

        if (dto.getStatusIgnoreCase() != null) {
            bankAccount.setStatus(dto.getStatusIgnoreCase());
        }

        if (dto.getBalance() != 0) {
            bankAccount.setBalance(dto.getBalance());
        }

        if (dto.getAbsoluteLimit() != 0) {
            bankAccount.setAbsoluteLimit(dto.getAbsoluteLimit());
        }

        return bankAccountRepository.save(bankAccount);
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

    private BankAccount mapDtoToBankAccount(BankAccountRegisterDTO dto) {

        BankAccount bankAccount = new BankAccount();
        bankAccount.setType(dto.getType());
        bankAccount.setStatus(BankAccountStatus.ACTIVE);
        bankAccount.setBalance(0);
        bankAccount.setAbsoluteLimit(0);
        bankAccount.setIBAN(this.GenerateIBAN());
        bankAccount.setUserAccount(userAccountService.getUserAccountById(dto.getUserId()));

        return bankAccount;
    }

    private BankAccount mapPreDtoToBankAccount(BankAccountPredefinedDTO dto) {

        BankAccount bankAccount = new BankAccount();
        bankAccount.setType(dto.getType());
        bankAccount.setStatus(BankAccountStatus.ACTIVE);
        bankAccount.setBalance(dto.getBalance());
        bankAccount.setAbsoluteLimit(0);
        bankAccount.setIBAN(dto.getIBAN());
        bankAccount.setUserAccount(userAccountService.getUserAccountById(dto.getUserId()));

        return bankAccount;
    }

    private BankAccounResponseDTO mapBankAccountToDto(BankAccount bankAccount) {
        BankAccounResponseDTO bankAccounResponseDTO = new BankAccounResponseDTO();
        bankAccounResponseDTO.setType(bankAccount.getType());
        bankAccounResponseDTO.setStatus(bankAccount.getStatus());
        bankAccounResponseDTO.setBalance(bankAccount.getBalance());
        bankAccounResponseDTO.setAbsoluteLimit(bankAccount.getAbsoluteLimit());
        bankAccounResponseDTO.setIBAN(bankAccount.getIBAN());

        return bankAccounResponseDTO;
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
