package net.alexblass.capstoneproject.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.alexblass.capstoneproject.MessagingActivity;
import net.alexblass.capstoneproject.R;
import net.alexblass.capstoneproject.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.alexblass.capstoneproject.data.Keys.MSG_DATA;
import static net.alexblass.capstoneproject.data.Keys.MSG_DATE_TIME;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENDER_EMAIL_KEY;
import static net.alexblass.capstoneproject.data.Keys.MSG_SENT_TO_EMAIL_KEY;

/**
 * A service tht listens for messages between users.
 */

public class MeetMeMessagingService extends FirebaseMessagingService {

    private static final String TAG = MeetMeMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + data);

            // Send a notification that you got a new message
            sendNotification(data);
        }
    }

    private void sendNotification(Map<String, String> data) {
        Intent launchInboxActivity = new Intent(this, MessagingActivity.class);
        launchInboxActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , launchInboxActivity,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_email_white_24dp)
                .setContentTitle(getString(R.string.message_notification_title))
                .setContentText(getString(R.string.message_notification_body))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
