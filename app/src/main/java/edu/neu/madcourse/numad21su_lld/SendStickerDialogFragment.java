package edu.neu.madcourse.numad21su_lld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import static android.content.ContentValues.TAG;


public class SendStickerDialogFragment extends DialogFragment {
    Spinner sticker_spinner;
    String[] textArray = { "Coffee",
            "Donut",
            "Egg",
            "French Fries",
            "Hamburger",
            "Ice Cream",
            "Milk",
            "Toast",
            "Watermwlon"};
    Integer[] imageArray = { R.drawable.coffee,
            R.drawable.donut,
            R.drawable.egg,
            R.drawable.french_fries,
            R.drawable.hamburger,
            R.drawable.ice_cream,
            R.drawable.milk,
            R.drawable.toast,
            R.drawable.watermelon};
    int valueToSend;

    public interface SendStickerDialogListener {
        void onDialogPositiveClick(DialogFragment sendDialog);
        void onDialogNegativeClick(DialogFragment sendDialog);
    }
    SendStickerDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Get the layout inflater
        View view = inflater.inflate(R.layout.fragment_send_sticker_dialog, null);
        sticker_spinner = (Spinner)view.findViewById(R.id.sticker_spinner);
        Context context = getContext();
        SpinnerAdapter adapter = new SpinnerAdapter(context, R.layout.spinner_value_layout, textArray, imageArray);
        sticker_spinner.setAdapter(adapter);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.send_sticker, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(SendStickerDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(SendStickerDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SendStickerDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement SendStickerDialogListener");
        }
    }

    public int getValueToSend(){
        return valueToSend;
    }
}
