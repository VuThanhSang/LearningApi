package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.participant.CreateParticipantRequest;
import com.example.learning_api.dto.request.participant.UpdateParticipantRequest;

public interface IParticipantService {
    void createParticipant(CreateParticipantRequest body);
    void deleteParticipant(String participantId);
    void updateParticipant(UpdateParticipantRequest body);

}
