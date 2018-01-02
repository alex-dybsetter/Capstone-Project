package net.alexblass.capstoneproject.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.alexblass.capstoneproject.R;
import net.alexblass.capstoneproject.models.Message;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays the messages in a thread.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Message[] mMessageResults;
    private LayoutInflater mInflator;
    private Context mContext;
    private String mEmail;
    private String mRecipient;

    public MessageAdapter(Context context, Message[] results, String email){
        this.mInflator = LayoutInflater.from(context);
        this.mMessageResults = results;
        this.mContext = context;
        this.mEmail = email;

        this.mRecipient = results[0].getSender().equals(email) ? results[0].getSentTo() : results[0].getSender();
    }

    public void updateMessageResults(Message[] results){
        mMessageResults = results;
        notifyDataSetChanged();
    }

    public String getRecipient(){
        return mRecipient;
    }

    public Message getItem(int index){
        return mMessageResults[index];
    }

    @Override
    public int getItemCount() {
        return mMessageResults.length;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_chat_bubble, parent, false);
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageAdapter.ViewHolder holder, int position) {
        Message selectedMessage = mMessageResults[position];

        if (selectedMessage != null){

            if(selectedMessage.getSender().equals(mEmail)){
                holder.chatBg.setBackground(mContext.getDrawable(R.drawable.chat_bubble_active_user_bg));
                ContextCompat.getDrawable(mContext, R.drawable.chat_bubble_active_user_bg);
            } else {
                holder.chatBg.setBackground(mContext.getDrawable(R.drawable.chat_bubble_other_user_bg));
                ContextCompat.getDrawable(mContext, R.drawable.chat_bubble_other_user_bg);
            }

            holder.chatSenderTv.setText(selectedMessage.getSender());
            holder.chatContentTv.setText(selectedMessage.getMessage());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chat_bubble_sender) TextView chatSenderTv;
        @BindView(R.id.chat_bubble_content) TextView chatContentTv;
        @BindView(R.id.chat_bubble_parent) ConstraintLayout chatBg;

        public ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}