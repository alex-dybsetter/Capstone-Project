package net.alexblass.capstoneproject;

import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.User;
import net.alexblass.capstoneproject.utils.UserDataUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static net.alexblass.capstoneproject.data.Keys.USER_BIRTHDAY_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_NAME_KEY;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private final int ACTION_SIGN_OUT = 0;
    private final int ACTION_RETURN_TO_DASH = 1;

    @BindView(R.id.edit_name_et) EditText mNameEt;
    @BindView(R.id.edit_zipcode_et) EditText mZipcodeEt;
    @BindView(R.id.edit_description_et) EditText mDescriptionEt;
    @BindView(R.id.edit_gender_spinner) Spinner mGenderSpinnner;
    @BindView(R.id.edit_sexuality_spinner) Spinner mSexualitySpinner;
    @BindView(R.id.edit_relationship_spinner) Spinner mRelationshipStatusSpinner;
    @BindView(R.id.edit_parent) ConstraintLayout mParent;

    @BindView(R.id.edit_name_helper) TextView mNameHelperTv;
    @BindView(R.id.edit_zipcode_helper) TextView mZipcodeHelperTv;
    @BindView(R.id.edit_gender_error) TextView mGenderErrorTv;
    @BindView(R.id.edit_sexuality_error) TextView mSexualityErrorTv;
    @BindView(R.id.edit_relationship_error) TextView mRelationshipErrorTv;

    @BindString(R.string.required_field) String mRequired;
    @BindString(R.string.invalid_entry) String mEntryErrorTitle;
    @BindColor(R.color.validation_error) int mErrorColor;
    @BindColor(R.color.colorPrimary) int mHelperColor;

    private FirebaseAuth mAuth;

    private User mUser;
    private long mBirthday;
    private String mName;
    private String mZipcode;
    private boolean mValidZipcode;
    private boolean mFirstEdit;

    // TODO: Handle image upload

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mValidZipcode = false;

        List<String> gendersList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.gender_choices)));
        mGenderSpinnner.setAdapter(getArrayAdapter(gendersList));

        List<String> sexualitiesList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sexuality_choices)));
        mSexualitySpinner.setAdapter(getArrayAdapter(sexualitiesList));

        List<String> relationshipsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.relationship_choices)));
        mRelationshipStatusSpinner.setAdapter(getArrayAdapter(relationshipsList));

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(USER_KEY)){
            mFirstEdit = false;
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mFirstEdit);

            mUser = intentThatStartedThisActivity.getParcelableExtra(USER_KEY);
            mName = mUser.getName();
            mBirthday = mUser.getBirthday();
            mZipcode = mUser.getZipcode();

            mNameEt.setText(mName);
            mZipcodeEt.setText(mZipcode);
            mDescriptionEt.setText(mUser.getDescription());
            mGenderSpinnner.setSelection((int)mUser.getGenderCode());
            mSexualitySpinner.setSelection(sexualitiesList.indexOf(mUser.getSexuality()));
            mRelationshipStatusSpinner.setSelection(relationshipsList.indexOf(mUser.getRelationshipStatus()));
        } else {
            mFirstEdit = true;
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mFirstEdit);

            mName = null;
            mBirthday = 0;

            Query query = FirebaseDatabase.getInstance().getReference().child(
                    mAuth.getCurrentUser().getEmail().replace(".", "(dot)"));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        mName = (String) dataSnapshot.child(USER_NAME_KEY).getValue();
                        mNameEt.setText(mName);

                        mBirthday = (long) dataSnapshot.child(USER_BIRTHDAY_KEY).getValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.verification_error), Toast.LENGTH_SHORT).show();
                }
            });
        }

        setFocusListeners();
    }

    @OnClick(R.id.edit_save_btn)
    public void saveData(){
        mName = mNameEt.getText().toString().trim();
        if (mName.isEmpty()){
            showDialog(mEntryErrorTitle, getString(R.string.empty_name));
            mNameEt.requestFocus();
            mNameHelperTv.setTextColor(mErrorColor);
            mNameHelperTv.setText(mRequired);
            return;
        }

        mZipcode = mZipcodeEt.getText().toString().trim();

        if (mZipcode.isEmpty() || mZipcode.length() != 5){
            showDialog(mEntryErrorTitle, getString(R.string.invalid_zipcode));
            mZipcodeEt.requestFocus();
            mZipcodeHelperTv.setTextColor(mErrorColor);
            mZipcodeHelperTv.setText(mRequired);
            return;
        } else {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(0, null, this);
        }

        String description = mDescriptionEt.getText().toString().trim();

        long gender = mGenderSpinnner.getSelectedItemId();
        if (mGenderSpinnner.getSelectedItemId() == 0){
            showDialog(mEntryErrorTitle, getString(R.string.invalid_gender));
            mGenderErrorTv.setVisibility(View.VISIBLE);
            return;
        }

        String sexuality = mSexualitySpinner.getSelectedItem().toString();
        if (mSexualitySpinner.getSelectedItemId() == 0){
            showDialog(mEntryErrorTitle, getString(R.string.invalid_sexuality));
            mSexualityErrorTv.setVisibility(View.VISIBLE);
            return;
        }

        String relationshipStatus = mRelationshipStatusSpinner.getSelectedItem().toString();
        if (mRelationshipStatusSpinner.getSelectedItemId() == 0){
            showDialog(mEntryErrorTitle, getString(R.string.invalid_relationship));
            mRelationshipErrorTv.setVisibility(View.VISIBLE);
            return;
        }

        String email = mAuth.getCurrentUser().getEmail();

        mUser = new User(email, mName, mBirthday, mZipcode, gender, sexuality, relationshipStatus, description);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference(email.replace(".", "(dot)"));
        database.setValue(mUser);

        Toast.makeText(this, getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();

        Intent dashboardActivity = new Intent(getApplicationContext(), DashboardActivity.class);
        dashboardActivity.putExtra(USER_KEY, mUser);
        startActivity(dashboardActivity);
    }

    private void showDialog(final String title, String body){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title)
                .setMessage(body)
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!title.equals(mEntryErrorTitle)){
                            getFragmentManager().popBackStack();
                        }
                    }
                });
        dialog.create().show();
    }

    private void clearFocus(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mNameEt.clearFocus();
            mZipcodeEt.clearFocus();
            mDescriptionEt.clearFocus();

            mNameHelperTv.setVisibility(View.GONE);
            mZipcodeHelperTv.setVisibility(View.GONE);
        }
    }

    private ArrayAdapter<String> getArrayAdapter(List<String> list){

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.item_edit_profile_hint, list){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View v = null;

                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {

                    v = super.getDropDownView(position, null, parent);
                }

                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.item_edit_profile_spinner);
        return spinnerArrayAdapter;
    }

    private void setFocusListeners(){
        mParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clearFocus();
                return false;
            }
        });

        mNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    mNameHelperTv.setVisibility(View.VISIBLE);
                    mNameHelperTv.setTextColor(mHelperColor);
                    mNameHelperTv.setText(getString(R.string.name_helper));
                } else {
                    mNameHelperTv.setVisibility(View.GONE);
                }
            }
        });

        mZipcodeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    mZipcodeHelperTv.setVisibility(View.VISIBLE);
                    mZipcodeHelperTv.setTextColor(mHelperColor);
                    mZipcodeHelperTv.setText(getString(R.string.zipcode_helper));
                } else {
                    mZipcodeHelperTv.setVisibility(View.GONE);
                }
            }
        });

        mGenderSpinnner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGenderErrorTv.setVisibility(View.GONE);
                clearFocus();
                return false;
            }
        });

        mSexualitySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mSexualityErrorTv.setVisibility(View.GONE);
                clearFocus();
                return false;
            }
        });

        mRelationshipStatusSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mRelationshipErrorTv.setVisibility(View.GONE);
                clearFocus();
                return false;
            }
        });
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return new UserDataUtils.CityLoader(this, mZipcode);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        mValidZipcode = (s!= null);
        if (!mValidZipcode){
            showDialog(mEntryErrorTitle, getString(R.string.invalid_zipcode));
            mZipcodeEt.requestFocus();
            mZipcodeHelperTv.setTextColor(mErrorColor);
            mZipcodeHelperTv.setText(mRequired);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    private void handleAction(int actionId){
        switch (actionId) {
            case ACTION_RETURN_TO_DASH:
                Intent dashboardActivity = new Intent(EditActivity.this, DashboardActivity.class);
                dashboardActivity.putExtra(USER_KEY, mUser);
                startActivity(dashboardActivity);
                break;
            case ACTION_SIGN_OUT:
                FirebaseAuth.getInstance().signOut();
                Intent loginActivity = new Intent(EditActivity.this, LoginActivity.class);
                startActivity(loginActivity);
                break;
            default:
                break;
        }
    }

    private void unsavedEditsPrompt(final int actionId){
        if (!mName.equals(mNameEt.getText().toString()) ||
                !mZipcode.equals(mZipcodeEt.getText().toString()) ||
                mGenderSpinnner.getSelectedItemId() != mUser.getGenderCode() ||
                !mSexualitySpinner.getSelectedItem().equals(mUser.getSexuality()) ||
                !mRelationshipStatusSpinner.getSelectedItem().equals(mUser.getRelationshipStatus())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.unsaved_edits_title))
                    .setMessage(getString(R.string.unsaved_edits_prompt))
                    .setPositiveButton(getString(R.string.positive_btn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            handleAction(actionId);
                        }
                    })
                    .setNegativeButton(getString(R.string.negative_btn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.create().show();
        } else {
            handleAction(actionId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            unsavedEditsPrompt(ACTION_RETURN_TO_DASH);
            return true;
        }
        if (id == R.id.action_sign_out){
            unsavedEditsPrompt(ACTION_SIGN_OUT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
