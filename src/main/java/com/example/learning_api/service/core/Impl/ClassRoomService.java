package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.classroom.*;
import com.example.learning_api.dto.response.classroom.*;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.dto.response.test.TestResultOfTestResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import com.example.learning_api.enums.JoinRequestStatus;
import com.example.learning_api.enums.StudentEnrollmentStatus;
import com.example.learning_api.enums.TestState;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ExcelReader;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.*;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassRoomService implements IClassRoomService {
    private final ModelMapperService modelMapperService;
    private final ClassRoomRepository classRoomRepository;
    private final CloudinaryService cloudinaryService;
    private final SectionRepository sectionRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final ScheduleRepository scheduleRepository;
    private final TermsRepository termRepository;
    private final FacultyRepository facultyRepository;
    private final ExcelReader excelReader;
    private final RecentClassRepository recentClassRepository;
    private final JoinClassRequestRepository joinClassRequestRepository;
    private final TestResultRepository testResultRepository;
    private final TestRepository testRepository;
    private final DeadlineRepository deadlineRepository;
    private final DeadlineSubmissionsRepository deadlineSubmissionsRepository;
    @Override
    public CreateClassRoomResponse createClassRoom(CreateClassRoomRequest body) {
        try{

            if (body.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }


            if (body.getTeacherId()==null){
                throw new IllegalArgumentException("TeacherId is required");
            }
            if (teacherRepository.findById(body.getTeacherId()).isEmpty()){
                throw new IllegalArgumentException("TeacherId is not found");
            }
            if (body.getTermId()==null){
                throw new IllegalArgumentException("TermId is required");
            }
            if (termRepository.findById(body.getTermId()).isEmpty()){
                throw new IllegalArgumentException("TermId is not found");
            }
            if (body.getFacultyId()==null){
                throw new IllegalArgumentException("FacultyId is required");
            }
            if (facultyRepository.findById(body.getFacultyId()).isEmpty()){
                throw new IllegalArgumentException("FacultyId is not found");
            }
            if (body.getEnrollmentCapacity()==null){
                throw new IllegalArgumentException("EnrollmentCapacity is required");
            }
            ClassRoomEntity classRoomEntity = modelMapperService.mapClass(body, ClassRoomEntity.class);
            classRoomEntity.setFacultyId(body.getFacultyId());
            classRoomEntity.setCurrentEnrollment(0);
            classRoomEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            classRoomEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            List<ScheduleEntity> schedules = new ArrayList<>();

            CreateClassRoomResponse resData = new CreateClassRoomResponse();
            if (body.getImage()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "classroom"),
                        newImage,
                        "image"
                );
                classRoomEntity.setImage(imageUploaded.getUrl());
            }

            classRoomRepository.save(classRoomEntity);
            if (body.getSessions()!=null){
                for (ClassSessionRequest session : body.getSessions()){
                    ScheduleEntity schedule = new ScheduleEntity();
                    schedule.setClassroomId(classRoomEntity.getId());
                    schedule.setDayOfWeek(session.getDayOfWeek());
                    schedule.setStartTime(session.getStartTime());
                    schedule.setEndTime(session.getEndTime());
                    scheduleRepository.save(schedule);
                }

            }
            else{
                throw new IllegalArgumentException("Sessions is required");
            }
            classRoomEntity.setInviteCode(generateInviteCode(classRoomEntity.getId()));
            classRoomRepository.save(classRoomEntity);
            resData.setId(classRoomEntity.getId());
            resData.setName(classRoomEntity.getName());
            resData.setDescription(classRoomEntity.getDescription());
            resData.setImage(classRoomEntity.getImage());
            resData.setTeacherId(classRoomEntity.getTeacherId());
            resData.setTermId(classRoomEntity.getTermId());
            resData.setFacultyId(classRoomEntity.getFacultyId());
            resData.setEnrollmentCapacity(classRoomEntity.getEnrollmentCapacity());
            resData.setCurrentEnrollment(classRoomEntity.getCurrentEnrollment());
            resData.setCredits(classRoomEntity.getCredits());
            resData.setStatus(classRoomEntity.getStatus().toString());
            resData.setCreatedAt(classRoomEntity.getCreatedAt().toString());
            resData.setUpdatedAt(classRoomEntity.getUpdatedAt().toString());
            resData.setSchedules(schedules);
            return resData;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String generateInviteCode(String id) {
        String encodedId = Base64.getEncoder().encodeToString(id.getBytes());
        return "CLASS-" + encodedId;
    }

    @Override
    public void updateClassRoom(UpdateClassRoomRequest body) {
        try {
            ClassRoomEntity classroom = classRoomRepository.findById(body.getId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            if(body.getImage()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "classroom"),
                        newImage,
                        "image"
                );
                classroom.setImage(imageUploaded.getUrl());
            }

            if (body.getName()!=null){
                classroom.setName(body.getName());
            }
            if (body.getDescription()!=null){
                classroom.setDescription(body.getDescription());
            }

            if (body.getTeacherId()!=null){
                if (teacherRepository.findById(body.getTeacherId()).isEmpty()){
                    throw new IllegalArgumentException("TeacherId is not found");
                }
                classroom.setTeacherId(body.getTeacherId());
            }
            if (body.getTermId()!=null){
                if (termRepository.findById(body.getTermId()).isEmpty()){
                    throw new IllegalArgumentException("TermId is not found");
                }
                classroom.setTermId(body.getTermId());
            }
            if (body.getFacultyId()!=null){
                if (facultyRepository.findById(body.getFacultyId()).isEmpty()){
                    throw new IllegalArgumentException("FacultyId is not found");
                }
                classroom.setFacultyId(body.getFacultyId());
            }
            if (body.getEnrollmentCapacity()!=null){
                classroom.setEnrollmentCapacity(body.getEnrollmentCapacity());
            }
            if (body.getCredits()!=null){
                classroom.setCredits(body.getCredits());
            }
            if (body.getStatus()!=null){
                classroom.setStatus(body.getStatus());
            }
            classRoomRepository.save(classroom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }


    @Override
    public void deleteClassRoom(String classroomId) {
        try {
             ClassRoomEntity classroom = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            classRoomRepository.delete(classroom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomsResponse getClassRooms(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ClassRoomEntity> classRooms = classRoomRepository.findByNameContaining(search, pageAble);
            List<GetClassRoomsResponse.ClassRoomResponse> resData = new ArrayList<>();
            for (ClassRoomEntity classRoom : classRooms){
                log.info("classRoom: {}", classRoom);
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
    public GetSectionsResponse getSectionsByClassroomId(int page, int size, String classroomId) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classroomId, pageAble);
            List<GetSectionsResponse.SectionResponse> sectionResponses = modelMapperService.mapList(sectionEntities.getContent(), GetSectionsResponse.SectionResponse.class);
            GetSectionsResponse resData = new GetSectionsResponse();
            resData.setTotalPage(sectionEntities.getTotalPages());
            resData.setTotalElements(sectionEntities.getTotalElements());
            resData.setSections(sectionResponses);
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetClassRoomsResponse getScheduleByDay(String studentId, String day) {
        try{

            List<ClassRoomEntity> classRooms = classRoomRepository.findStudentScheduleByDayAndStudentId(day,studentId);
            int start = 0;
            int end = Math.min(start + 10, classRooms.size());
            List<ClassRoomEntity> pagedCourses = classRooms.subList(start, end);

            List<GetClassRoomsResponse.ClassRoomResponse> resData = pagedCourses.stream()
                    .map(classRoom -> modelMapperService.mapClass(classRoom,GetClassRoomsResponse.ClassRoomResponse.class))
                    .collect(Collectors.toList());
            GetClassRoomsResponse res = new GetClassRoomsResponse();
            res.setClassRooms(resData);
            res.setTotalPage((int) Math.ceil((double) classRooms.size() / 10));
            res.setTotalElements((long) classRooms.size());
            return res;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public List<GetScheduleResponse> getScheduleByStudentId(String studentId) {
        try {
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            AggregationResults<GetScheduleResponse> results = studentEnrollmentsRepository.getWeeklySchedule(studentId);
            List<GetScheduleResponse> resData = results.getMappedResults();

            // Create a default schedule with all days of the week
            List<GetScheduleResponse> defaultSchedule = Arrays.asList(
                    new GetScheduleResponse("Monday", new ArrayList<>()),
                        new GetScheduleResponse("Tuesday", new ArrayList<>()),
                    new GetScheduleResponse("Wednesday", new ArrayList<>()),
                    new GetScheduleResponse("Thursday", new ArrayList<>()),
                    new GetScheduleResponse("Friday", new ArrayList<>()),
                    new GetScheduleResponse("Saturday", new ArrayList<>()),
                    new GetScheduleResponse("Sunday", new ArrayList<>())
            );

            // Replace the default schedule with the actual data where available
            for (GetScheduleResponse schedule : defaultSchedule) {
                GetScheduleResponse foundSchedule = resData.stream()
                        .filter(s -> s.getDayOfWeek().equals(schedule.getDayOfWeek()))
                        .findFirst()
                        .orElse(null);
                if (foundSchedule != null) {
                    schedule.setSessions(foundSchedule.getSessions());
                }
            }

            return defaultSchedule;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomDetailResponse getClassRoomByInvitationCode(String invitationCode) {
        try{
            ClassRoomEntity classRoomEntity = classRoomRepository.findClassRoomEntityByInviteCode(invitationCode);
            if (classRoomEntity==null){
                throw new IllegalArgumentException("ClassRoom is not found");
            }
            GetClassRoomDetailResponse resData = new GetClassRoomDetailResponse();
            Pageable pageAble = PageRequest.of(0, 15);
            resData.setClassRoom(classRoomEntity);
            Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classRoomEntity.getId(),pageAble);
            List<GetClassRoomDetailResponse.Section> sections = new ArrayList<>();
            for (SectionEntity sectionEntity : sectionEntities){
                GetClassRoomDetailResponse.Section section = new GetClassRoomDetailResponse.Section();
                section.setId(sectionEntity.getId());
                section.setName(sectionEntity.getName());
                section.setStatus(sectionEntity.getStatus()!=null?sectionEntity.getStatus().toString():null);
                section.setDescription(sectionEntity.getDescription());
                section.setIndex(sectionEntity.getIndex()!=null?sectionEntity.getIndex():0);
//                List<LessonEntity> lessons = lessonRepository.findBySectionId(sectionEntity.getId());
//                List<GetLessonDetailResponse> lessonDetails = new ArrayList<>();
//                for (LessonEntity lesson : lessons){
//                    GetLessonDetailResponse lessonDetail = lessonRepository.getLessonWithResourcesAndMediaAndSubstances(lesson.getId());
//                    lessonDetails.add(lessonDetail);
//                }
//                section.setLessons(lessonDetails);
                sections.add(section);

            }
            resData.setSections(sections);
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomDetailResponse getClassRoomDetail(String classroomId) {
       try{
              ClassRoomEntity classRoomEntity = classRoomRepository.findById(classroomId)
                      .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                GetClassRoomDetailResponse resData = new GetClassRoomDetailResponse();
                Pageable pageAble = PageRequest.of(0, 15);
                resData.setClassRoom(classRoomEntity);
                Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classroomId,pageAble);
                List<GetClassRoomDetailResponse.Section> sections = new ArrayList<>();
                for (SectionEntity sectionEntity : sectionEntities){
                    GetClassRoomDetailResponse.Section section = new GetClassRoomDetailResponse.Section();
                    section.setId(sectionEntity.getId());
                    section.setName(sectionEntity.getName());
                    section.setStatus(sectionEntity.getStatus() != null ? sectionEntity.getStatus().toString() : null);
                    section.setDescription(sectionEntity.getDescription());
                    section.setIndex(sectionEntity.getIndex()!=null?sectionEntity.getIndex():0);
//                    List<LessonEntity> lessons = lessonRepository.findBySectionId(sectionEntity.getId());
//                    List<GetLessonDetailResponse> lessonDetails = new ArrayList<>();
//                    for (LessonEntity lesson : lessons){
//                        GetLessonDetailResponse lessonDetail = lessonRepository.getLessonWithResourcesAndMediaAndSubstances(lesson.getId());
//                        lessonDetails.add(lessonDetail);
//                    }
//                    section.setLessons(lessonDetails);
                    sections.add(section);

                }
                resData.setSections(sections);
                return resData;

       }
         catch (Exception e){
              throw new IllegalArgumentException(e.getMessage());
         }
    }

    @Override
    public void importClassRoom(ImportClassRoomRequest body) {
        try{
            if (body.getFile()==null){
                throw new IllegalArgumentException(" File is required");
            }
            ObjectMapper mapper = new ObjectMapper();
            List<List<String>> data = excelReader.readExcel(body.getFile().getInputStream());
            for (int i = 1; i < data.size(); i++) {
                List<String> row = data.get(i);
                ClassRoomEntity classRoomEntity = new ClassRoomEntity();
                classRoomEntity.setName(row.get(0));
                classRoomEntity.setDescription(row.get(1));

                classRoomEntity.setTeacherId(row.get(4));
                if (termRepository.findById(row.get(3)).isEmpty()){
                    throw new IllegalArgumentException("TermId is not found");
                }
                classRoomEntity.setTermId(row.get(3));
                classRoomEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                classRoomEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                classRoomRepository.save(classRoomEntity);
            }
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomRecentResponse getRecentClasses(int page, int size, String studentId) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Slice<GetClassRoomRecentResponse.ClassRoomResponse> classRooms = studentEnrollmentsRepository.getRecentClasses(studentId, pageAble);
            GetClassRoomRecentResponse resData = new GetClassRoomRecentResponse();
            resData.setTotalPage(classRooms.getSize()/size);
            resData.setTotalElements(classRooms.getNumberOfElements());
            resData.setClassRooms(classRooms.getContent());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomRecentResponse getRecentClassesByTeacherId(int page, int size, String teacherId) {
        try {
            int skip = page * size;
            List<RecentClassDTO> classRooms = recentClassRepository.findRecentClassesByTeacherId(teacherId, skip, size);

            long totalElements = recentClassRepository.countRecentClassesByTeacherId(teacherId);
            int totalPages = (int) Math.ceil((double) totalElements / size);

            GetClassRoomRecentResponse resData = new GetClassRoomRecentResponse();
            resData.setTotalElements((int) totalElements);
            resData.setTotalPage(totalPages);
            List<GetClassRoomRecentResponse.ClassRoomResponse> data = new ArrayList<>();
            for (RecentClassDTO classRoom : classRooms) {
                GetClassRoomRecentResponse.ClassRoomResponse classRoomResponse = modelMapperService.mapClass(classRoom, GetClassRoomRecentResponse.ClassRoomResponse.class);
                data.add(classRoomResponse);
            }
            resData.setClassRooms(data);
            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void joinClassRoom(String classroomId, String studentId) {
        try {
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (classRoomRepository.findById(classroomId).isEmpty()) {
                throw new IllegalArgumentException("ClassroomId is not found");
            }
            StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndClassroomId(classroomId, studentId);
            if (studentEnrollmentsEntity != null) {
                throw new IllegalArgumentException("Student is already enrolled in this class");
            }
            JoinClassRequestEntity joinClassRequestEntity = new JoinClassRequestEntity();
            joinClassRequestEntity.setClassroomId(classroomId);
            joinClassRequestEntity.setStudentId(studentId);
            joinClassRequestEntity.setStatus(JoinRequestStatus.PENDING);
            joinClassRequestEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            joinClassRequestEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            joinClassRequestRepository.save(joinClassRequestEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetJoinClassResponse getJoinClassRequests(int page, int size, String classroomId, String teacherId, String email, String name) {
        try {
            ClassRoomEntity classRoom = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            if (!classRoom.getTeacherId().equals(teacherId)) {
                throw new IllegalArgumentException("TeacherId is not authorized to view join requests");
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<JoinClassRequestEntity> joinRequests = joinClassRequestRepository.findByClassroomId(classroomId, pageable);

            List<GetJoinClassResponse.JoinRequest> data = new ArrayList<>();
            long totalElements = 0;
            for (JoinClassRequestEntity joinRequest : joinRequests) {
                StudentEntity student = studentRepository.findById(joinRequest.getStudentId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                if ((email == null || student.getUser().getEmail().matches(".*" + email + ".*")) &&
                        (name == null || student.getUser().getFullname().matches(".*" + name + ".*"))) {
                    GetJoinClassResponse.JoinRequest resJoinRequest = new GetJoinClassResponse.JoinRequest();
                    resJoinRequest.setId(joinRequest.getId());
                    resJoinRequest.setStudent(student);
                    resJoinRequest.setClassroomId(joinRequest.getClassroomId());
                    resJoinRequest.setStatus(joinRequest.getStatus().toString());
                    resJoinRequest.setCreatedAt(joinRequest.getCreatedAt());
                    resJoinRequest.setUpdatedAt(joinRequest.getUpdatedAt());
                    data.add(resJoinRequest);
                    totalElements++;
                }
            }
            GetJoinClassResponse res = new GetJoinClassResponse();
            res.setJoinRequests(data);
            res.setTotalPage((int) Math.ceil((double) totalElements / size));
            res.setTotalElements(totalElements);
            return res;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public void acceptJoinClass(String classroomId, String studentId) {
        try {
            JoinClassRequestEntity joinRequest = joinClassRequestRepository.findByClassroomIdAndStudentId(classroomId, studentId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            joinRequest.setStatus(JoinRequestStatus.APPROVED);
            joinRequest.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            joinClassRequestRepository.save(joinRequest);
            StudentEnrollmentsEntity student = new StudentEnrollmentsEntity();
            student.setClassroomId(classroomId);
            student.setStudentId(studentId);
            student.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            student.setEnrolledAt(String.valueOf(System.currentTimeMillis()));
            student.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsRepository.save(student);


            ClassRoomEntity classRoom = classRoomRepository.findById(classroomId
            ).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            classRoom.setCurrentEnrollment(classRoom.getCurrentEnrollment() + 1);
            classRoomRepository.save(classRoom);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void rejectJoinClass(String classroomId, String studentId) {
        try {
            JoinClassRequestEntity joinRequest = joinClassRequestRepository.findByClassroomIdAndStudentId(classroomId, studentId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            joinRequest.setStatus(JoinRequestStatus.REJECTED);
            joinRequest.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            joinClassRequestRepository.save(joinRequest);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public InviteClassByEmailResponse inviteStudentByEmail(InviteStudentByEmailRequest body) {
        try{
            ClassRoomEntity classRoom = classRoomRepository.findById(body.getClassroomId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            if (!classRoom.getTeacherId().equals(body.getTeacherId())) {
                throw new IllegalArgumentException("TeacherId is not authorized to invite students");
            }
            List<String> errors = new ArrayList<>();
            List<String> success = new ArrayList<>();
            if(body.getInviteType().equals("FILE")){
                if (body.getFile()==null){
                    throw new IllegalArgumentException("File is required");
                }
                List<List<String>> data = excelReader.readExcel(body.getFile().getInputStream());
                for (int i = 0; i < data.size(); i++) {
                    List<String> row = data.get(i);
                    StudentEntity studentUser = studentRepository.findByEmail(row.get(0));
                    if (studentUser == null) {
                        errors.add("Student with email " + row.get(0) + " not found");
                        continue;
                    }
                    StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndClassroomId( studentUser.getId(),classRoom.getId());
                    if (studentEnrollmentsEntity != null) {
                        errors.add("Student with email " + row.get(0) + " is already enrolled in this class");
                        continue;
                    }
                    StudentEnrollmentsEntity newData = new StudentEnrollmentsEntity();
                    newData.setStudentId(studentUser.getId());
                    newData.setClassroomId(classRoom.getId());
                    newData.setGrade("0");
                    newData.setStatus(StudentEnrollmentStatus.IN_PROGRESS);
                    newData.setEnrolledAt(String.valueOf(System.currentTimeMillis()));
                    newData.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                    newData.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                    studentEnrollmentsRepository.save(newData);
                    success.add(row.get(0));
                }
            }else{

                for (String email : body.getEmails()) {
                    StudentEntity studentUser = studentRepository.findByEmail(email);
                    if (studentUser == null) {
                        throw new IllegalArgumentException("Student with email " + email + " not found");
                    }
                    StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndClassroomId( studentUser.getId(),classRoom.getId());
                    if (studentEnrollmentsEntity != null) {
                        errors.add("Student with email " + email + " is already enrolled in this class");
                        continue;
                    }
                    StudentEnrollmentsEntity newData = new StudentEnrollmentsEntity();
                    newData.setStudentId(studentUser.getId());
                    newData.setClassroomId(classRoom.getId());
                    newData.setGrade("0");
                    newData.setStatus(StudentEnrollmentStatus.IN_PROGRESS);
                    newData.setEnrolledAt(String.valueOf(System.currentTimeMillis()));
                    newData.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                    newData.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                    studentEnrollmentsRepository.save(newData);
                    success.add(email);
                }
            }
            InviteClassByEmailResponse res = new InviteClassByEmailResponse();
            res.setSuccess(success);
            res.setFail(errors);
            return res;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetDetailStudentInClassResponse getDetailStudentInClass(String classroomId, String studentId) {
        try {
            ClassRoomEntity classRoom = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            StudentEntity student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            StudentEnrollmentsEntity studentEnrollment = studentEnrollmentsRepository.findByStudentIdAndClassroomId(studentId, classroomId);
            if (studentEnrollment == null) {
                throw new IllegalArgumentException("Student is not enrolled in this class");
            }
            GetDetailStudentInClassResponse res = modelMapperService.mapClass(student, GetDetailStudentInClassResponse.class);
            List<DeadlineEntity> listDeadlineOfClass = deadlineRepository.findAllByClassroomId(classroomId);
            List<TestResultOfTestResponse> listTestResult = testResultRepository.findByStudentIdAndClassroomId(studentId, classroomId);
            List<DeadlineSubmissionsEntity> listDeadlineSubmission = deadlineSubmissionsRepository.findByStudentIdAndClassroomId(studentId, classroomId);
            Pageable pageable = PageRequest.of(0, 99);
            Page<TestEntity> listTestOfClass = testRepository.findByClassroomId(classroomId, pageable);
            List<TestEntity> listTest = listTestOfClass.getContent();

            // Fetch all test results
            List<TestResultEntity> allTestResults = listTest.stream()
                    .map(test -> listTestResult.stream()
                            .filter(result -> result.getTestId().equals(test.getId()))
                            .findFirst()
                            .map(result -> {
                                TestResultEntity entity = new TestResultEntity();
                                entity.setId(result.getResultId());
                                entity.setTestId(result.getTestId());
                                entity.setStudentId(result.getStudentId());
                                entity.setGrade(result.getGrade());
                                entity.setPassed(result.isPassed());
                                entity.setAttendedAt(result.getAttendedAt());
                                entity.setFinishedAt(result.getFinishedAt());
                                entity.setState(TestState.valueOf(result.getState()));
                                return entity;
                            })
                            .orElseGet(() -> {
                                TestResultEntity notAttempted = new TestResultEntity();
                                notAttempted.setTestId(test.getId());
                                notAttempted.setStudentId(studentId);
                                notAttempted.setState(TestState.NOT_STARTED);
                                return notAttempted;
                            }))
                    .collect(Collectors.toList());
            // Fetch all deadline submissions
            List<DeadlineSubmissionsEntity> allDeadlineSubmissions = listDeadlineOfClass.stream()
                    .map(deadline -> listDeadlineSubmission.stream()
                            .filter(submission -> submission.getDeadlineId().equals(deadline.getId()))
                            .findFirst()
                            .orElseGet(() -> {
                                DeadlineSubmissionsEntity notAttempted = new DeadlineSubmissionsEntity();
                                notAttempted.setDeadlineId(deadline.getId());
                                notAttempted.setStudentId(studentId);
                                notAttempted.setStatus(DeadlineSubmissionStatus.NOT_SUBMITTED);
                                return notAttempted;
                            }))
                    .collect(Collectors.toList());

            res.setStudentAvatar(student.getUser().getAvatar());
            res.setStudentName(student.getUser().getFullname());
            res.setTestResults(allTestResults);
            res.setDeadlineSubmissions(allDeadlineSubmissions);
            return res;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
