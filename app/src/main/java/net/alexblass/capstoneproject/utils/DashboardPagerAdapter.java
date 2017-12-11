package net.alexblass.capstoneproject.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.alexblass.capstoneproject.AccountPromptFragment;
import net.alexblass.capstoneproject.ConnectFragment;
import net.alexblass.capstoneproject.MyProfileFragment;
import net.alexblass.capstoneproject.R;

/**
 * An adapter to display the pages in the dashboard activity.
 */

public class DashboardPagerAdapter extends FragmentPagerAdapter {

    Context mContext;
    private static final int PAGE_COUNT = 2;

    public DashboardPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ConnectFragment();
            case 1:
                return new MyProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.connect);
            case 1:
                return mContext.getString(R.string.my_profile);
            default:
                return "";
        }
    }
}
