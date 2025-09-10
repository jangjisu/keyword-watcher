package com.app.keywordwatcher.config;

import com.app.keywordwatcher.scheduler.CrawlingOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final CrawlingOrchestrator crawlingOrchestrator;

    /**
     * 매일 오전 8시에 크롤링 작업 실행
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void scheduleDailyCrawling() {
        crawlingOrchestrator.performDailyCrawling();
    }
}
