package com.walmart.hack.walmartheatmap;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by a0m019z on 6/28/17.
 */


    //https://www.codementor.io/flame3/send-push-notifications-to-android-with-firebase-du10860kb
    //with firebase
    // https://console.firebase.google.com/project/testpush-29565/overview

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        DatabaseHelper mDbHelper;
        SQLiteDatabase db;
        mDbHelper = new DatabaseHelper(this);
        db = mDbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();

        // Create a new map of values, where column names are the keys

        values.put("title", remoteMessage.getNotification().getTitle());
        values.put("body", remoteMessage.getNotification().getBody());

        long newRowId = db.insert(
                "notification",
                null,
                values);
    }
}
