package vn.fpt.se18.MentorLinking_BackEnd.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import vn.fpt.se18.MentorLinking_BackEnd.service.JwtService;
import vn.fpt.se18.MentorLinking_BackEnd.service.UserService;
import vn.fpt.se18.MentorLinking_BackEnd.util.TokenType;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthChanelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserService userService;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor =StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.CONNECT.equals(stompHeaderAccessor.getCommand())){
            String authHeader = stompHeaderAccessor.getFirstNativeHeader("Authorization");

            if(authHeader != null && authHeader.startsWith("Bearer ")){
                String token = authHeader.substring("Bearer ".length());
                String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);

                if(username != null){
                    try{
                        UserDetails userDetails  = userService.getByUsername(username);
                        if(jwtService.isValid(token, TokenType.ACCESS_TOKEN, userDetails)){
                            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            stompHeaderAccessor.setUser(auth);
                        }


                    }catch (Exception e){
                        log.error("WebSocket authentication failed", e);
                        return null; // chặn kết nối nếu lỗi
                    }
                }
            }
        }
        return message;
    }
}
