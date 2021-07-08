package edu.neu.madcourse.numad21su_lld;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReceivedStickerHolder extends RecyclerView.ViewHolder {
    public TextView username;
    public TextView sticker;

    public ReceivedStickerHolder(View stickerHistoryView) {
        super(stickerHistoryView);
        username = stickerHistoryView.findViewById(R.id.username);
        //TODO make it a image/string keyvalue pair thing
        //sticker = stickerHistoryView.findViewById(R.id.sticker);
    }


}
