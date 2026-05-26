package com.ai.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ai.chat.models.AppUser;
import com.ai.chat.models.ChatMessage;

@Repository
public interface ChatRepository extends JpaRepository <ChatMessage,Long> {
	List<ChatMessage> findByUserOrderByCreatedAt(AppUser user);

}
