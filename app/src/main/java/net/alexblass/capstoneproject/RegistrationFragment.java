package net.alexblass.capstoneproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.alexblass.capstoneproject.data.UserDataUtils;
import net.alexblass.capstoneproject.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static net.alexblass.capstoneproject.data.Constants.LEGAL_ADULT_AGE;
import static net.alexblass.capstoneproject.data.Constants.MAX_AGE;
import static net.alexblass.capstoneproject.data.Constants.MIN_PASSWORD_LENGTH;

/**
 * A Fragment to create a new account.
 */
public class RegistrationFragment extends Fragment {

    private static final String TAG = "RegistrationFragment";

    private FirebaseAuth mAuth;

    @BindView(R.id.registration_name_et) EditText mNameEt;
    @BindView(R.id.registration_birthday_et) EditText mBirthdayEt;
    @BindView(R.id.registration_email_et) EditText mEmailEt;
    @BindView(R.id.registration_password_et) EditText mPasswordEt;
    @BindView(R.id.registration_parent) ConstraintLayout mParent;

    @BindView(R.id.registration_name_helper) TextView mNameHelperTv;
    @BindView(R.id.registration_birthday_helper) TextView mBirthdayHelperTv;
    @BindView(R.id.registration_email_helper) TextView mEmailHelperTv;
    @BindView(R.id.registration_password_helper) TextView mPasswordHelperTv;

    @BindString(R.string.required_field) String mRequired;
    @BindString(R.string.invalid_entry) String mEntryErrorTitle;
    @BindString(R.string.error_title) String mRegistrationErrorTitle;
    @BindColor(R.color.validation_error) int mErrorColor;
    @BindColor(R.color.colorPrimary) int mHelperColor;

    private Calendar mBdayCalendar;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        ButterKnife.bind(this, rootView);

        mAuth = FirebaseAuth.getInstance();
        mBdayCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mBdayCalendar.set(Calendar.YEAR, year);
                mBdayCalendar.set(Calendar.MONTH, monthOfYear);
                mBdayCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDate();
            }

        };


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
                    mNameHelperTv.setText(getContext().getString(R.string.name_helper));
                } else {
                    mNameHelperTv.setVisibility(View.GONE);
                }
            }
        });

        mBirthdayEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();

                DatePickerDialog dialog = new DatePickerDialog(getContext(), date, mBdayCalendar
                        .get(Calendar.YEAR), mBdayCalendar.get(Calendar.MONTH),
                        mBdayCalendar.get(Calendar.DAY_OF_MONTH));

                Calendar timeFrame = Calendar.getInstance();
                timeFrame.add(Calendar.YEAR, -1 * MAX_AGE);
                dialog.getDatePicker().setMinDate(timeFrame.getTimeInMillis());
                timeFrame.add(Calendar.YEAR, MAX_AGE);

                dialog.getDatePicker().setMaxDate(timeFrame.getTimeInMillis());
                dialog.show();
                
                mBirthdayHelperTv.setVisibility(View.GONE);
            }
        });

        mEmailEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    mEmailHelperTv.setVisibility(View.VISIBLE);
                    mEmailHelperTv.setTextColor(mHelperColor);
                    mEmailHelperTv.setText(getContext().getString(R.string.email_helper));
                } else {
                    mEmailHelperTv.setVisibility(View.GONE);
                }
            }
        });

        mPasswordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    mPasswordHelperTv.setVisibility(View.VISIBLE);
                    mPasswordHelperTv.setTextColor(mHelperColor);
                    mPasswordHelperTv.setText(getContext().getString(R.string.password_helper));
                } else {
                    mPasswordHelperTv.setVisibility(View.GONE);
                }
            }
        });

        return rootView;
    }

    @OnClick(R.id.registration_submit_btn)
    public void register(View v){

        clearFocus();

        final String name = mNameEt.getText().toString().trim();
        if (name.isEmpty()){
            showDialog(mEntryErrorTitle, getContext().getString(R.string.empty_name));
            mNameEt.requestFocus();
            mNameHelperTv.setTextColor(mErrorColor);
            mNameHelperTv.setText(mRequired);
            return;
        }

        String birthday = mBirthdayEt.getText().toString().trim();
        if (birthday.isEmpty()){
            showDialog(mEntryErrorTitle, getContext().getString(R.string.invalid_date));
            clearFocus();
            mBirthdayHelperTv.setVisibility(View.VISIBLE);
            return;
        }

        if (!isAdult()){
            showDialog(mEntryErrorTitle, getContext().getString(R.string.invalid_age));
            clearFocus();
            return;
        }

        final String email = mEmailEt.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showDialog(mEntryErrorTitle, getContext().getString(R.string.invalid_email));
            mEmailEt.requestFocus();
            mEmailHelperTv.setTextColor(mErrorColor);
            mEmailHelperTv.setText(mRequired);
            return;
        }

        String password = mPasswordEt.getText().toString().trim();
        if (password.isEmpty()){
            showDialog(mEntryErrorTitle, getContext().getString(R.string.empty_password));
            mPasswordEt.requestFocus();
            mPasswordHelperTv.setTextColor(mErrorColor);
            mPasswordHelperTv.setText(mRequired);
            return;
        }

        if (password.length() < MIN_PASSWORD_LENGTH || !containsDigitsAndLetters(password)){
            showDialog(mEntryErrorTitle, getContext().getString(R.string.invalid_password));
            mPasswordEt.requestFocus();
            mPasswordHelperTv.setTextColor(mErrorColor);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showDialog(getContext().getString(R.string.registration_complete),
                                                getContext().getString(R.string.verification_email, email));

                                        User user = new User(email, name, mBdayCalendar.getTimeInMillis());
                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference(email.replace(".", "(dot)"));

                                        database.setValue(user);
                                    }
                                }
                            });
                        } else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                showDialog(mRegistrationErrorTitle, getContext().getString(R.string.invalid_password));
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                showDialog(mRegistrationErrorTitle, getContext().getString(R.string.email_error));
                            } catch(FirebaseAuthUserCollisionException e) {
                                showDialog(mRegistrationErrorTitle, getContext().getString(R.string.user_exists_error));
                            } catch(Exception e) {
                                showDialog(mRegistrationErrorTitle, getContext().getString(R.string.verification_error));
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }

    private void showDialog(final String title, String body){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(title)
                .setMessage(body)
                .setPositiveButton(getContext().getString(R.string.okay), new DialogInterface.OnClickListener() {
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
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mNameEt.clearFocus();
            mBirthdayEt.clearFocus();
            mEmailEt.clearFocus();
            mPasswordEt.clearFocus();

            mNameHelperTv.setVisibility(View.GONE);
            mBirthdayHelperTv.setVisibility(View.GONE);
            mEmailHelperTv.setVisibility(View.GONE);
            mPasswordHelperTv.setVisibility(View.GONE);
        }
    }

    private void setDate() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mBirthdayEt.setText(sdf.format(mBdayCalendar.getTime()));
    }

    private boolean isAdult(){
        return UserDataUtils.calculateAge(mBdayCalendar) >= LEGAL_ADULT_AGE;
    }

    private boolean containsDigitsAndLetters(String password){
        boolean containsDigits = false;
        boolean containsLetters = false;
        for (char character : password.toCharArray()){
            if (Character.isLetter(character)){
                containsLetters = true;
            }
            if (Character.isDigit(character)){
                containsDigits = true;
            }
            if (containsLetters && containsDigits){
                return true;
            }
        }
        return false;
    }
}
