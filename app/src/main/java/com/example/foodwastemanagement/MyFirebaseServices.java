package com.example.foodwastemanagement;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.foodwastemanagement.model.NGOPushModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;


public class MyFirebaseServices extends FirebaseMessagingService {

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID ="101" ;

    private SessionHelper sessionHelper;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        EventBus.getDefault().post(remoteMessage);
    }



}
