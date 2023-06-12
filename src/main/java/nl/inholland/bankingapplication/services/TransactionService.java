package nl.inholland.bankingapplication.services;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.*;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.repositories.TransactionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.crypto.Data;
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

    public TransactionService(TransactionRepository transactionRepository, UserAccountService userAccountService, BankAccountService bankAccountService) {
        this.transactionRepository = transactionRepository;
        this.userAccountService = userAccountService;
        this.bankAccountService = bankAccountService;
    }

    public List<Transaction> getAllTransactions(UserAccount user, Integer userId,String IBANFrom, String IBANTo, Timestamp dateFrom, Timestamp dateTo, List<String> amount) {

        //This is such a dumb hack but hey I have to know if it is empty or not

        BankAccount bankAccountFrom = null;
        if(IBANFrom != null){
            bankAccountFrom = bankAccountService.getBankAccountByIBAN(IBANFrom);
        }

        BankAccount bankAccountTo = null;
        if (IBANTo != null){
            bankAccountTo = bankAccountService.getBankAccountByIBAN(IBANTo);
        }

        Transaction transaction = Transaction.builder().accountFrom(bankAccountFrom).accountTo(bankAccountTo).build();
        List<Transaction> transactions = transactionRepository.findAll(Example.of(transaction));

        if (!Objects.equals(user.getType(), UserAccountType.ROLE_EMPLOYEE)){
           transactions = filterTransactionsByUser(user, transactions);
        }
        else {
            if(userId !=null){
                transactions = filterTransactionsByUser(userAccountService.getUserAccountById(userId.longValue()),transactions);
            }
        }
        transactions = filterTransactionResponseForDates(transactions, dateFrom, dateTo);
        transactions = filterTransactionsResponseForAmount(transactions, amount);
        return transactions;
    }

    private List<Transaction> filterTransactionsByUser(UserAccount user, List<Transaction> transactions) {
        List <Transaction> transactionFrom = new ArrayList<>();
        List<Transaction> transactionsTo = new ArrayList<>();
        for (BankAccount bankAccount: user.getBankAccounts()
             ) {
            transactionFrom.addAll(transactions.stream().filter(t -> t.getAccountFrom().getIBAN().equals(bankAccount.getIBAN())).toList());
            transactionsTo.addAll(transactions.stream().filter(t -> t.getAccountTo().getIBAN().equals(bankAccount.getIBAN())).toList());
        }
        transactions.removeAll(transactions);
        transactions.addAll(transactionFrom);
        transactions.addAll(transactionsTo);
        return transactions;
    }

    public TransactionResponseDTO makeTransaction(MakeTransactionDTO makeTransactionDTO, UserAccount user) throws ResponseStatusException {
        //this method is such a mess, good luck reading this ♡♡ Cody.
        //I added comments to see if it helped with readability, but it really didn't f.

        List<BankAccount> bankAccountsOfUser = bankAccountService.getBankAccountsByUserAccountId(user.getId());
        Transaction transaction = mapMakeTransactionDtoToTransaction(makeTransactionDTO);

        if (transaction.getAccountFrom().getStatus() == BankAccountStatus.INACTIVE || transaction.getAccountTo().getStatus() == BankAccountStatus.INACTIVE){
            throw new DataIntegrityViolationException("one of the bankaccounts are de-activated");
        }

        //this if checks if the employee is logged in and if yes the transaction goes through no matter what
        if (Objects.equals(user.getType(), UserAccountType.ROLE_EMPLOYEE)){
            return finalizeTransaction(transaction, user);
        }
        //this if checks if a transaction is made between savings accounts.
        if (checkBankAccountType(transaction.getAccountFrom(), BankAccountType.SAVINGS) && checkBankAccountType(transaction.getAccountTo(), BankAccountType.SAVINGS)){
            throw new DataIntegrityViolationException("cant make a transaction between savings accounts");
        }

        //the first if checks if the owner of the account is indeed making the transaction.
        return makeTransactionChecks(user, bankAccountsOfUser, transaction);
    }

    private TransactionResponseDTO makeTransactionChecks(UserAccount user, List<BankAccount> bankAccountsOfUser, Transaction transaction) {
        if (bankAccountsOfUser.stream().anyMatch(b -> b.getIBAN().equals(transaction.getAccountFrom().getIBAN()))){

            //this if checks if the account to is a savings account
            if (checkBankAccountType(transaction.getAccountFrom(), BankAccountType.SAVINGS)) {

                // this if checks if the savings account is owned by the user

                if (checkIfBankAccountIsOwnedByUser(transaction.getAccountTo(), user)){
                    return finalizeTransaction(transaction, user);
                }
                else {
                    throw new DataIntegrityViolationException("Can't make a transaction to a savings account you don't own");
                }
            }
            // this if checks if the account is from a savings account.
            if (checkBankAccountType(transaction.getAccountTo(), BankAccountType.SAVINGS)){

                //this if checks if the account to is owned by the user.
                if (checkIfBankAccountIsOwnedByUser(transaction.getAccountTo(), user)){
                    if (checkIfBankAccountIsOwnedByUser(transaction.getAccountFrom(), user)){
                        return finalizeTransaction(transaction, user);
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
        return new Transaction(dto.getAmount(), userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName()), bankAccountService.getBankAccountByIBAN(dto.getAccountFrom()), bankAccountService.getBankAccountByIBAN(dto.getAccountTo()), dto.getDescription(), new Timestamp(date.getTime()));
    }

    private Transaction mapWithdrawalRequestDtoToTransaction(WithdrawalAndDepositRequestDTO dto){
        Date date = new Date();
        return new Transaction(dto.getAmount(), userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName()), bankAccountService.getBankAccountByIBAN(dto.getIBAN()),bankAccountService.getBankAccountsByUserAccountId(1L).get(0),"Withdrawal", new Timestamp(date.getTime()));
    }
    private Transaction mapDepositRequestDtoToTransaction(WithdrawalAndDepositRequestDTO dto){
        Date date = new Date();
        return new Transaction(dto.getAmount(), userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName()), bankAccountService.getBankAccountsByUserAccountId(1L).get(0),bankAccountService.getBankAccountByIBAN(dto.getIBAN()),"Deposit", new Timestamp(date.getTime()));
    }

    private List<Transaction> filterTransactionResponseForDates(List<Transaction> transactions, Timestamp dateFrom, Timestamp dateTo){
        if (dateFrom != null && dateTo != null){
           transactions = transactions.stream().filter(t -> t.occuredAt.after(dateFrom)).filter(t -> t.occuredAt.before(dateTo)).toList();
        }
        else if (dateFrom !=null){
            transactions = transactions.stream().filter(t -> t.occuredAt.after(dateFrom)).toList();
        }
        else if (dateTo != null){
            transactions = transactions.stream().filter(t -> t.occuredAt.before(dateTo)).toList();
        }
        return transactions;
    }

    private List<Transaction> filterTransactionsResponseForAmount(List<Transaction> transactions, List<String> amount){
        if (amount != null){
            if(amount.get(0).startsWith("<") && amount.get(0).startsWith(">",1)){
                Double actualAmount = Double.valueOf(amount.get(0).replace("<","").replace(">",""));
                Double actualAmount2 = Double.valueOf(amount.get(1));
                transactions = transactions.stream().filter(t -> t.getAmount() > actualAmount).filter(t -> t.getAmount() < actualAmount2).toList();
            }
            else
            if(amount.get(0).startsWith("<")){
                Double actualAmount = Double.valueOf(amount.get(0).replace("<",""));
                transactions = transactions.stream().filter(t -> t.getAmount() < actualAmount).toList();
            }
            else if (amount.get(0).startsWith(">")){
                Double actualAmount = Double.valueOf(amount.get(0).replace(">",""));
                transactions = transactions.stream().filter(t -> t.getAmount() > actualAmount).toList();
            }
            else{
                Double actualAmount = Double.valueOf(amount.get(0));
                transactions = transactions.stream().filter(t -> t.getAmount().equals(actualAmount)).toList();
            }

        }
        return transactions;
    }

    public Transaction makeMockTransaction(MakeTransactionDTO dto){
            return transactionRepository.save(mapMockTransactionDto(dto));
    }
    private Transaction mapMockTransactionDto(MakeTransactionDTO dto){
        Date date = new Date();
        return new Transaction(dto.getAmount(), null, bankAccountService.getBankAccountByIBAN(dto.getAccountFrom()), bankAccountService.getBankAccountByIBAN(dto.getAccountTo()), dto.getDescription(), new Timestamp(date.getTime()));

    }

    private TransactionResponseDTO mapTransactionTOTransactionResponseDTO(Transaction tra){
        return new TransactionResponseDTO(tra.getId(), tra.getAmount(),tra.getMadeBy().getId().intValue(),tra.getAccountFrom().getIBAN(),tra.getAccountTo().getIBAN(),tra.getDescription(),tra.getOccuredAt());
    }
}
