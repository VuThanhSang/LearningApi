package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.dto.common.TagVoteAggregate;
import com.example.learning_api.dto.common.VoteAuthorDto;
import com.example.learning_api.dto.request.forum.*;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.forum.*;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.enums.ForumStatus;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IForumService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForumService implements IForumService {
    private final ForumRepository forumRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final VoteRepository voteRepository;
    private final TagRepository tagRepository;
    private final FileRepository fileRepository;
    private final ClassRoomRepository classRoomRepository;
    private final UserRepository userRepository;
    public void processFiles (List<MultipartFile> files,String title, String ownerId, FileOwnerType ownerType){
        if (files == null) {
            return;
        }
        if (files.isEmpty()) {
            return;
        }
        for (MultipartFile file : files) {
            try {
                FAQEntity.SourceDto fileDto = processFile(file, title);
                if (fileDto == null) {
                    continue;
                }
                FileEntity fileEntity = new FileEntity();
                fileEntity.setUrl(fileDto.getPath());
                fileEntity.setType(fileDto.getType().name());
                fileEntity.setOwnerType(ownerType);
                fileEntity.setOwnerId(ownerId);
                fileEntity.setExtension(fileDto.getPath().substring(fileDto.getPath().lastIndexOf(".") + 1));
                fileEntity.setName(file.getOriginalFilename());
                fileEntity.setSize(String.valueOf(file.getSize()));
                fileEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                fileRepository.save(fileEntity);
            } catch (IOException e) {
                log.error("Error processing file: ", e);
                throw new IllegalArgumentException("Error processing file: " + e.getMessage());
            }
        }
    }

    public FAQEntity.SourceDto processFile(MultipartFile file, String title) throws IOException {
        if (file.getOriginalFilename().equals("")){
            return null;
        }
        byte[] fileBytes = file.getBytes();
        String fileName = StringUtils.generateFileName(file.getOriginalFilename(), "deadline");
        CloudinaryUploadResponse response;

        String contentType = file.getContentType();
        if (contentType.startsWith("image/")) {
            byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
        } else if (contentType.startsWith("video/")) {
            String videoFileType = getFileExtension(file.getOriginalFilename());
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
        } else if (contentType.startsWith("application/")) {
            String docFileType = getFileExtension(file.getOriginalFilename());
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
        }  else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + ".docx", fileBytes, "raw");
        }
        else {
            throw new IllegalArgumentException("Unsupported source type");
        }

        return FAQEntity.SourceDto.builder()
                .path(response.getSecureUrl())
                .type(contentType.startsWith("image/") ? FaqSourceType.IMAGE : contentType.startsWith("video/") ? FaqSourceType.VIDEO : FaqSourceType.DOCUMENT)
                .build();
    }
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    @Override
    public void createForum(CreateForumRequest request) {
        try{
           if (userRepository.findById(request.getAuthorId()).isEmpty()) {
               throw new IllegalArgumentException("Author Id is not found");
           }
            ForumEntity forumEntity = modelMapperService.mapClass(request, ForumEntity.class);
            List<String> tags = new ArrayList<>();
            if (request.getIsForClass()!=null&&request.getIsForClass()){
                ClassRoomEntity classRoomEntity = classRoomRepository.findById(request.getClassId()).orElseThrow(()->new IllegalArgumentException("Class Id is not found"));
                TagEntity tagEntity = tagRepository.findByClassId(request.getClassId());
                if (tagEntity == null) {
                    tagEntity = new TagEntity();
                    tagEntity.setName(classRoomEntity.getName());
                    tagEntity.setClassId(classRoomEntity.getId());
                    tagEntity.setIsForClass(true);
                    tagEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                    tagEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                    tagEntity.setPostCount(1);
                    tagRepository.save(tagEntity);
                }else{
                    tagEntity.setPostCount(tagEntity.getPostCount()+1);
                    tagRepository.save(tagEntity);
                }
                tags.add(tagEntity.getId());
            }
            for (String tag : request.getTags()) {
                TagEntity tagEntity = tagRepository.findByName(tag);
                if (tagEntity == null) {
                    tagEntity = new TagEntity();
                    tagEntity.setName(tag);
                    tagEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                    tagEntity.setPostCount(1);
                    tagRepository.save(tagEntity);
                }else {
                    tagEntity.setPostCount(tagEntity.getPostCount()+1);
                    tagRepository.save(tagEntity);
                }
                tags.add(tagEntity.getId());

            }
            forumEntity.setTags(tags);
            forumEntity.setCommentCount(0);
            forumEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            forumEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            forumRepository.save(forumEntity);
            processFiles(request.getSources(), request.getTitle(), forumEntity.getId(), FileOwnerType.FORUM);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    @Override
    public void updateForum(UpdateForumRequest request) {
        try{
            ForumEntity forumEntity = forumRepository.findById(request.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (request.getTitle()!=null){
                forumEntity.setTitle(request.getTitle());
            }
            if (request.getContent()!=null){
                forumEntity.setContent(request.getContent());
            }
            if (request.getStatus()!=null){
                forumEntity.setStatus(ForumStatus.valueOf(request.getStatus()));
            }

            if (request.getSources()!=null) {
                processFiles(request.getSources(), request.getTitle(), forumEntity.getId(), FileOwnerType.FORUM);
            }
            forumEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            forumRepository.save(forumEntity);

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteForum(String id) {
        try{
            ForumEntity forumEntity = forumRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            forumCommentRepository.deleteByForumId(id);
            forumRepository.delete(forumEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }
    @Override
    public void voteForum(VoteRequest request) {
        try {
            if (request.getAuthorId()==null){
                throw new IllegalArgumentException("Author Id is required");
            }
            if(userRepository.findById(request.getAuthorId()).isEmpty()){
                throw new IllegalArgumentException("Author Id is not found");
            }
            if (request.getTargetType().equals("FORUM")) {
                ForumEntity forumEntity = forumRepository.findById(request.getTargetId()).orElseThrow(() -> new IllegalArgumentException("Forum Id is not found"));
            } else if (request.getTargetType().equals("FORUM_COMMENT")) {
                ForumCommentEntity forumCommentEntity = forumCommentRepository.findById(request.getTargetId()).orElseThrow(() -> new IllegalArgumentException("Forum Comment Id is not found"));
            } else {
                throw new IllegalArgumentException("Invalid target type");
            }
            VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(request.getAuthorId(), request.getTargetId());

            if (voteEntity != null) {
                handleExistingVote(request, voteEntity);
            } else {
                VoteEntity newVote = modelMapperService.mapClass(request, VoteEntity.class);
                newVote.setUpvote(request.getIsUpvote());
                voteRepository.save(newVote);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }



    @Override
    public GetVotesResponse getVotedByForum(String forumId) {
        try {
            List<VoteEntity> upvotes= voteRepository.findUpVoteByTargetId(forumId, "FORUM");
            List<VoteEntity> downvotes= voteRepository.findDownVoteByTargetId(forumId, "FORUM");
            GetVotesResponse getVotesResponse = new GetVotesResponse();
            List<UserEntity> upvoteData = new ArrayList<>();
            List<UserEntity> downvoteData = new ArrayList<>();
            for (VoteEntity voteEntity : upvotes) {
                UserEntity vote = userRepository.findById(voteEntity.getAuthorId()).get();
                upvoteData.add(vote);
            }
            for (VoteEntity voteEntity : downvotes) {
                UserEntity vote = userRepository.findById(voteEntity.getAuthorId()).get();
                downvoteData.add(vote);
            }

            getVotesResponse.setUpVotes(upvoteData);
            getVotesResponse.setDownVotes(downvoteData);
            getVotesResponse.setTotalElement(upvotes.size()+downvotes.size());
            getVotesResponse.setTotalDownvote(downvotes.size());
            getVotesResponse.setTotalUpvote(upvotes.size());
            return getVotesResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetVotesResponse getVoteByComment(String commentId) {
        try {
            List<VoteEntity> upvotes= voteRepository.findUpVoteByTargetId(commentId, "FORUM_COMMENT");
            List<VoteEntity> downvotes= voteRepository.findDownVoteByTargetId(commentId, "FORUM_COMMENT");
            GetVotesResponse getVotesResponse = new GetVotesResponse();
            List<UserEntity> upvoteData = new ArrayList<>();
            List<UserEntity> downvoteData = new ArrayList<>();
            for (VoteEntity voteEntity : upvotes) {
                UserEntity vote = userRepository.findById(voteEntity.getAuthorId()).get();

                upvoteData.add(vote);
            }
            for (VoteEntity voteEntity : downvotes) {
                UserEntity vote = userRepository.findById(voteEntity.getAuthorId()).get();

                downvoteData.add(vote);
            }
            getVotesResponse.setUpVotes(upvoteData);
            getVotesResponse.setDownVotes(downvoteData);
            getVotesResponse.setTotalElement(upvotes.size()+downvotes.size());
            getVotesResponse.setTotalDownvote(upvotes.size());
            getVotesResponse.setTotalUpvote(downvotes.size());
            return getVotesResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    private void handleExistingVote(VoteRequest request, VoteEntity voteEntity) {
        if (voteEntity.isUpvote() == request.getIsUpvote()) {
            voteRepository.delete(voteEntity);
        } else {
            voteEntity.setUpvote(request.getIsUpvote());
            voteRepository.save(voteEntity);
        }
    }



    UserEntity getUser(String authorId, String role) {
        if (role.equals("USER")) {
            StudentEntity studentEntity = studentRepository.findById(authorId).orElse(null);
            if (studentEntity == null) {
                throw new IllegalArgumentException("Student Id is not found");
            }
            UserEntity user = userRepository.findById(studentEntity.getUserId()).orElse(null);
            studentEntity.setUser(null);
            user.setStudent(studentEntity);
            return user;
        } else {
            TeacherEntity teacherEntity = teacherRepository.findById(authorId).orElse(null);
            if (teacherEntity == null) {
                throw new IllegalArgumentException("Teacher Id is not found");
            }
            UserEntity user = userRepository.findById(teacherEntity.getUserId()).orElse(null);
            teacherEntity.setUser(null);
            user.setTeacher(teacherEntity);
            return user;
        }
    }

    @Override
    public GetForumDetailResponse getForumDetail(String id, String userId) {
        try {
            ForumEntity forumEntity = forumRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id is not found"));
            List<FileEntity> fileEntities = fileRepository.findByOwnerIdAndOwnerType(forumEntity.getId(), FileOwnerType.FORUM.name());
            GetForumDetailResponse getForumDetailResponse = modelMapperService.mapClass(forumEntity, GetForumDetailResponse.class);
            getForumDetailResponse.setUpvoteCount(voteRepository.countUpvoteByTargetId(id));
            getForumDetailResponse.setDownvoteCount(voteRepository.countDownvoteByTargetId(id));
            getForumDetailResponse.setSources(fileEntities);
            VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(userId, forumEntity.getId());
            if (voteEntity != null) {
                getForumDetailResponse.setIsUpvoted(voteEntity.isUpvote());
            }
            getForumDetailResponse.setAuthor(userRepository.findById(userId).get());
            List<ForumCommentEntity> forumCommentEntities = forumCommentRepository.findByForumId(id);
            List<GetForumDetailResponse.ForumComment> forumComments = new ArrayList<>();
            forumCommentEntities.forEach(forumCommentEntity -> {
                GetForumDetailResponse.ForumComment forumComment = modelMapperService.mapClass(forumCommentEntity, GetForumDetailResponse.ForumComment.class);
                forumComment.setSources(fileRepository.findByOwnerIdAndOwnerType(forumCommentEntity.getId(), FileOwnerType.FORUM_COMMENT.name()));
                forumComment.setAuthor(userRepository.findById(userId).get());
                VoteEntity voteEntity2 = voteRepository.findByAuthorIdAndTargetId(userId, forumEntity.getId());
                if (voteEntity2 != null) {
                    forumComment.setIsUpvoted(voteEntity.isUpvote());
                }
                forumComments.add(forumComment);
            });
            getForumDetailResponse.setComments(forumComments);
            return getForumDetailResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetForumsResponse getForums(int page, int size, String search, String sortOrder, String userId, String tag, String sortBy) {
        try {
            Sort sort;
            if ("vote".equalsIgnoreCase(sortBy)) {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(Sort.Order.desc("upvote + downvote")) : Sort.by(Sort.Order.asc("upvote + downvote"));
            } else {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            }
            Pageable pageable = PageRequest.of(page, size, sort);
            List<String> tagIds = tagRepository.findByNameRegexOrderByPostCount(tag).stream().map(TagEntity::getId).collect(Collectors.toList());
            Page<ForumEntity> forumEntities = forumRepository.findByAnyTagIdsAndTitleOrContentRegex(tagIds, search, pageable);
            GetForumsResponse getForumsResponse = new GetForumsResponse();
            List<GetForumsResponse.ForumResponse> data = new ArrayList<>();
            forumEntities.forEach(forumEntity -> {
                GetForumsResponse.ForumResponse forumResponse = GetForumsResponse.ForumResponse.formForumEntity(forumEntity);
                forumResponse.setAuthor(userRepository.findById(userId).get());
                List<FileEntity> fileEntities = fileRepository.findByOwnerIdAndOwnerType(forumEntity.getId(), FileOwnerType.FORUM.name());
                forumResponse.setTags(tagRepository.findByIdIn(forumEntity.getTags()));
                forumResponse.setSources(fileEntities);
                forumResponse.setUpvote(voteRepository.countUpvoteByTargetId(forumEntity.getId()));
                forumResponse.setDownvote(voteRepository.countDownvoteByTargetId(forumEntity.getId()));
                if (userId!=null){
                    VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(userId, forumEntity.getId());
                    if (voteEntity != null) {
                        forumResponse.setIsUpvoted(voteEntity.isUpvote());
                    }
                }

                data.add(forumResponse);
            });
            getForumsResponse.setForums(data);
            getForumsResponse.setTotalElements(forumEntities.getTotalElements());
            getForumsResponse.setTotalPage(forumEntities.getTotalPages());
            return getForumsResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetForumsResponse getForumsForAdmin(int page, int size, String search, String sortOrder, String userId, String tag, String sortBy) {
        try {
            Sort sort;
            if ("vote".equalsIgnoreCase(sortBy)) {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(Sort.Order.desc("upvote + downvote")) : Sort.by(Sort.Order.asc("upvote + downvote"));
            } else {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            }
            Pageable pageable = PageRequest.of(page, size, sort);
            List<String> tagIds = tagRepository.findByNameRegexOrderByPostCount(tag).stream().map(TagEntity::getId).collect(Collectors.toList());
            Page<ForumEntity> forumEntities = forumRepository.findByAnyTagIdsAndTitleOrContentRegexForAdmin(tagIds, search, pageable);
            GetForumsResponse getForumsResponse = new GetForumsResponse();
            List<GetForumsResponse.ForumResponse> data = new ArrayList<>();
            forumEntities.forEach(forumEntity -> {
                GetForumsResponse.ForumResponse forumResponse = GetForumsResponse.ForumResponse.formForumEntity(forumEntity);
                if (forumEntity.getAuthorId()!=null){
                    UserEntity userEntity = userRepository.findById(forumEntity.getAuthorId()).orElse(null);
                    if (userEntity!=null){
                        forumResponse.setAuthor(userEntity);
                    }
                }
                List<FileEntity> fileEntities = fileRepository.findByOwnerIdAndOwnerType(forumEntity.getId(), FileOwnerType.FORUM.name());
                forumResponse.setTags(tagRepository.findByIdIn(forumEntity.getTags()));
                forumResponse.setSources(fileEntities);
                forumResponse.setUpvote(voteRepository.countUpvoteByTargetId(forumEntity.getId()));
                forumResponse.setDownvote(voteRepository.countDownvoteByTargetId(forumEntity.getId()));
                if (userId!=null){
                    VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(userId, forumEntity.getId());
                    if (voteEntity != null) {
                        forumResponse.setIsUpvoted(voteEntity.isUpvote());
                    }
                }

                data.add(forumResponse);
            });
            getForumsResponse.setForums(data);
            getForumsResponse.setTotalElements(forumEntities.getTotalElements());
            getForumsResponse.setTotalPage(forumEntities.getTotalPages());
            return getForumsResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetForumsResponse getForumByAuthor(String authorId, int page, int size, String search, String sortOrder, String userId, String sortBy) {
        try {
            Sort sort;
            if ("vote".equalsIgnoreCase(sortBy)) {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(Sort.Order.desc("upvote + downvote")) : Sort.by(Sort.Order.asc("upvote + downvote"));
            } else {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            }
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ForumEntity> forumEntities = forumRepository.findByAuthorIdAndTitleOrContentRegex(authorId, search, pageable);
            GetForumsResponse getForumsResponse = new GetForumsResponse();
            List<GetForumsResponse.ForumResponse> data = new ArrayList<>();
            forumEntities.forEach(forumEntity -> {
                GetForumsResponse.ForumResponse forumResponse = GetForumsResponse.ForumResponse.formForumEntity(forumEntity);
                forumResponse.setSources(fileRepository.findByOwnerIdAndOwnerType(forumEntity.getId(), FileOwnerType.FORUM.name()));
                List<TagEntity> tagEntities = tagRepository.findByIdIn(forumEntity.getTags());
                forumResponse.setAuthor(userRepository.findById(userId).get());
                forumResponse.setTags(tagEntities);
                forumResponse.setUpvote(voteRepository.countUpvoteByTargetId(forumEntity.getId()));
                forumResponse.setDownvote(voteRepository.countDownvoteByTargetId(forumEntity.getId()));
                VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(userId, forumEntity.getId());
                if (voteEntity != null) {
                    forumResponse.setIsUpvoted(voteEntity.isUpvote());
                }
                data.add(forumResponse);
            });
            getForumsResponse.setForums(data);
            getForumsResponse.setTotalElements(forumEntities.getTotalElements());
            getForumsResponse.setTotalPage(forumEntities.getTotalPages());
            return getForumsResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetForumsResponse getForumByTag(List<String> tagNames, int page, int size, String search, String sortOrder, String userId, String sortBy) {
        try {
            Sort sort;
            if ("vote".equalsIgnoreCase(sortBy)) {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(Sort.Order.desc("upvote + downvote")) : Sort.by(Sort.Order.asc("upvote + downvote"));
            } else {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            }
            List<String> tagIds = tagRepository.findByNameIn(tagNames).stream().map(TagEntity::getId).collect(Collectors.toList());
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ForumEntity> forumEntities = forumRepository.findByTagIdsAndTitleOrContentRegex(tagIds, search, pageable);
            GetForumsResponse getForumsResponse = new GetForumsResponse();
            List<GetForumsResponse.ForumResponse> data = new ArrayList<>();
            forumEntities.forEach(forumEntity -> {
                GetForumsResponse.ForumResponse forumResponse = GetForumsResponse.ForumResponse.formForumEntity(forumEntity);
                forumResponse.setSources(fileRepository.findByOwnerIdAndOwnerType(forumEntity.getId(), FileOwnerType.FORUM.name()));
                forumResponse.setTags(tagRepository.findByIdIn(forumEntity.getTags()));
                forumResponse.setAuthor(userRepository.findById(userId).get());
                forumResponse.setUpvote(voteRepository.countUpvoteByTargetId(forumEntity.getId()));
                forumResponse.setDownvote(voteRepository.countDownvoteByTargetId(forumEntity.getId()));
                VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(userId, forumEntity.getId());
                if (voteEntity != null) {
                    forumResponse.setIsUpvoted(voteEntity.isUpvote());
                }
                data.add(forumResponse);
            });
            getForumsResponse.setForums(data);
            getForumsResponse.setTotalElements(forumEntities.getTotalElements());
            getForumsResponse.setTotalPage(forumEntities.getTotalPages());
            return getForumsResponse;
        } catch (Exception e) {
            log.error("Error in getForumByTag: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to get forums by tags: " + e.getMessage());
        }
    }

    @Override
    public GetForumsResponse getForumByClass(String classId, int page, int size, String search, String sortOrder, String userId, String sortBy,List<String> tags) {
        try {
            Sort sort;
            if ("vote".equalsIgnoreCase(sortBy)) {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(Sort.Order.desc("upvote + downvote")) : Sort.by(Sort.Order.asc("upvote + downvote"));
            } else {
                sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            }
            List<String> tagIds = tagRepository.findByNameIn(tags).stream().map(TagEntity::getId).collect(Collectors.toList());
            Pageable pageable = PageRequest.of(page, size, sort);
            TagEntity tagEntities = tagRepository.findByClassId(classId);
            if (tagEntities == null){
                GetForumsResponse getForumsResponse = new GetForumsResponse();
                getForumsResponse.setForums(new ArrayList<>());
                getForumsResponse.setTotalElements(0L);
                getForumsResponse.setTotalPage(0);
                return  getForumsResponse;
            }
            tagIds.add(tagEntities.getId());
            Page<ForumEntity> forumEntities = forumRepository.findByAnyTagIdsAndTitleOrContentRegex(tagIds, search, pageable);
            GetForumsResponse getForumsResponse = new GetForumsResponse();
            List<GetForumsResponse.ForumResponse> data = new ArrayList<>();
            forumEntities.forEach(forumEntity -> {
                GetForumsResponse.ForumResponse forumResponse = GetForumsResponse.ForumResponse.formForumEntity(forumEntity);
                forumResponse.setSources(fileRepository.findByOwnerIdAndOwnerType(forumEntity.getId(), FileOwnerType.FORUM.name()));
                forumResponse.setTags(tagRepository.findByIdIn(forumEntity.getTags()));
                forumResponse.setAuthor(userRepository.findById(forumResponse.getAuthorId()).get());
                forumResponse.setUpvote(voteRepository.countUpvoteByTargetId(forumEntity.getId()));
                forumResponse.setDownvote(voteRepository.countDownvoteByTargetId(forumEntity.getId()));
                VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(userId, forumEntity.getId());
                if (voteEntity != null) {
                    forumResponse.setIsUpvoted(voteEntity.isUpvote());
                }
                data.add(forumResponse);
            });
            getForumsResponse.setForums(data);
            getForumsResponse.setTotalElements(forumEntities.getTotalElements());
            getForumsResponse.setTotalPage(forumEntities.getTotalPages());
            return getForumsResponse;
        } catch (Exception e) {
            log.error("Error in getForumByClass: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to get forums by class: " + e.getMessage());
        }
    }
    @Override
    public void createForumComment(CreateForumCommentRequest request) {
        try{
            if (request.getAuthorId()==null){
                throw new IllegalArgumentException("Author Id is required");
            }
            if (userRepository.findById(request.getAuthorId()).isEmpty()){
                throw new IllegalArgumentException("Author Id is not found");
            }
            if (request.getParentId()!=null && forumCommentRepository.findById(request.getParentId()).isEmpty()){
                throw new IllegalArgumentException("Parent Id is not found");
            }
            ForumEntity forumEntity = forumRepository.findById(request.getForumId()).orElseThrow(()->new IllegalArgumentException("Forum Id is not found"));

            ForumCommentEntity forumCommentEntity = modelMapperService.mapClass(request, ForumCommentEntity.class);

            forumCommentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            forumCommentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            forumCommentEntity.setReplyCount(0);
            forumCommentRepository.save(forumCommentEntity);
            if (request.getSources() != null && !request.getSources().isEmpty()) {
                processFiles(request.getSources(), request.getContent(), forumCommentEntity.getId(), FileOwnerType.FORUM_COMMENT);
            }
            if (request.getParentId()!=null && forumCommentRepository.findById(request.getParentId()).isPresent()){
                ForumCommentEntity parentComment = forumCommentRepository.findById(request.getParentId()).get();
                parentComment.setReplyCount(parentComment.getReplyCount()+1);
                forumCommentRepository.save(parentComment);
            }
            forumEntity.setCommentCount(forumEntity.getCommentCount()+1);
            forumRepository.save(forumEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateForumComment(UpdateForumCommentRequest request) {
        try {
            ForumCommentEntity forumCommentEntity = forumCommentRepository.findById(request.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (request.getContent()!=null){
                forumCommentEntity.setContent(request.getContent());
            }
            if (request.getReplyCount()!=0){
                forumCommentEntity.setReplyCount(request.getReplyCount());
            }
            if (request.getStatus()!=null){
                forumCommentEntity.setStatus(ForumStatus.valueOf(request.getStatus()));
            }

            if (request.getSources() != null && !request.getSources().isEmpty()) {
                processFiles(request.getSources(), request.getContent(),forumCommentEntity.getId() , FileOwnerType.FORUM_COMMENT);
            }
            forumCommentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            forumCommentRepository.save(forumCommentEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteForumComment(String id) {
        try{
            ForumCommentEntity forumCommentEntity = forumCommentRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            forumCommentRepository.delete(forumCommentEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetForumCommentResponse getReplyComments(String parentIdm, int page, int size, String userId) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ForumCommentEntity> forumCommentEntities = forumCommentRepository.findByParentId(parentIdm, pageAble);
            GetForumCommentResponse getForumCommentResponse = new GetForumCommentResponse();
            List<GetForumCommentResponse.ForumCommentResponse> data = new ArrayList<>();
            forumCommentEntities.forEach(forumCommentEntity -> {
                GetForumCommentResponse.ForumCommentResponse forumComment = modelMapperService.mapClass(forumCommentEntity, GetForumCommentResponse.ForumCommentResponse.class);
                forumComment.setSources(fileRepository.findByOwnerIdAndOwnerType(forumCommentEntity.getId(), FileOwnerType.FORUM_COMMENT.name()));
                forumComment.setAuthor(userRepository.findById(forumComment.getAuthorId()).get());
                VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(userId, forumComment.getId());
                if (voteEntity != null) {
                    forumComment.setIsUpvoted(voteEntity.isUpvote());
                }
                data.add(forumComment);
            });
            getForumCommentResponse.setComments(data);
            getForumCommentResponse.setTotalElements(forumCommentEntities.getTotalElements());
            getForumCommentResponse.setTotalPage(forumCommentEntities.getTotalPages());
            return getForumCommentResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetForumCommentResponse getForumComments(String forumId, int page, int size, String sortOrder, String userId) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ForumCommentEntity> forumCommentEntities = forumCommentRepository.findByForumId(forumId, pageAble);
            GetForumCommentResponse getForumCommentResponse = new GetForumCommentResponse();
            List<GetForumCommentResponse.ForumCommentResponse> data = new ArrayList<>();
            forumCommentEntities.forEach(forumCommentEntity -> {
                GetForumCommentResponse.ForumCommentResponse forumComment = modelMapperService.mapClass(forumCommentEntity, GetForumCommentResponse.ForumCommentResponse.class);
                forumComment.setSources(fileRepository.findByOwnerIdAndOwnerType(forumCommentEntity.getId(), FileOwnerType.FORUM_COMMENT.name()));
                forumComment.setAuthor(userRepository.findById(forumComment.getAuthorId()).get());
                VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(userId, forumComment.getId());
                if (voteEntity != null) {
                    forumComment.setIsUpvoted(voteEntity.isUpvote());
                }
                data.add(forumComment);
            });
            getForumCommentResponse.setComments(data);
            getForumCommentResponse.setTotalElements(forumCommentEntities.getTotalElements());
            getForumCommentResponse.setTotalPage(forumCommentEntities.getTotalPages());
            return getForumCommentResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void createTag(TagEntity request) {
        try {
            TagEntity tagEntity = modelMapperService.mapClass(request, TagEntity.class);
            tagRepository.save(tagEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void updateTag(TagEntity request) {
        try {
            TagEntity tagEntity = tagRepository.findById(request.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (request.getName()!=null){
                tagEntity.setName(request.getName());
            }
            tagRepository.save(tagEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteTag(String id) {
        try {
            TagEntity tagEntity = tagRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            tagRepository.delete(tagEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetTagsResponse getTagEntity(String search, String sortOrder, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, sortOrder.equalsIgnoreCase("desc") ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending());
            Page<TagEntity> tagEntities = tagRepository.findByNameRegexOrderByPostCount(search, pageable);
            GetTagsResponse getTagsResponse = new GetTagsResponse();
            List<GetTagsResponse.Tag> data = new ArrayList<>();
            tagEntities.forEach(tagEntity -> {
                GetTagsResponse.Tag tag = modelMapperService.mapClass(tagEntity, GetTagsResponse.Tag.class);
                data.add(tag);
            });
            getTagsResponse.setTags(data);
            getTagsResponse.setTotalElement((int)tagEntities.getTotalElements());
            getTagsResponse.setTotalPage(tagEntities.getTotalPages());
            return getTagsResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetForumsResponse getBalancedPersonalizedNewsfeed(String userId, int page, int size, String sortOrder, String sortBy) {
        try {
            // Xác định cách sắp xếp
            Sort sort;
            if ("vote".equalsIgnoreCase(sortBy)) {
                sort = sortOrder.equalsIgnoreCase("desc")
                        ? Sort.by(Sort.Order.desc("upvote + downvote"))
                        : Sort.by(Sort.Order.asc("upvote + downvote"));
            } else {
                sort = sortOrder.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending();
            }

            // Tạo pageable với sắp xếp
            Pageable pageable = PageRequest.of(page, size, sort);

            // Lấy top tags user đã tương tác
            List<String> personalTags = voteRepository.findTopTagsByUserVotes(userId).stream()
                    .limit(3)
                    .map(TagVoteAggregate::get_id)
                    .collect(Collectors.toList());

            // Tìm forums
            Page<ForumEntity> forumEntities;
            if (personalTags.isEmpty()) {
                // Nếu không có tags cá nhân hóa, lấy forums chung
                forumEntities = forumRepository.findAll(pageable);
            } else {
                // Lấy forums theo tags cá nhân
                forumEntities = forumRepository.findByTagsIn(personalTags, pageable);

                // Nếu không đủ forums, bổ sung thêm forums chung
                if (forumEntities.getContent().size() < size) {
                    Page<ForumEntity> generalForums = forumRepository.findAll(pageable);

                    // Kết hợp các forums
                    List<ForumEntity> combinedForums = new ArrayList<>(forumEntities.getContent());
                    combinedForums.addAll(
                            generalForums.getContent().subList(
                                    0,
                                    Math.min(size - combinedForums.size(), generalForums.getContent().size())
                            )
                    );

                    // Tạo page mới từ combined forums
                    forumEntities = new PageImpl<>(combinedForums, pageable, forumEntities.getTotalElements());
                }
            }

            // Tạo response
            GetForumsResponse getForumsResponse = new GetForumsResponse();
            List<GetForumsResponse.ForumResponse> data = new ArrayList<>();
            Set<String> uniqueForumIds = new HashSet<>();

            // Xử lý từng forum
            forumEntities.forEach(forumEntity -> {
                if (!uniqueForumIds.contains(forumEntity.getId())) {
                    uniqueForumIds.add(forumEntity.getId());
                    GetForumsResponse.ForumResponse forumResponse = GetForumsResponse.ForumResponse.formForumEntity(forumEntity);

                    // Lấy thông tin user
                    forumResponse.setAuthor(userRepository.findById(forumResponse.getAuthorId()).get());

                    // Lấy files
                    List<FileEntity> fileEntities = fileRepository.findByOwnerIdAndOwnerType(
                            forumEntity.getId(),
                            FileOwnerType.FORUM.name()
                    );
                    forumResponse.setSources(fileEntities);

                    // Lấy tags
                    forumResponse.setTags(tagRepository.findByIdIn(forumEntity.getTags()));

                    // Tính toán votes
                    forumResponse.setUpvote(voteRepository.countUpvoteByTargetId(forumEntity.getId()));
                    forumResponse.setDownvote(voteRepository.countDownvoteByTargetId(forumEntity.getId()));

                    // Kiểm tra vote của user
                    VoteEntity voteEntity = voteRepository.findByAuthorIdAndTargetId(userId, forumEntity.getId());
                    if (voteEntity != null) {
                        forumResponse.setIsUpvoted(voteEntity.isUpvote());
                    }

                    data.add(forumResponse);
                }
            });

            // Điền thông tin response
            getForumsResponse.setForums(data);
            getForumsResponse.setTotalElements(forumEntities.getTotalElements());
            getForumsResponse.setTotalPage(forumEntities.getTotalPages());

            return getForumsResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
