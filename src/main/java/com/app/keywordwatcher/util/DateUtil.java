package com.app.keywordwatcher.util;

import com.app.keywordwatcher.exception.DateParseException;

import java.time.LocalDate;

public class DateUtil {
    private DateUtil() {
    }

    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return LocalDate.MIN;
        }

        String digits = dateString.replaceAll("\\D", "");
        try {
            return switch (digits.length()) {
                case 6 -> LocalDate.of(
                        2000 + Integer.parseInt(digits.substring(0, 2)),
                        Integer.parseInt(digits.substring(2, 4)),
                        Integer.parseInt(digits.substring(4, 6))
                );
                case 8 -> LocalDate.of(
                        Integer.parseInt(digits.substring(0, 4)),
                        Integer.parseInt(digits.substring(4, 6)),
                        Integer.parseInt(digits.substring(6, 8))
                );
                default -> throw new DateParseException("Unparseable date: " + dateString);
            };
        } catch (RuntimeException e) {
            return LocalDate.MIN;
        }
    }
}
