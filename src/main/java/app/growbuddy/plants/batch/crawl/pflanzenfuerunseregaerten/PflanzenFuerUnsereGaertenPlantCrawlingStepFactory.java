package app.growbuddy.plants.batch.crawl.pflanzenfuerunseregaerten;

import app.growbuddy.plants.batch.crawl.AbstractCrawlingStepFactory;
import app.growbuddy.plants.batch.crawl.SitemapEntry;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class PflanzenFuerUnsereGaertenPlantCrawlingStepFactory extends AbstractCrawlingStepFactory<ModifiablePflanzenFuerUnsereGaertenPlant> {

    public PflanzenFuerUnsereGaertenPlantCrawlingStepFactory(StepBuilderFactory stepBuilderFactory,
                                                             PflanzenFuerUnsereGaertenPlantRepository repository) {
        super(stepBuilderFactory, repository);
    }

    @Override
    protected String getSitemapFilterPattern() {
        return "http://www\\.pflanzen-fuer-unsere-gaerten\\.ch/de-ch/artikel/.*";
    }

    @Override
    protected ItemProcessor<SitemapEntry, ModifiablePflanzenFuerUnsereGaertenPlant> createCrawlingProcessor(CrudRepository<ModifiablePflanzenFuerUnsereGaertenPlant, String> repository) {
        return new PflanzenFuerUnsereGaertenPlantCrawlingProcessor(repository, true);
    }

    @Override
    protected String getSitemapUrl() {
        return "http://www.pflanzen-fuer-unsere-gaerten.ch/sitemap.xml";
    }

}
