package ch.projectplants.database.extractor;

import ch.projectplants.database.model.PlantInfo;
import ch.projectplants.database.model.RawPlantInfo;

public interface PlantInfoExtractor {

    PlantInfo extractPlantInfo(RawPlantInfo rawPlantInfo);

}
