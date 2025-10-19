package com.gtm.gtm.common.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

@Converter(autoApply = false)
public class JsonbConverter implements AttributeConverter<Map<String, Object>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> TYPE = new TypeReference<>(){};
    @Override public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try { return attribute == null ? "{}" : MAPPER.writeValueAsString(attribute); }
        catch (Exception e) { throw new IllegalStateException("JSON write error", e); }
    }
    @Override public Map<String, Object> convertToEntityAttribute(String dbData) {
        try { return dbData == null || dbData.isBlank() ? Map.of() : MAPPER.readValue(dbData, TYPE); }
        catch (Exception e) { throw new IllegalStateException("JSON read error", e); }
    }
}
