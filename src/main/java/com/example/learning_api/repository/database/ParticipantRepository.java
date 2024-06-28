package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ParticipantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParticipantRepository extends MongoRepository<ParticipantEntity, String> {
    ParticipantEntity findByMeetingIdAndUserId(String meetingId, String userId);
    void deleteByMeetingIdAndUserId(String meetingId, String userId);
    void deleteByMeetingId(String meetingId);
    void deleteByUserId(String userId);
}
