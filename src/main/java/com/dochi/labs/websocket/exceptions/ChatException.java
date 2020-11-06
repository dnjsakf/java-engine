package com.dochi.labs.websocket.exceptions;

public class ChatException extends Exception {
    private static final long serialVersionUID = 1L;

    ChatException(String message){
        super(message);
    }
}
