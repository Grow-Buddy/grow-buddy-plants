package app.growbuddy.plants.batch.process.pflanzenfuerunseregaerten;

import app.growbuddy.plants.PlantInfoRepository;
import app.growbuddy.plants.batch.AbstractBatchComponentFactory;
import app.growbuddy.plants.batch.crawl.pflanzenfuerunseregaerten.PflanzenFuerUnsereGaertenPlant;
import app.growbuddy.plants.batch.crawl.pflanzenfuerunseregaerten.PflanzenFuerUnsereGaertenPlantRepository;
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
public class PflanzenFuerUnsereGaertenPlantProcessingStepFactory extends AbstractBatchComponentFactory {

    private final StepBuilderFactory stepBuilderFactory;
    private final PflanzenFuerUnsereGaertenPlantRepository pflanzenFuerUnsereGaertenPlantRepository;
    private final PlantInfoRepository plantInfoRepository;

    public PflanzenFuerUnsereGaertenPlantProcessingStepFactory(StepBuilderFactory stepBuilderFactory,
                                                               PlantInfoRepository plantInfoRepository,
                                                               PflanzenFuerUnsereGaertenPlantRepository pflanzenFuerUnsereGaertenPlantRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.plantInfoRepository = plantInfoRepository;
        this.pflanzenFuerUnsereGaertenPlantRepository = pflanzenFuerUnsereGaertenPlantRepository;
    }

    public Step createStep() {
        return this.stepBuilderFactory.get(getBatchComponentBaseName())
                .<PflanzenFuerUnsereGaertenPlant, ModifiablePlantInfo>chunk(10)
                .reader(createReader())
                .processor(createProcessor())
                .writer(createWriter())
                .build();
    }

    private ItemReader<PflanzenFuerUnsereGaertenPlant> createReader() {
        return new RepositoryItemReaderBuilder<PflanzenFuerUnsereGaertenPlant>()
                .name(getBatchComponentBaseName() + "Reader")
                .repository(pflanzenFuerUnsereGaertenPlantRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .methodName("findAll")
                .build();
    }

    private ItemProcessor<PflanzenFuerUnsereGaertenPlant, ModifiablePlantInfo> createProcessor() {
        return new PflanzenFuerUnsereGaertenPlantProcessingProcessor();
    }

    private ItemWriter<ModifiablePlantInfo> createWriter() {
        return new RepositoryItemWriterBuilder<ModifiablePlantInfo>()
                .repository(plantInfoRepository)
                .build();
    }

}
