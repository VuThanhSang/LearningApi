package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.meeting.CreateMeetingRequest;
import com.example.learning_api.dto.request.meeting.UpdateMeetingRequest;

public interface IMeetingService {
    void createMeeting(CreateMeetingRequest body);
    void deleteMeeting(String meetingId);
    void updateMeeting(UpdateMeetingRequest body);

}
