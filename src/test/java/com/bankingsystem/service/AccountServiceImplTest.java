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
}
