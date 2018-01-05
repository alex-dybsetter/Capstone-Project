package net.alexblass.capstoneproject.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import net.alexblass.capstoneproject.MessagingActivity;
import net.alexblass.capstoneproject.R;

/**
 * A WidgetProvider to display a custom widget on the home screen.
 */

public class InboxWidgetProvider extends AppWidgetProvider {

    private static RemoteViews getRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        Intent intent = new Intent(context, WidgetService.class);
        views.setRemoteAdapter(R.id.widget_messages_list, intent);

        Intent appIntent = new Intent(context, MessagingActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_messages_list, appPendingIntent);

        views.setEmptyView(R.id.widget_messages_list, R.id.empty_view);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        for (int widgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(widgetId, getRemoteView(context));

            Intent intent = new Intent(context, WidgetService.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget);

            widget.setRemoteAdapter(widgetId, R.id.widget_messages_list, intent);

            Intent inboxIntent = new Intent(context, MessagingActivity.class);
            PendingIntent inboxPI = PendingIntent.getActivity(context, widgetId, inboxIntent, 0);
            widget.setPendingIntentTemplate(R.id.widget_messages_list, inboxPI);

            appWidgetManager.updateAppWidget(widgetId, widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // Update the ListView in the widget
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, InboxWidgetProvider.class);
            manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(componentName), R.id.widget_messages_list);
        }
        super.onReceive(context, intent);
    }
}