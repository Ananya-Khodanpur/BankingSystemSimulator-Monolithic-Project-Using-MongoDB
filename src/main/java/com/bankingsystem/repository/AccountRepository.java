package com.bankingsystem.repository;

import com.bankingsystem.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account,String> {
    Account findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
