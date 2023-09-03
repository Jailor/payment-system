package com.team1.paymentsystem.states.converters;

import com.team1.paymentsystem.states.ProfileType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ProfileTypeConverter implements AttributeConverter<ProfileType, String> {

    @Override
    public String convertToDatabaseColumn(ProfileType profileType) {
        return profileType.getName();
    }

    @Override
    public ProfileType convertToEntityAttribute(String status) {
        return new ProfileType(status);
    }
}