package net.alexblass.capstoneproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.Message;
import net.alexblass.capstoneproject.utils.MessageAdapter;
import net.alexblass.capstoneproject.utils.UserDataUtils;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.MSG_CONVERSATION_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_DATA_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_READ_FLAG_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENDER_EMAIL_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENT_TO_EMAIL_KEY;

public class ViewConversationActivity extends AppCompatActivity {

    @BindView(R.id.conversation_rv) RecyclerView mRecyclerView;
    @BindView(R.id.conversation_reply_msg_data) EditText mReplyContentEt;
    @BindView(R.id.conversation_reply_btn) Button mReplyBtn;
    @BindView(R.id.conversation_parent) LinearLayout mParent;

    private FirebaseAuth mAuth;
    private ArrayList<Message> mMessageThread;
    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter mAdapter;

    private Query mQuery;
    private ValueEventListener mListener;

    private AlertDialog mOfflineDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_conversation);
        ButterKnife.bind(this);

        AlertDialog.Builder offlineDialog = new AlertDialog.Builder(this);
        offlineDialog.setTitle(getString(R.string.offline_edits_dialog_title))
                .setMessage(getString(R.string.offline_messages_prompt))
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        mOfflineDialog = offlineDialog.create();

        if (!UserDataUtils.checkNetworkConnectivity(this)) {
            mOfflineDialog.show();
            mReplyBtn.setEnabled(false);
            mReplyBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_inactive_bg));
        } else {
            mReplyBtn.setEnabled(true);
            mReplyBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        }

        mAuth = FirebaseAuth.getInstance();
        final String email = mAuth.getCurrentUser().getEmail();

        mMessageThread = new ArrayList<>();

        mParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clearFocus();
                return false;
            }
        });

        Intent intentThatStartedThis = getIntent();
        if (intentThatStartedThis.hasExtra(MSG_CONVERSATION_KEY)){
            final String conversationKey = intentThatStartedThis.getStringExtra(MSG_CONVERSATION_KEY);

            mQuery = FirebaseDatabase.getInstance().getReference().child(MSG_KEY).child(conversationKey);

            mListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mMessageThread = new ArrayList<>();
                    mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);

                    mRecyclerView.setHasFixedSize(true);
                    mAdapter = new MessageAdapter(getApplicationContext(), new Message[0], email);
                    mRecyclerView.setAdapter(mAdapter);

                    if (dataSnapshot.exists() && savedInstanceState == null) {

                        Iterable<DataSnapshot> results = dataSnapshot.getChildren();
                        for (DataSnapshot messageData : results){

                            String sender = messageData.child(MSG_SENDER_EMAIL_KEY).getValue().toString();
                            String sentTo = messageData.child(MSG_SENT_TO_EMAIL_KEY).getValue().toString();

                            boolean isRead = (boolean)messageData.child(MSG_READ_FLAG_KEY).getValue();
                            if (!isRead){
                                isRead = email.equals(sentTo);
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference(MSG_KEY)
                                        .child(conversationKey).child(messageData.getKey()).child(MSG_READ_FLAG_KEY);
                                database.setValue(isRead);
                            }

                            Message message = new Message(sender, sentTo,
                                    messageData.child(MSG_DATA_KEY).getValue().toString(), isRead);

                            mMessageThread.add(message);
                        }
                        mAdapter.updateMessageResults(mMessageThread.toArray(new Message[mMessageThread.size()]));
                        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_retrieval_error), Toast.LENGTH_SHORT).show();
                }
            };

            mQuery.addValueEventListener(mListener);

            mReplyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearFocus();
                    if (!UserDataUtils.checkNetworkConnectivity(getApplicationContext())) {
                        if (!mOfflineDialog.isShowing()){
                            mOfflineDialog.show();
                        }
                        mReplyBtn.setEnabled(false);
                        mReplyBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_inactive_bg));
                        return;
                    } else {
                        mReplyBtn.setEnabled(true);
                        mReplyBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_bg));
                    }

                    final String recipient = mAdapter.getRecipient();

                    final String msg = mReplyContentEt.getText().toString();
                    if (msg.isEmpty()){
                        Toast.makeText(getApplicationContext(), getString(R.string.empty_message), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Query query = FirebaseDatabase.getInstance().getReference().child(recipient.replace(".", "(dot)"));
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Message message = new Message(email, recipient, msg, false);

                                String msgLbl = UserDataUtils.generateMessageLbl(email, recipient);
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference(MSG_KEY).child(msgLbl)
                                        .child(String.valueOf(new GregorianCalendar().getTimeInMillis()));
                                database.setValue(message);

                                mReplyContentEt.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOfflineDialog != null){
            mOfflineDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserDataUtils.checkNetworkConnectivity(this)) {
            if (!mOfflineDialog.isShowing()){
                mOfflineDialog.show();
            }
            mReplyBtn.setEnabled(false);
            mReplyBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_inactive_bg));
        } else {
            mReplyBtn.setEnabled(true);
            mReplyBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MSG_CONVERSATION_KEY, mMessageThread);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mMessageThread = savedInstanceState.getParcelableArrayList(MSG_CONVERSATION_KEY);

            mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mAdapter = new MessageAdapter(getApplicationContext(), new Message[0], FirebaseAuth.getInstance().getCurrentUser().getEmail());
            mRecyclerView.setAdapter(mAdapter);

            mAdapter.updateMessageResults(mMessageThread.toArray(new Message[mMessageThread.size()]));
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    private void clearFocus(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mReplyContentEt.clearFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(ViewConversationActivity.this, LoginActivity.class);
            startActivity(loginActivity);
            return true;
        }
        if (id == R.id.action_send_new_message){
            Intent newMessageActivity = new Intent(this, NewMessageActivity.class);
            startActivity(newMessageActivity);
            return true;
        }
        if (id == android.R.id.home){
            if (!UserDataUtils.checkNetworkConnectivity(this)) {
                UserDataUtils.resetApp(this);
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mQuery.removeEventListener(mListener);
    }
}
