package com.transaction.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.transaction.api.dto.helper.OperationTypeDeserializer;

@JsonDeserialize(using = OperationTypeDeserializer.class)
public enum OperationType {
    CASH_PURCHASE(1),
    INSTALLMENT_PURCHASE(2),
    WITHDRAWAL(3),
    PAYMENT(4);
    public int operationTypeId;

    OperationType(int operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public int getOperationTypeId() {
        return operationTypeId;
    }
}