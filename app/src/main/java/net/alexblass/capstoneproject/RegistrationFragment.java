package net.alexblass.capstoneproject;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A Fragment to create a new account.
 */
public class RegistrationFragment extends Fragment {

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
    @BindColor(R.color.validation_error) int mErrorColor;
    @BindColor(R.color.colorPrimary) int mHelperColor;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        ButterKnife.bind(this, rootView);

        mAuth = FirebaseAuth.getInstance();

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

        mBirthdayEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    mBirthdayHelperTv.setVisibility(View.VISIBLE);
                    mBirthdayHelperTv.setTextColor(mHelperColor);
                    mBirthdayHelperTv.setText(getContext().getString(R.string.birthday_helper));
                } else {
                    mBirthdayHelperTv.setVisibility(View.GONE);
                }
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

        String name = mNameEt.getText().toString().trim();
        if (name.isEmpty()){
            showErrorDialog(getContext().getString(R.string.empty_name));
            mNameEt.requestFocus();
            mNameHelperTv.setTextColor(mErrorColor);
            mNameHelperTv.setText(mRequired);
            return;
        }

        String birthday = mBirthdayEt.getText().toString().trim();
        if (birthday.isEmpty()){
            showErrorDialog(getContext().getString(R.string.invalid_date));
            mBirthdayEt.requestFocus();
            mBirthdayHelperTv.setTextColor(mErrorColor);
            mBirthdayHelperTv.setText(mRequired);
            return;
        }

        String email = mEmailEt.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showErrorDialog(getContext().getString(R.string.invalid_email));
            mEmailEt.requestFocus();
            mEmailHelperTv.setTextColor(mErrorColor);
            mEmailHelperTv.setText(mRequired);
            return;
        }

        String password = mPasswordEt.getText().toString().trim();
        if (password.isEmpty()){
            showErrorDialog(getContext().getString(R.string.empty_password));
            mPasswordEt.requestFocus();
            mPasswordHelperTv.setTextColor(mErrorColor);
            mPasswordHelperTv.setText(mRequired);
            return;
        }

        // TODO: Only create account if email is valid
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
                                        Toast.makeText(getActivity(), getContext().getString(R.string.verification_email),
                                                Toast.LENGTH_SHORT).show();
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showErrorDialog(String body){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(getContext().getString(R.string.invalid_entry))
                .setMessage(body)
                .setPositiveButton(getContext().getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
}
