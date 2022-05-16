package app.growbuddy.plants.batch.crawl.baldurgarten;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BaldurGartenProductRepository extends MongoRepository<ModifiableBaldurGartenProduct, String> {
}
