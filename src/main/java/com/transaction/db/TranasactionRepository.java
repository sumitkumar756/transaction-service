package com.transaction.db;

import com.transaction.api.dto.CreateTransactionInput;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranasactionRepository {
    public final Logger logger = LoggerFactory.getLogger(TranasactionRepository.class);
    private final DSLContext dslContext;

    public TranasactionRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public long createNewTransaction(CreateTransactionInput createTransactionInput) {
        try{
            var transactions = org.jooq.impl.DSL.table("transactions");
            var accountIdField = org.jooq.impl.DSL.field("account_id", Long.class);
            var operationTypeIdField = org.jooq.impl.DSL.field("operationtype_id", Integer.class);
            var amountField = org.jooq.impl.DSL.field("amount", java.math.BigDecimal.class);
            dslContext.insertInto(transactions, accountIdField, operationTypeIdField, amountField)
                    .values(createTransactionInput.accountId(),
                            createTransactionInput.operationType().getOperationTypeId(),
                            createTransactionInput.amount())
                    .execute();
            return dslContext.lastID().longValue();
        }catch (org.jooq.exception.DataAccessException exception){
            logger.error("Error creating new transaction at database level", exception);
            throw exception;
        }
    }
}
