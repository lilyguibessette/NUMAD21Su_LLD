package edu.neu.madcourse.numad21su_lld;

public class StickerMessage {
    public String username;
    public String sticker_id;

    public StickerMessage(){
    }

    public StickerMessage(String username, String sticker_id){
        this.username = username;
        this.sticker_id = sticker_id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getSticker_id() {
        return this.sticker_id;
    }
}
