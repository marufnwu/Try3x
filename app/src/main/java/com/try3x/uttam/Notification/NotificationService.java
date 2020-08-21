package com.try3x.uttam.Notification;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.try3x.uttam.MainActivity;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Map<String, String> data = remoteMessage.getData();
            handleData(data);

        } else if (remoteMessage.getNotification() != null) {

            handleNotification(remoteMessage.getNotification());
        }// Check if message contains a notification payload.

    }

    private void handleNotification(RemoteMessage.Notification notification) {
        NotificationData notificationData = new NotificationData();

        notificationData.tittle = notification.getTitle();
        notificationData.description = notification.getBody();

        Intent resultIntent  = new Intent(getApplicationContext(), MainActivity.class);

        NotificationUtil notificationUtil = new NotificationUtil(getApplicationContext());

        notificationUtil.displayNotification(notificationData);
    }

    private void handleData(Map<String, String> data) {
        NotificationData notificationData = new NotificationData();

        notificationData.tittle = data.get("tittle");
        notificationData.description = data.get("description");
        notificationData.imgUrl = data.get("imgUrl");
        notificationData.notiClearAble = Integer.parseInt(data.get("notiClearAble"));
        notificationData.action = Integer.parseInt(data.get("action"));
        notificationData.notiType = Integer.parseInt(data.get("notiType"));
        notificationData.actionUrl = data.get("actionUrl");
        notificationData.actionActivity = data.get("actionActivity");

        //Intent resultIntent  = new Intent(getApplicationContext(), MainActivity.class);

        NotificationUtil notificationUtil = new NotificationUtil(getApplicationContext());

        notificationUtil.displayNotification(notificationData);



    }
}
