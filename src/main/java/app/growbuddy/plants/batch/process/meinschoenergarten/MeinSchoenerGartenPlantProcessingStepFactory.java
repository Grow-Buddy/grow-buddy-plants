package app.growbuddy.plants.batch.process.meinschoenergarten;

import app.growbuddy.plants.PlantInfoRepository;
import app.growbuddy.plants.batch.AbstractBatchComponentFactory;
import app.growbuddy.plants.batch.crawl.meinschoenergarten.MeinSchoenerGartenPlant;
import app.growbuddy.plants.batch.crawl.meinschoenergarten.MeinSchoenerGartenPlantRepository;
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
public class MeinSchoenerGartenPlantProcessingStepFactory extends AbstractBatchComponentFactory {

    private final StepBuilderFactory stepBuilderFactory;
    private final MeinSchoenerGartenPlantRepository meinSchoenerGartenPlantRepository;
    private final PlantInfoRepository plantInfoRepository;

    public MeinSchoenerGartenPlantProcessingStepFactory(StepBuilderFactory stepBuilderFactory,
                                                        PlantInfoRepository plantInfoRepository,
                                                        MeinSchoenerGartenPlantRepository meinSchoenerGartenPlantRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.plantInfoRepository = plantInfoRepository;
        this.meinSchoenerGartenPlantRepository = meinSchoenerGartenPlantRepository;
    }

    public Step createStep() {
        return this.stepBuilderFactory.get(getBatchComponentBaseName())
                .<MeinSchoenerGartenPlant, ModifiablePlantInfo>chunk(10)
                .reader(createReader())
                .processor(createProcessor())
                .writer(createWriter())
                .build();
    }

    private ItemReader<MeinSchoenerGartenPlant> createReader() {
        return new RepositoryItemReaderBuilder<MeinSchoenerGartenPlant>()
                .name(getBatchComponentBaseName() + "Reader")
                .repository(meinSchoenerGartenPlantRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .methodName("findAll")
                .build();
    }

    private ItemProcessor<MeinSchoenerGartenPlant, ModifiablePlantInfo> createProcessor() {
        return new MeinSchoenerGartenPlantProcessingProcessor();
    }

    private ItemWriter<ModifiablePlantInfo> createWriter() {
        return new RepositoryItemWriterBuilder<ModifiablePlantInfo>()
                .repository(plantInfoRepository)
                .build();
    }

}
