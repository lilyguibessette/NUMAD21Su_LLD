package edu.neu.madcourse.numad21su_lld;

// TODO: I think we can delete this??
//https://www.geeksforgeeks.org/gridview-in-android-with-example/
public class Sticker {
    private String sticker;
    private int IMG_id;

    public Sticker(String sticker, int IMG_id) {
        this.sticker = sticker;
        this.IMG_id = IMG_id;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String course_name) {
        this.sticker = course_name;
    }

    public int getIMG_id() {
        return IMG_id;
    }

    public void setIMG_id(int IMG_id) {
        this.IMG_id = IMG_id;
    }
}
