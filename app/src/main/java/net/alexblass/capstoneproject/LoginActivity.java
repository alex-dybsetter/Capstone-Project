package net.alexblass.capstoneproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // TODO: If the user is already logged in, set the layout to the home screen
        // TODO: Add animation to the fragments/back button
        setContentView(R.layout.activity_login);

        AccountPromptFragment promptFragment = new AccountPromptFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.login_fragment_container, promptFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                if (getSupportFragmentManager().getBackStackEntryCount() == 1){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
