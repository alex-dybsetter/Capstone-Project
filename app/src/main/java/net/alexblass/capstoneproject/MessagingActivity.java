package net.alexblass.capstoneproject;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import net.alexblass.capstoneproject.utils.UserDataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagingActivity extends AppCompatActivity {

    @BindView(R.id.messaging_no_connection_tv) TextView mConnectivityTv;

    private FragmentTransaction mFt;
    private InboxFragment mInboxFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        ButterKnife.bind(this);

        if (!UserDataUtils.checkNetworkConnectivity(this)) {
            mConnectivityTv.setVisibility(View.VISIBLE);
            return;
        } else {
            mConnectivityTv.setVisibility(View.GONE);
        }

        mInboxFragment = new InboxFragment();
        mFt = getSupportFragmentManager().beginTransaction();
        mFt.replace(R.id.messaging_fragment_container, mInboxFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFt = getSupportFragmentManager().beginTransaction();
        if (!UserDataUtils.checkNetworkConnectivity(this)) {
            mConnectivityTv.setVisibility(View.VISIBLE);
            mFt.hide(mInboxFragment);
        } else {
            mConnectivityTv.setVisibility(View.GONE);
            mFt.show(mInboxFragment);
        }
        mFt.commit();
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
