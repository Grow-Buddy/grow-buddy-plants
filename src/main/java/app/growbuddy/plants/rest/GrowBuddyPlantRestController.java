package app.growbuddy.plants.rest;

import app.growbuddy.plants.PlantInfoRepository;
import app.growbuddy.plants.batch.PlantInfoBatchJobFactory;
import app.growbuddy.plants.model.ImmutablePlantInfo;
import app.growbuddy.plants.model.ModifiablePlantInfo;
import app.growbuddy.plants.model.PlantInfo;
import app.growbuddy.plants.model.PlantInfoSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/plant-infos")
public class GrowBuddyPlantRestController {

    private final JobLauncher jobLauncher;
    private final PlantInfoBatchJobFactory plantInfoBatchJobFactory;
    private final PlantInfoRepository plantInfoRepository;
    private final MongoTemplate mongoTemplate;

    public GrowBuddyPlantRestController(JobLauncher jobLauncher,
                                        PlantInfoBatchJobFactory plantInfoBatchJobFactory,
                                        PlantInfoRepository plantInfoRepository,
                                        MongoTemplate mongoTemplate) {

        this.jobLauncher = jobLauncher;
        this.plantInfoBatchJobFactory = plantInfoBatchJobFactory;
        this.plantInfoRepository = plantInfoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlantInfo> findAllByQuery(@RequestParam(required = false) String query,
                                          @RequestParam(required = false) List<PlantInfoSource> sources) {

        Query mongoQuery = new Query().limit(100);

        if (!StringUtils.isEmpty(query)) {
            mongoQuery.addCriteria(TextCriteria.forLanguage("de").matching(query));
            mongoQuery.with(Sort.by("score").descending());
        }

        if (CollectionUtils.isNotEmpty(sources)) {
            mongoQuery.addCriteria(Criteria.where("source").in(sources));
        }

        return mongoTemplate
                .find(mongoQuery, ModifiablePlantInfo.class)
                .stream()
                .map(ImmutablePlantInfo::copyOf)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<PlantInfo> findById(@PathVariable String id) {
        return plantInfoRepository.findById(id).map(ImmutablePlantInfo::copyOf);
    }

    @PostMapping(path = "/batch/crawl")
    public void startCrawlingJob() throws Exception {
        jobLauncher.run(plantInfoBatchJobFactory.createCrawlingJob(), createJobParameters());
    }

    @PostMapping(path = "/batch/process")
    public void startProcessingJob() throws Exception {
        jobLauncher.run(plantInfoBatchJobFactory.createProcessingJob(), createJobParameters());
    }

    private JobParameters createJobParameters() {
        return new JobParametersBuilder()
                // add a changing value to the job parameters else
                // it will not execute the job multiple times
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }

}
