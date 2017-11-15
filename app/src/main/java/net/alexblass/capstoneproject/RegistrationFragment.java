package net.alexblass.capstoneproject;


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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {

    private FirebaseAuth mAuth;

    @BindView(R.id.registration_name_et) EditText mNameEt;
    @BindView(R.id.registration_birthday_et) EditText mBirthdayEt;
    @BindView(R.id.registration_email_et) EditText mUsernameEt;
    @BindView(R.id.registration_password_et) EditText mPasswordEt;
    @BindView(R.id.registration_parent) ConstraintLayout mParent;

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

        return rootView;
    }

    @OnClick(R.id.registration_submit_btn)
    public void register(View v){

        clearFocus();

        String name = mNameEt.getText().toString().trim();
        if (name.isEmpty()){
            showErrorDialog(getContext().getString(R.string.empty_name));
            mNameEt.requestFocus();

            return;
        }

        String birthday = mBirthdayEt.getText().toString().trim();
        if (birthday.isEmpty()){
            showErrorDialog(getContext().getString(R.string.invalid_date));
            mBirthdayEt.requestFocus();

            return;
        }

        String email = mUsernameEt.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showErrorDialog(getContext().getString(R.string.invalid_email));
            mUsernameEt.requestFocus();

            return;
        }

        String password = mPasswordEt.getText().toString().trim();
        if (password.isEmpty()){
            showErrorDialog(getContext().getString(R.string.empty_password));
            mPasswordEt.requestFocus();

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
            mUsernameEt.clearFocus();
            mPasswordEt.clearFocus();
        }
    }
}
