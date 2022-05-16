package app.growbuddy.plants.batch.process.pflanzenfuerunseregaerten;

import app.growbuddy.plants.batch.crawl.pflanzenfuerunseregaerten.PflanzenFuerUnsereGaertenPlant;
import app.growbuddy.plants.batch.process.AbstractProcessingProcessor;
import app.growbuddy.plants.batch.process.AttributeValueMappingDefinition;
import app.growbuddy.plants.batch.process.ImmutableAttributeValueMappingDefinition;
import app.growbuddy.plants.model.ModifiablePlantInfo;
import app.growbuddy.plants.model.PlantInfoSource;
import app.growbuddy.plants.model.PreferredLocation;
import app.growbuddy.plants.model.WaterDemand;

import java.util.Map;

public class PflanzenFuerUnsereGaertenPlantProcessingProcessor extends AbstractProcessingProcessor<PflanzenFuerUnsereGaertenPlant> {

    private final AttributeValueMappingDefinition<PreferredLocation> preferredLocationMapping = createPreferredLocationMapping();
    private final AttributeValueMappingDefinition<WaterDemand> waterDemandMapping = createWaterDemandMapping();
    private final AttributeValueMappingDefinition<Boolean> isWinterProofMapping = createIsWinterProofMapping();

    @Override
    public ModifiablePlantInfo process(PflanzenFuerUnsereGaertenPlant pflanzenFuerUnsereGaertenPlant) {
        ModifiablePlantInfo plantInfo = new ModifiablePlantInfo();

        plantInfo.setName(pflanzenFuerUnsereGaertenPlant.getName());
        plantInfo.setBotanicalName(pflanzenFuerUnsereGaertenPlant.getBotanicalName());
        plantInfo.setSource(PlantInfoSource.PFLANZEN_FUER_UNSERE_GAERTEN);
        plantInfo.setDetailLink(pflanzenFuerUnsereGaertenPlant.getUrl());

        Map<String, String> attributes = pflanzenFuerUnsereGaertenPlant.getAttributes();
        plantInfo.setPreferredLocation(mapAttributeValue(attributes, preferredLocationMapping));
        plantInfo.setWaterDemand(mapAttributeValue(attributes, waterDemandMapping));
        plantInfo.setIsWinterProof(mapAttributeValue(attributes, isWinterProofMapping));

        return plantInfo;
    }

    private AttributeValueMappingDefinition<PreferredLocation> createPreferredLocationMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<PreferredLocation>builder()
                .addKeys("Standort")
                .putMapping("Sonnig", PreferredLocation.SUNNY)
                .putMapping("Halbschattig", PreferredLocation.HALF_SHADOWS)
                .putMapping("Schattig", PreferredLocation.SHADOW)
                .build();
    }

    private AttributeValueMappingDefinition<WaterDemand> createWaterDemandMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<WaterDemand>builder()
                .addKeys("Wasser")
                .putMapping("Wenig", WaterDemand.LOW)
                .putMapping("Mittel", WaterDemand.MEDIUM)
                .putMapping("Viel", WaterDemand.HIGH)
                .build();
    }

    private AttributeValueMappingDefinition<Boolean> createIsWinterProofMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<Boolean>builder()
                .addKeys("Frosthärte")
                .putMapping("Frosthart", true)
                .build();
    }

}
