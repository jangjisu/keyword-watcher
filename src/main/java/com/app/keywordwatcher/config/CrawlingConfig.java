package com.app.keywordwatcher.config;

import com.app.keywordwatcher.crawler.CrawlingHandler;
import com.app.keywordwatcher.crawler.IframeCrawlingHandler;
import com.app.keywordwatcher.crawler.TableCrawlingHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Configuration
public class CrawlingConfig {

    @Component
    @ConfigurationProperties(prefix = "crawling.connection")
    @Getter
    @Setter
    public static class ConnectionProperties {
        @Value("${crawling.connection.user-agent}")
        private String userAgent;
        @Value("${crawling.connection.timeout:10s}")
        private Duration timeout;
        @Value("${crawling.connection.max-body-size:10485760}")
        private int maxBodySize;

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }

        public void setMaxBodySize(int maxBodySize) {
            this.maxBodySize = maxBodySize;
        }
    }

    @Bean
    public CrawlingHandler crawlingHandlerChain() {
        CrawlingHandler iframeHandler = new IframeCrawlingHandler(null);
        return new TableCrawlingHandler(iframeHandler);
    }
}
