package edu.neu.madcourse.numad21su_lld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

public class SendStickerDialogFragment extends DialogFragment {
    Spinner sticker_spinner;
    String[] textArray = {
            "Coffee",
            "Donut",
            "Egg",
            "French Fries",
            "Hamburger",
            "Ice Cream",
            "Milk",
            "Toast",
            "Watermelon"};
    Integer[] imageArray = {
            R.drawable.coffee,
            R.drawable.donut,
            R.drawable.egg,
            R.drawable.french_fries,
            R.drawable.hamburger,
            R.drawable.ice_cream,
            R.drawable.milk,
            R.drawable.toast,
            R.drawable.watermelon};

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
        sticker_spinner = view.findViewById(R.id.sticker_spinner);
        sticker_spinner.setAdapter(new SpinnerAdapter(getContext(), R.layout.spinner_value_layout,
                textArray, imageArray));

        // set the buttons to send a sticker or cancel the operation
        builder.setView(view)
                // to send a sticker
                .setPositiveButton(R.string.send_sticker, (dialog, id) ->
                        listener.onDialogPositiveClick(SendStickerDialogFragment.this))
                // to cancel the dialog
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        listener.onDialogNegativeClick(SendStickerDialogFragment.this));

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SendStickerDialogListener) context;
        } catch (ClassCastException e) {
            // if the activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement SendStickerDialogListener");
        }
    }
}
