package app.growbuddy.plants.batch.crawl.meinschoenergarten;

import app.growbuddy.plants.batch.crawl.AbstractCrawlingProcessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.repository.CrudRepository;

import java.io.IOException;
import java.util.regex.Pattern;

public class MeinSchoenerGartenPlantCrawlingProcessor extends AbstractCrawlingProcessor<ModifiableMeinSchoenerGartenPlant> {

    private static final Pattern ID_PATTERN = Pattern.compile("https://www\\.mein-schoener-garten\\.de/pflanzen/(.+)");

    public MeinSchoenerGartenPlantCrawlingProcessor(CrudRepository<ModifiableMeinSchoenerGartenPlant, String> repository, boolean skipExisting) {
        super(repository, skipExisting);
    }

    @Override
    protected Pattern getIdFromUrlExtractionPattern() {
        return ID_PATTERN;
    }

    @Override
    protected ModifiableMeinSchoenerGartenPlant extract(String id, String url, Document document) throws IOException {
        ModifiableMeinSchoenerGartenPlant plant = new ModifiableMeinSchoenerGartenPlant();
        plant.setId(id);
        plant.setUrl(url);

        Element article = document.getElementsByTag("article").first();

        plant.setName(article.getElementsByClass("text-headline").first().text());
        plant.setBotanicalName(article.getElementsByClass("plants--botanic-name").first().text());

        for (Element element : article.getElementsByClass("plant-facts__item")) {
            String attributeKey = element.getElementsByTag("dt").text();
            String attributeValue = element.getElementsByTag("dd").text();

            plant.putAttributes(attributeKey, attributeValue);
        }

        return plant;
    }

}
