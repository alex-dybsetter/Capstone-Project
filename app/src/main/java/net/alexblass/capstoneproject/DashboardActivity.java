package net.alexblass.capstoneproject;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import net.alexblass.capstoneproject.models.User;
import net.alexblass.capstoneproject.utils.DashboardPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.capstoneproject.data.Keys.DASH_PG_NUM_KEY;
import static net.alexblass.capstoneproject.data.Keys.USER_KEY;

public class DashboardActivity extends AppCompatActivity {

    @BindView(R.id.dashboard_viewpager) ViewPager mPager;
    @BindView(R.id.dashboard_tabs) TabLayout mTabs;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        DashboardPagerAdapter adapter = new DashboardPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(adapter);

        mTabs.setupWithViewPager(mPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(loginActivity);
            return true;
        }
        if (id == R.id.action_edit_profile){
            User user = null;
            if (getIntent().hasExtra(USER_KEY)){
                user = getIntent().getParcelableExtra(USER_KEY);
            }
            Intent editorActivity = new Intent(this, EditActivity.class);
            editorActivity.putExtra(USER_KEY, user);
            startActivity(editorActivity);
            return true;
        }
        if (id == R.id.action_messages){
            Intent messagingActivityIntent = new Intent(this, MessagingActivity.class);
            startActivity(messagingActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(DASH_PG_NUM_KEY, mTabs.getSelectedTabPosition());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mPager.setCurrentItem(savedInstanceState.getInt(DASH_PG_NUM_KEY));

        }
    }
}
