package net.alexblass.capstoneproject.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.alexblass.capstoneproject.ConnectFragment;
import net.alexblass.capstoneproject.MyProfileFragment;
import net.alexblass.capstoneproject.R;

import static net.alexblass.capstoneproject.data.Constants.CONNECT_FRAG_INDEX;
import static net.alexblass.capstoneproject.data.Constants.MY_PROFILE_FRAG_INDEX;

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
            case CONNECT_FRAG_INDEX:
                return new ConnectFragment();
            case MY_PROFILE_FRAG_INDEX:
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
            case CONNECT_FRAG_INDEX:
                return mContext.getString(R.string.connect);
            case MY_PROFILE_FRAG_INDEX:
                return mContext.getString(R.string.my_profile);
            default:
                return "";
        }
    }
}
