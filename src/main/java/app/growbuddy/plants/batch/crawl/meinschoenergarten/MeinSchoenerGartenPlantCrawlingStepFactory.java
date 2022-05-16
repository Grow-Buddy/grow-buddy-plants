package app.growbuddy.plants.batch.crawl.meinschoenergarten;

import app.growbuddy.plants.batch.crawl.AbstractCrawlingStepFactory;
import app.growbuddy.plants.batch.crawl.SitemapEntry;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class MeinSchoenerGartenPlantCrawlingStepFactory extends AbstractCrawlingStepFactory<ModifiableMeinSchoenerGartenPlant> {

    public MeinSchoenerGartenPlantCrawlingStepFactory(StepBuilderFactory stepBuilderFactory,
                                                      MeinSchoenerGartenPlantRepository repository) {
        super(stepBuilderFactory, repository);
    }

    @Override
    protected String getSitemapFilterPattern() {
        return ".*";
    }

    @Override
    protected ItemProcessor<SitemapEntry, ModifiableMeinSchoenerGartenPlant> createCrawlingProcessor(CrudRepository<ModifiableMeinSchoenerGartenPlant, String> repository) {
        return new MeinSchoenerGartenPlantCrawlingProcessor(repository, true);
    }

    @Override
    protected String getSitemapUrl() {
        return "https://www.mein-schoener-garten.de/pflanzenseiten/sitemap.xml";
    }

}
