package com.team1.paymentsystem.states.converters;

import com.team1.paymentsystem.states.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusConverter implements AttributeConverter<Status, String> {

        @Override
        public String convertToDatabaseColumn(Status status) {
            if(status == null) {
                return null;
            }
            return status.getName();
        }

        @Override
        public Status convertToEntityAttribute(String status) {
            return new Status(status);
        }
}
