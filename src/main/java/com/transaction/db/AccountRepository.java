package com.transaction.db;

import com.transaction.api.dto.CreateAccountInput;
import com.transaction.api.dto.GetAccountInfo;
import com.transaction.api.dto.ImmutableGetAccountInfo;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

public class AccountRepository {
    public final Logger logger = LoggerFactory.getLogger(AccountRepository.class);
    private final DSLContext dslContext;

    public AccountRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public long createNewAccount(CreateAccountInput createAccountInput) {
        try{
            Table<?> accounts = DSL.table("accounts");
            Field<String> documentNumber = DSL.field("document_number", String.class);
            dslContext.insertInto(accounts, documentNumber).values(createAccountInput.documentNumber()).execute();
           return  dslContext.lastID().longValue();
        }catch (DataAccessException exception){
            logger.error("Error creating new account at database level", exception);
            throw exception;
        }
    }

    public Optional<GetAccountInfo> getAccountById(long accountId) {
        try{
            Table<?> accounts = DSL.table("accounts");
            Field<Long> idField = DSL.field("id", Long.class);
            Field<String> documentNumberField = DSL.field("document_number", String.class);
          return dslContext.select(idField, documentNumberField)
                    .from(accounts)
                    .where(idField.eq(accountId))
                    .fetchOptional().map(r-> ImmutableGetAccountInfo.builder().accountId(r.get(idField)).documentNumber(r.get(documentNumberField)).build());
            }
        catch (DataAccessException exception){
            logger.error("Error fetching account by ID at database level", exception);
            throw exception;
        }
    }
}
