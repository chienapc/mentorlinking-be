package vn.fpt.se18.MentorLinking_BackEnd.dto.request.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import vn.fpt.se18.MentorLinking_BackEnd.dto.request.BasePageRequest;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetUserRequest extends BasePageRequest {
    private String keySearch;
    private Integer roleId;
    private Integer status;
}