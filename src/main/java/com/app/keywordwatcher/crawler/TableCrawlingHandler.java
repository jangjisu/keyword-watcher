package com.app.keywordwatcher.crawler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Slf4j
public class TableCrawlingHandler extends CrawlingHandler {
    public TableCrawlingHandler(CrawlingHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    protected Elements extractRows(Document doc) {

        Elements tableElements = doc.select("table tbody tr");

        if (!tableElements.isEmpty()) {
            log.info("TableCrawlingHandler extract rows: Found {} rows in the document", tableElements.size());
        }

        return tableElements;
    }
}