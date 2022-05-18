package ch.projectplants.database.extractor;

import ch.projectplants.database.model.PreferredLocation;
import ch.projectplants.database.model.WaterDemand;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class PflanzenFuerUnsereGaertenPlantInfoExtractor extends AbstractPlantInfoExtractor {

    @Override
    protected AttributeValueMappingDefinition<PreferredLocation> createPreferredLocationMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<PreferredLocation>builder()
                .addKeys("Standort")
                .putMapping("Sonnig", PreferredLocation.SUNNY)
                .putMapping("Halbschattig, Sonnig", PreferredLocation.HALF_SHADOWS_TO_SUNNY)
                .putMapping("Halbschattig", PreferredLocation.HALF_SHADOWS)
                .putMapping("Halbschattig, Schattig", PreferredLocation.SHADOW_TO_HALF_SHADOWS)
                .putMapping("Schattig", PreferredLocation.SHADOW)
                .putMapping("Halbschattig, Schattig, Sonnig", PreferredLocation.NO_MATTER)
                .putMapping("Schattig, Sonnig", PreferredLocation.NO_MATTER)
                .build();
    }

    @Override
    protected AttributeValueMappingDefinition<WaterDemand> createWaterDemandMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<WaterDemand>builder()
                .addKeys("Wasser")
                .putMapping("Wenig", WaterDemand.LOW)
                .putMapping("Mittel, Wenig", WaterDemand.LOW_TO_MEDIUM)
                .putMapping("Mittel", WaterDemand.MEDIUM)
                .putMapping("Mittel, Viel", WaterDemand.MEDIUM_TO_HIGH)
                .putMapping("Viel", WaterDemand.HIGH)
                .build();
    }

    @Override
    protected AttributeValueMappingDefinition<Boolean> createIsWinterProofMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<Boolean>builder()
                .addKeys("Frosthärte")
                .putMapping("Frosthart", true)
                .build();
    }

    @Override
    protected Element selectName(Document document) {
        return document.getElementsByClass("articleName2").first();
    }

    @Override
    protected Element selectBotanicalName(Document document) {
        return document.getElementsByClass("articleName").first();
    }

    @Override
    protected List<Pair<Element, Element>> selectRawAttributes(Document document) {
        List<Pair<Element, Element>> rawAttributes = new ArrayList<>();

        for (Element element : document.getElementsByClass("piktoText")) {
            Element attributeKey = element.getElementsByClass("group").first();
            Element attributeValue = element.getElementsByClass("text").first();

            rawAttributes.add(Pair.of(attributeKey, attributeValue));
        }

        return rawAttributes;
    }

}
