package com.bankingsystem.service;

import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;

import java.util.List;

public interface AccountService
{
    Account createAccount(String holderName);
    Account getAccount(String accountNumber);
    Account updateAccount(String accountNumber, String holderName,String newStatus);
    String deleteAccount(String accountNumber);
    Account deposit(String accountNumber, double amount);
    Account withdraw(String accountNumber, double amount);
    List<Account> getAllAccounts();

    void transfer(String srcAccount, String destAccount, double amount);
    List<Transaction> getTransactionsForAccount(String accountNumber);
}
