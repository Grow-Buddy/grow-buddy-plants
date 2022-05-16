package app.growbuddy.plants.batch.process;

import app.growbuddy.plants.model.ModifiablePlantInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;

public abstract class AbstractProcessingProcessor<T> implements ItemProcessor<T, ModifiablePlantInfo> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected <V> V mapAttributeValue(Map<String, String> attributes,
                                      AttributeValueMappingDefinition<V> mappingDefinition) {

        for (String key : mappingDefinition.getKeys()) {
            String rawValue = attributes.get(key);
            if (StringUtils.isEmpty(rawValue)) {
                continue;
            }

            Map<String, V> mapping = mappingDefinition.getMapping();
            if (mapping.containsKey(rawValue)) {
                return mapping.get(rawValue);
            } else {
                logger.warn("unmapped value {} of attribute {}", rawValue, key);
            }
        }

        return mappingDefinition.getDefaultValue();
    }

}
