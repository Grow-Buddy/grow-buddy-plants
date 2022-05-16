package app.growbuddy.plants.batch.crawl;

import app.growbuddy.plants.batch.AbstractBatchComponentFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.core.io.UrlResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCrawlingStepFactory<T> extends AbstractBatchComponentFactory {

    private static final String SITEMAP_ENTRY_NAME = "url";

    private final StepBuilderFactory stepBuilderFactory;
    private final CrudRepository<T, String> repository;

    protected AbstractCrawlingStepFactory(StepBuilderFactory stepBuilderFactory,
                                          CrudRepository<T, String> repository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.repository = repository;
    }

    public Step createStep() throws IOException {
        return this.stepBuilderFactory.get(getBatchComponentBaseName())
                .<SitemapEntry, T>chunk(10)
                .reader(createReader())
                .processor(createProcessor())
                .writer(createWriter())
                .build();
    }

    private ItemReader<SitemapEntry> createReader() throws IOException {
        return new StaxEventItemReaderBuilder<SitemapEntry>()
                .name(getBatchComponentBaseName() + "Reader")
                .resource(new UrlResource(getSitemapUrl()))
                .addFragmentRootElements(SITEMAP_ENTRY_NAME)
                .unmarshaller(createSitemapUnmarshaller())
                .build();
    }

    private ItemProcessor<SitemapEntry, T> createProcessor() {
        CompositeItemProcessor<SitemapEntry, T> compositeProcessor = new CompositeItemProcessor<>();
        List<ItemProcessor<?, ?>> itemProcessors = new ArrayList<>();
        itemProcessors.add(createSitemapFilterProcessor());
        itemProcessors.add(createCrawlingProcessor(repository));
        compositeProcessor.setDelegates(itemProcessors);

        return compositeProcessor;
    }

    protected ItemProcessor<SitemapEntry, SitemapEntry> createSitemapFilterProcessor() {
        return new SitemapFilterProcessor(getSitemapFilterPattern());
    }

    protected abstract String getSitemapFilterPattern();

    protected abstract ItemProcessor<SitemapEntry, T> createCrawlingProcessor(CrudRepository<T, String> repository);

    protected abstract String getSitemapUrl();
    
    private static XStreamMarshaller createSitemapUnmarshaller() {
        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put(SITEMAP_ENTRY_NAME, ImmutableSitemapEntry.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(aliases);
        marshaller.getXStream().ignoreUnknownElements();

        return marshaller;
    }

    private ItemWriter<T> createWriter() {
        return new RepositoryItemWriterBuilder<T>().repository(repository).build();
    }

}
