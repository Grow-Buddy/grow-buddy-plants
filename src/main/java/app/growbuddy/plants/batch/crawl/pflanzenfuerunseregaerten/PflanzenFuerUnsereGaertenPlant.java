package app.growbuddy.plants.batch.crawl.pflanzenfuerunseregaerten;

import app.growbuddy.plants.model.InjectPersistenceConstructor;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "pflanzenFuerUnsereGaertenPlants")
@TypeAlias("pflanzenFuerUnsereGaertenPlant")
@Value.Immutable
@Value.Modifiable
@Value.Style(create = "new", allParameters = true)
@InjectPersistenceConstructor
public interface PflanzenFuerUnsereGaertenPlant {

    @Id
    String getId();

    String getUrl();

    String getName();

    String getBotanicalName();

    Map<String, String> getAttributes();

}
