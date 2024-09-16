package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.WorkspaceEntity;
import com.example.learning_api.entity.sql.database.WorkspaceMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface WorkspaceMemberRepository extends MongoRepository<WorkspaceMemberEntity, String> {
    @Query("{ 'workspaceId' : ?0, 'memberId' : ?1 }")
    WorkspaceMemberEntity findByWorkspaceIdAndMemberId(String workspaceId, String memberId);
    void deleteByWorkspaceIdAndMemberId(String workspaceId, String memberId);

    @Aggregation(pipeline = {
            "{ $match: { memberId: ?0 } }",
            "{ $addFields: { workspaceObjectId: { $toObjectId: '$workspaceId' } } }",
            "{ $lookup: { from: 'workspaces', localField: 'workspaceObjectId', foreignField: '_id', as: 'workspace' } }",
            "{ $unwind: '$workspace' }",
            "{ $match: { $or: [ " +
                    "{ 'workspace.name': { $regex: ?1, $options: 'i' } }, " +
                    "{ 'workspace.description': { $regex: ?1, $options: 'i' } } " +
                    "] } }",
            "{ $project: { _id: '$workspace._id', name: '$workspace.name', description: '$workspace.description', " +
                    "ownerId: '$workspace.ownerId', ownerRole: '$workspace.ownerRole', type: '$workspace.type', " +
                    "createdAt: '$workspace.createdAt', updatedAt: '$workspace.updatedAt', " +
                    "memberRole: '$memberRole', role: '$role' } }",
            "{ $skip: ?2 }",
            "{ $limit: ?3 }"
    })
    List<WorkspaceEntity> findWorkspacesByMemberIdAndSearch(String memberId, String searchTerm, int skip, int limit);

    @Aggregation(pipeline = {
            "{ $match: { memberId: ?0 } }",
            "{ $addFields: { workspaceObjectId: { $toObjectId: '$workspaceId' } } }",
            "{ $lookup: { from: 'workspaces', localField: 'workspaceObjectId', foreignField: '_id', as: 'workspace' } }",
            "{ $unwind: '$workspace' }",
            "{ $match: { $or: [ " +
                    "{ 'workspace.name': { $regex: ?1, $options: 'i' } }, " +
                    "{ 'workspace.description': { $regex: ?1, $options: 'i' } } " +
                    "] } }",
            "{ $count: 'total' }"
    })
    long countWorkspacesByMemberIdAndSearch(String memberId, String searchTerm);

    @Query("{ 'workspaceId' : ?0 }")
    Page<WorkspaceMemberEntity> findByWorkspaceId(String workspaceId, Pageable pageable);
//    @Aggregation(pipeline = {
//            "{ $match: { workspaceId: ?0 } }",
//            "{ $lookup: { from: 'users', localField: 'memberId', foreignField: '_id', as: 'user' } }",
//            "{ $unwind: '$user' }",
//            "{ $match: { $or: [ " +
//                    "{ 'user.username': { $regex: ?1, $options: 'i' } }, " +
//                    "{ 'user.email': { $regex: ?1, $options: 'i' } } " +
//                    "] } }",
//            "{ $project: { " +
//                    "_id: 1, " +
//                    "workspaceId: 1, " +
//                    "memberId: 1, " +
//                    "memberRole: 1, " +
//                    "role: 1, " +
//                    "username: '$user.username', " +
//                    "email: '$user.email', " +
//                    "fullName: '$user.fullName' " +
//                    "} }",
//            "{ $skip: ?2 }",
//            "{ $limit: ?3 }"
//    })
//    List<WorkspaceMemberEntity> findMembersByWorkspaceIdAndSearch(String workspaceId, String searchTerm, int skip, int limit);
//
//    @Aggregation(pipeline = {
//            "{ $match: { workspaceId: ?0 } }",
//            "{ $lookup: { from: 'users', localField: 'memberId', foreignField: '_id', as: 'user' } }",
//            "{ $unwind: '$user' }",
//            "{ $match: { $or: [ " +
//                    "{ 'user.username': { $regex: ?1, $options: 'i' } }, " +
//                    "{ 'user.email': { $regex: ?1, $options: 'i' } } " +
//                    "] } }",
//            "{ $count: 'total' }"
//    })
//    long countMembersByWorkspaceIdAndSearch(String workspaceId, String searchTerm);
}
