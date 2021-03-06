package edu.neu.madcourse.numad21su_lld;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReceivedStickerAdapter extends RecyclerView.Adapter<ReceivedStickerHolder>{
    private final ArrayList<StickerMessage> stickerHistory;

    public ReceivedStickerAdapter(ArrayList<StickerMessage> stickerHistory) {
        this.stickerHistory = stickerHistory;
    }

    @Override
    public ReceivedStickerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_card, parent, false);
        return new ReceivedStickerHolder(view);
    }

    @Override
    public void onBindViewHolder(ReceivedStickerHolder holder, int position) {
        StickerMessage currentItem = stickerHistory.get(position);
        if (currentItem != null) {
            Log.e("onBindViewHolder",currentItem.toString());
            holder.username.setText(currentItem.getUsername());
            holder.sticker_png_id.setImageResource(currentItem.getSticker_id());
        }
    }

    @Override
    public int getItemCount() {
        return stickerHistory.size();
    }

}
