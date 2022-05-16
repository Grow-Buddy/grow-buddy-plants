package app.growbuddy.plants.batch;

import app.growbuddy.plants.batch.crawl.baldurgarten.BaldurGartenProductCrawlingStepFactory;
import app.growbuddy.plants.batch.crawl.meinschoenergarten.MeinSchoenerGartenPlantCrawlingStepFactory;
import app.growbuddy.plants.batch.crawl.pflanzenfuerunseregaerten.PflanzenFuerUnsereGaertenPlantCrawlingStepFactory;
import app.growbuddy.plants.batch.process.baldurgarten.BaldurGartenProductProcessingStepFactory;
import app.growbuddy.plants.batch.process.meinschoenergarten.MeinSchoenerGartenPlantProcessingStepFactory;
import app.growbuddy.plants.batch.process.pflanzenfuerunseregaerten.PflanzenFuerUnsereGaertenPlantProcessingStepFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PlantInfoBatchJobFactory extends AbstractBatchComponentFactory {

    private final JobBuilderFactory jobBuilderFactory;
    private final BaldurGartenProductCrawlingStepFactory baldurGartenProductCrawlingStepFactory;
    private final MeinSchoenerGartenPlantCrawlingStepFactory meinSchoenerGartenPlantCrawlingStepFactory;
    private final PflanzenFuerUnsereGaertenPlantCrawlingStepFactory pflanzenFuerUnsereGaertenPlantCrawlingStepFactory;
    private final ClearPlantInfoRepositoryStepFactory clearPlantInfoRepositoryStepFactory;
    private final BaldurGartenProductProcessingStepFactory baldurGartenProductProcessingStepFactory;
    private final MeinSchoenerGartenPlantProcessingStepFactory meinSchoenerGartenPlantProcessingStepFactory;
    private final PflanzenFuerUnsereGaertenPlantProcessingStepFactory pflanzenFuerUnsereGaertenPlantProcessingStepFactory;
    private final IndexPlantInfoRepositoryStepFactory indexPlantInfoRepositoryStepFactory;

    public PlantInfoBatchJobFactory(JobBuilderFactory jobBuilderFactory,
                                    BaldurGartenProductCrawlingStepFactory baldurGartenProductCrawlingStepFactory,
                                    MeinSchoenerGartenPlantCrawlingStepFactory meinSchoenerGartenPlantCrawlingStepFactory,
                                    PflanzenFuerUnsereGaertenPlantCrawlingStepFactory pflanzenFuerUnsereGaertenPlantCrawlingStepFactory,
                                    ClearPlantInfoRepositoryStepFactory clearPlantInfoRepositoryStepFactory,
                                    BaldurGartenProductProcessingStepFactory baldurGartenProductProcessingStepFactory,
                                    MeinSchoenerGartenPlantProcessingStepFactory meinSchoenerGartenPlantProcessingStepFactory,
                                    PflanzenFuerUnsereGaertenPlantProcessingStepFactory pflanzenFuerUnsereGaertenPlantProcessingStepFactory,
                                    IndexPlantInfoRepositoryStepFactory indexPlantInfoRepositoryStepFactory) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.baldurGartenProductCrawlingStepFactory = baldurGartenProductCrawlingStepFactory;
        this.meinSchoenerGartenPlantCrawlingStepFactory = meinSchoenerGartenPlantCrawlingStepFactory;
        this.pflanzenFuerUnsereGaertenPlantCrawlingStepFactory = pflanzenFuerUnsereGaertenPlantCrawlingStepFactory;
        this.clearPlantInfoRepositoryStepFactory = clearPlantInfoRepositoryStepFactory;
        this.baldurGartenProductProcessingStepFactory = baldurGartenProductProcessingStepFactory;
        this.meinSchoenerGartenPlantProcessingStepFactory = meinSchoenerGartenPlantProcessingStepFactory;
        this.pflanzenFuerUnsereGaertenPlantProcessingStepFactory = pflanzenFuerUnsereGaertenPlantProcessingStepFactory;
        this.indexPlantInfoRepositoryStepFactory = indexPlantInfoRepositoryStepFactory;
    }

    @Bean
    public Job createCrawlingJob() throws IOException {
        return this.jobBuilderFactory
                .get("CrawlingJob")
                .start(baldurGartenProductCrawlingStepFactory.createStep())
                .next(meinSchoenerGartenPlantCrawlingStepFactory.createStep())
                .next(pflanzenFuerUnsereGaertenPlantCrawlingStepFactory.createStep())
                .build();
    }

    @Bean
    public Job createProcessingJob() {
        return this.jobBuilderFactory
                .get("ProcessingJob")
                .start(clearPlantInfoRepositoryStepFactory.createStep())
                .next(baldurGartenProductProcessingStepFactory.createStep())
                .next(meinSchoenerGartenPlantProcessingStepFactory.createStep())
                .next(pflanzenFuerUnsereGaertenPlantProcessingStepFactory.createStep())
                .next(indexPlantInfoRepositoryStepFactory.createStep())
                .build();
    }

}
