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
import net.alexblass.capstoneproject.utils.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.MSG_DATA;
import static net.alexblass.capstoneproject.data.Keys.MSG_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENDER_EMAIL_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENT_TO_EMAIL_KEY;

/**
 * A Fragment to display the user's message inbox.
 */
public class InboxFragment extends Fragment {

    @BindView(R.id.inbox_messages_rv) RecyclerView mRecyclerView;
    @BindView(R.id.inbox_empty_tv) TextView mEmptyInboxTv;

    private FirebaseAuth mAuth;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Message> mMessages;
    private MessageAdapter mAdapter;

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
        mMessages = new ArrayList<>();



        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MessageAdapter(getActivity(), new Message[0], email);
        //mAdapter.setClickListener(this); todo: launch message on click 
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
                                Message message = new Message(messageData.child(MSG_SENDER_EMAIL_KEY).getValue().toString(),
                                        messageData.child(MSG_SENT_TO_EMAIL_KEY).getValue().toString(),
                                        messageData.child(MSG_DATA).toString());
                                mMessages.add(message);
                            }
                        }
                    }

                    mAdapter.updateMessageResults(mMessages.toArray(new Message[mMessages.size()]));
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

}