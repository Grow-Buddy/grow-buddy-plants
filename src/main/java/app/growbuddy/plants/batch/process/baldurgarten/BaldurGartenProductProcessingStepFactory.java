package app.growbuddy.plants.batch.process.baldurgarten;

import app.growbuddy.plants.PlantInfoRepository;
import app.growbuddy.plants.batch.AbstractBatchComponentFactory;
import app.growbuddy.plants.batch.crawl.baldurgarten.BaldurGartenProduct;
import app.growbuddy.plants.batch.crawl.baldurgarten.BaldurGartenProductRepository;
import app.growbuddy.plants.model.ModifiablePlantInfo;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BaldurGartenProductProcessingStepFactory extends AbstractBatchComponentFactory {

    private final StepBuilderFactory stepBuilderFactory;
    private final BaldurGartenProductRepository baldurGartenProductRepository;
    private final PlantInfoRepository plantInfoRepository;

    public BaldurGartenProductProcessingStepFactory(StepBuilderFactory stepBuilderFactory,
                                                    PlantInfoRepository plantInfoRepository,
                                                    BaldurGartenProductRepository baldurGartenProductRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.plantInfoRepository = plantInfoRepository;
        this.baldurGartenProductRepository = baldurGartenProductRepository;
    }

    public Step createStep() {
        return this.stepBuilderFactory.get(getBatchComponentBaseName())
                .<BaldurGartenProduct, ModifiablePlantInfo>chunk(10)
                .reader(createReader())
                .processor(createProcessor())
                .writer(createWriter())
                .build();
    }

    private ItemReader<BaldurGartenProduct> createReader() {
        return new RepositoryItemReaderBuilder<BaldurGartenProduct>()
                .name(getBatchComponentBaseName() + "Reader")
                .repository(baldurGartenProductRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .methodName("findAll")
                .build();
    }

    private ItemProcessor<BaldurGartenProduct, ModifiablePlantInfo> createProcessor() {
        return new BaldurGartenProductProcessingProcessor();
    }

    private ItemWriter<ModifiablePlantInfo> createWriter() {
        return new RepositoryItemWriterBuilder<ModifiablePlantInfo>()
                .repository(plantInfoRepository)
                .build();
    }

}
