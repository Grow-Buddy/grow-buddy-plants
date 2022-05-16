package app.growbuddy.plants.batch;

import app.growbuddy.plants.PlantInfoRepository;
import app.growbuddy.plants.model.ModifiablePlantInfo;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClearPlantInfoRepositoryStepFactory extends AbstractBatchComponentFactory {

    private final StepBuilderFactory stepBuilderFactory;
    private final MongoTemplate mongoClient;

    public ClearPlantInfoRepositoryStepFactory(StepBuilderFactory stepBuilderFactory,
                                               MongoTemplate mongoClient) {

        this.stepBuilderFactory = stepBuilderFactory;
        this.mongoClient = mongoClient;
    }

    public Step createStep() {
        return this.stepBuilderFactory.get(getBatchComponentBaseName())
                .tasklet(new ClearPlantInfoRepositoryTasklet(mongoClient))
                .build();
    }

    private static class ClearPlantInfoRepositoryTasklet implements Tasklet {

        private final MongoTemplate mongoClient;

        public ClearPlantInfoRepositoryTasklet(MongoTemplate mongoClient) {
            this.mongoClient = mongoClient;
        }

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            mongoClient.dropCollection(ModifiablePlantInfo.class);

            return RepeatStatus.FINISHED;
        }
    }

}
