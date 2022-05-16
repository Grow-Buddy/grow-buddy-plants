package app.growbuddy.plants.batch.crawl.pflanzenfuerunseregaerten;

import app.growbuddy.plants.batch.crawl.AbstractCrawlingProcessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.repository.CrudRepository;

import java.io.IOException;
import java.util.regex.Pattern;

public class PflanzenFuerUnsereGaertenPlantCrawlingProcessor extends AbstractCrawlingProcessor<ModifiablePflanzenFuerUnsereGaertenPlant> {

    private static final Pattern ID_PATTERN = Pattern.compile("http://www\\.pflanzen-fuer-unsere-gaerten\\.ch/de-ch/artikel/(\\d+)/.*");

    public PflanzenFuerUnsereGaertenPlantCrawlingProcessor(CrudRepository<ModifiablePflanzenFuerUnsereGaertenPlant, String> repository, boolean skipExisting) {
        super(repository, skipExisting);
    }

    @Override
    protected Pattern getIdFromUrlExtractionPattern() {
        return ID_PATTERN;
    }

    @Override
    protected ModifiablePflanzenFuerUnsereGaertenPlant extract(String id, String url, Document document) throws IOException {
        ModifiablePflanzenFuerUnsereGaertenPlant plant = new ModifiablePflanzenFuerUnsereGaertenPlant();
        plant.setId(id);
        plant.setUrl(url);

        Element name = document.getElementsByClass("articleName2").first();
        if (name == null) {
            logger.warn("skipping ID {} as the name could not be extracted", id);

            return null;
        }

        plant.setName(document.getElementsByClass("articleName2").first().text());
        plant.setBotanicalName(document.getElementsByClass("articleName").first().text());

        for (Element element : document.getElementsByClass("piktoText")) {
            String attributeKey = element.getElementsByClass("group").text();
            String attributeValue = element.getElementsByClass("text").text();

            plant.putAttributes(attributeKey, attributeValue);
        }

        return plant;
    }

}
