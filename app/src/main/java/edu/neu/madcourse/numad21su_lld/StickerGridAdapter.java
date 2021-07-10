package edu.neu.madcourse.numad21su_lld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class StickerGridAdapter extends ArrayAdapter {
    public StickerGridAdapter(@NonNull Context context, ArrayList<Sticker> stickerArrayList) {
        super(context,0,stickerArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_items,
                    parent, false);
        }
        Sticker sticker = (Sticker) getItem(position);
        ImageView sticker_icon = listitemView.findViewById(R.id.spinner_image);
        sticker_icon.setImageResource(sticker.getIMG_id());
        return listitemView;
    }
}
