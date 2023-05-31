package nl.inholland.bankingapplication.controllers;

import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import nl.inholland.bankingapplication.models.dto.MakeTransactionDTO;
import nl.inholland.bankingapplication.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@CrossOrigin

@RequestMapping("Transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping()
    public ResponseEntity GetAllTransactions( @RequestParam(required = false) String IBANFrom ,@RequestParam(required = false) String IBANTo,@RequestParam(required = false) Timestamp dateFrom,@RequestParam(required = false) Timestamp dateTo){
        return ResponseEntity.ok(transactionService.getAllTransactions((UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), IBANFrom,IBANTo,dateFrom,dateTo));

//    @GetMapping(path = "/Transactions/UserId")
//    public ResponseEntity GetTransactionByUserId(@RequestParam Long userId){
//        try{
//            return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
//        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
//    }
//    @GetMapping(path = "/Transactions/IBANFrom")
//    public ResponseEntity GetTransactionsByIBANFrom(@RequestParam String IBAN){
//        try{
//            return ResponseEntity.ok(transactionService.getTransactionsByIBANFrom(IBAN));
//        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
//    }
//    @GetMapping(path = "/Transactions/IBANTo")
//    public ResponseEntity GetTransactionsByIBANTo(@RequestParam String IBAN){
//        try{
//            return ResponseEntity.ok(transactionService.getTransactionsByIBANTo(IBAN));
//        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
//    }
//    @GetMapping(path = "/Transactions/DateFrom")
//    public ResponseEntity GetTransactionsByDateFrom(@RequestParam Timestamp dateFrom){
//        try{
//            return ResponseEntity.ok(transactionService.getTransactionsByDateFrom(dateFrom));
//        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
//    }
//    @GetMapping(path = "/Transactions/DateTo")
//    public ResponseEntity GetTransactionsByDateTo(@RequestParam Timestamp dateTo){
//        try{
//            return ResponseEntity.ok(transactionService.getTransactionsByDateTo(dateTo));
//        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
//    }
//    @GetMapping(path = "/Transactions/DateBetween")
//    public ResponseEntity GetTransactionsByDateFromAndDateTo(@RequestParam List<Timestamp> timestamps){
//        try{
//            return ResponseEntity.ok(transactionService.getTransactionsByDateBetween(timestamps.get(0), timestamps.get(1)));
//        } catch (EntityNotFoundException e) {return this.handleException(404, e);}
//    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_CUSTOMER')")
    @PostMapping
    public ResponseEntity<Transaction> MakeTransaction(@RequestBody MakeTransactionDTO makeTransactionDTO) {
        try {
            return ResponseEntity.status(201).body(transactionService.makeTransaction(makeTransactionDTO, (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        } catch (Exception e) {
            return handleException(403, e);
        }
    }

    private ResponseEntity handleException(int status, Exception e) {
        ExceptionDTO dto = new ExceptionDTO(e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }
}
