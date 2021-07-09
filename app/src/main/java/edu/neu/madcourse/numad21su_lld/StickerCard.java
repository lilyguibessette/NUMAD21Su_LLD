package edu.neu.madcourse.numad21su_lld;

import android.graphics.drawable.Drawable;

public class StickerCard {
    private final String username;
    private final String sticker;
//    private final Drawable sticker_png;

    public StickerCard(String username, String sticker){
        this.username = username;
        this.sticker = sticker;
        // this.sticker_png = getApplicationContext().getResources().getIdentifier(sticker, "drawable", getPackageName());
    }

    // https://stackoverflow.com/questions/5254100/how-to-set-an-imageviews-image-from-a-string
    public StickerCard(String username, Drawable sticker_png){
        this.username = username;
   //     this.sticker_png = sticker_png;
        this.sticker = sticker_png.toString();
    }

    public String getSticker() {
        return this.sticker;
    }

    public String getStickerString() {
        return this.sticker.toString();
    }

    public String getUsername() {
        return this.username;
    }
}
