package net.alexblass.capstoneproject;


import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexblass.capstoneproject.data.UserDataUtils;
import net.alexblass.capstoneproject.models.User;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.USER_KEY;


/**
 * A Fragment to display the current user's profile.
 */
public class MyProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    @BindView(R.id.user_profile_name) TextView mNameTv;
    @BindView(R.id.user_profile_stats) TextView mStats;
    @BindView(R.id.user_profile_description) TextView mDescription;
    @BindView(R.id.user_profile_relationship_status) TextView mRelationshipStatus;

    private String mZipcode;
    private String mLocation;
    private int mAge;
    private String mGender;

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

            Calendar birthday = new GregorianCalendar();
            birthday.setTimeInMillis(user.getBirthday());
            mAge = UserDataUtils.calculateAge(birthday);

            int genderStringId = UserDataUtils.getGenderAbbreviationStringId(user.getGenderCode());
            mGender = getActivity().getString(genderStringId);

            mZipcode = user.getZipcode();

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(0, null, this);
        }

        return root;
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return new UserDataUtils.CityLoader(getActivity(), mZipcode);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        if (s != null) {
            mLocation = s;
        } else {
            mLocation = getActivity().getResources().getString(R.string.default_selection_abbreviation);
        }

        mStats.setText(getActivity().getResources().getString(R.string.stats_format,
                mAge, mGender, mLocation));
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }
}
