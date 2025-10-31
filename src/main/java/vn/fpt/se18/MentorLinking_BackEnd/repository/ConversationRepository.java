package vn.fpt.se18.MentorLinking_BackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpt.se18.MentorLinking_BackEnd.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
