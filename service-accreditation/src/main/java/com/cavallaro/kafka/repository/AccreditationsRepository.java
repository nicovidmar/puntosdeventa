package com.cavallaro.kafka.repository;

import com.cavallaro.kafka.model.Accreditation;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AccreditationsRepository extends MongoRepository<Accreditation, String> {

}

