package app.growbuddy.plants.batch.crawl.baldurgarten;

import app.growbuddy.plants.model.InjectPersistenceConstructor;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Optional;

@Document(collection = "baldurGartenProducts")
@TypeAlias("baldurGartenProduct")
@Value.Immutable
@Value.Modifiable
@Value.Style(create = "new", allParameters = true)
@InjectPersistenceConstructor
public interface BaldurGartenProduct {

    @Id
    String getId();

    String getUrl();

    String getName();

    Map<String, String> getAttributes();

}
