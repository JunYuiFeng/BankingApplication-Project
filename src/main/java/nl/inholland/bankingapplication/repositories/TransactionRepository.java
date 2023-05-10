package nl.inholland.bankingapplication.repositories;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    Optional<List<Transaction>> findTransactionsByMadeBy(UserAccount userAccount);
    Optional<List<Transaction>> findTransactionsByAccountFrom(BankAccount bankAccount);
    Optional<List<Transaction>> findTransactionsByAccountTo(BankAccount bankAccount);
    Optional<List<Transaction>> findTransactionsByOccuredAtAfter(Timestamp dateFrom);
    Optional<List<Transaction>> findTransactionsByOccuredAtBefore(Timestamp dateTo);
    Optional<List<Transaction>> findTransactionsByOccuredAtBetween(Timestamp dateFrom, Timestamp dateFor);
}