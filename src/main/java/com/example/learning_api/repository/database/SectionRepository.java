
package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.SectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface SectionRepository extends MongoRepository<SectionEntity, String> {
    @Query(value = "{'classRoomId': ?0, 'status': { $in: ?1 }}", sort = "{'index': 1}")
    Page<SectionEntity> findByClassRoomId(String classRoomId, Pageable pageable, List<String> statuses);
    @Aggregation(pipeline = {
            "{ '$match': { 'classroomId': ?0 } }",
            "{ '$group': { '_id': null, 'maxIndex': { '$max': '$index' } } }"
    })
    Integer findMaxIndexByClassRoomId(String sectionId);

}
