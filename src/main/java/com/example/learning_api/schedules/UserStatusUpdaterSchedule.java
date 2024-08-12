//package com.example.learning_api.schedules;
//
//import com.example.learning_api.entity.sql.database.UserEntity;
//import com.example.learning_api.repository.database.UserRepository;
//import com.example.learning_api.service.redis.UserTokenRedisService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Set;
//
//
//@Component
//@RequiredArgsConstructor
//public class UserStatusUpdaterSchedule {
//    private final UserTokenRedisService userTokenRedisService;
//    private final UserRepository userRepository;
//
//    @Scheduled(fixedRate = 300000) // Run every 5 minutes
//    public void updateUserStatus() {
//        List<UserEntity> users = userRepository.findAll();
//        for (UserEntity user : users) {
//            String status = userTokenRedisService.getUserStatus(user.getId());
//            if (status == null || status.equals("offline")) {
//                // If user has no status or is offline, check if they have any valid tokens
//                Set<String> tokens = userTokenRedisService.getAllTokensForUser(user.getId());
//                if (tokens.isEmpty()) {
//                    userTokenRedisService.setUserOffline(user.getId());
//                } else {
//                    userTokenRedisService.setUserOnline(user.getId());
//                }
//            }
//        }
//    }
//}
