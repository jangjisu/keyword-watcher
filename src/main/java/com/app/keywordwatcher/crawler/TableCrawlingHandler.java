package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.exception.CrawlingParseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public class TableCrawlingHandler extends CrawlingHandler {
    public TableCrawlingHandler(CrawlingHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    protected boolean supports(Document doc) {
        if (doc == null) {
            throw new CrawlingParseException("Document cannot be null");
        }
        return !doc.select("table tbody tr").isEmpty();
    }

    @Override
    protected List<Element> extractRows(Document doc) {
        return doc.select("table tbody tr");
    }
}
