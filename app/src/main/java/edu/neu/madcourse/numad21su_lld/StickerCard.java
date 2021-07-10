package edu.neu.madcourse.numad21su_lld;

public class StickerCard {
    private final String username;
    private final int sticker;
    //private final int sticker_png_id;

    public StickerCard(String username, int sticker){
        this.username = username;
        this.sticker = sticker;
        // this.sticker_png = getApplicationContext().getResources().getIdentifier(sticker, "drawable", getPackageName());
        //this.sticker_png_id = sticker_png_id;
    }

    // https://stackoverflow.com/questions/5254100/how-to-set-an-imageviews-image-from-a-string

    public int getSticker() {
        return this.sticker;
    }

    // TODO: I think we can delete this now
    public String getStickerPNGID() {
        return String.valueOf(this.sticker);
    }

    public String getUsername() {
        return this.username;
    }
}
