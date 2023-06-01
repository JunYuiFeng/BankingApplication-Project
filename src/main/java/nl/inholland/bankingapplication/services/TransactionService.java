package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.MakeTransactionDTO;
import nl.inholland.bankingapplication.models.dto.TransactionDTO;
import nl.inholland.bankingapplication.repositories.TransactionRepository;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
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

    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByUserId(Long id){
        UserAccount userAccount = userAccountService.getUserAccountById(id);
        return transactionRepository.findTransactionsByMadeBy(userAccount).orElseThrow(
                () -> new EntityNotFoundException("no transactions have been made on this account " + id)
        );
    }

    public List<Transaction> getTransactionsByIBANFrom(String IBAN){
        BankAccount bankAccount = bankAccountService.getBankAccountByIBAN(IBAN);
        return transactionRepository.findTransactionsByAccountFrom(bankAccount).orElseThrow(
                () -> new EntityNotFoundException("no transactions have been made from this IBAN " + IBAN)
        );
    }

    public List<Transaction> getTransactionsByIBANTo(String IBAN){
        BankAccount bankAccount = bankAccountService.getBankAccountByIBAN(IBAN);
        return transactionRepository.findTransactionsByAccountTo(bankAccount).orElseThrow(
                () -> new EntityNotFoundException("no transactions have been send to this IBAN " + IBAN)
        );
    }

    public List<Transaction> getTransactionsByDateFrom(Timestamp dateFrom){
        return transactionRepository.findTransactionsByOccuredAtAfter(dateFrom).orElseThrow(
                () -> new EntityNotFoundException("could not find transactions after timstamp "+ dateFrom)
        );
    }

    public List<Transaction> getTransactionsByDateTo(Timestamp dateTo){
        return transactionRepository.findTransactionsByOccuredAtBefore(dateTo).orElseThrow(
                () -> new EntityNotFoundException("could not find transactions before timestamp "+dateTo)
        );
    }
    public List<Transaction> getTransactionsByDateBetween(Timestamp dateFrom, Timestamp dateTo){
        return transactionRepository.findTransactionsByOccuredAtBetween(dateFrom, dateTo).orElseThrow(
                () -> new EntityNotFoundException("could not find transactions between timestamps "+ dateFrom+" "+dateTo)
        );
    }
    public Transaction makeTransaction(MakeTransactionDTO makeTransactionDTO){
        Transaction t = mapMakeTransactionDtoToTransaction(makeTransactionDTO);
        var i = transactionRepository.save(t);
        bankAccountService.updateAmount(makeTransactionDTO.getAccountFrom(), -makeTransactionDTO.getAmount());
        bankAccountService.updateAmount(makeTransactionDTO.getAccountTo(), makeTransactionDTO.getAmount());
        return i;
    }
    private Transaction mapTransactionDtoToTransaction(TransactionDTO dto) {
        return new Transaction(dto.getAmount(), dto.getMadeBy(), dto.getAccountFrom(),dto.getAccountTo(), dto.getDescription(), dto.getOccuredAt());
    }
    private Transaction mapMakeTransactionDtoToTransaction(MakeTransactionDTO dto){
        Date date = new Date();
        return new Transaction(dto.getAmount(), null, bankAccountService.getBankAccountByIBAN(dto.getAccountFrom()), bankAccountService.getBankAccountByIBAN(dto.getAccountTo()), dto.getDescription(), new Timestamp(date.getTime()));
    }

    public List<Transaction> getTransactionsById(Long userId) {
        //
        return null;
    }
}
