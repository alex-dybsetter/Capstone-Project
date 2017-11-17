package net.alexblass.capstoneproject;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.alexblass.capstoneproject.utils.DashboardPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity {

    @BindView(R.id.dashboard_viewpager) ViewPager mPager;
    @BindView(R.id.dashboard_tabs) TabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        DashboardPagerAdapter adapter = new DashboardPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(adapter);

        mTabs.setupWithViewPager(mPager);
    }
}
