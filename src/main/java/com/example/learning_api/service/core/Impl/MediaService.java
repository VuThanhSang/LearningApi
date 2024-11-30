package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.media.CreateMediaRequest;
import com.example.learning_api.dto.request.media.UpdateMediaRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.media.GetMediaCommentsResponse;
import com.example.learning_api.dto.response.media.GetMediaDetailResponse;
import com.example.learning_api.dto.response.media.GetMediaNotesResponse;
import com.example.learning_api.dto.response.media.GetMediaResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IMediaService;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService implements IMediaService {
    private final ModelMapperService modelMapperService;
    private final LessonRepository lessonRepository;
    private final CloudinaryService cloudinaryService;
    private final MediaRepository mediaRepository;
    private final MediaProgressRepository mediaProgressRepository;
    private final MediaCommentRepository mediaCommentRepository;
    private final MediaNoteRepository mediaNoteRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final ClassRoomRepository classroomRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    @Override
    public void createMedia(CreateMediaRequest body) {
        try {
            if (body.getLessonId() == null) {
                throw new IllegalArgumentException("LessonId is required");
            }
            LessonEntity lessonEntity = lessonRepository.findById(body.getLessonId()).orElseThrow(() -> new IllegalArgumentException("LessonId is not found"));
            SectionEntity sectionEntity = sectionRepository.findById(lessonEntity.getSectionId()).orElseThrow(() -> new IllegalArgumentException("SectionId is not found"));
            if (lessonEntity == null) {
                throw new IllegalArgumentException("Lesson is not found");
            }
            MediaEntity mediaEntity = modelMapperService.mapClass(body, MediaEntity.class);
            if (body.getFilePath() != null) {
                mediaEntity.setUrl(body.getFilePath());
                mediaEntity.setFileType(body.getFilePath().substring(body.getFilePath().lastIndexOf(".")));
                mediaEntity.setFileName(body.getFilePath().substring(body.getFilePath().lastIndexOf("/") + 1));
                mediaEntity.setFileSize("0");

                // Get video duration from URL
                URL url = new URL(body.getFilePath());
                URLConnection connection = url.openConnection();
                Tika tika = new Tika();
                Metadata metadata = new Metadata();
                AutoDetectParser parser = new AutoDetectParser();
                BodyContentHandler handler = new BodyContentHandler();
                parser.parse(connection.getInputStream(), handler, metadata, new ParseContext());
                String duration = metadata.get("xmpDM:duration");
                if (duration != null) {
                    mediaEntity.setDuration((int) (Double.parseDouble(duration) / 1000)); // Convert milliseconds to seconds
                }
            } else if (body.getFile() != null) {
                byte[] fileBytes = body.getFile().getBytes();
                String fileType = body.getFile().getOriginalFilename().substring(body.getFile().getOriginalFilename().lastIndexOf("."));
                CloudinaryUploadResponse fileUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "Media") + fileType,
                        fileBytes,
                        "video"
                );
                mediaEntity.setUrl(fileUploaded.getUrl());
                mediaEntity.setFileType(fileType);
                mediaEntity.setFileName(body.getFile().getOriginalFilename());
                mediaEntity.setFileSize(String.valueOf(body.getFile().getSize()));

                // Get video duration from URL
                URL url = new URL(fileUploaded.getUrl());
                URLConnection connection = url.openConnection();
                Tika tika = new Tika();
                Metadata metadata = new Metadata();
                AutoDetectParser parser = new AutoDetectParser();
                BodyContentHandler handler = new BodyContentHandler();
                parser.parse(connection.getInputStream(), handler, metadata, new ParseContext());
                String duration = metadata.get("xmpDM:duration");
                if (duration != null) {
                    mediaEntity.setDuration((int) (Double.parseDouble(duration) / 1000)); // Convert milliseconds to seconds
                }
            }
            mediaEntity.setClassroomId(sectionEntity.getClassRoomId());
            mediaEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            mediaEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            mediaRepository.save(mediaEntity);
        } catch (Exception e) {
            log.error("Error in createMedia: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public void deleteMedia(String mediaId) {
        try{
            if (mediaRepository.findById(mediaId ).isEmpty()){
                throw new IllegalArgumentException("MediaId is not found");
            }
            mediaRepository.deleteById(mediaId);
        }
        catch (Exception e) {
            log.error("Error in deleteMedia: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void updateMedia(UpdateMediaRequest body) {
        try{
            MediaEntity mediaEntity= mediaRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (body.getId()==null){
                throw new IllegalArgumentException("Id is required");
            }
            if (mediaEntity==null){
                throw new IllegalArgumentException("Media is not found");
            }
            if (body.getFile() != null) {
                byte[] fileBytes = body.getFile().getBytes();
                String fileType = body.getFile().getOriginalFilename().substring(body.getFile().getOriginalFilename().lastIndexOf("."));
                CloudinaryUploadResponse fileUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "Media") + fileType,
                        fileBytes,
                        "video"
                );
                mediaEntity.setUrl(fileUploaded.getUrl());
                mediaEntity.setFileType(fileType);
                mediaEntity.setFileName(body.getFile().getOriginalFilename());
                mediaEntity.setFileSize(String.valueOf(body.getFile().getSize()));
            }
            if (body.getName()!=null){
                mediaEntity.setName(body.getName());
            }
            if (body.getDescription()!=null){
                mediaEntity.setDescription(body.getDescription());
            }
            mediaRepository.save(mediaEntity);
        }
        catch (Exception e) {
            log.error("Error in updateMedia: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetMediaDetailResponse getMedia(String mediaId) {
        try {
            MediaEntity mediaEntity = mediaRepository.findById(mediaId).orElseThrow(() -> new IllegalArgumentException("Id is not found"));
            if (mediaEntity == null) {
                throw new IllegalArgumentException("Media is not found");
            }

            String classRoomId = sectionRepository.findById(lessonRepository.findById(mediaEntity.getLessonId()).get().getSectionId()).get().getClassRoomId();
            String userId = classroomRepository.findById(classRoomId).get().getTeacherId();
            GetMediaDetailResponse getMediaDetailResponse = modelMapperService.mapClass(mediaEntity, GetMediaDetailResponse.class);

            List<MediaNoteEntity> mediaNoteEntities = mediaNoteRepository.findByMediaIdAndUserId(mediaId, userId, RoleEnum.TEACHER.name());
            mediaNoteEntities.sort(Comparator.comparing((MediaNoteEntity note) -> {
                String importanceLevel = note.getImportanceLevel() != null ? note.getImportanceLevel() : "LOW";
                switch (importanceLevel) {
                    case "HIGH":
                        return 1;
                    case "MEDIUM":
                        return 2;
                    default:
                        return 3;
                }
            }));

            Map<Integer, List<GetMediaDetailResponse.MediaNote>> groupedNotes = new HashMap<>();

            for (MediaNoteEntity mediaNoteEntity : mediaNoteEntities) {
                int time = mediaNoteEntity.getTime();
                GetMediaDetailResponse.MediaNote mediaNote = modelMapperService.mapClass(mediaNoteEntity, GetMediaDetailResponse.MediaNote.class);
                TeacherEntity userEntity = teacherRepository.findById(mediaNoteEntity.getUserId()).orElseThrow(() -> new IllegalArgumentException("UserId is not found"));
                mediaNote.setAuthorName(userEntity.getUser().getFullname());
                mediaNote.setAuthorId(userEntity.getId());
                mediaNote.setAvatar(userEntity.getUser().getAvatar());
                mediaNote.setImportanceLevel(mediaNoteEntity.getImportanceLevel() != null ? mediaNoteEntity.getImportanceLevel() : "LOW");

                groupedNotes.computeIfAbsent(time, k -> new ArrayList<>()).add(mediaNote);
            }

            List<GetMediaDetailResponse.TimeGroupedNotes> timeGroupedNotes = groupedNotes.entrySet().stream()
                    .map(entry -> {
                        GetMediaDetailResponse.TimeGroupedNotes timeGroupedNote = new GetMediaDetailResponse.TimeGroupedNotes();
                        timeGroupedNote.setTime(entry.getKey());
                        timeGroupedNote.setMediaNotes(entry.getValue());
                        return timeGroupedNote;
                    })
                    .collect(Collectors.toList());

            getMediaDetailResponse.setNotes(timeGroupedNotes);
            return getMediaDetailResponse;
        } catch (Exception e) {
            log.error("Error in getMedia: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetMediaResponse getMediaByLessonId(String lessonId, Integer page, Integer size) {
       try {
              Pageable pageAble = PageRequest.of(page, size);
              Page<MediaEntity> mediaEntities = mediaRepository.findByLessonId(lessonId, pageAble);
              GetMediaResponse getMediaResponse = new GetMediaResponse();
              List<GetMediaResponse.MediaResponse> mediaResponses = new ArrayList<>();
                for (MediaEntity mediaEntity : mediaEntities) {
                    GetMediaResponse.MediaResponse mediaResponse = modelMapperService.mapClass(mediaEntity, GetMediaResponse.MediaResponse.class);
                    GetMediaResponse.FileResponse fileResponse = new GetMediaResponse.FileResponse();
                    fileResponse.setUrl(mediaEntity.getUrl());
                    fileResponse.setFileType(mediaEntity.getFileType());
                    fileResponse.setFileName(mediaEntity.getFileName());
                    fileResponse.setFileSize(mediaEntity.getFileSize());
                    mediaResponse.setFile(fileResponse);
                    mediaResponses.add(mediaResponse);

                }
                getMediaResponse.setMedia(mediaResponses);
                getMediaResponse.setTotalPage(mediaEntities.getTotalPages());
                getMediaResponse.setTotalElements(mediaEntities.getTotalElements());

                return getMediaResponse;

       }
         catch (Exception e) {
              log.error("Error in getMediaByLessonId: ", e);
              throw new IllegalArgumentException(e.getMessage());
         }
    }

    @Override
    public GetMediaResponse getMediaByClassroomId(String classroomId, Integer page, Integer size) {
        try {
            Pageable pageAble = PageRequest.of(page, size);
            Page<MediaEntity> mediaEntities = mediaRepository.findByClassroomId(classroomId, pageAble);
            GetMediaResponse getMediaResponse = new GetMediaResponse();
            List<GetMediaResponse.MediaResponse> mediaResponses = new ArrayList<>();
            for (MediaEntity mediaEntity : mediaEntities) {
                GetMediaResponse.MediaResponse mediaResponse = modelMapperService.mapClass(mediaEntity, GetMediaResponse.MediaResponse.class);
                GetMediaResponse.FileResponse fileResponse = new GetMediaResponse.FileResponse();
                fileResponse.setUrl(mediaEntity.getUrl());
                fileResponse.setFileType(mediaEntity.getFileType());
                fileResponse.setFileName(mediaEntity.getFileName());
                fileResponse.setFileSize(mediaEntity.getFileSize());
                mediaResponse.setFile(fileResponse);
                mediaResponses.add(mediaResponse);

            }
            getMediaResponse.setMedia(mediaResponses);
            getMediaResponse.setTotalPage(mediaEntities.getTotalPages());
            getMediaResponse.setTotalElements(mediaEntities.getTotalElements());

            return getMediaResponse;

        }
        catch (Exception e) {
            log.error("Error in getMediaByLessonId: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateMediaProgress(MediaProgressEntity body) {
        try{
            MediaProgressEntity mediaProgressEntity ;
            if (body.getId()!=null){
                mediaProgressEntity= mediaProgressRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            }else{
                mediaProgressEntity=null;
            }
            MediaEntity mediaEntity= mediaRepository.findById(body.getMediaId()).orElseThrow(()->new IllegalArgumentException("MediaId is not found"));
            if (mediaEntity==null){
                throw new IllegalArgumentException("Media is not found");
            }
            if (mediaProgressEntity==null){
                MediaProgressEntity mediaProgressEntity1 = new MediaProgressEntity();
                mediaProgressEntity1.setUserId(body.getUserId());
                mediaProgressEntity1.setMediaId(body.getMediaId());
                mediaProgressEntity1.setWatchedDuration(body.getWatchedDuration());
                mediaProgressEntity1.setCompleted(body.isCompleted());
                mediaProgressEntity1.setLastWatchedAt(body.getLastWatchedAt());
                mediaProgressRepository.save(mediaProgressEntity1);
            }else{
                if (body.getWatchedDuration()!=null){
                    mediaProgressEntity.setWatchedDuration(body.getWatchedDuration());
                }
                if (body.isCompleted()){
                    mediaProgressEntity.setCompleted(body.isCompleted());
                }
                if (body.getLastWatchedAt()!=null){
                    mediaProgressEntity.setLastWatchedAt(body.getLastWatchedAt());
                }
                mediaProgressEntity.setLastWatchedAt(new Date().toString());
                mediaProgressRepository.save(mediaProgressEntity);
            }



        }
        catch (Exception e) {
            log.error("Error in updateMediaProgress: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public MediaProgressEntity getMediaProgress(String userId, String mediaId) {
        try{
            MediaEntity mediaEntity= mediaRepository.findById(mediaId).orElseThrow(()->new IllegalArgumentException("MediaId is not found"));
            if (mediaEntity==null){
                throw new IllegalArgumentException("Media is not found");
            }
            UserEntity userEntity= userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("UserId is not found"));
            if (userEntity==null){
                throw new IllegalArgumentException("User is not found");
            }
            MediaProgressEntity data =  mediaProgressRepository.findByUserIdAndMediaId(userId, mediaId);
            if (data==null){
                MediaProgressEntity mediaProgressEntity = new MediaProgressEntity();
                mediaProgressEntity.setUserId(userId);
                mediaProgressEntity.setMediaId(mediaId);
                mediaProgressEntity.setWatchedDuration(0);
                mediaProgressEntity.setCompleted(false);
                mediaProgressEntity.setLastWatchedAt(new Date().toString());
                mediaProgressRepository.save(mediaProgressEntity);
                return mediaProgressEntity;
            }
            return data;
        }
        catch (Exception e) {
            log.error("Error in getMediaProgress: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void createMediaComment(MediaCommentEntity body) {
        try{
            if (body.getMediaId()==null){
                throw new IllegalArgumentException("MediaId is required");
            }
            if (mediaRepository.findById(body.getMediaId()).isEmpty()){
                throw new IllegalArgumentException("MediaId is not found");
            }
            if (body.getUserId()==null){
                throw new IllegalArgumentException("UserId is required");
            }
            if (userRepository.findById(body.getUserId()).isEmpty()){
                throw new IllegalArgumentException("UserId is not found");
            }
            MediaCommentEntity mediaCommentEntity = modelMapperService.mapClass(body, MediaCommentEntity.class);
            if (body.getIsReply()==null) {
                mediaCommentEntity.setIsReply(false);
            }else if (body.getIsReply()){
                if (body.getReplyTo()==null){
                    throw new IllegalArgumentException("ReplyTo is required");
                }
                if (mediaCommentRepository.findById(body.getReplyTo()).isEmpty()){
                    throw new IllegalArgumentException("ReplyTo is not found");
                }
            }
            mediaCommentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            mediaCommentRepository.save(mediaCommentEntity);
        }
        catch (Exception e) {
            log.error("Error in createMediaComment: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void updateMediaComment(MediaCommentEntity body) {
        try{
            MediaCommentEntity mediaCommentEntity= mediaCommentRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (body.getId()==null){
                throw new IllegalArgumentException("Id is required");
            }
            if (mediaCommentEntity==null){
                throw new IllegalArgumentException("MediaComment is not found");
            }
            if (body.getMediaId()==null){
                throw new IllegalArgumentException("MediaId is required");
            }
            if (mediaRepository.findById(body.getMediaId()).isEmpty()){
                throw new IllegalArgumentException("MediaId is not found");
            }
            if (body.getUserId()==null){
                throw new IllegalArgumentException("UserId is required");
            }
            if (userRepository.findById(body.getUserId()).isEmpty()){
                throw new IllegalArgumentException("UserId is not found");
            }
            if (body.getContent()!=null){
                mediaCommentEntity.setContent(body.getContent());
            }
            mediaCommentRepository.save(mediaCommentEntity);
        }
        catch (Exception e) {
            log.error("Error in updateMediaComment: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteMediaComment(String commentId) {
        try {
            if (mediaCommentRepository.findById(commentId).isEmpty()) {
                throw new IllegalArgumentException("CommentId is not found");
            }
            mediaCommentRepository.deleteById(commentId);

        }
        catch (Exception e) {
            log.error("Error in deleteMediaComment: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }



    @Override
    public MediaCommentEntity getMediaComment(String commentId) {
        try{
            return mediaCommentRepository.findById(commentId).orElseThrow(()->new IllegalArgumentException("Id is not found"));
        }
        catch (Exception e) {
            log.error("Error in getMediaComment: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetMediaCommentsResponse getMediaCommentByMediaId(String mediaId, Integer page, Integer size) {
        try {
            Pageable pageAble = PageRequest.of(page, size);
            Page<MediaCommentEntity> mediaCommentEntities = mediaCommentRepository.findByMediaIdAndIsReplyFalse(mediaId, pageAble);
            GetMediaCommentsResponse getMediaResponse = new GetMediaCommentsResponse();
            List<GetMediaCommentsResponse.MediaCommentResponse> mediaResponses = new ArrayList<>();
            for (MediaCommentEntity mediaCommentEntity : mediaCommentEntities) {
                GetMediaCommentsResponse.MediaCommentResponse mediaResponse = modelMapperService.mapClass(mediaCommentEntity, GetMediaCommentsResponse.MediaCommentResponse.class);
                UserEntity userEntity = userRepository.findById(mediaCommentEntity.getUserId()).orElseThrow(() -> new IllegalArgumentException("UserId is not found"));
                if (userEntity.getRole().equals(RoleEnum.USER)){
                    StudentEntity studentEntity = studentRepository.findByUserId(userEntity.getId());
                    studentEntity.setUser(null);
                    userEntity.setStudent(studentEntity);
                }
                else{
                    TeacherEntity teacherEntity = teacherRepository.findByUserId(userEntity.getId());
                    teacherEntity.setUser(null);
                    userEntity.setTeacher(teacherEntity);
                }
                mediaResponse.setUser(userEntity);
                mediaResponse.setTotalReply(mediaCommentRepository.countByReplyTo(mediaCommentEntity.getId()));
                mediaResponses.add(mediaResponse);
            }
            getMediaResponse.setMediaComments(mediaResponses);
            getMediaResponse.setTotalPage(mediaCommentEntities.getTotalPages());
            getMediaResponse.setTotalElements(mediaCommentEntities.getTotalElements());

            return getMediaResponse;

        } catch (Exception e) {
            log.error("Error in getMediaCommentByMediaId: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetMediaCommentsResponse getMediaCommentByUserId(String userId, Integer page, Integer size) {
        try {
            Pageable pageAble = PageRequest.of(page, size);
            Page<MediaCommentEntity> mediaCommentEntities = mediaCommentRepository.findByUserId(userId, pageAble);
            GetMediaCommentsResponse getMediaResponse = new GetMediaCommentsResponse();
            List<GetMediaCommentsResponse.MediaCommentResponse> mediaResponses = new ArrayList<>();
            for (MediaCommentEntity mediaCommentEntity : mediaCommentEntities) {
                GetMediaCommentsResponse.MediaCommentResponse mediaResponse = modelMapperService.mapClass(mediaCommentEntity, GetMediaCommentsResponse.MediaCommentResponse.class);
                UserEntity userEntity = userRepository.findById(mediaCommentEntity.getUserId()).orElseThrow(()->new IllegalArgumentException("UserId is not found"));
                if (userEntity.getRole().equals(RoleEnum.USER)){
                    StudentEntity studentEntity = studentRepository.findByUserId(userEntity.getId());
                    studentEntity.setUser(null);
                    userEntity.setStudent(studentEntity);
                }
                else{
                    TeacherEntity teacherEntity = teacherRepository.findByUserId(userEntity.getId());
                    teacherEntity.setUser(null);
                    userEntity.setTeacher(teacherEntity);
                }
                mediaResponse.setUser(userEntity);
                mediaResponse.setTotalReply(mediaCommentRepository.countByReplyTo(mediaCommentEntity.getId()));
                mediaResponses.add(mediaResponse);

            }
            getMediaResponse.setMediaComments(mediaResponses);
            getMediaResponse.setTotalPage(mediaCommentEntities.getTotalPages());
            getMediaResponse.setTotalElements(mediaCommentEntities.getTotalElements());

            return getMediaResponse;

        }
        catch (Exception e) {
            log.error("Error in getMediaCommentByUserId: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetMediaCommentsResponse getCommentReply(String commentId, Integer page, Integer size) {
        try {
            Pageable pageAble = PageRequest.of(page, size);
            Page<MediaCommentEntity> mediaCommentEntities = mediaCommentRepository.findByReplyTo(commentId, pageAble);
            GetMediaCommentsResponse getMediaResponse = new GetMediaCommentsResponse();
            List<GetMediaCommentsResponse.MediaCommentResponse> mediaResponses = new ArrayList<>();
            for (MediaCommentEntity mediaCommentEntity : mediaCommentEntities) {
                GetMediaCommentsResponse.MediaCommentResponse mediaResponse = modelMapperService.mapClass(mediaCommentEntity, GetMediaCommentsResponse.MediaCommentResponse.class);
                UserEntity userEntity = userRepository.findById(mediaCommentEntity.getUserId()).orElseThrow(()->new IllegalArgumentException("UserId is not found"));
                if (userEntity.getRole().equals(RoleEnum.USER)){
                    StudentEntity studentEntity = studentRepository.findByUserId(userEntity.getId());
                    studentEntity.setUser(null);
                    userEntity.setStudent(studentEntity);
                }
                else{
                    TeacherEntity teacherEntity = teacherRepository.findByUserId(userEntity.getId());
                    teacherEntity.setUser(null);
                    userEntity.setTeacher(teacherEntity);
                }
                mediaResponse.setUser(userEntity);
                mediaResponses.add(mediaResponse);

            }
            getMediaResponse.setMediaComments(mediaResponses);
            getMediaResponse.setTotalPage(mediaCommentEntities.getTotalPages());
            getMediaResponse.setTotalElements(mediaCommentEntities.getTotalElements());

            return getMediaResponse;

        }
        catch (Exception e) {
            log.error("Error in getCommentReply: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void createMediaNote(MediaNoteEntity body) {
        try{
            if (body.getMediaId()==null){
                throw new IllegalArgumentException("MediaId is required");
            }
            if (mediaRepository.findById(body.getMediaId()).isEmpty()){
                throw new IllegalArgumentException("MediaId is not found");
            }
            if (body.getUserId()==null){
                throw new IllegalArgumentException("UserId is required");
            }
            MediaNoteEntity mediaNoteEntity = modelMapperService.mapClass(body, MediaNoteEntity.class);
            mediaNoteEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            mediaNoteRepository.save(mediaNoteEntity);
        }
        catch (Exception e) {
            log.error("Error in createMediaNote: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void updateMediaNote(MediaNoteEntity body) {
        try{
            MediaNoteEntity mediaNoteEntity= mediaNoteRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (body.getId()==null){
                throw new IllegalArgumentException("Id is required");
            }
            if (mediaNoteEntity==null){
                throw new IllegalArgumentException("MediaNote is not found");
            }


            if (body.getImportanceLevel()!=null){
                mediaNoteEntity.setImportanceLevel(body.getImportanceLevel());
            }
            if (body.getContent()!=null){
                mediaNoteEntity.setContent(body.getContent());
            }
            mediaNoteRepository.save(mediaNoteEntity);
        }
        catch (Exception e) {
            log.error("Error in updateMediaNote: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteMediaNote(String noteId) {
        try {
            if (mediaNoteRepository.findById(noteId).isEmpty()) {
                throw new IllegalArgumentException("NoteId is not found");
            }
            mediaNoteRepository.deleteById(noteId);

        }
        catch (Exception e) {
            log.error("Error in deleteMediaNote: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public MediaNoteEntity getMediaNote(String noteId) {
        try{
           return mediaNoteRepository.findById(noteId).orElseThrow(()->new IllegalArgumentException("Id is not found"));
        }
        catch (Exception e) {
            log.error("Error in getMediaNote: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }
    @Override
    public List<GetMediaDetailResponse.TimeGroupedNotes> getMediaNoteByMediaId(String mediaId, Integer page, Integer size) {
        try {
            List<MediaNoteEntity> mediaNoteEntities = mediaNoteRepository.findByMediaId(mediaId);
            mediaNoteEntities.sort(Comparator.comparing((MediaNoteEntity note) -> {
                String importanceLevel = note.getImportanceLevel() != null ? note.getImportanceLevel() : "LOW";
                switch (importanceLevel) {
                    case "HIGH":
                        return 1;
                    case "MEDIUM":
                        return 2;
                    default:
                        return 3;
                }
            }));

            Map<Integer, List<GetMediaDetailResponse.MediaNote>> groupedNotes = new HashMap<>();

            for (MediaNoteEntity mediaNoteEntity : mediaNoteEntities) {
                int time = mediaNoteEntity.getTime();
                GetMediaDetailResponse.MediaNote mediaNote = modelMapperService.mapClass(mediaNoteEntity, GetMediaDetailResponse.MediaNote.class);
                if (mediaNoteEntity.getRole().equals(RoleEnum.USER)) {
                    StudentEntity userEntity = studentRepository.findById(mediaNoteEntity.getUserId()).orElseThrow(() -> new IllegalArgumentException("UserId is not found"));
                    mediaNote.setAuthorName(userEntity.getUser().getFullname());
                    mediaNote.setAuthorId(userEntity.getId());
                    mediaNote.setAvatar(userEntity.getUser().getAvatar());
                } else {
                    TeacherEntity userEntity = teacherRepository.findById(mediaNoteEntity.getUserId()).orElseThrow(() -> new IllegalArgumentException("UserId is not found"));
                    mediaNote.setAuthorName(userEntity.getUser().getFullname());
                    mediaNote.setAuthorId(userEntity.getId());
                    mediaNote.setAvatar(userEntity.getUser().getAvatar());
                }
                mediaNote.setImportanceLevel(mediaNoteEntity.getImportanceLevel() != null ? mediaNoteEntity.getImportanceLevel() : "LOW");

                groupedNotes.computeIfAbsent(time, k -> new ArrayList<>()).add(mediaNote);
            }

            List<GetMediaDetailResponse.TimeGroupedNotes> timeGroupedNotes = groupedNotes.entrySet().stream()
                    .map(entry -> {
                        GetMediaDetailResponse.TimeGroupedNotes timeGroupedNote = new GetMediaDetailResponse.TimeGroupedNotes();
                        timeGroupedNote.setTime(entry.getKey());
                        timeGroupedNote.setMediaNotes(entry.getValue());
                        return timeGroupedNote;
                    })
                    .collect(Collectors.toList());

            return timeGroupedNotes;
        } catch (Exception e) {
            log.error("Error in getMediaNoteByUserId: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<GetMediaDetailResponse.TimeGroupedNotes> getMediaNoteByUserIdAndMediaId(String userId, String role, String mediaId, Integer page, Integer size) {
        try {
            List<MediaNoteEntity> mediaNoteEntities = mediaNoteRepository.findByMediaIdAndUserId(mediaId, userId, role);
            mediaNoteEntities.sort(Comparator.comparing((MediaNoteEntity note) -> {
                String importanceLevel = note.getImportanceLevel() != null ? note.getImportanceLevel() : "LOW";
                switch (importanceLevel) {
                    case "HIGH":
                        return 1;
                    case "MEDIUM":
                        return 2;
                    default:
                        return 3;
                }
            }));

            Map<Integer, List<GetMediaDetailResponse.MediaNote>> groupedNotes = new HashMap<>();

            for (MediaNoteEntity mediaNoteEntity : mediaNoteEntities) {
                int time = mediaNoteEntity.getTime();
                GetMediaDetailResponse.MediaNote mediaNote = modelMapperService.mapClass(mediaNoteEntity, GetMediaDetailResponse.MediaNote.class);
                if (role.equals("USER")) {
                    StudentEntity userEntity = studentRepository.findById(mediaNoteEntity.getUserId()).orElseThrow(() -> new IllegalArgumentException("UserId is not found"));
                    mediaNote.setAuthorName(userEntity.getUser().getFullname());
                    mediaNote.setAuthorId(userEntity.getId());
                    mediaNote.setAvatar(userEntity.getUser().getAvatar());
                } else {
                    TeacherEntity userEntity = teacherRepository.findById(mediaNoteEntity.getUserId()).orElseThrow(() -> new IllegalArgumentException("UserId is not found"));
                    mediaNote.setAuthorName(userEntity.getUser().getFullname());
                    mediaNote.setAuthorId(userEntity.getId());
                    mediaNote.setAvatar(userEntity.getUser().getAvatar());
                }
                mediaNote.setImportanceLevel(mediaNoteEntity.getImportanceLevel() != null ? mediaNoteEntity.getImportanceLevel() : "LOW");

                groupedNotes.computeIfAbsent(time, k -> new ArrayList<>()).add(mediaNote);
            }

            List<GetMediaDetailResponse.TimeGroupedNotes> timeGroupedNotes = groupedNotes.entrySet().stream()
                    .map(entry -> {
                        GetMediaDetailResponse.TimeGroupedNotes timeGroupedNote = new GetMediaDetailResponse.TimeGroupedNotes();
                        timeGroupedNote.setTime(entry.getKey());
                        timeGroupedNote.setMediaNotes(entry.getValue());
                        return timeGroupedNote;
                    })
                    .collect(Collectors.toList());

            return timeGroupedNotes;
        } catch (Exception e) {
            log.error("Error in getMediaNoteByUserId: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
