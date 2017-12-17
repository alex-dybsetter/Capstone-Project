package net.alexblass.capstoneproject;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import net.alexblass.capstoneproject.models.User;
import net.alexblass.capstoneproject.utils.UserDataUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.USER_KEY;

/**
 * A Fragment to display a user's profile.
 */
public class ViewProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    @BindView(R.id.view_profile_image) ImageView mProfilePic;
    @BindView(R.id.view_profile_name) TextView mNameTv;
    @BindView(R.id.view_profile_stats) TextView mStats;
    @BindView(R.id.view_profile_description_tv) TextView mDescriptionTv;
    @BindView(R.id.view_profile_sexuality) TextView mSexuality;
    @BindView(R.id.view_profile_relationship_status) TextView mRelationshipStatus;

    private User mUser;
    private String mZipcode;
    private String mLocation;
    private int mAge;
    private String mGender;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_profile, container, false);
        ButterKnife.bind(this, root);

        Intent intentThatStartedThisActivity = getActivity().getIntent();
        if (intentThatStartedThisActivity.hasExtra(USER_KEY)) {
            mUser = intentThatStartedThisActivity.getParcelableExtra(USER_KEY);

            mNameTv.setText(mUser.getName());
            mDescriptionTv.setText(mUser.getDescription());
            mSexuality.setText(mUser.getSexuality());
            mRelationshipStatus.setText(mUser.getRelationshipStatus());

            Calendar birthday = new GregorianCalendar();
            birthday.setTimeInMillis(mUser.getBirthday());
            mAge = UserDataUtils.calculateAge(birthday);

            int genderStringId = UserDataUtils.getGenderAbbreviationStringId(mUser.getGenderCode());
            mGender = getActivity().getString(genderStringId);

            mZipcode = mUser.getZipcode();

            if (!mUser.getProfilePicUri().isEmpty()){
                StorageReference profilePicFile = FirebaseStorage.getInstance().getReference()
                        .child(Uri.parse(mUser.getProfilePicUri()).getPath());
                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    profilePicFile.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Picasso.with(getContext())
                                            .load(localFile)
                                            .placeholder(R.drawable.ic_person_white_48dp)
                                            .centerCrop()
                                            .fit()
                                            .into(mProfilePic);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
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
            mLocation = mUser.getZipcode();
        }

        mStats.setText(getActivity().getResources().getString(R.string.stats_format,
                mAge, mGender, mLocation));
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }
}
