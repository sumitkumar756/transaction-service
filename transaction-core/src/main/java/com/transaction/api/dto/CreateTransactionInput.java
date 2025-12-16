package com.transaction.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonDeserialize(as = ImmutableCreateTransactionInput.class)
@JsonSerialize(as = ImmutableCreateTransactionInput.class)
public interface CreateTransactionInput {
   @JsonProperty("account_id")
     long accountId();
   @JsonProperty("operation_type_id")
     OperationType operationType();
    @JsonProperty("amount")
     BigDecimal amount();
}
