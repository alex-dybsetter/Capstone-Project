package net.alexblass.capstoneproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.User;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static net.alexblass.capstoneproject.data.Keys.USER_BIRTHDAY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_DESCRIPTION_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_GENDER_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_NAME_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_RELATIONSHIP_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_SEXUALITY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_ZIPCODE_KEY;

public class EditActivity extends AppCompatActivity {

    @BindView(R.id.edit_name_et) EditText mNameEt;
    @BindView(R.id.edit_zipcode_et) EditText mZipcodeEt;
    @BindView(R.id.edit_description_et) EditText mDescriptionEt;
    @BindView(R.id.edit_gender_spinner) Spinner mGenderSpinnner;
    @BindView(R.id.edit_sexuality_spinner) Spinner mSexualitySpinner;
    @BindView(R.id.edit_relationship_spinner) Spinner mRelationshipStatusSpinner;

    private FirebaseAuth mAuth;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.edit_save_btn)
    public void saveData(){
        final String name = mNameEt.getText().toString().trim();
        final String zipcode = mZipcodeEt.getText().toString().trim();
        final String description = mDescriptionEt.getText().toString().trim();

        final long gender = mGenderSpinnner.getSelectedItemId();
        final String sexuality = mSexualitySpinner.getSelectedItem().toString();
        final String relationshipStatus = mRelationshipStatusSpinner.getSelectedItem().toString();

        final String email = mAuth.getCurrentUser().getEmail();

        Query query = FirebaseDatabase.getInstance().getReference().child(
                mAuth.getCurrentUser().getEmail().replace(".", "(dot)"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long birthday = 0;
                if (dataSnapshot.exists()) {

                    birthday = (long) dataSnapshot.child(USER_BIRTHDAY_KEY).getValue();
                }

                mUser = new User(email, name, birthday, zipcode, gender, sexuality, relationshipStatus, description);

                DatabaseReference database = FirebaseDatabase.getInstance().getReference(email.replace(".", "(dot)"));
                database.setValue(mUser);

                Intent dashboardActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                dashboardActivity.putExtra(USER_KEY, mUser);
                startActivity(dashboardActivity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.verification_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
