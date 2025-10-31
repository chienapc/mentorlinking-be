package vn.fpt.se18.MentorLinking_BackEnd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "message_image")
public class MessageImage extends AbstractEntity<Long> implements Serializable {

    @Column(nullable = false, name = "url")
    private String url;

    @Column(nullable = false, name = "public_id")
    private String publicId;

    @ManyToOne
    private Message message;
}
