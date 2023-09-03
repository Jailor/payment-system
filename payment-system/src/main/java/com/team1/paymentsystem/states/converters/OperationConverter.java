package com.team1.paymentsystem.states.converters;

import com.team1.paymentsystem.states.Operation;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OperationConverter implements AttributeConverter<Operation, String> {
    @Override
    public String convertToDatabaseColumn(Operation operation) {
        if (operation == null) {
            return null;
        }
        return operation.getName();
    }

    @Override
    public Operation convertToEntityAttribute(String operation) {
        return new Operation(operation);
    }
}