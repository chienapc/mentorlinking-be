package vn.fpt.se18.MentorLinking_BackEnd.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "username must be not blank")
    private String password;

}
