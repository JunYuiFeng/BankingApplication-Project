package nl.inholland.bankingapplication.controllers;

import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import nl.inholland.bankingapplication.models.dto.MakeTransactionDTO;
import nl.inholland.bankingapplication.models.dto.TransactionResponseDTO;
import nl.inholland.bankingapplication.models.dto.WithdrawalAndDepositRequestDTO;
import nl.inholland.bankingapplication.services.TransactionService;
import nl.inholland.bankingapplication.services.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("Transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserAccountService userAccountService;

    public TransactionController(TransactionService transactionService, UserAccountService userAccountService) {
        this.transactionService = transactionService;
        this.userAccountService = userAccountService;
    }

    @GetMapping()

    public ResponseEntity GetAllTransactions(@RequestParam(required = false) Integer userId, @RequestParam(required = false) String IBANFrom ,@RequestParam(required = false) String IBANTo,@RequestParam(required = false) Timestamp dateFrom,@RequestParam(required = false) Timestamp dateTo ,@RequestParam(required = false) List<String> amount){
        try{
            return ResponseEntity.ok(transactionService.getAllTransactions(userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName()),userId, IBANFrom, IBANTo, dateFrom, dateTo, amount));
        }
        catch (ResponseStatusException e) {
            return handleException(e.getStatusCode().value(), e);
        }
	}

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> MakeTransaction(@RequestBody MakeTransactionDTO makeTransactionDTO) {
        try {
            return ResponseEntity.status(201).body(transactionService.makeTransaction(makeTransactionDTO, userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
        } catch (ResponseStatusException e) {
            return handleException(e.getStatusCode().value(), e);
        }
    }
    @PostMapping("Deposit")
    public ResponseEntity<TransactionResponseDTO> Deposit(@RequestBody WithdrawalAndDepositRequestDTO withdrawalAndDepositRequestDTO){
        try{
            return ResponseEntity.status(201).body(transactionService.makeDeposit(withdrawalAndDepositRequestDTO, userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
        } catch (ResponseStatusException e) {return handleException(e.getStatusCode().value(),e);}
    }
    @PostMapping("Withdrawal")
    public ResponseEntity<TransactionResponseDTO> Withdrawal(@RequestBody WithdrawalAndDepositRequestDTO withdrawalAndDepositRequestDTO){
        try{
            return ResponseEntity.status(201).body(transactionService.makeWithdrawal(withdrawalAndDepositRequestDTO, userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
        } catch (ResponseStatusException e) {return handleException(e.getStatusCode().value(),e);}
    }

    private ResponseEntity handleException(int status, Exception e) {
        ExceptionDTO dto = new ExceptionDTO(status, e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }
}
