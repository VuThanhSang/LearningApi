package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.classroom.CreateClassRoomRequest;
import com.example.learning_api.dto.request.classroom.UpdateClassRoomRequest;
import com.example.learning_api.dto.response.ClassRoomResponse.CreateClassRoomResponse;
import com.example.learning_api.dto.request.classroom.DeleteClassRoomRequest;
import com.example.learning_api.dto.response.ClassRoomResponse.GetClassRoomsResponse;

public interface IClassRoomService {
    CreateClassRoomResponse createClassRoom(CreateClassRoomRequest body);
    void updateClassRoom(UpdateClassRoomRequest body);
    void deleteClassRoom(String classroomId);
    GetClassRoomsResponse getClassRooms(int page,int size, String search);
}
