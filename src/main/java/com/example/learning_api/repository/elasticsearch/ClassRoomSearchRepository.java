//package com.example.learning_api.repository.elasticsearch;
//
//import com.example.learning_api.entity.elasticsearch.ClassRoomIndex;
//import org.springframework.data.domain.Page;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.data.mongodb.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import org.springframework.data.domain.Pageable;
//
//@Repository
//public interface ClassRoomSearchRepository extends ElasticsearchRepository<ClassRoomIndex, String> {
//    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}]}}")
//    Page<ClassRoomIndex> findByName(String name, Pageable pageable);
//}