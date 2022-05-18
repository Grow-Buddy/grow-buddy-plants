package ch.projectplants.database.extractor;

import ch.projectplants.database.model.*;
import ch.projectplants.database.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractPlantInfoExtractor implements PlantInfoExtractor {

    private final AttributeValueMappingDefinition<PreferredLocation> preferredLocationMapping;
    private final AttributeValueMappingDefinition<WaterDemand> waterDemandMapping;
    private final AttributeValueMappingDefinition<Boolean> isWinterProofMapping;

    protected AbstractPlantInfoExtractor() {
        preferredLocationMapping = createPreferredLocationMapping();
        waterDemandMapping = createWaterDemandMapping();
        isWinterProofMapping = createIsWinterProofMapping();
    }

    protected abstract AttributeValueMappingDefinition<PreferredLocation> createPreferredLocationMapping();

    protected abstract AttributeValueMappingDefinition<WaterDemand> createWaterDemandMapping();

    protected abstract AttributeValueMappingDefinition<Boolean> createIsWinterProofMapping();

    @Override
    public PlantInfo extractPlantInfo(RawPlantInfo rawPlantInfo) {
        var builder = ImmutablePlantInfo.builder();

        Document document = Jsoup.parse(rawPlantInfo.getHtml());
        builder.source(rawPlantInfo.getSource());
        builder.detailLink(rawPlantInfo.getUrl());

        String name = extractName(document);
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        builder.name(name);

        builder.botanicalName(extractBotanicalName(document));

        Map<String, String> rawAttributes = extractRawAttributes(document);
        builder.preferredLocation(extractPreferredLocation(rawAttributes));
        builder.waterDemand(extractWaterDemand(rawAttributes));
        builder.isWinterProof(extractIsWinterProof(rawAttributes));

        return builder.build();
    }

    protected String extractName(Document document) {
        return extractTextFromElement(selectName(document));
    }

    protected abstract Element selectName(Document document);

    protected String extractBotanicalName(Document document) {
        return extractTextFromElement(selectBotanicalName(document));
    }

    protected Element selectBotanicalName(Document document) {
        return null;
    }

    protected Map<String, String> extractRawAttributes(Document document) {
        return selectRawAttributes(document)
                .stream()
                .map(pair -> Pair.of(extractTextFromElement(pair.getLeft()), extractTextFromElement(pair.getRight())))
                .filter(pair -> StringUtils.isNotEmpty(pair.getLeft()) && StringUtils.isNotEmpty(pair.getRight()))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (a, b) -> String.join(", ", a, b)));
    }

    protected abstract List<Pair<Element, Element>> selectRawAttributes(Document document);

    protected PreferredLocation extractPreferredLocation(Map<String, String> rawAttributes) {
        return mapAttributeValue(rawAttributes, preferredLocationMapping);
    }

    protected WaterDemand extractWaterDemand(Map<String, String> rawAttributes) {
        return mapAttributeValue(rawAttributes, waterDemandMapping);
    }

    protected Boolean extractIsWinterProof(Map<String, String> rawAttributes) {
        return mapAttributeValue(rawAttributes, isWinterProofMapping);
    }

    private <V> V mapAttributeValue(Map<String, String> attributes,
                                    AttributeValueMappingDefinition<V> mappingDefinition) {

        for (String key : mappingDefinition.getKeys()) {
            String rawValue = attributes.get(key);
            if (StringUtils.isEmpty(rawValue)) {
                continue;
            }

            Map<String, V> mapping = mappingDefinition.getMapping();
            if (mapping.containsKey(rawValue)) {
                return mapping.get(rawValue);
            }
        }

        return mappingDefinition.getDefaultValue();
    }

    protected String extractTextFromElement(Element element) {
        if (element == null) {
            return null;
        }

        return StringUtils.stripToNull(element.text());
    }

}
