package app.growbuddy.plants.batch.crawl;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.repository.CrudRepository;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractCrawlingProcessor<T> implements ItemProcessor<SitemapEntry, T> {

    private static final int DEFAULT_FETCH_TIMEOUT = 5000;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final CrudRepository<T, String> repository;
    private final boolean skipExisting;

    protected AbstractCrawlingProcessor(CrudRepository<T, String> repository,
                                        boolean skipExisting) {

        this.repository = repository;
        this.skipExisting = skipExisting;
    }

    @Override
    public T process(SitemapEntry sitemapEntry) {
        try {
            String url = sitemapEntry.getLoc();
            String id = extractIdFromUrl(url);
            if (StringUtils.isEmpty(id)) {
                logger.debug("skipping URL {} as the ID could not be extracted", url);

                return null;
            }

            if (alreadyCrawled(id) && skipExisting) {
                logger.debug("skipping already crawled ID {} (URL {})", id, url);

                return null;
            }

            return crawl(id, url);
        } catch (Exception e) {
            logger.warn("failed to crawl URL " + sitemapEntry.getLoc(), e);

            return null;
        }
    }

    private String extractIdFromUrl(String url) {
        Matcher matcher = getIdFromUrlExtractionPattern().matcher(url);
        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1);
    }

    protected abstract Pattern getIdFromUrlExtractionPattern();

    private boolean alreadyCrawled(String id) {
        return repository.findById(id).isPresent();
    }

    protected T crawl(String id, String url) throws IOException, InterruptedException {
        Document document = fetchDocument(url);
        if (document == null) {
            return null;
        }

        return extract(id, url, document);
    }

    private Document fetchDocument(String url) throws IOException {
        try {
            logger.debug("fetching URL {}", url);

            return Jsoup.connect(url)
                    .timeout(DEFAULT_FETCH_TIMEOUT)
                    .get();
        } catch (HttpStatusException e) {
            logger.warn("failed to crawl URL {} (HTTP code {})", url, e.getStatusCode());
        } catch (SocketTimeoutException e) {
            logger.warn("failed to crawl URL {} (timeout)", url);
        }

        return null;
    }

    protected abstract T extract(String id, String url, Document document) throws IOException;

}
