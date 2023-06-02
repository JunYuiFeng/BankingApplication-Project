package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.MakeTransactionDTO;
import nl.inholland.bankingapplication.models.dto.TransactionDTO;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.repositories.TransactionRepository;
import org.springframework.data.domain.Example;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public List<Transaction> getAllTransactions(UserAccount user, Integer userId,String IBANFrom, String IBANTo, Timestamp dateFrom, Timestamp dateTo, Double amount) {

        //This is such a dumb hack but hey I have to know if it is empty or not

        BankAccount bankAccountFrom = null;
        if(IBANFrom != null){
            bankAccountFrom = bankAccountService.getBankAccountByIBAN(IBANFrom);
        }

        BankAccount bankAccountTo = null;
        if (IBANTo != null){
            bankAccountTo = bankAccountService.getBankAccountByIBAN(IBANTo);
        }

        List<Transaction> transactions = new ArrayList<>();

        if (!user.getTypes().contains(UserAccountType.ROLE_EMPLOYEE)){
            for (BankAccount bankAccount: user.getBankAccounts()
                 ) {
                transactions.addAll(getTransactionsByIBANFrom(bankAccount.getIBAN()));
                transactions.addAll(getTransactionsByIBANTo(bankAccount.getIBAN()));
            }
            // TODO: fix this so you can also apply the query for the customer transactions
            return transactions;
        }
        // TODO: make it so you can also filter between dates and amounts also make it so it filters transactions if userID is provided

        Transaction transaction = Transaction.builder().accountFrom(bankAccountFrom).accountTo(bankAccountTo).occuredAt(dateFrom).amount(amount).build();
        return transactionRepository.findAll(Example.of(transaction));
    }

    // TODO get rid of all these methods
    private List<Transaction> getTransactionsByUserId(Long id){
        UserAccount userAccount = userAccountService.getUserAccountById(id);
        return transactionRepository.findTransactionsByMadeBy(userAccount).orElse(
                null
        );
    }

    private List<Transaction> getTransactionsByIBANFrom(String IBAN){
        BankAccount bankAccount = bankAccountService.getBankAccountByIBAN(IBAN);
        return transactionRepository.findTransactionsByAccountFrom(bankAccount).orElse(null);
    }

    private List<Transaction> getTransactionsByIBANTo(String IBAN){
        BankAccount bankAccount = bankAccountService.getBankAccountByIBAN(IBAN);
        return transactionRepository.findTransactionsByAccountTo(bankAccount).orElseThrow(
                () -> new EntityNotFoundException("no transactions have been send to this IBAN " + IBAN)
        );
    }

    private List<Transaction> getTransactionsByDateFrom(Timestamp dateFrom){
        return transactionRepository.findTransactionsByOccuredAtAfter(dateFrom).orElseThrow(
                () -> new EntityNotFoundException("could not find transactions after timstamp "+ dateFrom)
        );
    }

    private List<Transaction> getTransactionsByDateTo(Timestamp dateTo){
        return transactionRepository.findTransactionsByOccuredAtBefore(dateTo).orElseThrow(
                () -> new EntityNotFoundException("could not find transactions before timestamp "+dateTo)
        );
    }
    private List<Transaction> getTransactionsByDateBetween(Timestamp dateFrom, Timestamp dateTo){
        return transactionRepository.findTransactionsByOccuredAtBetween(dateFrom, dateTo).orElseThrow(
                () -> new EntityNotFoundException("could not find transactions between timestamps "+ dateFrom+" "+dateTo)
        );
    }
    public Transaction makeTransaction(MakeTransactionDTO makeTransactionDTO, UserAccount user) throws Exception {
        //this method is such a mess, good luck reading this ♡♡ Cody.
        //I added comments to see if it helped with readability, but it really didn't f.

        List<BankAccount> bankAccountsOfUser = bankAccountService.getBankAccountByUserAccountId(user.getId());
        Transaction transaction = mapMakeTransactionDtoToTransaction(makeTransactionDTO);

        if (transaction.getAccountFrom().getStatus() == BankAccountStatus.INACTIVE || transaction.getAccountTo().getStatus() == BankAccountStatus.INACTIVE){
            throw new Exception("one of the bankaccounts are de-activated");
        }

        //this if checks if the employee is logged in and if yes the transaction goes through no matter what
        if (user.getTypes().contains(UserAccountType.ROLE_EMPLOYEE)){
            return finalizeTransaction(transaction, user);
        }

        //this if checks if a transaction is made between savings accounts.
        if (checkBankAccountType(transaction.getAccountFrom(), BankAccountType.SAVINGS) && checkBankAccountType(transaction.getAccountFrom(), BankAccountType.SAVINGS)){
            throw new Exception("cant make a transaction between savings accounts");
        }

        //the first if checks if the owner of the account is indeed making the transaction.
        if (bankAccountsOfUser.contains(transaction.getAccountFrom())){

            //this if checks if the account to is a savings account
            if (checkBankAccountType(transaction.accountFrom, BankAccountType.SAVINGS)) {

                // this if checks if the savings account is owned by the user
                if (checkIfBankAccountIsOwnedByUser(transaction.getAccountTo(), user)){
                    return finalizeTransaction(transaction, user);
                }
                else {
                    throw new Exception("Can't make a transaction to a savings account you don't own");
                }
            }
            // this if checks if the account is from a savings account.
            if (checkBankAccountType(transaction.accountTo, BankAccountType.SAVINGS)){

                //this if checks if the account to is owned by the user.
                if (checkIfBankAccountIsOwnedByUser(transaction.getAccountFrom(), user)){
                    return finalizeTransaction(transaction, user);
                }
                else {
                    throw new Exception("Can't make a transaction to a savings account you don't own");
                }
            }
            return finalizeTransaction(transaction, user);
        }
        else {
            throw new Exception("you don't own the bankaccount you are making the transaction with");
        }
    }

    private Transaction finalizeTransaction(Transaction transaction, UserAccount user) throws Exception {
        if (!user.getTypes().contains(UserAccountType.ROLE_EMPLOYEE)) {
            checkDayLimit(transaction, user);
            checkTransactionLimit(user);
            checkAbsoluteLimit(transaction);
        }
        return saveTransaction(transaction);
    }

    private Transaction saveTransaction(Transaction transaction) {
        var i = transactionRepository.save(transaction);
        bankAccountService.updateAmount(transaction.getAccountFrom().getIBAN(), -transaction.getAmount());
        bankAccountService.updateAmount(transaction.getAccountTo().getIBAN(), transaction.getAmount());
        return i;
    }

    private void checkAbsoluteLimit(Transaction transaction) throws Exception {
        if (transaction.getAccountFrom().getBalance() - transaction.getAmount() < transaction.getAccountFrom().getAbsoluteLimit()) {
            throw new Exception("with this transaction you will pass the absolute limit of your account");
        }
    }

    private void checkTransactionLimit(UserAccount user) throws Exception {
        if (user.getCurrentTransactionLimit() >= user.getTransactionLimit()){
            throw new Exception("you have passed your transaction limit");
        }
        else {
            user.setCurrentTransactionLimit(user.getCurrentTransactionLimit() + 1);
        }
    }

    private void checkDayLimit(Transaction transaction, UserAccount user) throws Exception {
        if (user.getCurrentDayLimit() >= user.getDayLimit()){
            throw new Exception("passed your day limit");
        }
        else {
            if (user.getCurrentDayLimit() + transaction.getAmount() >= user.getDayLimit()){
                throw new Exception ("you are passing your day limit lower the amount of the transaction to proceed");
            }
            else {
                user.setCurrentDayLimit(user.getCurrentDayLimit() + transaction.getAmount());
            }
        }
    }

    private Boolean checkBankAccountType(BankAccount bankAccount, BankAccountType bankAccountType){
        return bankAccount.getType().equals(bankAccountType);
    }

    private Boolean checkIfBankAccountIsOwnedByUser(BankAccount bankAccount, UserAccount userAccount){
        return bankAccount.getUserAccount().equals(userAccount);
    }

    private Transaction mapTransactionDtoToTransaction(TransactionDTO dto) {
        return new Transaction(dto.getAmount(), dto.getMadeBy(), dto.getAccountFrom(),dto.getAccountTo(), dto.getDescription(), dto.getOccuredAt());
    }
    private Transaction mapMakeTransactionDtoToTransaction(MakeTransactionDTO dto){
        Date date = new Date();
        return new Transaction(dto.getAmount(), userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName()), bankAccountService.getBankAccountByIBAN(dto.getAccountFrom()), bankAccountService.getBankAccountByIBAN(dto.getAccountTo()), dto.getDescription(), new Timestamp(date.getTime()));
    }

    public List<Transaction> getTransactionsById(Long userId) {
        //
        return null;
    }
}
