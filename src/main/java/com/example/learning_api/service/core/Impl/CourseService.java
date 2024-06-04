package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.course.CreateCourseRequest;
import com.example.learning_api.dto.request.course.DeleteCourseRequest;
import com.example.learning_api.dto.request.course.UpdateCourseRequest;
import com.example.learning_api.dto.response.classroom.GetClassRoomsResponse;
import com.example.learning_api.dto.response.course.CreateCourseResponse;
import com.example.learning_api.dto.response.course.GetCoursesResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.CourseEntity;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.CourseRepository;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ICourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService implements ICourseService {
    private final ModelMapperService modelMapperService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;
    @Override
    public CreateCourseResponse createCourse(CreateCourseRequest body) {
        try{
            if (body.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }
            if (body.getTeacherId()==null){
                throw new IllegalArgumentException("TeacherId is required");
            }
            if (userRepository.findById(body.getTeacherId()).isEmpty()){
                throw new IllegalArgumentException("TeacherId is not found");
            }
            CourseEntity courseEntity = modelMapperService.mapClass(body, CourseEntity.class);
            courseEntity.setCreatedAt(new Date());
            courseEntity.setUpdatedAt(new Date());
            CreateCourseResponse resData = new CreateCourseResponse();


            courseRepository.save(courseEntity);

            resData.setName(courseEntity.getName());
            resData.setDescription(courseEntity.getDescription());
            resData.setTeacherId(courseEntity.getTeacherId());
            resData.setId(courseEntity.getId());
            return resData;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public void updateCourse(UpdateCourseRequest body) {
        try {
            CourseEntity courseEntity = courseRepository.findById(body.getId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));

            if(body.getName()!=null){
                courseEntity.setName(body.getName());
            }
            if(body.getDescription()!=null){
                courseEntity.setDescription(body.getDescription());
            }
            if(body.getTeacherId()!=null){
                courseEntity.setTeacherId(body.getTeacherId());
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            if(body.getStartDate()!=null){
                Date startDate = formatter.parse(body.getStartDate());
                courseEntity.setStartDate(startDate);
            }
            if(body.getEndDate()!=null){
                Date endDate = formatter.parse(body.getEndDate());
                courseEntity.setEndDate(endDate);
            }
            courseRepository.save(courseEntity);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public void deleteCourse(DeleteCourseRequest body) {
        try {
            CourseEntity classroom = courseRepository.findById(body.getId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            courseRepository.delete(classroom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetCoursesResponse getCourses(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<CourseEntity> courses = courseRepository.findByNameContaining(search, pageAble);
            List<GetCoursesResponse.CourseResponse> resData = new ArrayList<>();
            for (CourseEntity course : courses){
                log.info("classRoom: {}", course);
                GetCoursesResponse.CourseResponse courseResponse = modelMapperService.mapClass(course, GetCoursesResponse.CourseResponse.class);
                resData.add(courseResponse);
            }
            GetCoursesResponse res = new GetCoursesResponse();
            res.setCourses(resData);
            res.setTotalPage(courses.getTotalPages());
            res.setTotalElements(courses.getTotalElements());
            return res;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }
    @Override
    public GetClassRoomsResponse getClassRoomsByCourseId(int page, int size, String courseId) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ClassRoomEntity> classRooms = classRoomRepository.findByCourseId(courseId, pageAble);
            List<GetClassRoomsResponse.ClassRoomResponse> resData = new ArrayList<>();
            for (ClassRoomEntity classRoom : classRooms){
                GetClassRoomsResponse.ClassRoomResponse classRoomResponse = modelMapperService.mapClass(classRoom, GetClassRoomsResponse.ClassRoomResponse.class);
                resData.add(classRoomResponse);
            }
            GetClassRoomsResponse res = new GetClassRoomsResponse();
            res.setClassRooms(resData);
            res.setTotalPage(classRooms.getTotalPages());
            res.setTotalElements(classRooms.getTotalElements());
            return res;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetCoursesResponse getCoursesInProgress(int page, int size, String studentId, String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = sdf.parse(dateStr);

            List<CourseEntity> courses = courseRepository.findCoursesInProgressByDateAndStudentId(date, studentId);

            // Phân trang thủ công vì Aggregation không hỗ trợ Pageable
            int start = page * size;
            int end = Math.min(start + size, courses.size());
            List<CourseEntity> pagedCourses = courses.subList(start, end);

            List<GetCoursesResponse.CourseResponse> resData = pagedCourses.stream()
                    .map(course -> modelMapperService.mapClass(course, GetCoursesResponse.CourseResponse.class))
                    .collect(Collectors.toList());

            GetCoursesResponse res = new GetCoursesResponse();
            res.setCourses(resData);
            res.setTotalPage((int) Math.ceil((double) courses.size() / size));
            res.setTotalElements((long) courses.size());
            return res;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
