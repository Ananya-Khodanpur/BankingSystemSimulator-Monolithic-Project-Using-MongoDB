package com.bankingsystem.model.DTOs;

import com.bankingsystem.model.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class TransferRequest {

    @NotBlank
    private String sourceAccount;
    @NotBlank
    private String destinationAccount;
    @Positive
    private double amount;

    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
