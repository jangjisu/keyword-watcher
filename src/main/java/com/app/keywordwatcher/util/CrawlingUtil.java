package com.app.keywordwatcher.util;

import com.app.keywordwatcher.config.CrawlingConfig;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 프로덕션 환경에서 안전한 웹 크롤링을 위한 유틸리티 클래스
 */
@Component
public class CrawlingUtil {

    private static CrawlingConfig.ConnectionProperties connectionProperties;

    @Autowired
    public CrawlingUtil(CrawlingConfig.ConnectionProperties connectionProperties) {
        CrawlingUtil.connectionProperties = connectionProperties;
    }

    public static Connection createSecureConnection(String url) {
        return Jsoup.connect(url)
                .userAgent(connectionProperties.getUserAgent())
                .timeout((int) connectionProperties.getTimeout().toMillis())
                .maxBodySize(connectionProperties.getMaxBodySize());
    }

    public static Document getDocument(String url) throws IOException {
        validateUrl(url);

        try {
            return createSecureConnection(url).get();
        } catch (IOException e) {
            throw new IOException("Failed to retrieve document from URL: " + url, e);
        }
    }

    private static void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        String trimmedUrl = url.trim();
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with http:// or https://. Got: " + trimmedUrl);
        }
    }
}
