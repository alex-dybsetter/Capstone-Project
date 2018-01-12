package net.alexblass.capstoneproject;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.User;
import net.alexblass.capstoneproject.utils.DashboardPagerAdapter;
import net.alexblass.capstoneproject.utils.UserDataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.USER_BANNER_IMG_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_BIRTHDAY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_DESCRIPTION_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_GENDER_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_NAME_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_PROFILE_IMG_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_RELATIONSHIP_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_SEXUALITY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_ZIPCODE_KEY;

public class DashboardActivity extends AppCompatActivity {

    @BindView(R.id.dashboard_viewpager) ViewPager mPager;
    @BindView(R.id.dashboard_tabs) TabLayout mTabs;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        if (!UserDataUtils.checkNetworkConnectivity(this)) {
            this.finish();
            return;
        }

        DashboardPagerAdapter adapter = new DashboardPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(loginActivity);
            return true;
        }
        if (id == R.id.action_edit_profile){
            Intent editorActivity = new Intent(this, EditActivity.class);
            editorActivity.putExtra(USER_KEY, mUser);
            startActivity(editorActivity);
            return true;
        }
        if (id == R.id.action_messages){
            Intent messagingActivityIntent = new Intent(this, MessagingActivity.class);
            startActivity(messagingActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserDataUtils.checkNetworkConnectivity(this)) {
            this.finish();
        }
        if (getIntent().hasExtra(USER_KEY)){
            mUser = getIntent().getParcelableExtra(USER_KEY);
        } else {
            final Query query = FirebaseDatabase.getInstance().getReference().child(
                    FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "(dot)"));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name, zipcode, description, sexuality, relationshipStatus, email, profilePicUri, bannerPicUri;
                        long gender;

                        name = (String) dataSnapshot.child(USER_NAME_KEY).getValue();

                        long birthdayInMillis = (long) dataSnapshot.child(USER_BIRTHDAY_KEY).getValue();

                        zipcode = (String) dataSnapshot.child(USER_ZIPCODE_KEY).getValue();
                        description = (String) dataSnapshot.child(USER_DESCRIPTION_KEY).getValue();

                        gender = (long) dataSnapshot.child(USER_GENDER_KEY).getValue();
                        sexuality = (String) dataSnapshot.child(USER_SEXUALITY_KEY).getValue();
                        relationshipStatus = (String) dataSnapshot.child(USER_RELATIONSHIP_KEY).getValue();
                        profilePicUri = (String) dataSnapshot.child(USER_PROFILE_IMG_KEY).getValue();
                        bannerPicUri = (String) dataSnapshot.child(USER_BANNER_IMG_KEY).getValue();

                        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        mUser = new User(email, name, birthdayInMillis, zipcode, gender,
                                sexuality, relationshipStatus, description, profilePicUri, bannerPicUri);
                        query.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(USER_KEY, mUser);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null){
            mUser = savedInstanceState.getParcelable(USER_KEY);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
