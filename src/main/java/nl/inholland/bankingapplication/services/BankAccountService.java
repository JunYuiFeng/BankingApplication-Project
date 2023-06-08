package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountResponseDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountPredefinedDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountUpdateDTO;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.repositories.BankAccountRepository;
import nl.inholland.bankingapplication.services.mappers.BankAccountResponseDTOMapper;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserAccountService userAccountService;
    private final BankAccountResponseDTOMapper bankAccountResponseDTOMapper;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserAccountService userAccountService, BankAccountResponseDTOMapper bankAccountResponseDTOMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.userAccountService = userAccountService;
        this.bankAccountResponseDTOMapper = bankAccountResponseDTOMapper;
    }

    public List<BankAccountResponseDTO> getAllBankAccounts() {
        try {
            List<BankAccount> bankAccounts = (List<BankAccount>) bankAccountRepository.findAll();

            if (bankAccounts.isEmpty()) {
                throw new EntityNotFoundException("No bank accounts found.");
            }

            return bankAccounts.stream()
                    .map(bankAccountResponseDTOMapper)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Something went wrong");
        }
    }

//    public List<BankAccount> getAllBankAccounts() {
//        return (List<BankAccount>) bankAccountRepository.findAll();
//    }


    public List<BankAccount> getBankAccountsExceptOwnAccount(Long userId) {
        return bankAccountRepository.findAllExceptOwnAccount(userAccountService.getUserAccountById(userId)); //already returns a list so no need to cast
    }

    public BankAccount getBankAccountByIBAN(String IBAN) {
        return bankAccountRepository.findBankAccountByIBAN(IBAN)
                .orElseThrow(() -> new EntityNotFoundException("Bank account with IBAN " + IBAN + " not found" ));
    }

    public List<BankAccount> getBankAccountsByUserAccountId(Long id) {
        List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByUserAccountId(id);
        if (bankAccounts.isEmpty()) {
            throw new EntityNotFoundException("Bank accounts not found");
        }
        return bankAccounts;
    }

    public List<BankAccount> getBankAccountsByStatus(String status) {
        List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByStatus(BankAccountStatus.valueOf(status.toUpperCase()));
        if (bankAccounts.isEmpty()) {
            throw new EntityNotFoundException("No bank accounts found with status " + status);
        }
        return bankAccounts;
    }

    public List<BankAccount> getBankAccountsByName(String name) {
        List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByUserAccountFirstNameIgnoreCaseOrUserAccountLastNameIgnoreCase(name, name);
        if (bankAccounts.isEmpty()) {
            throw new EntityNotFoundException("No bank accounts found with name '" + name + "'");
        }
        return bankAccounts;
    }


    public Boolean hasSavingsAccount(Long userId) {
        return bankAccountRepository.existsByUserAccountIdAndType(userId, BankAccountType.SAVINGS);
    }
    public BankAccountResponseDTO addBankAccount(BankAccountRegisterDTO dto) {
        if (this.hasSavingsAccount(dto.getUserId()) && dto.getType().equals(BankAccountType.SAVINGS)) {
            throw new DataIntegrityViolationException("User already has a savings account, only 1 savings account can be created");
        }

        BankAccount bankAccount = bankAccountRepository.save(this.mapDtoToBankAccount(dto));
        return mapBankAccountToDto(bankAccount);
    }

    public BankAccountResponseDTO addPredefinedBankAccount(BankAccountPredefinedDTO dto) {
        BankAccount bankAccount = bankAccountRepository.save(this.mapPreDtoToBankAccount(dto));
        return mapBankAccountToDto(bankAccount);
    }


    public BankAccount updateBankAccount(BankAccountUpdateDTO dto, String IBAN, UserAccount userAccount) {
        try {

            BankAccount bankAccount = this.getBankAccountByIBAN(IBAN);

            if(userAccount.getType().equals(UserAccountType.ROLE_EMPLOYEE)) {
                if (dto.getStatusIgnoreCase() != null) {
                    bankAccount.setStatus(dto.getStatusIgnoreCase());
                }

                if (dto.getAbsoluteLimit() != 0) {
                    bankAccount.setAbsoluteLimit(dto.getAbsoluteLimit());
                }
            }

            if (dto.getBalance() != 0) {
                bankAccount.setBalance(dto.getBalance());
            }

            return bankAccountRepository.save(bankAccount);
        } catch(Exception e) {
            throw new DataIntegrityViolationException("Failed to update bank account.");
        }
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

    private BankAccountResponseDTO mapBankAccountToDto(BankAccount bankAccount) {
        BankAccountResponseDTO bankAccountResponseDTO = new BankAccountResponseDTO();
        bankAccountResponseDTO.setType(bankAccount.getType());
        bankAccountResponseDTO.setStatus(bankAccount.getStatus());
        bankAccountResponseDTO.setBalance(bankAccount.getBalance());
        bankAccountResponseDTO.setAbsoluteLimit(bankAccount.getAbsoluteLimit());
        bankAccountResponseDTO.setIBAN(bankAccount.getIBAN());

        return bankAccountResponseDTO;
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
