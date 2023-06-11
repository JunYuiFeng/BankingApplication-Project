package nl.inholland.bankingapplication.controllers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountResponseDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountUpdateDTO;
import nl.inholland.bankingapplication.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //an annotation provided by Spring MVC that combines @Controller and @ResponseBody.
// It is used to create a RESTful web service endpoint that directly returns data, rather than rendering a web page like traditional MVC controllers.
@RequestMapping("BankAccounts")
public class BankAccountController {
    private BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public ResponseEntity<List<BankAccountResponseDTO>> getAllBankAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllBankAccounts());
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("ExcludeUserAccount/{userId}")
    public ResponseEntity<List<BankAccountResponseDTO>> getAllBankAccountsExceptBankOwnAccount(@PathVariable Long userId) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsExceptOwnAccount(userId));
    }

    @GetMapping("{IBAN}")
    public ResponseEntity getBankAccountByIBAN(@PathVariable String IBAN) {
        return ResponseEntity.ok(bankAccountService.getBankAccountByIBAN(IBAN));
    }

    @PreAuthorize("principal.username == @userAccountService.getUserAccountById(#id).username")
    @GetMapping("UserAccount/{id}")
    public ResponseEntity<List<BankAccount>> getBankAccountByUserAccountId(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsByUserAccountId(id));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping(params = "status")
    public ResponseEntity<List<BankAccountResponseDTO>> getBankAccountByStatus(@RequestParam String status) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsByStatus(status));
    }
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_USER', 'ROLE_CUSTOMER')")
    @GetMapping(params = "name")
    public ResponseEntity<List<BankAccountResponseDTO>> getBankAccountByName(@RequestParam String name) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsByName(name));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping
    public ResponseEntity<BankAccountResponseDTO> addBankAccount(@RequestBody BankAccountRegisterDTO dto) {
        return ResponseEntity.status(201).body(bankAccountService.addBankAccount(dto));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PatchMapping("{IBAN}")
    public ResponseEntity<BankAccountResponseDTO> updateBankAccount(@RequestBody BankAccountUpdateDTO dto, @PathVariable String IBAN) {
        return ResponseEntity.status(201).body(bankAccountService.updateBankAccount(dto, IBAN));
    }
}
