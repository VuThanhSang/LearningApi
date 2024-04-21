package com.example.learning_api.constant;

public class RouterConstant {
    //==========================================
    public static final String USER_AUTH_BASE_PATH = "/auth";
    //authentication
    public static final String POST_USER_AUTH_REGISTER_SUB_PATH = "/register";
    public static final String POST_USER_AUTH_LOGIN_SUB_PATH = "/login";
    public static final String USER_AUTH_LOGIN_SUM ="User login to get token";
    public static final String USER_AUTH_GOOGLE_LOGIN_PATH = "/google";
    public static final String USER_AUTH_GOOGLE_LOGIN_SUM = "User login with google to get token";
    public static final String USER_AUTH_LOGOUT_PATH = "/logout";
    public static final String USER_AUTH_LOGOUT_SUM = "User logout";
    public static final String USER_AUTH_REFRESH_PATH = "/refresh";
    public static final String USER_AUTH_REFRESH_SUM = "User refresh token";

    //==========================================
    public static final String POST_USER_AUTH_SEND_CODE_TO_REGISTER_SUB_PATH = "/send-code-register";
    public static final String USER_AUTH_SEND_CODE_TO_EMAIL_TO_REGISTER_SUM = "/verify-code";

    public static final String POST_USER_AUTH_SEND_CODE_TO_GET_PWD_SUB_PATH = "/send-code-get-pwd";
    public static final String USER_AUTH_SEND_CODE_TO_EMAIL_TO_GET_PWD_SUM = "/send-code-get-pwd";

    public static final String POST_USER_AUTH_VERIFY_EMAIL_SUB_PATH = "/verify-email";
    public static final String USER_AUTH_VERIFY_EMAIL_SUM = "/verify-email";

    public static final String PATCH_USER_AUTH_CHANGE_PASSWORD_SUB_PATH = "/change-password";
    public static final String USER_AUTH_CHANGE_PASSWORD_SUM = "/change-password";
}
