package com.example.demo.repository;

import com.example.demo.model.ChatMessageModel;
import com.example.demo.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageModel, Long> {
    @Query("SELECT c FROM ChatMessageModel c WHERE (c.sender = ?1 AND c.receiver = ?2) OR (c.sender = ?2 AND c.receiver = ?1) ORDER BY c.sentAt DESC")
    List<ChatMessageModel> findConversation(Optional<UserModel> user1, UserModel user2);

    @Query("SELECT c FROM ChatMessageModel c WHERE c.sentAt IN " +
           "(SELECT MAX(cm.sentAt) FROM ChatMessageModel cm " +
           "WHERE cm.sender.id = ?1 OR cm.receiver.id = ?1 " +
           "GROUP BY CASE " +
           "WHEN cm.sender.id = ?1 THEN cm.receiver.id " +
           "WHEN cm.receiver.id = ?1 THEN cm.sender.id " +
           "END) " +
           "ORDER BY c.sentAt DESC")
    List<ChatMessageModel> findLastMessagesForUser(Long userId);
}
