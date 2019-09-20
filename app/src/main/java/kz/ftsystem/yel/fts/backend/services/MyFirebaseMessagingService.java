package kz.ftsystem.yel.fts.backend.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ftsystem.yel.fts.R;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Objects;

import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.MainActivity;
import kz.ftsystem.yel.fts.OrderResultActivity;
import kz.ftsystem.yel.fts.SplashScreenActivity;
import kz.ftsystem.yel.fts.TranslatingFinishedActivity;
import kz.ftsystem.yel.fts.WaitingOrderActivity;
import kz.ftsystem.yel.fts.backend.MessageEvent;
import kz.ftsystem.yel.fts.backend.MyApplication;
import kz.ftsystem.yel.fts.backend.MyConstants;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;


public class MyFirebaseMessagingService extends FirebaseMessagingService implements MyCallback {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(MyConstants.TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(MyConstants.TAG, "Message data payload: " + remoteMessage.getData());

        }

        if (remoteMessage.getNotification() != null) {
            if (((MyApplication) getApplicationContext()).isAppForeground()) {
                if (Objects.requireNonNull(remoteMessage.getNotification().getTitle()).equals("Заказ обработан") ||
                        Objects.requireNonNull(remoteMessage.getNotification().getTitle()).equals("Найден переводчик") ||
                        Objects.requireNonNull(remoteMessage.getNotification().getTitle()).equals("Заказ завершен")) {
                    EventBus.getDefault().post(new MessageEvent("1"));
                }
            } else {
                String msgTitle = remoteMessage.getNotification().getTitle();
                String msgBody = remoteMessage.getNotification().getBody();
//                switch (Objects.requireNonNull(remoteMessage.getNotification().getTitle())) {
//                    case "Обработан":
//                        msgTitle = getString(R.string.title_1);
//                        msgBody = getString(R.string.body_1);
//                        break;
//                    case "2":
//                        msgTitle = getString(R.string.title_2);
//                        msgBody = getString(R.string.body_2);
//                        break;
//                    case "3":
//                        msgTitle = getString(R.string.title_3);
//                        msgBody = getString(R.string.body_3);
//                        break;
//                }
                sendNotification(msgTitle, msgBody);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
//        super.onNewToken(token);
        Log.d(MyConstants.TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String fcmToken) {
        DB preference = new DB(this);
        preference.open();
        String myId = preference.getVariable(MyConstants.MY_ID);
        String myToken = preference.getVariable(MyConstants.MY_TOKEN);
        Backend backend = new Backend(this, this);
        backend.sendNewFcmToken(myId, myToken, fcmToken);
        Log.d(MyConstants.TAG, "sendRegistrationToServer: " + fcmToken);
    }


    private void sendNotification(String title, String body) {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_exposure_plus_1_black_24dp)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.default_notification_channel_id),
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onMessageSent(String s) {
//        super.onMessageSent(s);
        Log.d(MyConstants.TAG, "onMessageSent " + s);
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {

    }
}
