package edu.neu.madcourse.numad21su_lld;

public class StickerCard {
    private final String username;
    private final int sticker;

    public StickerCard(String username, int sticker){
        this.username = username;
        this.sticker = sticker;
    }

    public int getSticker() {
        return this.sticker;
    }

    public String getUsername() {
        return this.username;
    }
}
