package app.growbuddy.plants.batch.process;

import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Value.Immutable
public interface AttributeValueMappingDefinition<T> {

    List<String> getKeys();

    @Nullable
    T getDefaultValue();

    Map<String, T> getMapping();

}
