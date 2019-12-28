package com.ygaps.travelapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.RetrofitClient;
import com.ygaps.travelapp.activity.MainActivity;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "TTTTTTTTT";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d( TAG, "onMessageReceived: Nhận" );
        if (remoteMessage.getData().size() > 0) {
            Map data=remoteMessage.getData();
            Log.d(TAG, "Key Data : " +  remoteMessage.getData().get("key")); //Get specific key data
            if (data.isEmpty()) { // message type is notification.
                Log.e("data", "isNull");
                //  sendNotification(remoteMessage.getNotification().getBody());
                sendNotification(remoteMessage.getNotification().getBody());
            } else { // message type is data.
                StringBuilder temp = new StringBuilder();
                temp.append(data.get("From ")).append(" invites you to Tour: ").append(data.get("name"));
                String body = temp.toString();
                sendNotification( body);
            }
        }

        super.onMessageReceived( remoteMessage );
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

//        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences sharedPreferences =  getSharedPreferences("isLogin",MODE_PRIVATE);
        String Authorization = sharedPreferences.getString( "token","" );
        if (!Authorization.equals(""))
        {
            //Log.e("AAAAAAAAA",Authorization);
            //Log.e( "TOKENNNNNNNNNNNNNNNNN", token );
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .register_Firebase(Authorization,token,android_id,1,"1.0");

            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200){
                        try {
                            String body = response.body().string();
                            Log.i("Firebase", body);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Log.i("Firebase", "false");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString( R.string.project_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setLargeIcon( BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                        .setContentTitle(getString(R.string.project_id))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setDefaults( Notification.DEFAULT_ALL)
                        .setPriority( NotificationManager.IMPORTANCE_HIGH)
                        .addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_missed,
                                "Cancel",
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)))
                        .addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_outgoing,
                                "OK",
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)));

        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
