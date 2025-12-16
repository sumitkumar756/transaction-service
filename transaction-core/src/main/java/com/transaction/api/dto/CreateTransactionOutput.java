package com.transaction.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCreateTransactionOutput.class)
@JsonSerialize(as = ImmutableCreateTransactionOutput.class)
public interface CreateTransactionOutput {
    @JsonProperty("transaction_id")
    long transactionId();
}
