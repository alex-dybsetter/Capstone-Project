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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  A Fragment to display the login screen.
 */
public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    @BindView(R.id.login_username_et) EditText mUsernameEt;
    @BindView(R.id.login_password_et) EditText mPasswordEt;
    @BindView(R.id.error_tv) TextView mErrorTv;
    @BindView(R.id.login_parent) ConstraintLayout mParent;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
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

    @OnClick(R.id.login_submit_btn)
    public void login(View v){

        clearFocus();

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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            // TODO: Launch mainscreen fragment
                        } else {
                            mErrorTv.setVisibility(View.VISIBLE);
                            mUsernameEt.requestFocus();
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
            mUsernameEt.clearFocus();
            mPasswordEt.clearFocus();
        }
    }
}
