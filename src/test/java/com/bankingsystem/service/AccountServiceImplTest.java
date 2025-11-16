package com.bankingsystem.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.bankingsystem.model.Account;
import com.bankingsystem.repository.AccountRepository;
import com.bankingsystem.repository.TransactionRepository;
import com.bankingsystem.exception.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountServiceImplTest {
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AccountServiceImpl svc;

    @BeforeEach
    void setup() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        svc = new AccountServiceImpl(accountRepository, transactionRepository);
    }

    @Test
    void createAccount_success() {
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        Account saved = new Account();
        saved.setAccountNumber("ANN1234");
        saved.setHolderName("Anna");
        saved.setBalance(0.0);
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        Account result = svc.createAccount("Anna");
        assertNotNull(result);
        assertEquals("Anna", result.getHolderName());
    }

    @Test
    void withdraw_insufficientBalance() {
        Account a = new Account();
        a.setAccountNumber("ANN1234");
        a.setBalance(50.0);
        when(accountRepository.findByAccountNumber("ANN1234")).thenReturn(a);

        assertThrows(InsufficientBalanceException.class, () -> svc.withdraw("ANN1234", 100.0));
    }

    @Test
    void getAccount_success() {
        Account a = new Account();
        a.setAccountNumber("ANN1234");
        when(accountRepository.findByAccountNumber("ANN1234")).thenReturn(a);

        Account result = svc.getAccount("ANN1234");

        assertNotNull(result);
        assertEquals("ANN1234", result.getAccountNumber());
    }

    @Test
    void getAccount_notFound() {
        when(accountRepository.findByAccountNumber("XYZ9999")).thenReturn(null);

        assertThrows(AccountNotFoundException.class,
                () -> svc.getAccount("XYZ9999"));
    }

    @Test
    void deposit_success() {
        Account a = new Account();
        a.setAccountNumber("ANN1234");
        a.setBalance(100.0);

        when(accountRepository.findByAccountNumber("ANN1234")).thenReturn(a);
        when(transactionRepository.save(any())).thenReturn(null); // We don't care about result
        when(accountRepository.save(any())).thenReturn(a);

        Account result = svc.deposit("ANN1234", 50);

        assertEquals(150.0, result.getBalance());
    }

    @Test
    void deposit_invalidAmount() {
        assertThrows(InvalidAmountException.class,
                () -> svc.deposit("ANN1234", -10));
    }

    @Test
    void updateAccount_success() {

        Account original = new Account();
        original.setAccountNumber("ANN1234");
        original.setHolderName("Anna");
        original.setStatus("ACTIVE");

        when(accountRepository.findByAccountNumber("ANN1234")).thenReturn(original);
        when(accountRepository.save(any())).thenReturn(original);

        Account updated = svc.updateAccount("ANN1234", "Ananya", "INACTIVE");

        assertEquals("Ananya", updated.getHolderName());
        assertEquals("INACTIVE", updated.getStatus());
    }

    @Test
    void transfer_success() {
        Account src = new Account();
        src.setAccountNumber("SRC123");
        src.setBalance(500);

        Account dest = new Account();
        dest.setAccountNumber("DEST123");
        dest.setBalance(200);

        when(accountRepository.findByAccountNumber("SRC123")).thenReturn(src);
        when(accountRepository.findByAccountNumber("DEST123")).thenReturn(dest);

        when(transactionRepository.save(any())).thenReturn(null);
        when(accountRepository.save(any())).thenReturn(null);

        svc.transfer("SRC123", "DEST123", 100);

        assertEquals(400, src.getBalance());
        assertEquals(300, dest.getBalance());
    }

    @Test
    void transfer_insufficientBalance() {
        Account src = new Account();
        src.setAccountNumber("SRC123");
        src.setBalance(50);

        Account dest = new Account();
        dest.setAccountNumber("DEST123");
        dest.setBalance(200);

        when(accountRepository.findByAccountNumber("SRC123")).thenReturn(src);
        when(accountRepository.findByAccountNumber("DEST123")).thenReturn(dest);

        when(transactionRepository.save(any())).thenReturn(null);

        assertThrows(InsufficientBalanceException.class,
                () -> svc.transfer("SRC123", "DEST123", 200));
    }

    @Test
    void transfer_invalidAmount() {
        assertThrows(InvalidAmountException.class,
                () -> svc.transfer("SRC123", "DEST123", -10));
    }

    @Test
    void transfer_sameAccount() {
        assertThrows(IllegalArgumentException.class,
                () -> svc.transfer("ACC123", "ACC123", 50));
    }


}
