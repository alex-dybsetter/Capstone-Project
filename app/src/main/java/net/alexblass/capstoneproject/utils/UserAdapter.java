package net.alexblass.capstoneproject.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.alexblass.capstoneproject.R;
import net.alexblass.capstoneproject.models.User;

import butterknife.ButterKnife;

/**
 * An adapter to display a list of users in the connect fragment.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private User[] mUserResults;
    private LayoutInflater mInflator;
    private Context mContext;
    //private ItemClickListener mClickListener;

    public UserAdapter(Context context, User[] results){
        this.mInflator = LayoutInflater.from(context);
        this.mUserResults = results;
        this.mContext = context;
    }

    public void updateUserResults(User[] results){
        mUserResults = results;
        notifyDataSetChanged();
    }

//    public void setClickListener(ItemClickListener itemClickListener){
//        mClickListener = itemClickListener;
//    }

    public User getItem(int index){
        return mUserResults[index];
    }

    @Override
    public int getItemCount() {
        return mUserResults.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_connect_profile_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position) {
        User selectedUser = mUserResults[position];

        if (selectedUser != null){
            // TODO
        }
    }

//    public interface ItemClickListener{
//        void onItemClick(View view, int position);
//    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private User selectedUser;

        public ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            if (mClickListener != null){
//                mClickListener.onItemClick(v, getAdapterPosition());
//            }
        }
    }
}