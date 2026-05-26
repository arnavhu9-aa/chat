package com.ai.chat.dto;

public class ChatRequest {
     private String message;
     
     public ChatRequest() {}
     public ChatRequest(String ch) {
    	 this.message = ch;
     }
	 public String getMessage() {
		 return message;
	 }
	 public void setMessage(String message) {
		 this.message = message;
	 }
     
}
