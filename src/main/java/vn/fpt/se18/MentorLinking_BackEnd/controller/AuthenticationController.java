package vn.fpt.se18.MentorLinking_BackEnd.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.fpt.se18.MentorLinking_BackEnd.dto.request.BaseRequest;
import vn.fpt.se18.MentorLinking_BackEnd.dto.request.auth.SignInRequest;
import vn.fpt.se18.MentorLinking_BackEnd.dto.request.auth.SignUpMentorRequest;
import vn.fpt.se18.MentorLinking_BackEnd.dto.request.auth.SignUpRequest;
import vn.fpt.se18.MentorLinking_BackEnd.dto.response.BaseResponse;
import vn.fpt.se18.MentorLinking_BackEnd.dto.response.auth.TokenResponse;
import vn.fpt.se18.MentorLinking_BackEnd.service.AuthenticationService;

import java.util.Date;

import static org.springframework.http.HttpStatus.OK;


@Slf4j
@Validated
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/access-token")
    public ResponseEntity<TokenResponse> accessToken(@RequestBody SignInRequest request) {
        return new ResponseEntity<>(authenticationService.accessToken(request), OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.refreshToken(request), OK);
    }

    @PostMapping("/remove-token")
    public ResponseEntity<String> removeToken(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.removeToken(request), OK);
    }


    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody SignInRequest request) {
        return authenticationService.accessToken(request);
    }

    @PostMapping("/signup")
    public TokenResponse signUp(@Valid @RequestBody SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @PostMapping("/mentor-signup")
    public TokenResponse MentorSignUp(@Valid @ModelAttribute SignUpMentorRequest request) {
        return authenticationService.signUpMentor(request);
    }



//    @PostMapping("/forgot-password")
//    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
//        return new ResponseEntity<>(authenticationService.forgotPassword(email), OK);
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<String> resetPassword(@RequestBody String secretKey) {
//        return new ResponseEntity<>(authenticationService.resetPassword(secretKey), OK);
//    }
//
//    @PostMapping("/change-password")
//    public ResponseEntity<String> changePassword(@RequestBody @Valid ResetPasswordDTO request) {
//        return new ResponseEntity<>(authenticationService.changePassword(request), OK);
//    }
}
