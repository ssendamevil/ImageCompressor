package org.aoa.imagecompressor.repository;

import org.aoa.imagecompressor.domain.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
}
