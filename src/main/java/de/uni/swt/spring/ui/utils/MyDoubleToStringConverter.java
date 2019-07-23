package de.uni.swt.spring.ui.utils;

import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.converter.Converter;

/**
 * Konverter für Double -> String implementiert das vaadin-Converter-Interface. Wird für
 * Client und Serverseitige Validierung verwendet.
 */
public class MyDoubleToStringConverter implements Converter<Double, String> {
    @Override
    public Result<String> convertToModel(Double fieldValue, ValueContext context) {
        try {
            return Result.ok(Integer.valueOf(fieldValue.intValue()).toString());
        } catch (NumberFormatException e) {
            return Result.error("Please enter a number");
        }
    }
    @Override
    public Double convertToPresentation(String string, ValueContext context) {
        return Double.valueOf(string);
    }
}