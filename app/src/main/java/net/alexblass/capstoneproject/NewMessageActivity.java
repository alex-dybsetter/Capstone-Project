package net.alexblass.capstoneproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import net.alexblass.capstoneproject.models.Message;

import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewMessageActivity extends AppCompatActivity {

    @BindView(R.id.new_msg_recipient) EditText mRecipientEt;
    @BindView(R.id.new_msg_data) EditText mMsgDataEt;
    @BindView(R.id.new_msg_recipient_helper) TextView mRecipientHelperTv;
    @BindView(R.id.new_msg_parent) LinearLayout mParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        ButterKnife.bind(this);

        mParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clearFocus();
                return false;
            }
        });
    }

    @OnClick(R.id.new_msg_send_btn)
    public void sendMessage(){
        String recipient = mRecipientEt.getText().toString();
        if (recipient.isEmpty()){
            mRecipientHelperTv.setVisibility(View.VISIBLE);
            mRecipientHelperTv.setText(getString(R.string.required_field));
            return;
        }

        // TODO: Test that recipient is valid

        String msg = mMsgDataEt.getText().toString();
        if (msg.isEmpty()){
            Toast.makeText(this, getString(R.string.empty_message), Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                recipient,
                msg,
                new GregorianCalendar().getTime().toString());

        Toast.makeText(this, getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
        finish();
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
        return super.onOptionsItemSelected(item);
    }
}
