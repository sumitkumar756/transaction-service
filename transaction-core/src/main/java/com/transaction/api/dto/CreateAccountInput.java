package com.transaction.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCreateAccountInput.class)
@JsonSerialize(as = ImmutableCreateAccountInput.class)
public interface CreateAccountInput {
     @JsonProperty("document_number")
     String documentNumber();
}
