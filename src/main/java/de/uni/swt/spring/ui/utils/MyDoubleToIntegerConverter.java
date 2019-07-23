package de.uni.swt.spring.ui.utils;

import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.converter.Converter;

/**
 * Konverter für Double -> Integer implementiert das vaadin-Converter-Interface. Wird für
 * Client und Serverseitige Validierung verwendet.
 */

public class MyDoubleToIntegerConverter implements Converter<Double, Integer> {
    @Override
    public Result<Integer> convertToModel(Double fieldValue, ValueContext context) {
        try {
            return Result.ok(fieldValue.intValue());
        } catch (NumberFormatException e) {
            return Result.error("Please enter a number");
        }
    }
    @Override
    public Double convertToPresentation(Integer integer, ValueContext context) {
        return Double.valueOf(integer.toString());
    }
}