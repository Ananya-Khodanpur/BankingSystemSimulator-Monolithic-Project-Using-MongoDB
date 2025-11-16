package com.bankingsystem.model.DTOs;

import jakarta.validation.constraints.NotBlank;

public class CreateAccountRequest {
    @NotBlank
    private String holderName;

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }
}
