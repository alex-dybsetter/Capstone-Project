package net.alexblass.capstoneproject;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
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

import static net.alexblass.capstoneproject.data.Constants.CONNECT_FRAG_INDEX;
import static net.alexblass.capstoneproject.data.Keys.DASH_PG_NUM_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_BANNER_IMG_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_BIRTHDAY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_DESCRIPTION_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_EMAIL_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_FAVORITES_KEY;
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
    @BindView(R.id.connect_progressbar) ProgressBar mProgress;

    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private UserAdapter mAdapter;
    private ArrayList<User> mUsers;
    private ArrayList<String> mFavorites;

    private Query mQuery;
    private ValueEventListener mListener;

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

        if (getContext().getResources().getBoolean(R.bool.isTablet)){
            int numberOfCols = 2;
            mGridLayoutManager = new GridLayoutManager(getActivity(), numberOfCols);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
        } else {
            mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
        }

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new UserAdapter(getActivity(), new User[0]);

        final ArrayList<String> favorites = new ArrayList<>();

        final Query query = FirebaseDatabase.getInstance()
                .getReference(FirebaseAuth.getInstance().getCurrentUser().getEmail()
                        .replace(".", "(dot)"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot = dataSnapshot.child(USER_FAVORITES_KEY);

                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        favorites.add(child.getValue().toString());
                    }
                    query.removeEventListener(this);
                    mFavorites = favorites;
                    mAdapter.setFavorites(favorites);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            mQuery = FirebaseDatabase.getInstance().getReference();

            mListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mUsers = new ArrayList<User>();
                        for (DataSnapshot result : dataSnapshot.getChildren()) {

                            // Show only confirmed users and exclude the current user
                            if (result.hasChild(USER_ZIPCODE_KEY)) {
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

                                if (!email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                    User userResult = new User(email, name, birthday, zipcode, genderCode,
                                            sexuality, relationshipStatus, description, profilePicUri, bannerPicUri);

                                    mUsers.add(userResult);
                                }
                            }
                        }
                        mProgress.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
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
            };

            mQuery.addValueEventListener(mListener);
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

        outState.putInt(DASH_PG_NUM_KEY, CONNECT_FRAG_INDEX);

        outState.putParcelableArrayList(LIST_KEY, mUsers);
        outState.putStringArrayList(USER_FAVORITES_KEY, mFavorites);

        listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);

        if (mLinearLayoutManager != null) {
            mPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        } else {
            mPosition = mGridLayoutManager.findFirstVisibleItemPosition();
        }
        outState.putInt(POSITION_KEY, mPosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            mUsers = savedInstanceState.getParcelableArrayList(LIST_KEY);
            mFavorites = savedInstanceState.getStringArrayList(USER_FAVORITES_KEY);
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            mPosition = savedInstanceState.getInt(POSITION_KEY);

            if (mLinearLayoutManager != null) {
                mLinearLayoutManager.onRestoreInstanceState(listState);
            } else {
                mGridLayoutManager.onRestoreInstanceState(listState);
            }

            mProgress.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.setFavorites(mFavorites);
            mAdapter.updateUserResults(mUsers.toArray(new User[mUsers.size()]));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mQuery != null) {
            mQuery.removeEventListener(mListener);
        }
    }
}
