package app.growbuddy.plants.model;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE)
public interface PlantInfo {

    String getName();

    String getBotanicalName();

    PlantInfoSource getSource();

    String getDetailLink();

    WaterDemand getWaterDemand();

    PreferredLocation getPreferredLocation();

    Boolean isWinterProof();

}
