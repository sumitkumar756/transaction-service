package com.transaction.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGetAccountInfo.class)
@JsonSerialize(as = ImmutableGetAccountInfo.class)
public interface GetAccountInfo {
        @JsonProperty("account_id")
          long accountId();
        @JsonProperty("document_number")
         String documentNumber();
}
