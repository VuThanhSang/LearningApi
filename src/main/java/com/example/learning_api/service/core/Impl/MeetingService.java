package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.meeting.CreateMeetingRequest;
import com.example.learning_api.dto.request.meeting.UpdateMeetingRequest;
import com.example.learning_api.entity.sql.database.MeetingEntity;
import com.example.learning_api.repository.database.LessonRepository;
import com.example.learning_api.repository.database.MeetingRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IMeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService implements IMeetingService {
    private final MeetingRepository meetingRepository;
    private final ModelMapperService modelMapperService;
    private final TeacherRepository teacherRepository;


    @Override
    public void createMeeting(CreateMeetingRequest body) {
        try{
            if (body.getTeacherId()==null){
                throw new IllegalArgumentException("TeacherId is required");
            }
            if (teacherRepository.findById(body.getTeacherId()).isEmpty()){
                throw new IllegalArgumentException("TeacherId is not found");
            }
             MeetingEntity meetingEntity = modelMapperService.mapClass(body, MeetingEntity.class);
             meetingEntity.setCreatedAt(new Date());
             meetingEntity.setUpdatedAt(new Date());
             meetingRepository.save(meetingEntity);

        }
        catch (Exception e) {
            log.error("Error in createMeeting: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteMeeting(String meetingId) {
        try{
            MeetingEntity meetingEntity = meetingRepository.findById(meetingId).orElse(null);
            if (meetingEntity == null){
                throw new IllegalArgumentException("MeetingId is not found");
            }
            meetingRepository.deleteById(meetingId);
        }
        catch (Exception e) {
            log.error("Error in deleteMeeting: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void updateMeeting(UpdateMeetingRequest body) {
        try{
            MeetingEntity meetingEntity = meetingRepository.findById(body.getId()).orElse(null);
            if (meetingEntity == null){
                throw new IllegalArgumentException("MeetingId is not found");
            }
            meetingEntity = modelMapperService.mapClass(body, MeetingEntity.class);
            meetingEntity.setUpdatedAt(new Date());
            meetingRepository.save(meetingEntity);
        }
        catch (Exception e) {
            log.error("Error in updateMeeting: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
