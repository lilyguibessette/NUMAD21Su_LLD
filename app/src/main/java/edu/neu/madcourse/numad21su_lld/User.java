package edu.neu.madcourse.numad21su_lld;

import java.util.ArrayList;

public class User {
    public String username;
    public String token;
    public ArrayList<StickerMessage> received_history;
    public int stickers_sent;

    public User(){
    }

    public User(String username, String token){
        this.username = username;
        this.token = token;
        this.stickers_sent = 0;
        this.received_history = new ArrayList<StickerMessage>();
        this.received_history.add(new StickerMessage("WELCOME", R.drawable.muncha_crunch));
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() {
        return this.token;
    }

    public ArrayList<StickerMessage> getReceived_history(){
        return this.received_history;
    }

    public int getStickers_sent(){
        return this.stickers_sent;
    }

    public void addMessage(StickerMessage message){
        received_history.add(message);
    }
}
