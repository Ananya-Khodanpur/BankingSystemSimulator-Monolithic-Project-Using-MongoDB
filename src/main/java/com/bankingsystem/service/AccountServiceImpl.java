package com.bankingsystem.service;

import com.bankingsystem.exception.AccountNotFoundException;
import com.bankingsystem.exception.InsufficientBalanceException;
import com.bankingsystem.exception.InvalidAmountException;
import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.repository.AccountRepository;
import com.bankingsystem.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
    }

    private String generateAccountNumber(String holderName) {
        String initials = holderName.replaceAll("[^A-Za-z ]", "").trim()
                .split(" ").length > 0 ?
                Arrays.stream(holderName.trim().split(" "))
                        .filter(s -> !s.isBlank())
                          .map(s -> s.substring(0,1).toUpperCase())
                        .reduce("", String::concat)
                : holderName.substring(0, Math.min(3, holderName.length())).toUpperCase();

        int num = new Random().nextInt(9000) + 1000; // 1000-9999
        String acct = (initials.length() >= 3 ? initials.substring(0,3) : initials) + num;
        // ensure uniqueness
        while (accountRepository.existsByAccountNumber(acct)) {
            num = new Random().nextInt(9000) + 1000;
            acct = (initials.length() >= 3 ? initials.substring(0,3) : initials) + num;
        }
        return acct;
    }

    private String generateTransactionId() {
        String prefix = "TXN-";
        String date = java.time.LocalDate.now().toString().replace("-", "");
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + date + "-" + uuid;
    }

    @Override
    public Account createAccount(String holderName){
        if(holderName == null || holderName.trim().isEmpty()){
            throw new IllegalArgumentException("Holder name must not be empty");
        }

        Account account = new Account();
        account.setHolderName(holderName);
        account.setBalance(0.0);
        account.setAccountNumber(generateAccountNumber(holderName));
        account.setCreatedAt(new Date());
        account.setStatus("ACTIVE");
        account = accountRepository.save(account);
        logger.info("Account Created with account number" +account.getAccountNumber());
        return account;
    }

    @Override
    public Account getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if(account== null) throw new AccountNotFoundException("Account " + accountNumber + " not found");
        return  account;
    }

    @Override
    public Account updateAccount(String accountNumber, String newHolderName, String newStatus) {

        Account account = getAccount(accountNumber); // reuses existing method

        if (newHolderName != null && !newHolderName.trim().isEmpty()) {
            account.setHolderName(newHolderName);
        }

        if (newStatus != null && !newStatus.trim().isEmpty()) {
            if (!newStatus.equalsIgnoreCase("ACTIVE") && !newStatus.equalsIgnoreCase("INACTIVE")) {
                throw new IllegalArgumentException("Status must be ACTIVE or INACTIVE");
            }
            account.setStatus(newStatus.toUpperCase());
        }

        accountRepository.save(account);

        logger.info("Updated account {} - newName={}, newStatus={}",
                accountNumber, newHolderName, newStatus);

        return account;
    }


    @Override
    public String deleteAccount(String accountNumber) {
        Account account = getAccount(accountNumber);

        account.setStatus("INACTIVE");
        accountRepository.save(account);

        logger.info("Account {} marked as INACTIVE", accountNumber);

        return "Account " + accountNumber + " marked as INACTIVE";
    }


    @Override
    public Account deposit(String accountNumber, double amount) {
        if(amount <= 0) throw new InvalidAmountException("Deposit amount must be positive");
        Account acc = getAccount(accountNumber);
        acc.setBalance(acc.getBalance() + amount);
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setAmount(amount);
        transaction.setType("DEPOSIT");
        transaction.setStatus("SUCCESS");
        transaction.setTimestamp(new Date());
        transaction.setDestinationAccount(accountNumber);
        transaction = transactionRepository.save(transaction);

        acc.addTransactionId(transaction.getTransactionId());
        accountRepository.save(acc);
        logger.info("Deposited {} to {}", amount , accountNumber);
        return  acc;
    }

    @Override
    public Account withdraw(String accountNumber, double amount) {
        if(amount <= 0) throw new InvalidAmountException("Withdraw amount must be positive");
        Account acc = getAccount(accountNumber);
        if(acc.getBalance() < amount){
            Transaction transaction = new Transaction();
            transaction.setTransactionId(generateTransactionId());
            transaction.setAmount(amount);
            transaction.setType("WITHDRAW");
            transaction.setStatus("FAILED!");
            transaction.setTimestamp(new Date());
            transaction.setSourceAccount(accountNumber);
            transactionRepository.save(transaction);
            throw new InvalidAmountException("Insufficient Balance");
        }
        acc.setBalance(acc.getBalance() - amount);
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setAmount(amount);
        transaction.setType("WITHDRAW");
        transaction.setStatus("SUCCESS");
        transaction.setTimestamp(new Date());
        transaction.setSourceAccount(accountNumber);
        transactionRepository.save(transaction);

        acc.addTransactionId(transaction.getTransactionId());
        accountRepository.save(acc);
        logger.info("Withdrawed {} from {}", amount, accountNumber);
        return  acc;
    }

    @Override
    @Transactional
    public void transfer(String sourceAccount, String destinationAccount, double amount) {

        if (sourceAccount.equals(destinationAccount)) {
            throw new IllegalArgumentException("Source and Destination account must not be the same");
        }

        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive");
        }

        Account src = getAccount(sourceAccount);
        Account dest = getAccount(destinationAccount);

        if (src.getBalance() < amount) {

            Transaction txn = new Transaction();
            txn.setTransactionId(generateTransactionId());
            txn.setAmount(amount);
            txn.setType("TRANSFER");
            txn.setStatus("FAILED");
            txn.setTimestamp(new Date());
            txn.setSourceAccount(sourceAccount);
            txn.setDestinationAccount(destinationAccount);
            transactionRepository.save(txn);

            throw new InsufficientBalanceException("Insufficient balance in source account");
        }

        // Deduct & Add Balances
        src.setBalance(src.getBalance() - amount);
        dest.setBalance(dest.getBalance() + amount);

        // Create SUCCESS transaction
        Transaction txn = new Transaction();
        txn.setTransactionId(generateTransactionId());
        txn.setAmount(amount);
        txn.setType("TRANSFER");
        txn.setStatus("SUCCESS");
        txn.setTimestamp(new Date());
        txn.setSourceAccount(sourceAccount);
        txn.setDestinationAccount(destinationAccount);
        transactionRepository.save(txn);

        src.addTransactionId(txn.getTransactionId());
        dest.addTransactionId(txn.getTransactionId());

        accountRepository.save(src);
        accountRepository.save(dest);

        logger.info("Transferred {} from {} to {}", amount, sourceAccount, destinationAccount);
    }



    @Override
    public List<Transaction> getTransactionsForAccount(String accountNumber){
        return transactionRepository.findBySourceAccountOrDestinationAccount(accountNumber, accountNumber);
    }

    @Override
    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

}
