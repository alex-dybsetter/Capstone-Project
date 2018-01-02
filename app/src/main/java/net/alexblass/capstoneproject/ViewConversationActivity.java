package net.alexblass.capstoneproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.MSG_CONVERSATION_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_EMAIL_KEY;

public class ViewConversationActivity extends AppCompatActivity {

    @BindView(R.id.conversation_rv) RecyclerView mRecyclerView;
    @BindView(R.id.conversation_reply_msg_data) EditText mReplyContentEt;
    @BindView(R.id.conversation_reply_btn) Button mReplyBtn;
    @BindView(R.id.conversation_parent) LinearLayout mParent;

    private FirebaseAuth mAuth;
    private ArrayList<Message> mMessageThread;
    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_conversation);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        final String email = mAuth.getCurrentUser().getEmail();

        mParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clearFocus();
                return false;
            }
        });

        Intent intentThatStartedThis = getIntent();
        if (intentThatStartedThis.hasExtra(MSG_CONVERSATION_KEY)){
            mMessageThread = intentThatStartedThis.getParcelableArrayListExtra(MSG_CONVERSATION_KEY);

            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mRecyclerView.setHasFixedSize(true);

            mAdapter = new MessageAdapter(this,
                    mMessageThread.toArray(new Message[mMessageThread.size()]), email);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

            mReplyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearFocus();

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

                                Message message = new Message(email,
                                        recipient,
                                        msg);

                                String msgLbl = NewMessageActivity.generateMessageLbl(email.toLowerCase(), recipient.toLowerCase()).replace(".", "(dot)");
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference(MSG_KEY).child(msgLbl)
                                        .child(String.valueOf(new GregorianCalendar().getTimeInMillis()));
                                database.setValue(message);

                                // todo send notification to recepient

                                Toast.makeText(getApplicationContext(), getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
                                mReplyContentEt.setText("");
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_username), Toast.LENGTH_SHORT).show();
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
        return super.onOptionsItemSelected(item);
    }
}
