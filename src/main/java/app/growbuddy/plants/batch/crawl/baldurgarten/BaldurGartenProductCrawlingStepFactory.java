package app.growbuddy.plants.batch.crawl.baldurgarten;

import app.growbuddy.plants.batch.crawl.AbstractCrawlingStepFactory;
import app.growbuddy.plants.batch.crawl.SitemapEntry;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class BaldurGartenProductCrawlingStepFactory extends AbstractCrawlingStepFactory<ModifiableBaldurGartenProduct> {

    public BaldurGartenProductCrawlingStepFactory(StepBuilderFactory stepBuilderFactory,
                                                  BaldurGartenProductRepository repository) {
        super(stepBuilderFactory, repository);
    }

    @Override
    protected String getSitemapFilterPattern() {
        return "https://www.baldur-garten.ch/produkt/.*";
    }

    @Override
    protected ItemProcessor<SitemapEntry, ModifiableBaldurGartenProduct> createCrawlingProcessor(CrudRepository<ModifiableBaldurGartenProduct, String> repository) {
        return new BaldurGartenProductCrawlingProcessor(repository, true);
    }

    @Override
    protected String getSitemapUrl() {
        return "https://www.baldur-garten.ch/sitemap.xml";
    }

}
