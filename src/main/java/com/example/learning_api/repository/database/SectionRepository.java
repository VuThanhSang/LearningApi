
package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.SectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;



public interface SectionRepository extends MongoRepository<SectionEntity, String> {
    @Query("{'classRoomId': ?0}")
    Page<SectionEntity> findByClassRoomId(String classRoomId, Pageable pageable);
    @Aggregation(pipeline = {
            "{ '$match': { 'classroomId': ?0 } }",
            "{ '$group': { '_id': null, 'maxIndex': { '$max': '$index' } } }"
    })
    Integer findMaxIndexByClassRoomId(String sectionId);

}
