package com.team1.paymentsystem.states.converters;

import com.team1.paymentsystem.states.PaymentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, String> {
    @Override
    public String convertToDatabaseColumn(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            return null;
        }
        return paymentStatus.getName();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(String paymentStatus) {
        return new PaymentStatus(paymentStatus);
    }
}
