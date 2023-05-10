package nl.inholland.bankingapplication.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import nl.inholland.bankingapplication.models.dto.MakeTransactionDTO;
import nl.inholland.bankingapplication.repositories.TransactionRepository;
import nl.inholland.bankingapplication.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("Transactions")
public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity GetAllTransactions(){
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
    @GetMapping
    public ResponseEntity GetTransactionByUserId(@RequestParam(required = false) Long userId){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }
    @GetMapping
    public ResponseEntity GetTransactionsByIBANFrom(@RequestParam(required = false) String IBAN){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByIBANFrom(IBAN));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }
    @GetMapping
    public ResponseEntity GetTransactionsByIBANTo(@RequestParam(required = false) String IBAN){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByIBANTo(IBAN));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }
    @GetMapping
    public ResponseEntity GetTransactionsByDateFrom(@RequestParam(required = false) Timestamp dateFrom){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByDateFrom(dateFrom));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }
    @GetMapping
    public ResponseEntity GetTransactionsByDateTo(@RequestParam(required = false) Timestamp dateTo){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByDateTo(dateTo));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }
    @GetMapping
    public ResponseEntity GetTransactionsByDateFromAndDateTo(@RequestParam(required = false) Timestamp dateFrom, @RequestParam(required = false) Timestamp dateTo){
        try{
            return ResponseEntity.ok(transactionService.getTransactionsByDateBetween(dateFrom, dateTo));
        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
    }

    @PostMapping
    public ResponseEntity<Transaction> MakeTransaction(@RequestBody MakeTransactionDTO makeTransactionDTO) {
        try {
            return ResponseEntity.status(201).body(transactionService.makeTransaction(makeTransactionDTO));
        } catch (Exception e) {
            return null;
        }
    }

    private ResponseEntity handleException(int status, EntityNotFoundException e) {
        ExceptionDTO dto = new ExceptionDTO(e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }
}
