package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.classroom.CreateClassRoomRequest;
import com.example.learning_api.dto.request.classroom.ImportClassRoomRequest;
import com.example.learning_api.dto.request.classroom.UpdateClassRoomRequest;
import com.example.learning_api.dto.response.classroom.CreateClassRoomResponse;
import com.example.learning_api.dto.response.classroom.GetClassRoomDetailResponse;
import com.example.learning_api.dto.response.classroom.GetClassRoomsResponse;
import com.example.learning_api.dto.response.classroom.GetScheduleResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;

import java.util.List;

public interface IClassRoomService {
    CreateClassRoomResponse createClassRoom(CreateClassRoomRequest body);
    void updateClassRoom(UpdateClassRoomRequest body);
    void deleteClassRoom(String classroomId);
    GetClassRoomsResponse getClassRooms(int page,int size, String search);
    GetSectionsResponse getSectionsByClassroomId(int page, int size, String search);
    GetClassRoomsResponse getScheduleByDay(String studentId, String day);
    List<GetScheduleResponse> getScheduleByStudentId(String studentId);
    GetClassRoomDetailResponse getClassRoomDetail(String classroomId);
    void importClassRoom(ImportClassRoomRequest body);
}
