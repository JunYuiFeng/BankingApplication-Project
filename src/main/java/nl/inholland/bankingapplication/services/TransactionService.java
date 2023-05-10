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
        //TODO: which cant be done rn because there are no methods to access bank accounts by their IBAN - CODY
        //BankAccount bankAccount = bankAccountService.getBankAccountByIBAN(IBAN);
        //return transactionRepository.findTransactionsByAccountFrom(bankAccount).orElseThrow(
         //       () -> new EntityNotFoundException("no transactions have been made from this IBAN " + IBAN)
        //);
        return null;
    }

    public List<Transaction> getTransactionsByIBANTo(String IBAN){
      //  //TODO: which cant be done rn because there are no methods to access bank accounts by their IBAN - CODY
        //BankAccount bankAccount = bankAccountService.getBankAccountByIBAN(IBAN);
        //return transactionRepository.findTransactionsByAccountTo(bankAccount).orElseThrow(
          //      () -> new EntityNotFoundException("no transactions have been send to this IBAN " + IBAN)
        //);
    return null;
    }

    public List<Transaction> getTransactionsByDateFrom(Timestamp dateFrom){
        return transactionRepository.findTransactionsByOccuredAtAfter(dateFrom).orElseThrow(
                () -> new EntityNotFoundException("something went wrong")
        );
    }

    public List<Transaction> getTransactionsByDateTo(Timestamp dateTo){
        return transactionRepository.findTransactionsByOccuredAtBefore(dateTo).orElseThrow(
                () -> new EntityNotFoundException("something went wrong")
        );
    }
    public List<Transaction> getTransactionsByDateBetween(Timestamp dateFrom, Timestamp dateTo){
        return transactionRepository.findTransactionsByOccuredAtBetween(dateFrom, dateTo).orElseThrow(
                () -> new EntityNotFoundException("something went wrong")
        );
    }
    public Transaction makeTransaction(MakeTransactionDTO makeTransactionDTO){
        //Transaction t = mapMakeTransactionDtoToTransaction(makeTransactionDTO);
      //  return transactionRepository.save(t);
        return null;
    }
    private Transaction mapTransactionDtoToTransaction(TransactionDTO dto) {
        return new Transaction(dto.getAmount(), dto.getMadeBy(), dto.getAccountFrom(),dto.getAccountTo(), dto.getDescription(), dto.getOccuredAt());
    }
    //private Transaction mapMakeTransactionDtoToTransaction(MakeTransactionDTO dto){
        // TODO: For this to work I would need to know the bearer token in which is in the header which isn't implemented at the moment so this isn't able to be feature complete - Cody
        //Date date = new Date();
      //  return new Transaction(dto.getAmount(), null, bankAccountService.getBankAccountByIBAN(dto.getAccountFrom()), bankAccountService.getBankAccountByIBAN(dto.getAccountTo()), dto.getDescription(), new Timestamp(date.getTime()));
    //}
}
