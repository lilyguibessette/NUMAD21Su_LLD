package edu.neu.madcourse.numad21su_lld;

public class StickerMessage {
    public String username;
    public int sticker_id;

    public StickerMessage(){
    }

    public StickerMessage(String username, int sticker_id){
        this.username = username;
        this.sticker_id = sticker_id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getStickerPNGID() {
        return String.valueOf(this.sticker_id);
    }

    public int getSticker_id() {
        return this.sticker_id;
    }
}
