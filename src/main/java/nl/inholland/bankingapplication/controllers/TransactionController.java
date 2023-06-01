package nl.inholland.bankingapplication.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import nl.inholland.bankingapplication.models.dto.MakeTransactionDTO;
import nl.inholland.bankingapplication.repositories.TransactionRepository;
import nl.inholland.bankingapplication.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("Transactions")
public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping()
    public ResponseEntity GetAllTransactions(){
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
    @GetMapping(value = "{userId}")
    public ResponseEntity GetTransactionByUserId(@PathVariable Long userId, Authentication authentication) {
        try {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"))) {
                return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
            } else {
                //auth gets the name of the user who is doing the transaction and then get ehe name of the one
                if (transactionService.getTransactionsByUserId(userId).isEmpty()) {
                    return ResponseEntity.status(404).body(new Exception("No transactions have been made on this account"));
                }
                if (authentication.getName().equals(transactionService.getTransactionsById(userId).get(0).getMadeBy().getUsername())) {
                    return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
                }else{
                    return ResponseEntity.status(401).body(new Exception("You are not authorized to view this transaction"));
                }

            }
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }

    }
    @GetMapping(value = "IBANFrom/{IBAN}")
    public ResponseEntity GetTransactionsByIBANFrom(@PathVariable String IBAN){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByIBANFrom(IBAN));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }
    @GetMapping(path = "/Transactions/IBANTo")
    public ResponseEntity GetTransactionsByIBANTo(@RequestParam String IBAN){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByIBANTo(IBAN));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }
    @GetMapping(path = "/Transactions/DateFrom")
    public ResponseEntity GetTransactionsByDateFrom(@RequestParam Timestamp dateFrom){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByDateFrom(dateFrom));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }
    @GetMapping(path = "/Transactions/DateTo")
    public ResponseEntity GetTransactionsByDateTo(@RequestParam Timestamp dateTo){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByDateTo(dateTo));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }
    @GetMapping(path = "/Transactions/DateBetween")
    public ResponseEntity GetTransactionsByDateFromAndDateTo(@RequestParam List<Timestamp> timestamps){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByDateBetween(timestamps.get(0), timestamps.get(1)));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }

    @PostMapping
    public ResponseEntity<Transaction> MakeTransaction(@RequestBody MakeTransactionDTO makeTransactionDTO) {
        try {
            return ResponseEntity.status(201).body(transactionService.makeTransaction(makeTransactionDTO));
        } catch (EntityNotFoundException e) {
            return this.handleException(403, e);
        }
    }

    private ResponseEntity handleException(int status, EntityNotFoundException e) {
        ExceptionDTO dto = new ExceptionDTO(e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }
}
