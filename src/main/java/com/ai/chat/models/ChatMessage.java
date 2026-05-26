package com.ai.chat.models;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ChatMessage {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
       private Long id;
       private String role;
       @Column (columnDefinition = "TEXT")
       private String content;
       private LocalDateTime createdAt = LocalDateTime.now();
       
       @ManyToOne
       private AppUser user;
       
       public ChatMessage() {}
       public ChatMessage(Long id,String role,String test,AppUser ap) {
    	   this.content = test;
    	   this.id = id;
    	   this.role =role;
    	   this.user =ap;
       }
       public Long getId() {
    	   return id;
       }
	   public String getRole() {
		   return role;
	   }
	   public void setRole(String role) {
		   this.role = role;
	   }
	   public String getContent() {
		   return content;
	   }
	   public void setContent(String content) {
		   this.content = content;
	   }
	   public LocalDateTime getCreatedAt() {
		   return createdAt;
	   }
	   public void setCreatedAt(LocalDateTime createdAt) {
		   this.createdAt = createdAt;
	   }
	   public AppUser getUser() {
		   return user;
	   }
	   public void setUser(AppUser user) {
		   this.user = user;
	   }
	   public void setId(Long id) {
		   this.id = id;
	   }
       
}
