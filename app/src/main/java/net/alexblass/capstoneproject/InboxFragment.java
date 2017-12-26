package net.alexblass.capstoneproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.Message;
import net.alexblass.capstoneproject.utils.InboxAdapter;
import net.alexblass.capstoneproject.utils.MessageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.MSG_DATA;
import static net.alexblass.capstoneproject.data.Keys.MSG_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENDER_EMAIL_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENT_TO_EMAIL_KEY;

/**
 * A Fragment to display the user's message inbox.
 */
public class InboxFragment extends Fragment implements InboxAdapter.ItemClickListener {

    @BindView(R.id.inbox_messages_rv) RecyclerView mRecyclerView;
    @BindView(R.id.inbox_empty_tv) TextView mEmptyInboxTv;

    private FirebaseAuth mAuth;
    private LinearLayoutManager mLinearLayoutManager;
    private Map<String, List<Message>> mMessages;
    private InboxAdapter mAdapter;

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inbox, container, false);
        ButterKnife.bind(this, root);

        mAuth = FirebaseAuth.getInstance();
        final String email = mAuth.getCurrentUser().getEmail().replace(".", "(dot)");
        mMessages = new HashMap<>();



        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new InboxAdapter(getActivity(), mMessages, mAuth.getCurrentUser().getEmail());
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        Query query = FirebaseDatabase.getInstance().getReference().child(MSG_KEY);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> results = dataSnapshot.getChildren();
                    for (DataSnapshot messageThreadData : results){
                        if(messageThreadData.getKey().contains(email)){

                            for (DataSnapshot messageData : messageThreadData.getChildren()) {
                                String sender = messageData.child(MSG_SENDER_EMAIL_KEY).getValue().toString();
                                String sentTo = messageData.child(MSG_SENT_TO_EMAIL_KEY).getValue().toString();
                                Message message  = new Message(sender, sentTo,
                                        messageData.child(MSG_DATA).toString());

                                if (mMessages.containsKey(sender)){
                                    List<Message> thread = mMessages.get(sender);
                                    thread.add(message);
                                    mMessages.remove(sender);
                                    mMessages.put(sender, thread);
                                } else if (mMessages.containsKey(sentTo)){
                                    List<Message> thread = mMessages.get(sentTo);
                                    thread.add(message);
                                    mMessages.remove(sentTo);
                                    mMessages.put(sentTo, thread);
                                } else {
                                    String recipient;
                                    if(sender.equals(mAuth.getCurrentUser().getEmail())){
                                        recipient = sentTo;
                                    } else {
                                        recipient = sender;
                                    }
                                    List<Message> newThread = new ArrayList<Message>();
                                    newThread.add(message);
                                    mMessages.put(recipient, newThread);
                                }
                            }
                        }
                    }

                    mAdapter.updateMessageResults(mMessages);
                    if (mMessages.size() > 0){
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mEmptyInboxTv.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), getResources().getString(R.string.message_retrieval_error), Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
