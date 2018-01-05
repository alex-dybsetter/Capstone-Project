package net.alexblass.capstoneproject.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import net.alexblass.capstoneproject.MessagingActivity;
import net.alexblass.capstoneproject.R;

import java.util.ArrayList;

/**
 * A class to display data in the widget.
 */

public class InboxViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext = null;
    private int mAppWidgetId;

    private ArrayList<String> mMessages;

    public InboxViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mMessages = new ArrayList<>();
    }

    @Override
    public void onCreate() {
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
        return(1);
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
        mMessages = new ArrayList<>();
        // TODO: Get the real data
        mMessages.add("A");
        mMessages.add("B");
        mMessages.add("C");
        mMessages.add("A");
        mMessages.add("B");
        mMessages.add("C");
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews messageRow = new RemoteViews(mContext.getPackageName(),
                R.layout.row_message);

        // TODO: display the right date
        messageRow.setTextViewText(R.id.row_user_name, mMessages.get(position));
        messageRow.setTextViewText(R.id.row_time, mMessages.get(position));

        Intent inboxIntent = new Intent(mContext, MessagingActivity.class);
        messageRow.setOnClickFillInIntent(R.id.row, inboxIntent);

        return messageRow;
    }
}