package ch.projectplants.database.extractor;

import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Immutable
@Value.Style(validationMethod = Value.Style.ValidationMethod.NONE)
public interface AttributeValueMappingDefinition<T> {

    List<String> getKeys();

    T getDefaultValue();

    Map<String, T> getMapping();

}
