package app.growbuddy.plants.batch.crawl.baldurgarten;

import app.growbuddy.plants.batch.crawl.AbstractCrawlingProcessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.repository.CrudRepository;

import java.io.IOException;
import java.util.regex.Pattern;

public class BaldurGartenProductCrawlingProcessor extends AbstractCrawlingProcessor<ModifiableBaldurGartenProduct> {

    private static final Pattern ID_PATTERN = Pattern.compile("https:\\/\\/www.baldur-garten.ch\\/produkt\\/[^\\/]+\\/(\\d+)\\/.*");

    public BaldurGartenProductCrawlingProcessor(CrudRepository<ModifiableBaldurGartenProduct, String> repository,
                                                boolean skipExisting) {
        super(repository, skipExisting);
    }

    @Override
    protected Pattern getIdFromUrlExtractionPattern() {
        return ID_PATTERN;
    }

    @Override
    protected ModifiableBaldurGartenProduct extract(String id, String url, Document document) throws IOException {
        ModifiableBaldurGartenProduct product = new ModifiableBaldurGartenProduct();
        product.setId(id);
        product.setUrl(url);

        product.setName(document.getElementById("qs_product_name").text());

        for (Element element : document.getElementsByClass("pds-feature__item")) {
            String attributeKey = element.getElementsByClass("pds-feature__label").text();
            String attributeValue = element.getElementsByClass("pds-feature__content").text();

            product.putAttributes(attributeKey, attributeValue);
        }

        return product;
    }
}
