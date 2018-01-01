package net.alexblass.capstoneproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import net.alexblass.capstoneproject.models.Message;
import net.alexblass.capstoneproject.utils.InboxAdapter;
import net.alexblass.capstoneproject.utils.MessageAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.MSG_CONVERSATION_KEY;

public class ViewConversationActivity extends AppCompatActivity {

    @BindView(R.id.conversation_rv) RecyclerView mRecyclerView;

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
            //mAdapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
