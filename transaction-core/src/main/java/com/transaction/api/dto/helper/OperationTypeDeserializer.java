package com.transaction.api.dto.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.transaction.api.dto.BadInputException;
import com.transaction.api.dto.OperationType;

import java.io.IOException;

public class OperationTypeDeserializer extends JsonDeserializer<OperationType> {
    @Override
    public OperationType deserialize(JsonParser p, DeserializationContext ctxt)  {
        try {
            int ordinal = p.getIntValue();
            OperationType[] values = OperationType.values();
            if (ordinal <= 0 || ordinal-1 >= values.length) {
                throw new BadInputException("Invalid value for OperationType Id:  " + ordinal);
            }
            return values[ordinal-1];
        }catch (IOException e){
            throw new BadInputException("Invalid type OperationType", e);
        }
    }
}