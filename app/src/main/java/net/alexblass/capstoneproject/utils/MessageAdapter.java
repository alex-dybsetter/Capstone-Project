package net.alexblass.capstoneproject.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.alexblass.capstoneproject.R;
import net.alexblass.capstoneproject.models.Message;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays a list of messages.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Message[] mMessageResults;
    private LayoutInflater mInflator;
    private Context mContext;
    private String mEmail;
    private MessageAdapter.ItemClickListener mClickListener;

    public MessageAdapter(Context context, Message[] results, String email){
        this.mInflator = LayoutInflater.from(context);
        this.mMessageResults = results;
        this.mContext = context;
        this.mEmail = email;
    }

    public void updateMessageResults(Message[] results){
        mMessageResults = results;
        notifyDataSetChanged();
    }

    public void setClickListener(MessageAdapter.ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
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
        View view = mInflator.inflate(R.layout.item_message, parent, false);
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageAdapter.ViewHolder holder, int position) {
        Message selectedMessage = mMessageResults[position];

        if (selectedMessage != null){

            String recipient;
            if(selectedMessage.getSender().equals(mEmail)){
                recipient = selectedMessage.getSentTo();
            } else {
                recipient = selectedMessage.getSender();
            }
            holder.userNameTv.setText(recipient);

            try {
                String start_dt = selectedMessage.getDateTime();
                DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                Date date = (Date) formatter.parse(start_dt);
                SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy");
                String finalString = newFormat.format(date);
                holder.messageTimeTv.setText(finalString);
            } catch (ParseException e){
                e.printStackTrace();
                holder.messageTimeTv.setText(selectedMessage.getDateTime());
            }
        }
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.message_user_img) ImageView userProfilePic;
        @BindView(R.id.message_user_name) TextView userNameTv;
        @BindView(R.id.message_time) TextView messageTimeTv;
        @BindView(R.id.message_delete_btn) ImageButton messageDeleteBtn;

        public ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null){
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}