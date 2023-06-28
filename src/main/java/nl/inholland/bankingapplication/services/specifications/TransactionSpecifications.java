package nl.inholland.bankingapplication.services.specifications;

import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.services.BankAccountService;
import nl.inholland.bankingapplication.services.UserAccountService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class TransactionSpecifications {
    private final BankAccountService bankAccountService;

    public TransactionSpecifications(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public  Specification<Transaction> hasIBANFrom(String IBAN) {
        if (IBAN == null || IBAN.isEmpty())
            return null;
        return (transaction, cq, cb) -> cb.equal(transaction.get("accountFrom"), bankAccountService.getBankAccountByIBAN(IBAN));
    }
    public  Specification<Transaction> hasIBANTo(String IBAN) {
        if (IBAN == null || IBAN.isEmpty())
            return null;
        return (transaction, cq, cb) -> cb.equal(transaction.get("accountTo"), bankAccountService.getBankAccountByIBAN(IBAN));
    }
    public  Specification<Transaction> beforeDate(Timestamp date) {
        if (date == null)
            return null;
        return (transaction, cq, cb) -> cb.lessThan(transaction.get("occuredAt"), date);
    }
    public  Specification<Transaction> afterDate(Timestamp date) {
        if (date == null)
            return null;
        return (transaction, cq, cb) -> cb.greaterThan(transaction.get("occuredAt"), date);
    }
    public  Specification<Transaction> betweenDates(Timestamp dateFrom, Timestamp dateTo) {
        if (dateFrom == null || dateTo == null)
            return null;
        return (transaction, cq, cb) -> cb.between(transaction.get("occuredAt"), dateFrom, dateTo);
    }
    public  Specification<Transaction> lessThanAmount(Double amount) {
        if (amount == null)
            return null;
        return (transaction, cq, cb) -> cb.lessThan(transaction.get("amount"), amount);
    }
    public  Specification<Transaction> greaterThanAmount(Double amount) {
        if (amount == null)
            return null;
        return (transaction, cq, cb) -> cb.greaterThan(transaction.get("amount"), amount);
    }
    public  Specification<Transaction> betweenAmounts(Double amountFrom, Double amountTo) {
        if (amountFrom == null || amountTo == null)
            return null;
        return (transaction, cq, cb) -> cb.between(transaction.get("amount"), amountFrom, amountTo);
    }
    public Specification<Transaction> equalsAmount(Double amount) {
        if (amount == null)
            return null;
        return (transaction, cq, cb) -> cb.equal(transaction.get("amount"), amount);
    }
    public Specification<Transaction> hasUserAccountTo(UserAccount userAccount) {
        if (userAccount == null)
            return null;
        return (transaction, cq, cb) -> cb.equal(transaction.get("madeTo"), userAccount);
    }
    public Specification<Transaction> hasUserAccountFrom(UserAccount userAccount) {
        if (userAccount == null)
            return null;
        return (transaction, cq, cb) -> cb.equal(transaction.get("madeFrom"), userAccount);
    }
}
