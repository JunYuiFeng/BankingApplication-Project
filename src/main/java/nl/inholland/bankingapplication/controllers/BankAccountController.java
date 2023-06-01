package nl.inholland.bankingapplication.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccounResponseDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountUpdateDTO;
import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import nl.inholland.bankingapplication.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //an annotation provided by Spring MVC that combines @Controller and @ResponseBody.
// It is used to create a RESTful web service endpoint that directly returns data, rather than rendering a web page like traditional MVC controllers.
@CrossOrigin
@RequestMapping("BankAccounts")
public class BankAccountController {
    private BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> getAllBankAccounts() {
        try {
            List<BankAccount> bankAccounts = bankAccountService.getAllBankAccounts();
            if (bankAccounts.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(bankAccounts);
        } catch (Exception e) {
            return this.handleException(500, e);
        }
    }

    @GetMapping("ExceptBank")
    public ResponseEntity<List<BankAccount>> getAllBankAccountsExceptBankOwnAccount() {
        try {
            List<BankAccount> bankAccounts = bankAccountService.getAllBankAccountsExceptBankOwnAccount();
            if (bankAccounts.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(bankAccounts);
        } catch (Exception e) {
            return this.handleException(500, e);
        }
    }

    @GetMapping("{IBAN}")
    public ResponseEntity getBankAccountByIBAN(@PathVariable String IBAN) {
        try {
            return ResponseEntity.ok(bankAccountService.getBankAccountByIBAN(IBAN));
        } catch (EntityNotFoundException enfe) {
            return this.handleException(404, enfe);
        }
    }

    @GetMapping("UserAccount/{id}")
    public ResponseEntity<List<BankAccount>> getBankAccountByUserAccountId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bankAccountService.getBankAccountByUserAccountId(id));
        } catch (EntityNotFoundException enfe) {
            return this.handleException(404, enfe);
        }
    }

    @GetMapping(params = "status")
    public ResponseEntity<List<BankAccount>> getBankAccountByStatus(@RequestParam String status) {
        try {
            return ResponseEntity.ok(bankAccountService.getBankAccountByStatus(status));
        } catch (EntityNotFoundException enfe) {
            return this.handleException(404, enfe);
        }
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<BankAccount>> getBankAccountName(@RequestParam String name) {
        try {
            return ResponseEntity.ok(bankAccountService.getBankAccountByName(name));
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

    @PatchMapping("/{IBAN}")
    public ResponseEntity<BankAccount> updateBankAccount(@RequestBody BankAccountUpdateDTO dto, @PathVariable String IBAN) {
        try {
            return ResponseEntity.status(200).body(bankAccountService.updateBankAccount(dto, IBAN));
        } catch (Exception e) {
            return this.handleException(400, e);
        }
    }

    private ResponseEntity handleException(int status, Exception e) {
        ExceptionDTO dto = new ExceptionDTO(e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }
}
