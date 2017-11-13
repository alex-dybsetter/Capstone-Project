package net.alexblass.capstoneproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A Fragment to display the buttons where a user can either sign in or create an account.
 */
public class AccountPromptFragment extends Fragment {


    public AccountPromptFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_prompt, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.login_btn)
    public void showLoginScreen(View v){
        LoginFragment loginFragment = new LoginFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.login_fragment_container, loginFragment)
                .addToBackStack(null)
                .commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.sign_up_btn)
    public void showSignUpScreen(View v){
        Toast.makeText(getActivity(), "sign up", Toast.LENGTH_SHORT).show();
    }
}
