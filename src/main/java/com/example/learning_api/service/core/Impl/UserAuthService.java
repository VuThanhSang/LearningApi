package com.example.learning_api.service.core.Impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.auth.ChangePasswordRequest;
import com.example.learning_api.dto.request.auth.LoginUserRequest;
import com.example.learning_api.dto.request.auth.RegisterUserRequest;
import com.example.learning_api.dto.response.auth.LoginResponse;
import com.example.learning_api.dto.response.auth.RefreshTokenResponse;
import com.example.learning_api.dto.response.auth.RegisterResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.ConfirmationCodeStatus;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.UserStatus;
//import com.example.learning_api.kafka.message.CodeEmailMsgData;
//import com.example.learning_api.kafka.publisher.MailerKafkaPublisher;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IUserAuthService;
//import com.example.learning_api.service.redis.Impl.BaseRedisServiceImpl;
//import com.example.learning_api.service.redis.UserTokenRedisService;
import com.example.learning_api.utils.GeneratorUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static com.example.learning_api.constant.ErrorConstant.EXISTED_DATA;
import static com.example.learning_api.constant.ErrorConstant.UNAUTHORIZED;


import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService  implements IUserAuthService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${spring.mail.username}")
    private String mailFrom;
    private final ModelMapperService modelMapperService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final ConfirmationRepository confirmationRepository;
//    private final UserTokenRedisService userTokenRedisService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
//    private final MailerKafkaPublisher mailerKafkaPublisher;
    @Autowired
    private final JavaMailSender javaMailSender;
    
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;


    private UserEntity authenticateUser(String email, String password) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("Account not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("Password is incorrect");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return user;
    }

    private LoginResponse buildLoginResponse(UserEntity user, String jwt, String refreshToken) {
        LoginResponse.LoginResponseBuilder responseBuilder = LoginResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .role(user.getRole().toString())
                .status(user.getStatus()!= null ? user.getStatus().toString() : UserStatus.INACTIVE.toString());
        if (user.getRole() == RoleEnum.TEACHER) {
            TeacherEntity teacher = teacherRepository.findByUserId(user.getId());
            if (teacher != null) {
                teacher.setUser(null);
                user.setTeacher(teacher);
            }
        } else if (user.getRole() == RoleEnum.USER) {
            StudentEntity student = studentRepository.findByUserId(user.getId());
            if (student != null) {
                user.setStudent(student);
                student.setUser(null);
            }
        }
        responseBuilder.user(user);
        return responseBuilder.build();
    }


    @Transactional
    @Override
    public RegisterResponse registerUser(RegisterUserRequest body) {
        UserEntity userEntity = modelMapperService.mapClass(body, UserEntity.class);
        if(userRepository.findByEmail(userEntity.getEmail()).orElse(null) != null) {
            throw new CustomException( "Email already registered");
        }

        RegisterResponse resData = new RegisterResponse();
        modelMapperService.map(userEntity, resData);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setAuthType("normal");
        userEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        userEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        userEntity.setStatus(UserStatus.INACTIVE);
        userEntity = userRepository.save(userEntity);
        var accessToken = jwtService.issueAccessToken(userEntity.getId(), userEntity.getEmail(), userEntity.getRole());
        var refreshToken = jwtService.issueRefreshToken(userEntity.getId(), userEntity.getEmail(), userEntity.getRole());
//        userTokenRedisService.upsertUserToken(userEntity.getId(), refreshToken, false);
        resData.setAccessToken(accessToken);
        resData.setRefreshToken(refreshToken);
        resData.setUserId(userEntity.getId());
        return resData;
    }
    @Override
    public LoginResponse loginUser(LoginUserRequest body) {
        try {
            UserEntity user = authenticateUser(body.getEmail(), body.getPassword());
            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new CustomException("Account is not active");
            }
            if (user.getStatus() == UserStatus.BLOCKED) {
                throw new CustomException("Account is blocked");
            }
            String jwt = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
            String refreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), user.getRole());

//            userTokenRedisService.upsertUserToken(userId, refreshToken, false);
//            userTokenRedisService.setUserOnline(user.getId());
            return buildLoginResponse(user, jwt, refreshToken);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }
    @Override
    public LoginResponse loginGoogleUser(OAuth2User oAuth2User) {
        try {
            String email = oAuth2User.getAttribute("email");
            UserEntity user = userRepository.findByEmailAndAuthType(email,"google").orElse(null);

            if(user == null) {
                user = new UserEntity();
                user.setEmail(email);
                user.setFullname(oAuth2User.getAttribute("name"));
                user.setRole(RoleEnum.USER);
                user.setAuthType("google");
                user.setStatus(UserStatus.ACTIVE);
                user.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                user.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                user = userRepository.save(user);
            }
            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new CustomException("Account is not active");
            }
            if (user.getStatus() == UserStatus.BLOCKED) {
                throw new CustomException("Account is blocked");
            }
            String jwt = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
            String refreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), user.getRole());

            return buildLoginResponse(user, jwt, refreshToken);
        }
        catch (Exception e){
            throw new CustomException(e.getMessage());
        }
    }
    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        DecodedJWT decodedJWT = jwtService.decodeRefreshToken(refreshToken);
        String userId = decodedJWT.getSubject();
//        Boolean tokenInfo = userTokenRedisService.getUserTokenValue(userId, refreshToken);
//        if(tokenInfo==null){
//            throw new CustomException("Token not found");
//        }
//        if(tokenInfo){
//            userTokenRedisService.deleteAllTokenOfUser(userId);
//            throw new CustomException("Token has been used");
//        }
        UserEntity user = userRepository.findById(userId).orElseThrow();
        String newAccessToken = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), user.getRole());
//        userTokenRedisService.upsertUserToken(user.getId(), newRefreshToken, false);
//        userTokenRedisService.upsertUserToken(user.getId(), refreshToken, true);
        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public UserEntity getUserById(String userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow();
        if (user.getRole() == RoleEnum.TEACHER) {
            TeacherEntity teacher = teacherRepository.findByUserId(user.getId());
            if (teacher != null) {
                teacher.setUser(null);
                user.setTeacher(teacher);
            }
        } else if (user.getRole() == RoleEnum.USER) {
            StudentEntity student = studentRepository.findByUserId(user.getId());
            if (student != null) {
                user.setStudent(student);
                student.setUser(null);
            }
        }
        return user;
    }

    @Override
    public void logout(String userId) {
//        userTokenRedisService.deleteAllTokenOfUser(userId);
        UserEntity user = userRepository.findById(userId).orElseThrow();
//        userTokenRedisService.setUserOffline(user.getId());
        revokeAllTokenByUser(user);
    }

    private void revokeAllTokenByUser(UserEntity user) {
        List<TokenEntity> validTokens = tokenRepository.findAllTokenByUser(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }
    private void saveUserToken(String jwt, UserEntity user) {
        TokenEntity token = tokenRepository.findByToken(user.getId()).orElse(null);
        if (token != null) {
            // Update existing token
            token.setToken(jwt);
            token.setLoggedOut(false);
        } else {
            // Create new token
            token = new TokenEntity();
            token.setUser(user);
        }
        tokenRepository.save(token);
    }


    public void createOrUpdateConfirmationInfo(String email, String code) {
        ConfirmationEntity oldConfirmation = confirmationRepository.findByEmail(email).orElse(null);
        Date currentDate = new Date();
        Instant instant = currentDate.toInstant();
        Instant newInstant = instant.plus(Duration.of(3, ChronoUnit.MINUTES));
        Date newDate = Date.from(newInstant);
        if (oldConfirmation == null) {
            ConfirmationEntity confirmation = ConfirmationEntity.builder()
                    .email(email)
                    .code(code)
                    .status(ConfirmationCodeStatus.UNUSED)
                    .expireAt(newDate)
                    .build();
            confirmationRepository.save(confirmation);
        } else {
            oldConfirmation.setExpireAt(newDate);
            oldConfirmation.setStatus(ConfirmationCodeStatus.UNUSED);
            oldConfirmation.setCode(code);
            confirmationRepository.save(oldConfirmation);
        }
    }
    @Override
    @Async
    public void sendCodeToRegister(String email) {
        logger.info("Send code to register email: " + email +" ... ");
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user != null && user.getStatus() == UserStatus.INACTIVE){
            String code = GeneratorUtils.generateRandomCode(6);
            createOrUpdateConfirmationInfo(email, code);
//            mailerKafkaPublisher.sendMessageToCodeEmail(new CodeEmailMsgData(code, email));
            sendEmailWithCode(email, code, "Active User Successfully");
            return;
        }else if(user != null && user.getStatus() == UserStatus.ACTIVE){
            throw new CustomException( "User already Active");
        }
        throw new CustomException( "Not Found User with email");
    }

    @Override
    @Async
    public void sendCodeForgotPassword(String email) {
        userRepository.findByEmailAndAuthType(email,"normal")
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + "User with email " + email));
        String code = GeneratorUtils.generateRandomCode(6);
        createOrUpdateConfirmationInfo(email, code);
        sendEmailWithCode(email, code, "Get Password Code Learning App");

    }
    private void sendEmailWithCode(String toMail,
                                   String body,
                                   String subject
                                   ) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("body", body);
            context.setVariable("toMail", toMail);
            String htmlContent = templateEngine.process("email-template", context);


            mimeMessageHelper.setFrom(mailFrom);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setSubject(subject);



            javaMailSender.send(mimeMessage);
            logger.info("Send email to " + toMail + " successfully");
        } catch (MessagingException e) {
            throw new CustomException( "Error while sending email");
        }
    }
    @Override
    public void verifyCodeByEmail(String code, String email) {
        ConfirmationEntity confirmationCollection = confirmationRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + "Confirmation data with email " + email));
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + "User with email " + email));
        Date currentTime = new Date();

        if (!code.equals(confirmationCollection.getCode())) {
            throw new CustomException("Code is incorrect");
        }

        if (currentTime.after(confirmationCollection.getExpireAt())) {
            throw new CustomException( "Code has expired");
        }

        confirmationCollection.setStatus(ConfirmationCodeStatus.USED);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        if (user.getRole() == RoleEnum.TEACHER) {
            TeacherEntity teacher = new TeacherEntity();
            teacher.setUserId(user.getId());
            teacher.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            teacher.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            teacher.setUser(user);
            teacherRepository.save(teacher);
        } else if (user.getRole() == RoleEnum.USER) {
            StudentEntity student = new StudentEntity();
            student.setUserId(user.getId());
            student.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            student.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            student.setUser(user);
            studentRepository.save(student);
        }
        confirmationRepository.save(confirmationCollection);
    }



    @Transactional
    @Override
    public void changePasswordForgot(ChangePasswordRequest body) {
        UserEntity user = userRepository.findByEmail(body.getEmail())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "User with email " + body.getEmail()));

        ConfirmationEntity confirmation = confirmationRepository.findByEmailAndCode(body.getEmail(), body.getCode())
                .orElseThrow(() -> new CustomException("Code is incorrect ", "Code is incorrect"));
        if (confirmation.getStatus() == ConfirmationCodeStatus.USED) {
            throw new CustomException("Code has been used", "Code has been used");
        }

        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.save(user);
    }
}
