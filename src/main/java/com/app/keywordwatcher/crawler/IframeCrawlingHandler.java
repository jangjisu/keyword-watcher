package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.util.CrawlingUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

@Slf4j
public class IframeCrawlingHandler extends CrawlingHandler {

    public IframeCrawlingHandler(CrawlingHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    protected Elements extractRows(Document doc) {
        Elements iframes = doc.select("iframe");

        if (iframes.isEmpty()) {
            return new Elements();
        }

        Elements allRows = new Elements();

        for (Element iframe : iframes) {
            Elements rows = processIframe(iframe);
            if (!rows.isEmpty()) {
                allRows.addAll(rows);
            }
        }

        return allRows;
    }

    private Elements processIframe(Element iframe) {
        String iframeUrl = iframe.absUrl("src");

        if (iframeUrl.isEmpty()) {
            return new Elements();
        }

        try {
            Document iframeDoc = CrawlingUtil.fetchDocument(iframeUrl);
            Elements rows = iframeDoc.select("table tbody tr");

            if (!rows.isEmpty()) {
                log.info("TableCrawlingHandler extract rows: Found {} rows in the document in iframe : {}", rows.size(), iframeUrl);
            }

            return rows;
        } catch (IOException e) {
            log.error("Failed to process iframe: {}, Error: {}", iframeUrl, e.getMessage());
            return new Elements();
        }
    }
}
