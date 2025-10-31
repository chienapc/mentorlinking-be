package vn.fpt.se18.MentorLinking_BackEnd.dto.response.chat;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageResponse implements Serializable {

    private String localId;

    private Long fromUser;

    private String fromUserAvatarUrl;

    private String fromUserFullName;

    private String messageText;

    private String[] messageImage;

    private Date sendDateTime;
}

