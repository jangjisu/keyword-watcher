package com.app.keywordwatcher.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
public class DateUtil {
    private DateUtil() {
    }

    public static Optional<LocalDate> parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return Optional.empty();
        }

        String digits = dateString.replaceAll("\\D", "");
        try {
            return switch (digits.length()) {
                case 6 -> Optional.of(LocalDate.of(
                        2000 + Integer.parseInt(digits.substring(0, 2)),
                        Integer.parseInt(digits.substring(2, 4)),
                        Integer.parseInt(digits.substring(4, 6))
                ));
                case 8 -> Optional.of(LocalDate.of(
                        Integer.parseInt(digits.substring(0, 4)),
                        Integer.parseInt(digits.substring(4, 6)),
                        Integer.parseInt(digits.substring(6, 8))
                ));
                default -> Optional.empty();
            };
        } catch (RuntimeException e) {
            log.warn("Failed to parse date from string: {}, Error: {}", dateString, e.getMessage());
            return Optional.empty();
        }
    }
}
