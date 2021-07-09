package edu.neu.madcourse.numad21su_lld;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReceivedStickerHolder extends RecyclerView.ViewHolder {
    public TextView username;
    //public TextView sticker;
    public ImageView sticker_png_id;

    public ReceivedStickerHolder(View stickerHistoryView) {
        super(stickerHistoryView);
        username = stickerHistoryView.findViewById(R.id.username);
        sticker_png_id = stickerHistoryView.findViewById(R.id.sticker_icon);
    }


}
