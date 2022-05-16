package app.growbuddy.plants.batch.crawl;

import org.springframework.batch.item.ItemProcessor;

import java.util.regex.Pattern;

public class SitemapFilterProcessor implements ItemProcessor<SitemapEntry, SitemapEntry> {

    private final Pattern pattern;

    public SitemapFilterProcessor(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public SitemapEntry process(SitemapEntry item) {
        if (pattern.matcher(item.getLoc()).matches()) {
            return item;
        }

        return null;
    }

}
