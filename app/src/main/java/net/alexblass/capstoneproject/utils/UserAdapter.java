package net.alexblass.capstoneproject.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import net.alexblass.capstoneproject.R;
import net.alexblass.capstoneproject.models.User;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An adapter to display a list of users in the connect fragment.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

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
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, int position) {
        User selectedUser = mUserResults[position];

        if (selectedUser != null){

            holder.userNameTv.setText(selectedUser.getName());

            Calendar birthday = new GregorianCalendar();
            birthday.setTimeInMillis(selectedUser.getBirthday());
            int age = UserDataUtils.calculateAge(birthday);
            int genderId = UserDataUtils.getGenderAbbreviationStringId(selectedUser.getGenderCode());
            holder.userStatsTv.setText(mContext.getResources().getString(R.string.stats_format,
                    age, mContext.getString(genderId),
                    selectedUser.getZipcode()));

            if (!selectedUser.getProfilePicUri().isEmpty()){
                StorageReference profilePicFile = FirebaseStorage.getInstance().getReference()
                        .child(Uri.parse(selectedUser.getProfilePicUri()).getPath());
                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    profilePicFile.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Picasso.with(mContext)
                                            .load(localFile)
                                            .placeholder(R.drawable.ic_person_white_48dp)
                                            .centerCrop()
                                            .fit()
                                            .into(holder.userProfilePic);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

//    public interface ItemClickListener{
//        void onItemClick(View view, int position);
//    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private User selectedUser;

        @BindView(R.id.card_user_imageview) ImageView userProfilePic;
        @BindView(R.id.card_name_tv) TextView userNameTv;
        @BindView(R.id.card_stats_tv) TextView userStatsTv;

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