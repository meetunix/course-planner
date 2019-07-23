package de.uni.swt.spring.ui.utils;

import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.converter.Converter;

/**
 * Konverter für String -> Integer implementiert das vaadin-Converter-Interface. Wird für
 * Client und Serverseitige Validierung verwendet.
 */
public class MyStringToIntegerConverter implements Converter<String, Integer> {
    @Override
    public Result<Integer> convertToModel(String fieldValue, ValueContext context) {
        try {
            return Result.ok(Integer.valueOf(fieldValue));
        } catch (NumberFormatException e) {
            return Result.error("Please enter a number");
        }
    }
    @Override
    public String convertToPresentation(Integer integer, ValueContext context) {
        return integer.toString();
    }
}