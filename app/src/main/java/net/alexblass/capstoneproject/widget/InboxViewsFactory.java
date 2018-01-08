package net.alexblass.capstoneproject.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.alexblass.capstoneproject.MessagingActivity;
import net.alexblass.capstoneproject.R;
import net.alexblass.capstoneproject.models.Message;
import net.alexblass.capstoneproject.models.WidgetMessage;
import net.alexblass.capstoneproject.utils.UserDataUtils;

import java.util.ArrayList;

import static net.alexblass.capstoneproject.data.Keys.MSG_DATA_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_READ_FLAG_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENDER_EMAIL_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENT_TO_EMAIL_KEY;

/**
 * A class to display data in the widget.
 */

public class InboxViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext = null;
    private int mAppWidgetId;

    private ArrayList<WidgetMessage> mMessages;

    public InboxViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mMessages = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "(dot)");

        FirebaseDatabase.getInstance().getReference().child(MSG_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessages = new ArrayList<>();
                Message lastMessage = null;
                Iterable<DataSnapshot> results = dataSnapshot.getChildren();
                for (DataSnapshot messageThreadData : results){
                    if(messageThreadData.getKey().contains(email)){
                        Iterable<DataSnapshot> messages = messageThreadData.getChildren();
                        DataSnapshot message = messages.iterator().next();
                        String sender = message.child(MSG_SENDER_EMAIL_KEY).getValue().toString();
                        String sentTo = message.child(MSG_SENT_TO_EMAIL_KEY).getValue().toString();
                        lastMessage = new Message(sender, sentTo,
                                message.child(MSG_DATA_KEY).getValue().toString(),
                                (boolean)message.child(MSG_READ_FLAG_KEY).getValue());
                        Log.e("collected data", lastMessage.getDateTime());

                        String messageSender = email.replace("(dot)", ".").equals(sender) ? sentTo : sender;
                        mMessages.add(new WidgetMessage(messageSender, UserDataUtils.formatDate(lastMessage), lastMessage.isRead()));
                    }
                }
                AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.widget_messages_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Required override method
    @Override
    public void onDestroy() {
    }

    // Required override method
    @Override
    public RemoteViews getLoadingView() {
        return(null);
    }

    // Required override method
    @Override
    public int getViewTypeCount() {
        return(2);
    }

    // Required override method
    @Override
    public long getItemId(int position) {
        return(position);
    }

    // Required override method
    @Override
    public boolean hasStableIds() {
        return(true);
    }

    // Required override method
    @Override
    public void onDataSetChanged() {
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews messageRow;
        int id;
        if (mMessages.get(position).isIsRead()) {
            messageRow = new RemoteViews(mContext.getPackageName(),
                    R.layout.row_message);

            messageRow.setTextViewText(R.id.row_user_name, mMessages.get(position).getSender());
            messageRow.setTextViewText(R.id.row_time, mMessages.get(position).getDate());
            id = R.id.row;
        } else {
            messageRow = new RemoteViews(mContext.getPackageName(),
                    R.layout.row_message_unread);

            messageRow.setTextViewText(R.id.row_user_name_unread, mMessages.get(position).getSender());
            messageRow.setTextViewText(R.id.row_time_unread, mMessages.get(position).getDate());

            id = R.id.row_unread;
        }

        Intent inboxIntent = new Intent(mContext, MessagingActivity.class);
        messageRow.setOnClickFillInIntent(id, inboxIntent);

        return messageRow;
    }
}