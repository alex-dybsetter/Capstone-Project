package net.alexblass.capstoneproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.User;

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

/**
 *  A Fragment to display the login screen.
 */
public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @BindView(R.id.login_email_et) EditText mEmailEt;
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
        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        String email = mEmailEt.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mErrorTv.setText(getContext().getString(R.string.invalid_email));
            mErrorTv.setVisibility(View.VISIBLE);
            mEmailEt.requestFocus();

            return;
        }

        String password = mPasswordEt.getText().toString().trim();
        if (password.isEmpty()){
            mErrorTv.setText(getContext().getString(R.string.empty_password));
            mErrorTv.setVisibility(View.VISIBLE);
            mPasswordEt.requestFocus();

            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       logUserIn(task);
                    }
                });
    }

    private void logUserIn(Task<AuthResult> task){

        if (task.isSuccessful()) {

            if (!mAuth.getCurrentUser().isEmailVerified()){
                unverifiedEmailDialog();
                return;
            }

            String userEmail = mAuth.getCurrentUser().getEmail().replace(".", "(dot)");

            Query query = mDatabase.child(userEmail);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.child(USER_ZIPCODE_KEY).exists()){
                        String name, zipcode, description, sexuality, relationshipStatus, email;
                        long gender;

                        name = (String) dataSnapshot.child(USER_NAME_KEY).getValue();

                        long birthdayInMillis = (long) dataSnapshot.child(USER_BIRTHDAY_KEY).getValue();

                        zipcode = (String) dataSnapshot.child(USER_ZIPCODE_KEY).getValue();
                        description = (String) dataSnapshot.child(USER_DESCRIPTION_KEY).getValue();

                        gender = (long) dataSnapshot.child(USER_GENDER_KEY).getValue();
                        sexuality = (String) dataSnapshot.child(USER_SEXUALITY_KEY).getValue();
                        relationshipStatus = (String) dataSnapshot.child(USER_RELATIONSHIP_KEY).getValue();

                        email = mAuth.getCurrentUser().getEmail();

                        User user = new User(email, name, birthdayInMillis,
                                zipcode, gender, sexuality, relationshipStatus, description);

                        Intent dashboardActivity = new Intent(getActivity(), DashboardActivity.class);
                        dashboardActivity.putExtra(USER_KEY, user);
                        startActivity(dashboardActivity);
                    } else {
                        launchEditor();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), getContext().getString(R.string.verification_error), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            mErrorTv.setText(getContext().getString(R.string.invalid_credentials));
            mErrorTv.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.password_recovery)
    public void resetPassword(){
        mErrorTv.setVisibility(View.GONE);
        String email = mEmailEt.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mErrorTv.setText(getContext().getString(R.string.enter_email_password_reset_error));
            mErrorTv.setVisibility(View.VISIBLE);
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), getContext().getString(R.string.reset_email_sent),
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            mErrorTv.setText(getContext().getString(R.string.no_account_found));
                            mErrorTv.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), getContext().getString(R.string.reset_failure),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void unverifiedEmailDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(getContext().getString(R.string.error_title))
                .setMessage(getContext().getString(R.string.unverified_email))
                .setPositiveButton(getContext().getString(R.string.go_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                        getActivity().startActivity(intent);
                    }
                })
                .setNegativeButton(getContext().getString(R.string.resend_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(),
                                            getContext().getString(R.string.email_sent),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
        dialog.create().show();
    }

    private void clearFocus(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mEmailEt.clearFocus();
            mPasswordEt.clearFocus();
        }
    }

    private void launchEditor(){
        Intent editorActivity = new Intent(getActivity(), EditActivity.class);
        startActivity(editorActivity);
    }
}
