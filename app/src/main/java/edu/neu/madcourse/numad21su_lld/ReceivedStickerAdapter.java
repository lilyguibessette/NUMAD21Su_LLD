package edu.neu.madcourse.numad21su_lld;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import androidx.recyclerview.widget.RecyclerView;
    
    import java.util.ArrayList;

public class ReceivedStickerAdapter extends RecyclerView.Adapter<ReceivedStickerHolder>{
    private final ArrayList<StickerCard> stickerHistory;

    public ReceivedStickerAdapter(ArrayList<StickerCard> stickerHistory) {
        this.stickerHistory = stickerHistory;
    }

    @Override
    public ReceivedStickerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_card, parent, false);
        return new ReceivedStickerHolder(view);
    }

    @Override
    public void onBindViewHolder(ReceivedStickerHolder holder, int position)  {
        StickerCard currentItem = stickerHistory.get(position);
        holder.username.setText(currentItem.getUsername());
        holder.sticker_png_id.setImageResource(currentItem.getPNGid());
    }

    @Override
    public int getItemCount() {
        return stickerHistory.size();
    }

}
