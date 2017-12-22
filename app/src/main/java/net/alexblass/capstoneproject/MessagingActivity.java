package net.alexblass.capstoneproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MessagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        InboxFragment inboxFragment = new InboxFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.messaging_fragment_container, inboxFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(MessagingActivity.this, LoginActivity.class);
            startActivity(loginActivity);
            return true;
        }
        if (id == R.id.action_send_new_message){
            Intent newMessageActivity = new Intent(this, NewMessageActivity.class);
            startActivity(newMessageActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
