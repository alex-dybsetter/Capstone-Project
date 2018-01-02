package net.alexblass.capstoneproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import net.alexblass.capstoneproject.models.Message;
import net.alexblass.capstoneproject.utils.MessageAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.MSG_CONVERSATION_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_EMAIL_KEY;

public class ViewConversationActivity extends AppCompatActivity {

    @BindView(R.id.conversation_rv) RecyclerView mRecyclerView;
    @BindView(R.id.conversation_reply_btn) Button mReplyBtn;

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
        String email = mAuth.getCurrentUser().getEmail();

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

            final String recipient = mAdapter.getRecipient();
            mReplyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newMessageActivity = new Intent(getApplicationContext(), NewMessageActivity.class);
                    newMessageActivity.putExtra(USER_EMAIL_KEY, recipient);
                    startActivity(newMessageActivity);
                }
            });
        }
    }
}
