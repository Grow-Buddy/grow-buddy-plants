package app.growbuddy.plants.batch.crawl.meinschoenergarten;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MeinSchoenerGartenPlantRepository extends MongoRepository<ModifiableMeinSchoenerGartenPlant, String> {
}
