package com.example.learning_api.constant;

public class RouterConstant {
    // CRUD PATH
    public static final String CREATE_PATH = "/create";
    public static final String READ_PATH = "/read";
    public static final String UPDATE_PATH = "/update";
    public static final String DELETE_PATH = "/delete";

    public static final String ADMIN_BASE_PATH = "/api/v1/admin";
    public static final String FACULTY_BASE_PATH = "/api/v1/faculty";
    public static final String FAQ_BASE_PATH = "/api/v1/faq";
    public static final String COMMENT_BASE_PATH = "/api/v1/comment";
    public static final String TEST_BASE_PATH = "/api/v1/test";
    public static final String LESSON_BASE_PATH = "/api/v1/lesson";
    public static final String SUBSTANCE_BASE_PATH = "/api/v1/substance";
    public static final String MEDIA_BASE_PATH = "/api/v1/media";
    public static final String RESOURCE_BASE_PATH = "/api/v1/resource";
    public static final String QUESTION_BASE_PATH = "/api/v1/question";
    public static final String ANSWER_BASE_PATH = "/api/v1/answer";
    public static final String STUDENT_BASE_PATH = "/api/v1/student";
    public static final String TEACHER_BASE_PATH = "/api/v1/teacher";
    public static final String COURSE_BASE_PATH = "/api/v1/course";
    public static final String SECTION_BASE_PATH = "/api/v1/section";
    public static final String CLASSROOM_BASE_PATH = "/api/v1/classroom";
    public static final String USER_AUTH_BASE_PATH = "/api/v1/auth";
    public static final String STUDENT_ENROLLMENTS_BASE_PATH = "/api/v1/student-enrollments";
    public static final String ACADEMIC_YEARS_BASE_PATH = "/api/v1/academic-years";
    public static final String MAJORS_BASE_PATH = "/api/v1/majors";
    public static final String TERMS_BASE_PATH = "/api/v1/terms";
    public static final String RECENT_CLASS_BASE_PATH = "/api/v1/recent-class";
    public static final String DEADLINE_BASE_PATH = "/api/v1/deadline";
    public static final String SUMMARY_BASE_PATH = "/api/v1/summary";
    public static final String DOCUMENT_BASE_PATH = "/api/v1/document";
    public static final String WORKSPACE_BASE_PATH = "/api/v1/workspace";
    // authentication
    public static final String POST_USER_AUTH_REGISTER_SUB_PATH = "/register";
    public static final String POST_USER_AUTH_LOGIN_SUB_PATH = "/login";
    public static final String USER_AUTH_LOGIN_SUM = "User login to get token";
    public static final String USER_AUTH_GOOGLE_LOGIN_PATH = "/google";
    public static final String USER_AUTH_GOOGLE_LOGIN_SUM = "User login with google to get token";
    public static final String USER_AUTH_LOGOUT_PATH = "/logout";
    public static final String USER_AUTH_LOGOUT_SUM = "User logout";
    public static final String USER_AUTH_REFRESH_PATH = "/refresh";
    public static final String USER_AUTH_REFRESH_SUM = "User refresh token";

    // ==========================================
    public static final String POST_USER_AUTH_SEND_CODE_TO_REGISTER_SUB_PATH = "/send-code-register";
    public static final String USER_AUTH_SEND_CODE_TO_EMAIL_TO_REGISTER_SUM = "/verify-code";

    public static final String POST_AUTH_SEND_CODE_FORGOT_PWD = "/send-code-forgot-pwd";
    public static final String USER_AUTH_SEND_CODE_TO_EMAIL_TO_GET_PWD_SUM = "/send-code-get-pwd";

    public static final String POST_USER_AUTH_VERIFY_EMAIL_SUB_PATH = "/verify-email";
    public static final String USER_AUTH_VERIFY_EMAIL_SUM = "/verify-email";

    public static final String PATCH_USER_AUTH_CHANGE_PASSWORD_SUB_PATH = "/change-password";
    public static final String USER_AUTH_CHANGE_PASSWORD_SUM = "/change-password";

    // ClassRoom path
}
