package edu.neu.madcourse.numad21su_lld;

        import android.app.Dialog;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.fragment.app.DialogFragment;
        import androidx.recyclerview.widget.ItemTouchHelper;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.google.android.material.floatingactionbutton.FloatingActionButton;
        import com.google.android.material.snackbar.Snackbar;
        import java.util.ArrayList;


public class ReceivedActivity extends AppCompatActivity implements SendStickerDialogFragment.SendStickerDialogListener {
    private final ArrayList<StickerCard> stickerHistory = new ArrayList<>();
    private RecyclerView stickerRecyclerView;
    private ReceivedStickerAdapter receivedStickerAdapter;
    private RecyclerView.LayoutManager receivedStickerLayoutManager;
    private FloatingActionButton sendStickerButton; //TODO

    private static final String KEY_OF_STICKER = "KEY_OF_STICKER";
    private static final String NUMBER_OF_STICKERS = "NUMBER_OF_STICKERS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);

        //TODO Add in layout button
        sendStickerButton = findViewById(R.id.sendStickerButton);
        sendStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use dialog for sending link
                startSendDialog();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(ReceivedActivity.this, "Send Sticker Back!", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getLayoutPosition();
                //TODO maybe allow this to open dialog for that particular username?
                // - autopopulate the username field in the dialog frag?
                //stickerHistory.remove(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(stickerRecyclerView);

        // TODO Configure receiving from database the list of stickers
        // Need to add these to when receiving from database
        // --- stickerHistory.add(0, new StickerCard(username, sticker));
        // --- receivedStickerAdapter.notifyItemInserted(0);
        // Also need to keep track of number received and sent
    }

    public void startSendDialog() {
        DialogFragment sendDialog = new SendStickerDialogFragment();
        sendDialog.show(getSupportFragmentManager(), "sendDialogFragment");
    }

    public void onDialogPositiveClick(DialogFragment sendDialog) {
        Dialog addSendDialog = sendDialog.getDialog();

        String username = ((EditText) addSendDialog.findViewById(R.id.username)).getText().toString();
        // Need to figure out how to select icons and associate them with a string/enum
        // TODO currently just have stickers as text/string but need to change to icon
        String sticker = ((EditText) addSendDialog.findViewById(R.id.sticker)).getText().toString();


        // TODO put this in thread
        // --------------
        // do this in another thread?

        // need to change here to some sort of validation that the username exists in the database
        if (isValidUsername(username)) {
            sendDialog.dismiss();
            // add to database as well here
            // TODO

            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, R.string.send_sticker_confirm, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else {
            //invalid username
            Toast.makeText(ReceivedActivity.this, R.string.send_sticker_error, Toast.LENGTH_SHORT).show();
        }
        // --------------
    }

    // TODO validate username
    // currently fake method to be changed to connection to database
    private boolean isValidUsername(String username) {
        // do in another thread ?
        if (username == "blah"){
            return true;
        }
        return false;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment sendDialog) {
        sendDialog.dismiss();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int size = stickerHistory == null ? 0 : stickerHistory.size();
        outState.putInt(NUMBER_OF_STICKERS, size);
        for (int i = 0; i < size; i++) {
            outState.putString(KEY_OF_STICKER + i + "0", stickerHistory.get(i).getUsername());
            outState.putString(KEY_OF_STICKER + i + "1", stickerHistory.get(i).getStickerString());
        }
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
        createRecyclerView();
    }

    private void initialItemData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_STICKERS)) {
            if (stickerHistory == null || stickerHistory.size() == 0) {

                int size = savedInstanceState.getInt(NUMBER_OF_STICKERS);

                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String username = savedInstanceState.getString(KEY_OF_STICKER + i + "0");
                    String sticker = savedInstanceState.getString(KEY_OF_STICKER + i + "1");
                    StickerCard StickerCard = new StickerCard(username, Sticker.valueOf(sticker));
                    stickerHistory.add(StickerCard);
                }
            }
        }
    }

    private void createRecyclerView() {
        receivedStickerLayoutManager = new LinearLayoutManager(this);
        stickerRecyclerView = findViewById(R.id.recycler_view);
        stickerRecyclerView.setHasFixedSize(true);
        receivedStickerAdapter = new ReceivedStickerAdapter(stickerHistory);
        stickerRecyclerView.setAdapter(receivedStickerAdapter);
        stickerRecyclerView.setLayoutManager(receivedStickerLayoutManager);
    }

}

