package com.team1.paymentsystem.states.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.sql.Timestamp;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime locDateTime) {
        return locDateTime == null ? null : locDateTime.toString();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String s) {
        return LocalDateTime.parse(s);
    }
}