package nl.inholland.bankingapplication.services;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.*;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.repositories.TransactionRepository;
import nl.inholland.bankingapplication.services.specifications.TransactionSpecifications;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserAccountService userAccountService;
    private final BankAccountService bankAccountService;
    private final TransactionSpecifications transactionSpecifications;

    public TransactionService(TransactionRepository transactionRepository, UserAccountService userAccountService, BankAccountService bankAccountService, TransactionSpecifications transactionSpecifications) {
        this.transactionRepository = transactionRepository;
        this.userAccountService = userAccountService;
        this.bankAccountService = bankAccountService;
        this.transactionSpecifications = transactionSpecifications;
    }

    public List<Transaction> getAllTransactions(UserAccount user, Integer userId,String IBANFrom, String IBANTo, Timestamp dateFrom, Timestamp dateTo, List<String> amount) {
        Specification<Transaction> specification = Specification.where(transactionSpecifications.hasIBANFrom(IBANFrom))
                .and(transactionSpecifications.hasIBANTo(IBANTo));
        specification = specification.and(getSpecificationForAmount(specification,amount));
        if (dateFrom != null && dateTo != null) {
            specification = specification.and(transactionSpecifications.betweenDates(dateFrom, dateTo));
        } else if (dateFrom != null) {
            specification = specification.and(transactionSpecifications.afterDate(dateFrom));
        } else if (dateTo != null) {
            specification = specification.and(transactionSpecifications.beforeDate(dateTo));
        }
        if (user.getType().equals(UserAccountType.ROLE_CUSTOMER)) {
            specification = specification.and(transactionSpecifications.hasUserAccountFrom(user)
                    .or(transactionSpecifications.hasUserAccountTo(user)));
        }
        else if (user.getType().equals(UserAccountType.ROLE_EMPLOYEE) && userId != null) {
            specification = specification.and(transactionSpecifications.hasUserAccountFrom(userAccountService.getUserAccountById(userId.longValue()))
                    .or(transactionSpecifications.hasUserAccountTo(userAccountService.getUserAccountById(userId.longValue()))));
        }

        return transactionRepository.findAll(specification);
    }

    public TransactionResponseDTO makeTransaction(MakeTransactionDTO makeTransactionDTO, UserAccount user) throws ResponseStatusException {
        //this method is such a mess, good luck reading this ♡♡ Cody.
        //I added comments to see if it helped with readability, but it really didn't f.

        List<BankAccount> bankAccountsOfUser = bankAccountService.getBankAccountsByUserAccountId(user.getId());
        Transaction transaction = mapMakeTransactionDtoToTransaction(makeTransactionDTO);

        if (transaction.getAccountFrom().getStatus() == BankAccountStatus.INACTIVE || transaction.getAccountTo().getStatus() == BankAccountStatus.INACTIVE){
            throw new DataIntegrityViolationException("one of the bankaccounts are de-activated");
        }

        if (transaction.getAmount() <= 0){
            throw new DataIntegrityViolationException("amount can't be 0 or lower");
        }

        if (transaction.getAccountFrom().getIBAN().equals("NL01INHO0000000001") || transaction.getAccountTo().getIBAN().equals("NL01INHO0000000001")){
            throw new DataIntegrityViolationException("can't use that account");
        }
        if (checkBankAccountType(transaction.getAccountFrom(), BankAccountType.SAVINGS) && checkBankAccountType(transaction.getAccountTo(), BankAccountType.SAVINGS)){
            throw new DataIntegrityViolationException("cant make a transaction between savings accounts");
        }

        //this if checks if the employee is logged in and if yes the transaction goes through no matter what

        //this if checks if a transaction is made between savings accounts.


        //the first if checks if the owner of the account is indeed making the transaction.
        return makeTransactionChecks(user, bankAccountsOfUser, transaction);
    }
    private TransactionResponseDTO makeTransactionChecks(UserAccount user, List<BankAccount> bankAccountsOfUser, Transaction transaction) {
        if (bankAccountsOfUser.stream().anyMatch(b -> b.getIBAN().equals(transaction.getAccountFrom().getIBAN()) || user.getType().equals(UserAccountType.ROLE_EMPLOYEE))) {

            //this if checks if the account to is a savings account
            if (checkBankAccountType(transaction.getAccountFrom(), BankAccountType.SAVINGS)) {

                // this if checks if the savings account is owned by the user

                if (checkIfBankAccountIsOwnedByUser(transaction.getAccountTo(), user)){
                    return finalizeSavingsTransactions(transaction, user);
                }
                else {
                    throw new DataIntegrityViolationException("Can't from savings to a current account you don't own");
                }
            }
            // this if checks if the account is from a savings account.
            if (checkBankAccountType(transaction.getAccountTo(), BankAccountType.SAVINGS)){

                //this if checks if the account to is owned by the user.
                if (checkIfBankAccountIsOwnedByUser(transaction.getAccountTo(), user)){
                    if (checkIfBankAccountIsOwnedByUser(transaction.getAccountFrom(), user)){
                        return finalizeSavingsTransactions(transaction, user);
                    }
                    else {
                        throw new DataIntegrityViolationException("Can't make a transaction to a current account you don't own");
                    }
                }
                else {
                    throw new DataIntegrityViolationException("Can't make a transaction to a savings account you don't own");

                }
            }
            return finalizeTransaction(transaction, user);
        }
        else {
            throw new DataIntegrityViolationException("you don't own the bankaccount you are making the transaction with");

        }
    }
    public TransactionResponseDTO makeDeposit(WithdrawalAndDepositRequestDTO dto, UserAccount user){
        return checkForViolationsWithdrawalAndDeposit(dto, user, false);
    }
    public TransactionResponseDTO makeWithdrawal(WithdrawalAndDepositRequestDTO dto, UserAccount user){
        return checkForViolationsWithdrawalAndDeposit(dto, user, true);
    }
    private TransactionResponseDTO checkForViolationsWithdrawalAndDeposit(WithdrawalAndDepositRequestDTO dto, UserAccount user, boolean isWithdrawal) {
        BankAccount account = bankAccountService.getBankAccountByIBAN(dto.getIBAN());
        if (dto.getAmount() <= 1)
            throw new DataIntegrityViolationException("amount needs to be higher than 0");
        if (account.getStatus() == BankAccountStatus.INACTIVE){
            throw new DataIntegrityViolationException("bankaccount is de-activated");
        }
        if (account.getType() == BankAccountType.SAVINGS){
            if (isWithdrawal){
                throw new DataIntegrityViolationException("cant make a withdrawal from a savings account");
            }
            else
                throw new DataIntegrityViolationException("cant make a deposit to a savings account");
        }
        if (isWithdrawal){
            checkDayLimit(mapWithdrawalRequestDtoToTransaction(dto), user);
            checkAbsoluteLimit(mapWithdrawalRequestDtoToTransaction(dto));
        }
        if (checkIfBankAccountIsOwnedByUser(account, user)){
            if (isWithdrawal){
                return mapTransactionTOTransactionResponseDTO(saveTransaction(mapWithdrawalRequestDtoToTransaction(dto)));
            }
            else
                return mapTransactionTOTransactionResponseDTO(saveTransaction(mapDepositRequestDtoToTransaction(dto)));
        }
        else {
            throw new DataIntegrityViolationException("You are not allowed to make deposits with bank-accounts you don't own");
        }
    }

    private TransactionResponseDTO finalizeTransaction(Transaction transaction, UserAccount user) throws ResponseStatusException {
        checkAbsoluteLimit(transaction);
        if (!Objects.equals(user.getType(), UserAccountType.ROLE_EMPLOYEE)) {
            checkTransactionLimit(transaction, user);
            checkDayLimit(transaction, user);
        }
        Transaction transaction1 = saveTransaction(transaction);
        return mapTransactionTOTransactionResponseDTO(transaction1);
    }
    private TransactionResponseDTO finalizeSavingsTransactions(Transaction transaction, UserAccount user) throws ResponseStatusException {
        checkAbsoluteLimit(transaction);
        if (!Objects.equals(user.getType(), UserAccountType.ROLE_EMPLOYEE)) {
            checkTransactionLimit(transaction, user);
        }
        Transaction transaction1 = saveTransaction(transaction);
        return mapTransactionTOTransactionResponseDTO(transaction1);
    }

    private Transaction saveTransaction(Transaction transaction) {
        var i = transactionRepository.save(transaction);
        bankAccountService.updateAmount(transaction.getAccountFrom().getIBAN(), -transaction.getAmount());
        bankAccountService.updateAmount(transaction.getAccountTo().getIBAN(), transaction.getAmount());
        return i;
    }

    private void checkAbsoluteLimit(Transaction transaction) throws ResponseStatusException {
        if (transaction.getAccountFrom().getBalance() - transaction.getAmount() < transaction.getAccountFrom().getAbsoluteLimit()) {
            throw new DataIntegrityViolationException("with this transaction you will pass the absolute limit of your account");
        }
    }

    private void checkTransactionLimit(Transaction transaction, UserAccount user) throws ResponseStatusException {
        if (transaction.getAmount() > user.getTransactionLimit()){
            throw new DataIntegrityViolationException("this transactions passes you transaction limit");
        }
    }

    private void checkDayLimit(Transaction transaction, UserAccount user) {
        if (user.getCurrentDayLimit() >= user.getDayLimit()){
            throw new DataIntegrityViolationException("passed your day limit");

        }
        else {
            if (user.getCurrentDayLimit() + transaction.getAmount() >= user.getDayLimit()){
                throw new DataIntegrityViolationException("you are passing your day limit lower the amount of the transaction to proceed");

            }
            else {
                user.setCurrentDayLimit(user.getCurrentDayLimit() + transaction.getAmount());
                userAccountService.patchUserAccount(user.getId().longValue(), new UserAccountPatchDTO(user.getStatus(),user.getCurrentDayLimit()));
            }
        }
    }

    private Boolean checkBankAccountType(BankAccount bankAccount, BankAccountType bankAccountType){
        return bankAccount.getType().equals(bankAccountType);
    }

    private Boolean checkIfBankAccountIsOwnedByUser(BankAccount bankAccount, UserAccount userAccount){
        return bankAccount.getUserAccount().getId().equals(userAccount.getId());
    }

    private Transaction mapMakeTransactionDtoToTransaction(MakeTransactionDTO dto){
        Date date = new Date();
        BankAccount bFrom = bankAccountService.getBankAccountByIBAN(dto.getAccountFrom());
        BankAccount bTo = bankAccountService.getBankAccountByIBAN(dto.getAccountTo());
        return new Transaction(dto.getAmount(), userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName()), bFrom,bFrom.getUserAccount(), bTo,bTo.getUserAccount(), dto.getDescription(), new Timestamp(date.getTime()));
    }

    private Transaction mapWithdrawalRequestDtoToTransaction(WithdrawalAndDepositRequestDTO dto){
        Date date = new Date();
        BankAccount bFrom = bankAccountService.getBankAccountByIBAN(dto.getIBAN());
        BankAccount bTo = bankAccountService.getBankAccountsByUserAccountId(1L).get(0);
        return new Transaction(dto.getAmount(), userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName()), bFrom,bFrom.getUserAccount(),bTo,bTo.getUserAccount(),"Withdrawal", new Timestamp(date.getTime()));
    }
    private Transaction mapDepositRequestDtoToTransaction(WithdrawalAndDepositRequestDTO dto){
        Date date = new Date();
        BankAccount bFrom = bankAccountService.getBankAccountsByUserAccountId(1L).get(0);
        BankAccount bTo = bankAccountService.getBankAccountByIBAN(dto.getIBAN());
        return new Transaction(dto.getAmount(), userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName()), bFrom,bFrom.getUserAccount(),bTo,bTo.getUserAccount(),"Deposit", new Timestamp(date.getTime()));
    }


    private Specification<Transaction> getSpecificationForAmount(Specification<Transaction> specification, List<String> amount){
        if (amount != null){
            if(amount.get(0).startsWith("<") && amount.get(0).startsWith(">",1)){
                double actualAmount = Double.parseDouble(amount.get(0).replace("<","").replace(">",""));
                double actualAmount2 = Double.parseDouble(amount.get(1));
                actualAmount += 0.01;
                actualAmount2 -= 0.01;
                specification = specification.and(transactionSpecifications.betweenAmounts(actualAmount, actualAmount2));
            }
            else
            if(amount.get(0).startsWith("<")){
                double actualAmount = Double.parseDouble(amount.get(0).replace("<",""));
                actualAmount += 0.01;
                specification = specification.and(transactionSpecifications.lessThanAmount(actualAmount));
            }
            else if (amount.get(0).startsWith(">")){
                double actualAmount = Double.parseDouble(amount.get(0).replace(">",""));
                actualAmount -= 0.01;
                specification = specification.and(transactionSpecifications.greaterThanAmount(actualAmount));
            }
            else{
                double actualAmount = Double.parseDouble(amount.get(0));
                specification = specification.and(transactionSpecifications.equalsAmount(actualAmount));
            }

        }
        return specification;
    }

    public Transaction makeMockTransaction(MakeTransactionDTO dto){
            return transactionRepository.save(mapMockTransactionDto(dto));
    }
    private Transaction mapMockTransactionDto(MakeTransactionDTO dto){
        Date date = new Date();
        BankAccount bFrom = bankAccountService.getBankAccountByIBAN(dto.getAccountFrom());
        BankAccount bTo = bankAccountService.getBankAccountByIBAN(dto.getAccountTo());
        return new Transaction(dto.getAmount(), null, bFrom,bFrom.getUserAccount(), bTo, bTo.getUserAccount(),dto.getDescription(), new Timestamp(date.getTime()));

    }

    private TransactionResponseDTO mapTransactionTOTransactionResponseDTO(Transaction tra){
        return new TransactionResponseDTO(tra.getId(), tra.getAmount(),tra.getMadeBy().getId().intValue(),tra.getAccountFrom().getIBAN(),tra.getAccountTo().getIBAN(),tra.getDescription(),tra.getOccuredAt());
    }
}
