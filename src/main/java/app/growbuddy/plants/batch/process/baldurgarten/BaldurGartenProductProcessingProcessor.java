package app.growbuddy.plants.batch.process.baldurgarten;

import app.growbuddy.plants.batch.crawl.baldurgarten.BaldurGartenProduct;
import app.growbuddy.plants.batch.process.AbstractProcessingProcessor;
import app.growbuddy.plants.batch.process.AttributeValueMappingDefinition;
import app.growbuddy.plants.batch.process.ImmutableAttributeValueMappingDefinition;
import app.growbuddy.plants.model.ModifiablePlantInfo;
import app.growbuddy.plants.model.PlantInfoSource;
import app.growbuddy.plants.model.PreferredLocation;
import app.growbuddy.plants.model.WaterDemand;

import java.util.Map;

public class BaldurGartenProductProcessingProcessor extends AbstractProcessingProcessor<BaldurGartenProduct> {

    private final AttributeValueMappingDefinition<PreferredLocation> preferredLocationMapping = createPreferredLocationMapping();
    private final AttributeValueMappingDefinition<WaterDemand> waterDemandMapping = createWaterDemandMapping();
    private final AttributeValueMappingDefinition<Boolean> isWinterProofMapping = createIsWinterProofMapping();

    @Override
    public ModifiablePlantInfo process(BaldurGartenProduct baldurGartenProduct) {
        ModifiablePlantInfo plantInfo = new ModifiablePlantInfo();

        plantInfo.setName(baldurGartenProduct.getName());
        plantInfo.setSource(PlantInfoSource.BALDUR_GARTEN);
        plantInfo.setDetailLink(baldurGartenProduct.getUrl());

        Map<String, String> attributes = baldurGartenProduct.getAttributes();
        plantInfo.setPreferredLocation(mapAttributeValue(attributes, preferredLocationMapping));
        plantInfo.setWaterDemand(mapAttributeValue(attributes, waterDemandMapping));
        plantInfo.setIsWinterProof(mapAttributeValue(attributes, isWinterProofMapping));

        return plantInfo;
    }

    private AttributeValueMappingDefinition<PreferredLocation> createPreferredLocationMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<PreferredLocation>builder()
                .addKeys("Standort")
                .putMapping("Sonne", PreferredLocation.SUNNY)
                .putMapping("Sonne bis Halbschatten", PreferredLocation.HALF_SHADOWS_TO_SUNNY)
                .putMapping("Halbschatten", PreferredLocation.HALF_SHADOWS)
                .putMapping("Halbschatten bis Schatten", PreferredLocation.SHADOW_TO_HALF_SHADOWS)
                .putMapping("Schatten", PreferredLocation.SHADOW)
                .putMapping("Sonne bis Schatten", PreferredLocation.NO_MATTER)
                .build();
    }

    private AttributeValueMappingDefinition<WaterDemand> createWaterDemandMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<WaterDemand>builder()
                .addKeys("Wasserbedarf")
                .putMapping("gering", WaterDemand.LOW)
                .putMapping("gering - mittel", WaterDemand.LOW_TO_MEDIUM)
                .putMapping("mittel", WaterDemand.MEDIUM)
                .putMapping("mittel - hoch", WaterDemand.MEDIUM_TO_HIGH)
                .putMapping("hoch", WaterDemand.HIGH)
                .build();
    }

    private AttributeValueMappingDefinition<Boolean> createIsWinterProofMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<Boolean>builder()
                .addKeys("Winterhart")
                .putMapping("ja", true)
                .build();
    }

}
