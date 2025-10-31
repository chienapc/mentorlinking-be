package vn.fpt.se18.MentorLinking_BackEnd.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "message")
public class Message extends AbstractEntity<Long> implements Serializable {

    @ManyToOne
    @JoinColumn(name = "from_user", referencedColumnName = "id")
    private User fromUser;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageImage> messageImages;

    @Column(name = "message_text")
    private String messageText;

    @Column(name = "send_date_time")
    private Date sendDateTime;

    @ManyToOne
    @JoinColumn(name = "conversation_id", referencedColumnName = "id")
    private Conversation conversationId;
}
