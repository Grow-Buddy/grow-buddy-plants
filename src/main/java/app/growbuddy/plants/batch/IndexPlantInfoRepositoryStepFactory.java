package app.growbuddy.plants.batch;

import app.growbuddy.plants.model.ModifiablePlantInfo;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.stereotype.Service;

@Service
public class IndexPlantInfoRepositoryStepFactory extends AbstractBatchComponentFactory {

    private final StepBuilderFactory stepBuilderFactory;
    private final MongoTemplate mongoTemplate;

    public IndexPlantInfoRepositoryStepFactory(StepBuilderFactory stepBuilderFactory,
                                               MongoTemplate mongoTemplate) {

        this.stepBuilderFactory = stepBuilderFactory;
        this.mongoTemplate = mongoTemplate;
    }

    public Step createStep() {
        return this.stepBuilderFactory.get(getBatchComponentBaseName())
                .tasklet(new IndexPlantInfoRepositoryTasklet(mongoTemplate))
                .build();
    }

    private static class IndexPlantInfoRepositoryTasklet implements Tasklet {

        private final MongoTemplate mongoTemplate;

        public IndexPlantInfoRepositoryTasklet(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate
                    .getConverter()
                    .getMappingContext();

            IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

            IndexOperations indexOps = mongoTemplate.indexOps(ModifiablePlantInfo.class);
            resolver.resolveIndexFor(ModifiablePlantInfo.class).forEach(indexOps::ensureIndex);

            return RepeatStatus.FINISHED;
        }

    }
}