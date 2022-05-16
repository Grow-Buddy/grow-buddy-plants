package app.growbuddy.plants.extractor;

import app.growbuddy.plants.model.PlantInfo;
import app.growbuddy.plants.model.RawPlantInfo;

public interface PlantInfoExtractor {

    PlantInfo extractPlantInfo(RawPlantInfo rawPlantInfo);

}
