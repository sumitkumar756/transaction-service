package com.transaction.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.math.BigInteger;

@Value.Immutable
@JsonSerialize(as = ImmutableCreateAccountOutput.class)
@JsonDeserialize(as = ImmutableCreateAccountOutput.class)
public interface CreateAccountOutput {
    long accountId();
}
