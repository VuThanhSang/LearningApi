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
    GetClassRoomsResponse getClassRooms(int page, int size, String search, String studentId, String role, String status, String category);

    GetClassRoomsResponse getUnregisteredClassRooms(int page, int size, String search, String studentId, String status, String category,String tag,String order);

    GetClassRoomsResponse getClassRoomsByTeacherId(int page, int size, String teacherId);
    GetSectionsResponse getSectionsByClassroomId(int page, int size, String search,String role);
    GetClassRoomDetailResponse getClassRoomByInvitationCode(String invitationCode);
    GetClassRoomDetailResponse getClassRoomDetail(String classroomId,String role,String userId);
    void importClassRoom(ImportClassRoomRequest body);
    GetClassRoomRecentResponse getRecentClassesByTeacherId(int page,int size, String userId,String role);
    void joinClassRoom(String classroomId, String studentId);
    GetJoinClassResponse getJoinClassRequests(int page, int size, String classroomId,String teacherId,String email,String name);
    void acceptJoinClass(String classroomId, String studentId);
    void rejectJoinClass(String classroomId, String studentId);
    void removeStudentFromClass(String classroomId, String studentId, String teacherId);
    InviteClassByEmailResponse inviteStudentByEmail(InviteStudentByEmailRequest body);
    GetDetailStudentInClassResponse getDetailStudentInClass(String classroomId, String studentId);
    GetClassRoomForAdminResponse getClassRoomsForAdmin( String search);
    void changeStatusClassRoom(String classroomId, String status);
    GetClassRoomRankinResponse getClassRoomRanking(String classroomId,int page,int size,Integer rating);

}
