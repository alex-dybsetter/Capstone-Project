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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.models.Message;

import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static net.alexblass.capstoneproject.data.Keys.MSG_KEY;

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

        // TODO : message data is stored by email but user will be sending message to display name
        Query query = FirebaseDatabase.getInstance().getReference().child(recipient.replace(".", "(dot)"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Message message = new Message(sender,
                            recipient,
                            msg);

                    String msgLbl = generateMessageLbl(sender.toLowerCase(), recipient.toLowerCase()).replace(".", "(dot)");
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference(MSG_KEY).child(msgLbl)
                            .child(String.valueOf(new GregorianCalendar().getTimeInMillis()));
                    database.setValue(message);

                    // todo send notification to recepient

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
        });
    }

    private String generateMessageLbl(String email1, String email2){

        for (int i = 0; i < email1.length() && i < email2.length(); i++) {
            if (email1.charAt(i) < email2.charAt(i)) {
                return email1 + "-" + email2;
            }
            if (email2.charAt(i) < email1.charAt(i)) {
                return email2 + "-" + email1;
            }
            // If the characters are the same, loop again
        }
        // return the shorter email first
        return email1.length() < email2.length() ? email1 + "-" + email2 : email2 + "-" + email1;
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
