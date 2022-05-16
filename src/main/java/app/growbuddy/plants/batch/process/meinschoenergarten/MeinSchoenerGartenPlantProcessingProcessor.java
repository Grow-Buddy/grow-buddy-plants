package app.growbuddy.plants.batch.process.meinschoenergarten;

import app.growbuddy.plants.batch.crawl.meinschoenergarten.MeinSchoenerGartenPlant;
import app.growbuddy.plants.batch.process.AbstractProcessingProcessor;
import app.growbuddy.plants.batch.process.AttributeValueMappingDefinition;
import app.growbuddy.plants.batch.process.ImmutableAttributeValueMappingDefinition;
import app.growbuddy.plants.model.ModifiablePlantInfo;
import app.growbuddy.plants.model.PlantInfoSource;
import app.growbuddy.plants.model.PreferredLocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service
public class MeinSchoenerGartenPlantProcessingProcessor extends AbstractProcessingProcessor<MeinSchoenerGartenPlant> {

    private final AttributeValueMappingDefinition<PreferredLocation> preferredLocationMapping = createPreferredLocationMapping();
    private final AttributeValueMappingDefinition<Boolean> isWinterProofMapping = createIsWinterProofMapping();

    @Override
    public ModifiablePlantInfo process(MeinSchoenerGartenPlant meinSchoenerGartenPlant) {
        ModifiablePlantInfo plantInfo = new ModifiablePlantInfo();

        plantInfo.setName(meinSchoenerGartenPlant.getName());
        if (StringUtils.isNotEmpty(meinSchoenerGartenPlant.getBotanicalName())) {
            plantInfo.setBotanicalName(meinSchoenerGartenPlant.getBotanicalName());
        }
        plantInfo.setSource(PlantInfoSource.MEIN_SCHOENER_GARTEN);
        plantInfo.setDetailLink(meinSchoenerGartenPlant.getUrl());

        Map<String, String> attributes = meinSchoenerGartenPlant.getAttributes();
        plantInfo.setPreferredLocation(mapAttributeValue(attributes, preferredLocationMapping));
        plantInfo.setIsWinterProof(mapAttributeValue(attributes, isWinterProofMapping));
        if (plantInfo.isWinterProof() == null) {
            plantInfo.setIsWinterProof(extractWinterProofnessFromClimateZone(attributes));
        }

        return plantInfo;
    }

    private AttributeValueMappingDefinition<PreferredLocation> createPreferredLocationMapping() {
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

    private AttributeValueMappingDefinition<Boolean> createIsWinterProofMapping() {
        return ImmutableAttributeValueMappingDefinition
                .<Boolean>builder()
                .addKeys("Winterhärte")
                .putMapping("winterhart", true)
                .putMapping("bedingt winterhart", true)
                .putMapping("frostempfindlich", false)
                .build();
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
            logger.warn("error while parsing hardiness zones", e);

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

}
