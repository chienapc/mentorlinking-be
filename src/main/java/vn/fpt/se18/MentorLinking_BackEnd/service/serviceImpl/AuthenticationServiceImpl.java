package vn.fpt.se18.MentorLinking_BackEnd.service.serviceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.fpt.se18.MentorLinking_BackEnd.dto.request.auth.ResetPasswordDTO;
import vn.fpt.se18.MentorLinking_BackEnd.dto.request.auth.SignInRequest;
import vn.fpt.se18.MentorLinking_BackEnd.dto.request.auth.SignUpRequest;
import vn.fpt.se18.MentorLinking_BackEnd.dto.response.auth.TokenResponse;
import vn.fpt.se18.MentorLinking_BackEnd.entity.Role;
import vn.fpt.se18.MentorLinking_BackEnd.entity.Token;
import vn.fpt.se18.MentorLinking_BackEnd.entity.User;
import vn.fpt.se18.MentorLinking_BackEnd.exception.AppException;
import vn.fpt.se18.MentorLinking_BackEnd.exception.ErrorCode;
import vn.fpt.se18.MentorLinking_BackEnd.repository.RoleRepository;
import vn.fpt.se18.MentorLinking_BackEnd.repository.UserRepository;
import vn.fpt.se18.MentorLinking_BackEnd.service.AuthenticationService;
import vn.fpt.se18.MentorLinking_BackEnd.service.JwtService;
import vn.fpt.se18.MentorLinking_BackEnd.service.TokenService;
import vn.fpt.se18.MentorLinking_BackEnd.service.UserService;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static vn.fpt.se18.MentorLinking_BackEnd.exception.ErrorCode.UNCATEGORIZED;
import static vn.fpt.se18.MentorLinking_BackEnd.util.TokenType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public TokenResponse accessToken(SignInRequest request) {
        log.info("---------- authenticate ----------");

        // authenticate
        var user = userService.getByUsername(request.getUsername());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
        ));

        // generate token
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // save to db
        tokenService.save(Token.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");

        // get token from request
        final String authorization = request.getHeader(AUTHORIZATION);
        final String refreshToken;
        final String userName;

        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            throw new AppException(UNCATEGORIZED);
        }

        refreshToken = authorization.substring(7);
        userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);

        if (StringUtils.isNotBlank(userName)) {
            var user = userService.getByUsername(userName);
            var storedToken = tokenService.getByUsername(userName);

            if (jwtService.isValid(refreshToken, REFRESH_TOKEN, user) &&
                    refreshToken.equals(storedToken.getRefreshToken())) {
                var accessToken = jwtService.generateToken(user);

                // save new token to db
                tokenService.save(Token.builder()
                        .username(user.getUsername())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build());

                return TokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .userId(user.getId())
                        .build();
            }
        }

        throw new AppException(UNCATEGORIZED);
    }

    @Override
    public String removeToken(HttpServletRequest request) {
        log.info("---------- removeToken ----------");

        // get token from request
        final String authorization = request.getHeader(AUTHORIZATION);
        final String token;
        final String userName;

        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            throw new AppException(UNCATEGORIZED);
        }

        token = authorization.substring(7);
        userName = jwtService.extractUsername(token, ACCESS_TOKEN);

        // remove token from db
        tokenService.delete(userName);

        return "Remove token successfully";
    }

    @Override
    public String forgotPassword(String email) {
        log.info("---------- forgotPassword ----------");

        // check email exists or not
        User user = userService.getUserByEmail(email);

        // generate reset token
        String resetToken = jwtService.generateResetToken(user);

        // save to db
        tokenService.save(Token.builder()
                .username(user.getUsername())
                .resetToken(resetToken)
                .build());

        // TODO send email to user
        String confirmLink = String.format("curl --location 'http://localhost:80/auth/reset-password' \\\n" +
                "--header 'accept: */*' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '%s'", resetToken);
        log.info("--> confirmLink: {}", confirmLink);

        return resetToken;
    }

    @Override
    public String resetPassword(String secretKey) {
        log.info("---------- resetPassword ----------");

        // validate token
        var user = validateToken(secretKey);

        // check token by username
        tokenService.getByUsername(user.getUsername());

        return "Reset";
    }

    @Override
    public String changePassword(ResetPasswordDTO request) {
        log.info("---------- changePassword ----------");

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(UNCATEGORIZED);
        }

        // get user by reset token
        var user = validateToken(request.getSecretKey());

        // update password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);

        return "Password changed successfully";
    }

    @Override
    public TokenResponse signUp(SignUpRequest request) {
        log.info("---------- signUp ----------");

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Password and confirm password do not match");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Email already exists");
        }

        // Get role or set default role
        Role role;
        if (StringUtils.isNotBlank(request.getRoleName())) {
            role = roleRepository.findByName(request.getRoleName())
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Role not found"));
        } else {
            role = roleRepository.findByName("admin")
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Default role not found"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullname(request.getFullName())
                .role(role)
                .build();

        userRepository.save(user);

        // Generate tokens
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Save tokens to db
        tokenService.save(Token.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    private User validateToken(String token) {
        // validate token
        var userName = jwtService.extractUsername(token, RESET_TOKEN);

        // validate user is active or not
        var user = userService.getByUsername(userName);
        if (!user.isEnabled()) {
            throw new AppException(UNCATEGORIZED);
        }

        return user;
    }
}