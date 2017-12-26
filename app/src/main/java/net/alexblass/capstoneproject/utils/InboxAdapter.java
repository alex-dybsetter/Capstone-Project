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
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An adapter to display the message threads in a user's inbox.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private Map<String, List<Message>> mMessageResults;
    private LayoutInflater mInflator;
    private Context mContext;
    private String mEmail;
    private Object[] mKeys;
    private InboxAdapter.ItemClickListener mClickListener;

    public InboxAdapter(Context context, Map<String, List<Message>> messages, String email){
        this.mInflator = LayoutInflater.from(context);
        this.mMessageResults = messages;
        this.mContext = context;
        this.mEmail = email;

        this.mKeys = messages.keySet().toArray();
    }

    public void updateMessageResults(Map<String, List<Message>> messages){
        this.mMessageResults = messages;
        this.mKeys = messages.keySet().toArray();
        notifyDataSetChanged();
    }

    public void setClickListener(InboxAdapter.ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
    }

    public List<Message> getItem(String key){
        return mMessageResults.get(key);
    }

    @Override
    public int getItemCount() {
        return mMessageResults.size();
    }

    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_message, parent, false);
        InboxAdapter.ViewHolder viewHolder = new InboxAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final InboxAdapter.ViewHolder holder, int position) {
        List<Message> messageThread = mMessageResults.get(mKeys[position]);

        if (messageThread != null){
            Message mostRecentMsg = messageThread.get(messageThread.size() - 1);
            String recipient;
            if(mostRecentMsg.getSender().equals(mEmail)){
                recipient = mostRecentMsg.getSentTo();
            } else {
                recipient = mostRecentMsg.getSender();
            }
            holder.userNameTv.setText(recipient);

            try {
                String unformattedTimeStamp = mostRecentMsg.getDateTime();
                DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                Date date = (Date) formatter.parse(unformattedTimeStamp);
                SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy");
                String formattedTimeStamp = newFormat.format(date);
                holder.messageTimeTv.setText(formattedTimeStamp);
            } catch (ParseException e){
                e.printStackTrace();
                holder.messageTimeTv.setText(mostRecentMsg.getDateTime());
            }
        }
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.message_user_name) TextView userNameTv;
        @BindView(R.id.message_time) TextView messageTimeTv;

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