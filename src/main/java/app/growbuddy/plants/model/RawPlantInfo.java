package app.growbuddy.plants.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE)
public interface RawPlantInfo {

    String getUrl();

    PlantInfoSource getSource();

    String getHtml();

}
