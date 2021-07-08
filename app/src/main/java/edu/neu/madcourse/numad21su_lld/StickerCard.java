package edu.neu.madcourse.numad21su_lld;

public class StickerCard {
    private final String username;
    private final Sticker sticker;

    public StickerCard(String username, Sticker sticker){
        this.username = username;
        this.sticker = sticker;
    }

    public StickerCard(String username, String sticker){
        this.username = username;
        this.sticker = Sticker.valueOf(sticker);
    }

    public Sticker getSticker() {
        return this.sticker;
    }

    public String getStickerString() {
        return this.sticker.toString();
    }

    public String getUsername() {
        return this.username;
    }
}
