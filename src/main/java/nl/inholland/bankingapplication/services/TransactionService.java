package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.MakeTransactionDTO;
import nl.inholland.bankingapplication.models.dto.TransactionDTO;
import nl.inholland.bankingapplication.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByUserId(Long id){
        UserAccount userAccount = userAccountService.getUserAccountById(id);
        return transactionRepository.findTransactionsByMadeBy(userAccount).orElseThrow(
                () -> new EntityNotFoundException("no transactions have been made on this account" + id)
        );
    }

    public List<Transaction> getTransactionsByIBANFrom(String IBAN){
        BankAccount bankAccount = bankAccountService.getBankAccountByIBAN(IBAN);
        return transactionRepository.findTransactionsByAccountFrom(bankAccount).orElseThrow(
                () -> new EntityNotFoundException("no transactions have been made from this IBAN" + IBAN)
        );
    }

    public List<Transaction> getTransactionsByIBANTo(String IBAN){
        BankAccount bankAccount = bankAccountService.getBankAccountByIBAN(IBAN);
        return transactionRepository.findTransactionsByAccountTo(bankAccount).orElseThrow(
                () -> new EntityNotFoundException("no transactions have been send to this IBAN" + IBAN)
        );
    }

    public List<Transaction> getTransactionsByDateFrom(Timestamp dateFrom){
        return transactionRepository.findTransactionsByOccuredAtAfter(dateFrom).orElseThrow(
                () -> new EntityNotFoundException("something went wrong")
        );
    }

    public List<Transaction> getTransactionsByDateTo(Timestamp dateTo){
        return transactionRepository.findTransactionsByOccuredAtAfter(dateTo).orElseThrow(
                () -> new EntityNotFoundException("something went wrong")
        );
    }
    public List<Transaction> getTransactionsByDateBetween(Timestamp dateFrom, Timestamp dateTo){
        return transactionRepository.findTransactionsByOccuredAtBetween(dateFrom, dateTo).orElseThrow(
                () -> new EntityNotFoundException("something went wrong")
        );
    }
    public Transaction makeTransaction(MakeTransactionDTO makeTransactionDTO){
        Transaction t = mapMakeTransactionDtoToTransaction(makeTransactionDTO);
        return transactionRepository.save(t);
    }

    private Transaction mapTransactionDtoToTransaction(TransactionDTO dto) {
        Transaction transaction = new Transaction(dto.getAmount(), dto.getMadeBy(), dto.getAccountFrom(),dto.getAccountTo(), dto.getDescription(), dto.getOccuredAt());
        return transaction;
    }
    private Transaction mapMakeTransactionDtoToTransaction(MakeTransactionDTO dto){
        // TODO: For this to work I would need to know the bearer token in which is in the header which isn't implemented at the moment so this isn't able to be feature complete - Cody
        Date date = new Date();
        Transaction transaction = new Transaction(dto.getAmount(), null, bankAccountService.getBankAccountByIBAN(dto.getAccountFrom()), bankAccountService.getBankAccountByIBAN(dto.getAccountTo()), dto.getDescription(), new Timestamp(date.getTime()));
        return transaction;
    }
}
