package net.alexblass.capstoneproject;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexblass.capstoneproject.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.USER_KEY;


/**
 * A Fragment to display the current user's profile.
 */
public class MyProfileFragment extends Fragment {

    @BindView(R.id.user_profile_name) TextView mNameTv;
    @BindView(R.id.user_profile_stats) TextView mStats;
    @BindView(R.id.user_profile_description) TextView mDescription;
    @BindView(R.id.user_profile_relationship_status) TextView mRelationshipStatus;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, root);

        Intent intentThatStartedThis = getActivity().getIntent();
        if (intentThatStartedThis != null && intentThatStartedThis.hasExtra(USER_KEY)){

            User user = intentThatStartedThis.getParcelableExtra(USER_KEY);

            mNameTv.setText(user.getName());
            mDescription.setText(user.getDescription());
            mRelationshipStatus.setText(user.getRelationshipStatus());
        }

        return root;
    }

}
