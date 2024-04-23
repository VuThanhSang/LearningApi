//package com.example.learning_api.service.elasticsearch;
//
//import com.example.learning_api.entity.elasticsearch.ClassRoomIndex;
//import com.example.learning_api.repository.elasticsearch.ClassRoomSearchRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ClassRoomSearchService {
//    private final ClassRoomSearchRepository classRoomSearchRepository;
//
//
//    public Page<ClassRoomIndex> findByName(String name, int page, int size){
//        Pageable pageAble = PageRequest.of(page, size);
//        return classRoomSearchRepository.findByName(name, pageAble);
//    }
//}
