package com.example.sora.coins;
import java.util.Date;

public class ChatMessage {

    private String username;
    private String message;
    private Date date;
    private boolean incomingMessage;

    public ChatMessage() {
        super();
    }

    public ChatMessage(String message) {
        super();
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIncomingMessage() {
        return incomingMessage;
    }


    public Date getDate() {
        return date;
    }

    public boolean isSystemMessage(){
        return getUsername()==null;
    }

}
