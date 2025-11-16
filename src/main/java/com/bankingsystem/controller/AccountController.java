package com.bankingsystem.controller;

import com.bankingsystem.model.DTOs.CreateAccountRequest;
import com.bankingsystem.model.DTOs.TransferRequest;
import com.bankingsystem.service.AccountService;
import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;


import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService svc;

    public AccountController(AccountService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest req) {
        Account created = svc.createAccount(req.getHolderName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        Account a = svc.getAccount(accountNumber);
        return ResponseEntity.ok(a);
    }

    @PutMapping("/{accountNumber}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable String accountNumber,
            @RequestBody Map<String, String> body) {

        String newName = body.get("holderName");
        String newStatus = body.get("status");

        Account updated = svc.updateAccount(accountNumber, newName, newStatus);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<?> deleteAccount(@PathVariable String accountNumber) {
        String msg = svc.deleteAccount(accountNumber);
        return ResponseEntity.ok(Map.of("message", msg));
    }


    @PutMapping("/{accountNumber}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable String accountNumber, @RequestBody Map<String, Double> body) {
        double amount = body.getOrDefault("amount", 0.0);
        Account a = svc.deposit(accountNumber, amount);
        return ResponseEntity.ok(a);
    }

    @PutMapping("/{accountNumber}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable String accountNumber, @RequestBody Map<String, Double> body) {
        double amount = body.getOrDefault("amount", 0.0);
        Account a = svc.withdraw(accountNumber, amount);
        return ResponseEntity.ok(a);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest req) {
        svc.transfer(req.getSourceAccount(), req.getDestinationAccount(), req.getAmount());
        return ResponseEntity.ok(Map.of("message", "Transfer successful"));
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String accountNumber) {
        return ResponseEntity.ok(svc.getTransactionsForAccount(accountNumber));
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(svc.getAllAccounts());
    }

}

