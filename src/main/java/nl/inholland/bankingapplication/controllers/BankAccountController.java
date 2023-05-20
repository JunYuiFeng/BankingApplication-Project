package nl.inholland.bankingapplication.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccounResponseDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountStatusUpdateDTO;
import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import nl.inholland.bankingapplication.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //an annotation provided by Spring MVC that combines @Controller and @ResponseBody.
// It is used to create a RESTful web service endpoint that directly returns data, rather than rendering a web page like traditional MVC controllers.
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

    @GetMapping("{IBAN}")
    public ResponseEntity getBankAccountByIBAN(@PathVariable String IBAN) {
        try {
            return ResponseEntity.ok(bankAccountService.getBankAccountByIBAN(IBAN));
        } catch (EntityNotFoundException enfe) {
            return this.handleException(404, enfe);
        }
    }

    @GetMapping(params = "firstname")
    public ResponseEntity<BankAccount> getBankAccountByFirstname(@RequestParam String firstname) {
        try {
            return ResponseEntity.ok(bankAccountService.getBankAccountByFirstname(firstname));
        } catch (EntityNotFoundException enfe) {
            return this.handleException(404, enfe);
        }
    }

    @GetMapping("UserAccount/{id}")
    public ResponseEntity<BankAccount> getBankAccountByUserAccountId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bankAccountService.getBankAccountByUserAccountId(id));
        } catch (EntityNotFoundException enfe) {
            return this.handleException(404, enfe);
        }
    }

    @PostMapping
    public ResponseEntity<BankAccounResponseDTO> addBankAccount(@RequestBody BankAccountRegisterDTO dto) {
        try {
            return ResponseEntity.status(201).body(bankAccountService.addBankAccount(dto));
        } catch (Exception e) {
            return this.handleException(400, e);
        }
    }

    @PatchMapping("/{IBAN}/statusupdate")
    public ResponseEntity<BankAccount> updateBankAccountStatus(@RequestBody BankAccountStatusUpdateDTO dto, @PathVariable String IBAN) {
        try {
            return ResponseEntity.status(200).body(bankAccountService.updateBankAccountStatus(dto, IBAN));
        } catch (Exception e) {
            return this.handleException(400, e);
        }
    }

    private ResponseEntity handleException(int status, Exception e) {
        ExceptionDTO dto = new ExceptionDTO(e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }
}
