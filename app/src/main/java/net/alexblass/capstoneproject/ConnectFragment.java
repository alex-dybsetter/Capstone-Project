package net.alexblass.capstoneproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.alexblass.capstoneproject.models.User;
import net.alexblass.capstoneproject.utils.UserAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A Fragment to display a list of other app users.
 */
public class ConnectFragment extends Fragment {

    @BindView(R.id.connect_recyclerview) RecyclerView mRecyclerView;

    private LinearLayoutManager mLinearLayoutManager;
    private UserAdapter mAdapter;

    public ConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_connect, container, false);
        ButterKnife.bind(this, root);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new UserAdapter(getActivity(), new User[10]);
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

}
