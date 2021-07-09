package edu.neu.madcourse.numad21su_lld;

import java.util.ArrayList;

public class User {
    public String username;
    public String token;
    public ArrayList<StickerMessage> received_history;


    public User(){
    }

    public User(String username, String token){
        this.username = username;
        this.token = token;
    }

    public User(String username, String token, ArrayList<StickerMessage> received_history){
        this.username = username;
        this.token = token;
        this.received_history = received_history;
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() {
        return this.token;
    }

    public ArrayList<StickerMessage> getReceivedHistory(){
        return this.received_history;
    }


    public void addMessage(StickerMessage message){
        received_history.add(message);
    }
}
