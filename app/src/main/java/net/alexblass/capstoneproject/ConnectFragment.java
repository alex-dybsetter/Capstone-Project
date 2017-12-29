package net.alexblass.capstoneproject;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.User;
import net.alexblass.capstoneproject.utils.UserAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.USER_BANNER_IMG_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_BIRTHDAY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_DESCRIPTION_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_EMAIL_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_GENDER_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_NAME_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_PROFILE_IMG_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_RELATIONSHIP_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_SEXUALITY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_ZIPCODE_KEY;

/**
 * A Fragment to display a list of other app users.
 */
public class ConnectFragment extends Fragment implements UserAdapter.ItemClickListener {

    private final String LIST_STATE_KEY = "list_state";
    private final String POSITION_KEY = "position";
    private final String LIST_KEY = "user_list";


    @BindView(R.id.connect_recyclerview) RecyclerView mRecyclerView;

    private LinearLayoutManager mLinearLayoutManager;
    private UserAdapter mAdapter;
    private ArrayList<User> mUsers;

    private Parcelable listState;
    private int mPosition = RecyclerView.NO_POSITION;

    public ConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_connect, container, false);
        ButterKnife.bind(this, root);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new UserAdapter(getActivity(), new User[0]);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            Query query = FirebaseDatabase.getInstance().getReference();
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mUsers = new ArrayList<User>();
                        for (DataSnapshot result : dataSnapshot.getChildren()) {

                            if (result.hasChild(USER_EMAIL_KEY)) {
                                String email = (String) result.child(USER_EMAIL_KEY).getValue();
                                String name = (String) result.child(USER_NAME_KEY).getValue();
                                long birthday = (long) result.child(USER_BIRTHDAY_KEY).getValue();
                                String zipcode = String.valueOf(result.child(USER_ZIPCODE_KEY).getValue());
                                long genderCode = (long) result.child(USER_GENDER_KEY).getValue();
                                String sexuality = (String) result.child(USER_SEXUALITY_KEY).getValue();
                                String relationshipStatus = (String) result.child(USER_RELATIONSHIP_KEY).getValue();
                                String description = (String) result.child(USER_DESCRIPTION_KEY).getValue();
                                String profilePicUri = (String) result.child(USER_PROFILE_IMG_KEY).getValue();
                                String bannerPicUri = (String) result.child(USER_BANNER_IMG_KEY).getValue();

                                User userResult = new User(email, name, birthday, zipcode, genderCode,
                                        sexuality, relationshipStatus, description, profilePicUri, bannerPicUri);

                                mUsers.add(userResult);
                            }
                        }
                        mAdapter.updateUserResults(mUsers.toArray(new User[mUsers.size()]));

                        if (mPosition == RecyclerView.NO_POSITION) {
                            mPosition = 0;}
                        mRecyclerView.smoothScrollToPosition(mPosition);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.toException().printStackTrace();
                }
            });
        } else {
            mUsers = savedInstanceState.getParcelableArrayList(LIST_KEY);
            mAdapter.updateUserResults(mUsers.toArray(new User[mUsers.size()]));
        }

        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        User user = mAdapter.getItem(position);
        Intent launchProfileViewer = new Intent(getContext(), ViewProfileActivity.class);
        launchProfileViewer.putExtra(USER_KEY, user);
        startActivity(launchProfileViewer);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LIST_KEY, mUsers);

        listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);

        mPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(POSITION_KEY, mPosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            mUsers = savedInstanceState.getParcelableArrayList(LIST_KEY);
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            mPosition = savedInstanceState.getInt(POSITION_KEY);
            mLinearLayoutManager.onRestoreInstanceState(listState);
        }
    }
}
