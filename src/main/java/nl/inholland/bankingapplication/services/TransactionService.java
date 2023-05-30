package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.MakeTransactionDTO;
import nl.inholland.bankingapplication.models.dto.TransactionDTO;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.repositories.TransactionRepository;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<Transaction> getAllTransactions(UserAccount user, String IBANFrom, String IBANTo, Timestamp dateFrom, Timestamp dateTo) {
        if (!user.getTypes().contains(UserAccountType.ROLE_EMPLOYEE)){
            List<Transaction> transactions = new ArrayList<>();
            for (BankAccount bankAccount: user.getBankAccounts()
                 ) {
                transactions.addAll(getTransactionsByIBANFrom(bankAccount.getIBAN()));
                transactions.addAll(getTransactionsByIBANTo(bankAccount.getIBAN()));
            }
            return transactions;
        }
        // TODO: add the employee thing where they query each option given and check for them might do this via stream. Cody.
        return null;
    }

    private List<Transaction> getTransactionsByUserId(Long id){
        UserAccount userAccount = userAccountService.getUserAccountById(id);
        return transactionRepository.findTransactionsByMadeBy(userAccount).orElseThrow(
                () -> new EntityNotFoundException("no transactions have been made on this account " + id)
        );
    }

    private List<Transaction> getTransactionsByIBANFrom(String IBAN){
        BankAccount bankAccount = bankAccountService.getBankAccountByIBAN(IBAN);
        return transactionRepository.findTransactionsByAccountFrom(bankAccount).orElseThrow(
                () -> new EntityNotFoundException("no transactions have been made from this IBAN " + IBAN)
        );
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
        //this method is such a mess good luck reading this xx Cody.
        List<BankAccount> bankAccountsOfUser = bankAccountService.getBankAccountByUserAccountId(user.getId());
        Transaction transaction = mapMakeTransactionDtoToTransaction(makeTransactionDTO);
        if (user.getTypes().contains(UserAccountType.ROLE_EMPLOYEE)){
            return finalizeTransaction(transaction, user);
        }
        if (bankAccountsOfUser.contains(transaction.getAccountFrom())){

            if (transaction.getAccountTo().getType().equals(BankAccountType.SAVINGS)) {
                if (transaction.getAccountTo().getUserAccount().equals(user)){
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
        checkDayLimit(transaction, user);
        checkTransactionLimit(user);
        checkAbsoluteLimit(transaction);
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

    private Transaction mapTransactionDtoToTransaction(TransactionDTO dto) {
        return new Transaction(dto.getAmount(), dto.getMadeBy(), dto.getAccountFrom(),dto.getAccountTo(), dto.getDescription(), dto.getOccuredAt());
    }
    private Transaction mapMakeTransactionDtoToTransaction(MakeTransactionDTO dto){
        Date date = new Date();
        return new Transaction(dto.getAmount(), (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), bankAccountService.getBankAccountByIBAN(dto.getAccountFrom()), bankAccountService.getBankAccountByIBAN(dto.getAccountTo()), dto.getDescription(), new Timestamp(date.getTime()));
    }
}
