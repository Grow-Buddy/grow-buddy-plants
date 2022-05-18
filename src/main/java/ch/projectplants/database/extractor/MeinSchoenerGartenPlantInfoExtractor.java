package ch.projectplants.database.extractor;

import ch.projectplants.database.model.PreferredLocation;
import ch.projectplants.database.model.WaterDemand;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MeinSchoenerGartenPlantInfoExtractor extends AbstractPlantInfoExtractor {

    @Override
    protected Boolean extractIsWinterProof(Map<String, String> rawAttributes) {
        Boolean isWinterProof = super.extractIsWinterProof(rawAttributes);

        return isWinterProof != null ? isWinterProof : extractWinterProofnessFromClimateZone(rawAttributes);
    }

    @Override
    protected AttributeValueMappingDefinition<PreferredLocation> createPreferredLocationMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<PreferredLocation>builder()
                .addKeys("Licht")
                .putMapping("absonnig", PreferredLocation.SUNNY)
                .putMapping("absonnig bis halbschattig", PreferredLocation.HALF_SHADOWS_TO_SUNNY)
                .putMapping("absonnig bis schattig", PreferredLocation.NO_MATTER)
                .putMapping("halbschattig", PreferredLocation.HALF_SHADOWS)
                .putMapping("halbschattig bis schattig", PreferredLocation.SHADOW_TO_HALF_SHADOWS)
                .putMapping("schattig", PreferredLocation.SHADOW)
                .putMapping("sonnig", PreferredLocation.SUNNY)
                .putMapping("sonnig bis absonnig", PreferredLocation.SUNNY)
                .putMapping("sonnig bis halbschattig", PreferredLocation.HALF_SHADOWS_TO_SUNNY)
                .putMapping("sonnig bis schattig", PreferredLocation.NO_MATTER)
                .putMapping("sonnig halbschattig", PreferredLocation.HALF_SHADOWS_TO_SUNNY)
                .putMapping("sonnig halbschattig bis schattig", PreferredLocation.NO_MATTER)
                .build();
    }

    @Override
    protected AttributeValueMappingDefinition<WaterDemand> createWaterDemandMapping() {
        return ImmutableAttributeValueMappingDefinition.<WaterDemand>builder().build();
    }

    @Override
    protected AttributeValueMappingDefinition<Boolean> createIsWinterProofMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<Boolean>builder()
                .addKeys("Winterhärte")
                .putMapping("winterhart", true)
                .putMapping("bedingt winterhart", true)
                .putMapping("frostempfindlich", false)
                .build();
    }

    @Override
    protected Element selectName(Document document) {
        return selectArticle(document).getElementsByClass("text-headline").first();
    }

    @Override
    protected Element selectBotanicalName(Document document) {
        return selectArticle(document).getElementsByClass("plants--botanic-name").first();
    }

    @Override
    protected List<Pair<Element, Element>> selectRawAttributes(Document document) {
        List<Pair<Element, Element>> rawAttributes = new ArrayList<>();

        for (Element element : selectArticle(document).getElementsByClass("plant-facts__item")) {
            Element attributeKey = element.getElementsByTag("dt").first();
            Element attributeValue = element.getElementsByTag("dd").first();

            rawAttributes.add(Pair.of(attributeKey, attributeValue));
        }

        return rawAttributes;
    }

    private Boolean extractWinterProofnessFromClimateZone(Map<String, String> attributes) {
        String hardinessZones = attributes.get("Klimazonen nach USDA");
        if (StringUtils.isEmpty(hardinessZones)) {
            return null;
        }

        try {
            // see https://en.wikipedia.org/wiki/Hardiness_zone
            return parseMinHardinessZone(hardinessZones) <= 8;
        } catch (Exception e) {
            return null;
        }
    }

    private int parseMinHardinessZone(String rawhardinessZones) {
        String trimmedHardinessZones = StringUtils.trim(rawhardinessZones);

        return Arrays.stream(StringUtils.split(trimmedHardinessZones, " "))
                .mapToInt(Integer::parseInt)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    private Element selectArticle(Document document) {
        return document.getElementsByTag("article").first();
    }

}
