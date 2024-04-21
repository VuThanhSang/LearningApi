package com.example.learning_api.controller;

import com.example.learning_api.dto.common.MessageDto;
import com.example.learning_api.dto.common.TokenDto;
import com.example.learning_api.dto.common.UrlDto;
import com.example.learning_api.model.OauthProperties;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static com.example.learning_api.constant.RouterConstant.USER_AUTH_BASE_PATH;

@RestController
public class DemoController {

    private final OauthProperties oauthProperties;

    public DemoController(OauthProperties oauthProperties) {
        this.oauthProperties = oauthProperties;
    }

    @GetMapping("/demo")
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok("Hello from secured url");
    }

    @GetMapping("/admin/only")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Hello from admin only url");
    }
    @GetMapping("/user/only")
    public ResponseEntity<String> userOnly() {
        return new ResponseEntity("Hello from user only url", HttpStatus.OK);
    }

    @GetMapping("/oauth2/user-info")
    public Object home(@AuthenticationPrincipal OAuth2User principal) {
        return  principal.getAttributes();
    }


}
