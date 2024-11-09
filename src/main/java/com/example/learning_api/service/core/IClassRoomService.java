package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.classroom.CreateClassRoomRequest;
import com.example.learning_api.dto.request.classroom.ImportClassRoomRequest;
import com.example.learning_api.dto.request.classroom.InviteClassByEmailResponse;
import com.example.learning_api.dto.request.classroom.UpdateClassRoomRequest;
import com.example.learning_api.dto.response.classroom.*;
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
    GetClassRoomDetailResponse getClassRoomByInvitationCode(String invitationCode);
    GetClassRoomDetailResponse getClassRoomDetail(String classroomId);
    void importClassRoom(ImportClassRoomRequest body);
    GetClassRoomRecentResponse getRecentClasses(int page,int size, String studentId);
    GetClassRoomRecentResponse getRecentClassesByTeacherId(int page,int size, String teacherId);
    void joinClassRoom(String classroomId, String studentId);
    GetJoinClassResponse getJoinClassRequests(int page, int size, String classroomId,String teacherId,String email,String name);
    void acceptJoinClass(String classroomId, String studentId);
    void rejectJoinClass(String classroomId, String studentId);
    InviteClassByEmailResponse inviteStudentByEmail(InviteStudentByEmailRequest body);
}
