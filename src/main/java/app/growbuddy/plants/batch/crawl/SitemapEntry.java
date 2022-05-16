package app.growbuddy.plants.batch.crawl;

import org.immutables.value.Value;

@Value.Immutable
public interface SitemapEntry {

    String getLoc();

}
