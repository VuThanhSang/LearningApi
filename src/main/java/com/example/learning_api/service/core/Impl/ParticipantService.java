package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.participant.CreateParticipantRequest;
import com.example.learning_api.dto.request.participant.UpdateParticipantRequest;
import com.example.learning_api.entity.sql.database.ParticipantEntity;
import com.example.learning_api.repository.database.MeetingRepository;
import com.example.learning_api.repository.database.ParticipantRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantService implements IParticipantService {
    private final ModelMapperService modelMapperService;
    private final ParticipantRepository participantRepository;
    private final MeetingRepository meetingRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    public void createParticipant(CreateParticipantRequest body) {
        try{
            if (body.getMeetingId()==null){
                throw new IllegalArgumentException("MeetingId is required");
            }
            if (body.getUserId()==null){
                throw new IllegalArgumentException("UserId is required");
            }
            if (meetingRepository.findById(body.getMeetingId()).isEmpty()){
                throw new IllegalArgumentException("MeetingId is not found");
            }
            if (teacherRepository.findById(body.getUserId()).isEmpty() && studentRepository.findById(body.getUserId()).isEmpty()){
                throw new IllegalArgumentException("UserId is not found");
            }
            ParticipantEntity participantEntity = modelMapperService.mapClass(body, ParticipantEntity.class);
            participantEntity.setCreatedAt(new Date());
            participantEntity.setUpdatedAt(new Date());
            participantRepository.save(participantEntity);



        }
        catch (Exception e) {
            log.error("Error in createParticipant: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteParticipant(String participantId) {
        try {
            ParticipantEntity participantEntity = participantRepository.findById(participantId).orElse(null);
            if (participantEntity == null) {
                throw new IllegalArgumentException("ParticipantId is not found");
            }
            participantRepository.deleteById(participantId);
        }
        catch (Exception e) {
            log.error("Error in deleteParticipant: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateParticipant(UpdateParticipantRequest body) {
        try {
            ParticipantEntity participantEntity = participantRepository.findByMeetingIdAndUserId(body.getMeetingId(), body.getUserId());
            if (participantEntity == null) {
                throw new IllegalArgumentException("ParticipantId is not found");
            }
            participantEntity = modelMapperService.mapClass(body, ParticipantEntity.class);
            participantEntity.setUpdatedAt(new Date());
            participantRepository.save(participantEntity);
        }
        catch (Exception e) {
            log.error("Error in updateParticipant: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }


    }
}
