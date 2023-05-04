package nl.inholland.bankingapplication.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountDTO;
import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import nl.inholland.bankingapplication.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("BankAccounts")
public class BankAccountController {
    private BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public ResponseEntity getAllBankAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllBankAccounts());
    }

    @GetMapping("{id}")
    public ResponseEntity getBankAccountById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bankAccountService.getBankAccountById(id));
        } catch (EntityNotFoundException enfe) {
            return this.handleException(404, enfe);
        }
    }

    @PostMapping
    public ResponseEntity<BankAccount> addBankAccount(@RequestBody BankAccountDTO bankAccountDTO) {
        try {
            return ResponseEntity.status(201).body(bankAccountService.addBankAccount(bankAccountDTO));
        } catch (Exception e) {
            return this.handleException(400, e);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<BankAccount> deactivateBankAccount(@PathVariable Long id) {
        try {
            return ResponseEntity.status(200).body(bankAccountService.deactivateBankAccount(id));
        } catch (Exception e) {
            return this.handleException(400, e);
        }
    }

    private ResponseEntity handleException(int status, Exception e) {
        ExceptionDTO dto = new ExceptionDTO(e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }
}
