package com.ai.chat.dto;

public class ChatResponse {
     private String reply;
     
     public ChatResponse() {}
     
     public ChatResponse (String rep) {
    	 this.reply = rep;
     }

	 public String getReply() {
		 return reply;
	 }

	 public void setReply(String reply) {
		 this.reply = reply;
	 }
     
}
