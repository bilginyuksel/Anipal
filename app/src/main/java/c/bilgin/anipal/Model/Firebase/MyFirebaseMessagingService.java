package c.bilgin.anipal.Model.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.NavigationActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MSGService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().size()>0){
            Log.d(TAG,"Message Data Payload : "+ remoteMessage.getData());

            // handle messages within 10 seconds

        }

        if( remoteMessage.getNotification() != null){
            Log.d(TAG,"Message Notification Title : "+remoteMessage.getNotification().getTitle());
            Log.d(TAG,"Message Notification Body : "+remoteMessage.getNotification().getBody());
            String dataTag = remoteMessage.getData().get("tag");
            Log.d(TAG,"Message Notification Data : " + dataTag);


            if(NavigationActivity.LIVING_FRAGMENT.equals("MESSAGE") && dataTag.equals("MESSAGE")) ;//no notif
            else sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String messageTitle,String messageBody) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = "Anipal";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.blue_pati)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
