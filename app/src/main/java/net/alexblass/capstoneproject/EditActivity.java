package net.alexblass.capstoneproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.alexblass.capstoneproject.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditActivity extends AppCompatActivity {

    @BindView(R.id.edit_name_et) EditText mNameEt;
    @BindView(R.id.edit_zipcode_et) EditText mZipcodeEt;
    @BindView(R.id.edit_description_et) EditText mDescriptionEt;
    @BindView(R.id.edit_gender_spinner) Spinner mGenderSpinnner;
    @BindView(R.id.edit_sexuality_spinner) Spinner mSexualitySpinner;
    @BindView(R.id.edit_relationship_spinner) Spinner mRelationshipStatusSpinner;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.edit_save_btn)
    public void saveData(){
        String name = mNameEt.getText().toString().trim();
        String zipcode = mZipcodeEt.getText().toString().trim();
        String description = mDescriptionEt.getText().toString().trim();

        String gender = mGenderSpinnner.getSelectedItem().toString();
        String sexuality = mSexualitySpinner.getSelectedItem().toString();
        String relationshipStatus = mRelationshipStatusSpinner.getSelectedItem().toString();

        User user = new User(name, zipcode, gender, sexuality, relationshipStatus, description);

        // TODO: Set a unique key to identify each user, since the key overwrites data
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");

        myRef.setValue(user);

        Intent dashboardActivity = new Intent(this, DashboardActivity.class);
        startActivity(dashboardActivity);
    }
}
