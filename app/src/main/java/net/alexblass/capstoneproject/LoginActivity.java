package net.alexblass.capstoneproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.User;

import static net.alexblass.capstoneproject.data.Keys.USER_BIRTHDAY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_DESCRIPTION_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_GENDER_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_NAME_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_PROFILE_IMG_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_RELATIONSHIP_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_SEXUALITY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_ZIPCODE_KEY;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // TODO: Add animation to the fragments/back button
        setContentView(R.layout.activity_login);

        if (mAuth.getCurrentUser() == null) {
            AccountPromptFragment promptFragment = new AccountPromptFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_fragment_container, promptFragment)
                    .commit();
        } else {
            Query query = FirebaseDatabase.getInstance().getReference().child(
                    mAuth.getCurrentUser().getEmail().replace(".", "(dot)"));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name, zipcode, description, sexuality, relationshipStatus, email, profilePicUri;
                        long gender;

                        name = (String) dataSnapshot.child(USER_NAME_KEY).getValue();

                        long birthdayInMillis = (long) dataSnapshot.child(USER_BIRTHDAY_KEY).getValue();

                        zipcode = (String) dataSnapshot.child(USER_ZIPCODE_KEY).getValue();
                        description = (String) dataSnapshot.child(USER_DESCRIPTION_KEY).getValue();

                        gender = (long) dataSnapshot.child(USER_GENDER_KEY).getValue();
                        sexuality = (String) dataSnapshot.child(USER_SEXUALITY_KEY).getValue();
                        relationshipStatus = (String) dataSnapshot.child(USER_RELATIONSHIP_KEY).getValue();
                        profilePicUri = (String) dataSnapshot.child(USER_PROFILE_IMG_KEY).getValue();

                        email = mAuth.getCurrentUser().getEmail();

                        User user = new User(email, name, birthdayInMillis, zipcode, gender,
                                sexuality, relationshipStatus, description, profilePicUri);

                        Intent dashboardActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                        dashboardActivity.putExtra(USER_KEY, user);
                        startActivity(dashboardActivity);
                    }
                }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.verification_error), Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                if (getSupportFragmentManager().getBackStackEntryCount() == 1){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
