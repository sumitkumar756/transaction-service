package com.transaction.service;

import com.transaction.api.dto.CreateAccountInput;
import com.transaction.api.dto.CreateTransactionInput;
import com.transaction.api.dto.GetAccountInfo;
import com.transaction.db.AccountRepository;
import com.transaction.db.TranasactionRepository;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class TransactionService {
    public final Logger logger = LoggerFactory.getLogger(TransactionService.class);
      private AccountRepository accountRepository;
      private TranasactionRepository tranasactionRepository;

      public TransactionService(DSLContext dslContext) {
            this.accountRepository = new AccountRepository(dslContext);
            this.tranasactionRepository = new TranasactionRepository(dslContext);
      }

    public long createNewAccount(CreateAccountInput createAccountInput) {
           return accountRepository.createNewAccount(createAccountInput);
    }

    public Optional<GetAccountInfo> getAccountById(long accountId) {
        return accountRepository.getAccountById(accountId);
    }

    public long createNewTransaction(CreateTransactionInput createTransactionInput) {
        return tranasactionRepository.createNewTransaction(createTransactionInput);
    }
}
