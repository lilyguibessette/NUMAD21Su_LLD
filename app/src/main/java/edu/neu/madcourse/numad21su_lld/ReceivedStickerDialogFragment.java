package edu.neu.madcourse.numad21su_lld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.DialogFragment;

public class ReceivedStickerDialogFragment extends DialogFragment {

    public interface ReceivedStickerDialogListener {
        void onDialogPositiveClick(DialogFragment sendDialog);
        void onDialogNegativeClick(DialogFragment sendDialog);
    }

    ReceivedStickerDialogFragment.ReceivedStickerDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_received_sticker_dialog, null))
                .setPositiveButton(R.string.send_sticker_back, (dialog, id) ->
                        listener.onDialogPositiveClick(ReceivedStickerDialogFragment.this))
                .setNegativeButton(R.string.close, (dialog, id) ->
                        listener.onDialogNegativeClick(ReceivedStickerDialogFragment.this));
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ReceivedStickerDialogFragment.ReceivedStickerDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement ReceivedStickerDialogListener");
        }
    }
}
