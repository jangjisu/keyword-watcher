package com.app.keywordwatcher.util;

import java.time.LocalDate;
import java.util.function.Supplier;

public class DateUtil {
    private DateUtil() {
    }

    public static LocalDate parseDate(String dateString, Supplier<LocalDate> onError) {
        if (dateString == null || dateString.isEmpty()) {
            return onError.get();
        }

        String digits = dateString.replaceAll("[^0-9]", "");
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
                default -> onError.get();
            };
        } catch (Exception e) {
            return onError.get();
        }
    }

    public static LocalDate parseDate(String dateString) {
        return parseDate(dateString, () -> LocalDate.MIN);
    }
}
