package app.growbuddy.plants;

import app.growbuddy.plants.model.ModifiablePlantInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlantInfoRepository extends MongoRepository<ModifiablePlantInfo, String> {

}
