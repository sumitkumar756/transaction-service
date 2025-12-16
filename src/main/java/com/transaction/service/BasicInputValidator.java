package com.transaction.service;

import com.transaction.api.dto.BadInputException;
import com.transaction.api.dto.CreateAccountInput;

public class BasicInputValidator {

    protected void validateCreateAccountInput(CreateAccountInput createAccountInput) {
        if(createAccountInput.documentNumber() == null || createAccountInput.documentNumber().isEmpty()){
            throw new BadInputException("Document number cannot be null or empty");
        }
        if (createAccountInput.documentNumber().length()>19) {
            throw new BadInputException("Document number can not be more than 19 characters");
        }
    }

    protected long getValidatedAccountId(String accountId) {
        try {
            long id = Long.parseLong(accountId);
            if (id <= 0) {
                throw new BadInputException("Account ID must be a positive number");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new BadInputException("Account ID must be a valid number");
        }
    }
}
