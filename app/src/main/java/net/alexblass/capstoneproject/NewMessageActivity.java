package net.alexblass.capstoneproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.Message;
import net.alexblass.capstoneproject.utils.UserDataUtils;

import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static net.alexblass.capstoneproject.data.Keys.MSG_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_EMAIL_KEY;

public class NewMessageActivity extends AppCompatActivity {

    @BindView(R.id.new_msg_recipient) EditText mRecipientEt;
    @BindView(R.id.new_msg_data) EditText mMsgDataEt;
    @BindView(R.id.new_msg_recipient_helper) TextView mRecipientHelperTv;
    @BindView(R.id.new_msg_send_btn) Button mSendBtn;
    @BindView(R.id.new_msg_parent) LinearLayout mParent;

    private Query mQuery;
    private ValueEventListener mListener;

    private AlertDialog mOfflineDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        ButterKnife.bind(this);

        AlertDialog.Builder offlineDialog = new AlertDialog.Builder(this);
        offlineDialog.setTitle(getString(R.string.offline_edits_dialog_title))
                .setMessage(getString(R.string.offline_messages_prompt))
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        mOfflineDialog = offlineDialog.create();

        if (!UserDataUtils.checkNetworkConnectivity(this)) {
            mOfflineDialog.show();
            mSendBtn.setEnabled(false);
            mSendBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_inactive_bg));
        } else {
            mSendBtn.setEnabled(true);
            mSendBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        }

        mParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clearFocus();
                return false;
            }
        });

        Intent intentThatStartedThis = getIntent();
        if (intentThatStartedThis.hasExtra(USER_EMAIL_KEY)){
            mRecipientEt.setText(intentThatStartedThis.getStringExtra(USER_EMAIL_KEY));
        }
    }

    @OnClick(R.id.new_msg_send_btn)
    public void sendMessage(){
        if (!UserDataUtils.checkNetworkConnectivity(this)) {
            if (!mOfflineDialog.isShowing()) {
                mOfflineDialog.show();
            }
            mSendBtn.setEnabled(false);
            mSendBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_inactive_bg));
            return;
        } else {
            mSendBtn.setEnabled(true);
            mSendBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        }

        final String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String recipient = mRecipientEt.getText().toString();
        if (recipient.isEmpty()){
            mRecipientHelperTv.setVisibility(View.VISIBLE);
            mRecipientHelperTv.setText(getString(R.string.required_field));
            return;
        }

        final String msg = mMsgDataEt.getText().toString();
        if (msg.isEmpty()){
            Toast.makeText(this, getString(R.string.empty_message), Toast.LENGTH_SHORT).show();
            return;
        }

        mQuery = FirebaseDatabase.getInstance().getReference().child(recipient.replace(".", "(dot)"));

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Message message = new Message(sender, recipient, msg, false);

                    String msgLbl = UserDataUtils.generateMessageLbl(sender, recipient);
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference(MSG_KEY).child(msgLbl)
                            .child(String.valueOf(new GregorianCalendar().getTimeInMillis()));
                    database.setValue(message);

                    Toast.makeText(getApplicationContext(), getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_username), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_error), Toast.LENGTH_SHORT).show();
            }
        };

        mQuery.addValueEventListener(mListener);
    }

    private void clearFocus(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mRecipientEt.clearFocus();
            mMsgDataEt.clearFocus();
            mRecipientHelperTv.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(NewMessageActivity.this, LoginActivity.class);
            startActivity(loginActivity);
            return true;
        }
        if (id == android.R.id.home){
            if (!UserDataUtils.checkNetworkConnectivity(this)) {
                UserDataUtils.resetApp(this);
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mQuery != null) {
            mQuery.removeEventListener(mListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOfflineDialog != null){
            mOfflineDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserDataUtils.checkNetworkConnectivity(this)) {
            if (!mOfflineDialog.isShowing()) {
                mOfflineDialog.show();
            }
            mSendBtn.setEnabled(false);
            mSendBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_inactive_bg));
        } else {
            mSendBtn.setEnabled(true);
            mSendBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        }
    }
}
