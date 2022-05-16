package app.growbuddy.plants.batch.crawl.pflanzenfuerunseregaerten;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PflanzenFuerUnsereGaertenPlantRepository extends MongoRepository<ModifiablePflanzenFuerUnsereGaertenPlant, String> {
}
