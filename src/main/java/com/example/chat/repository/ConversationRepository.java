package com.example.chat.repository;

import java.util.List;
import java.util.UUID;

import com.example.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

  @Query(
      "select c from Conversation c where c.toUser = :toUser and c.deliveryStatus in ('NOT_DELIVERED', 'DELIVERED') and c.fromUser = :fromUser")
  List<Conversation> findUnseenMessages(
      @Param("toUser") UUID toUser, @Param("fromUser") UUID fromUser);

  @Query(
      value =
          "select * from Conversation where to_user = :toUser and delivery_status in ('NOT_DELIVERED', 'DELIVERED')",
      nativeQuery = true)
  List<Conversation> findUnseenMessagesCount(@Param("toUser") UUID toUser);
}
