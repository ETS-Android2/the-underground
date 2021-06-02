package com.example.finalproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BackgroundService extends Service {

    Context context = this;
    //isMuted flag
    Boolean isMuted = false;

    //for notification sounds
//    Uri gross_sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.gross);
//    Uri bloch_sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.bloch);
//    Uri food_sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.food);
//    Uri rishum_sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.rishum);



    //Firebase initialization
    private DatabaseReference mDatabase;

    //on create method
    @Override
    public void onCreate() {
        super.onCreate();

        //Run the Service properly, based off SDK
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        //Start Database
        mDatabase = FirebaseDatabase.getInstance().getReference();




        //Start ReadUsers Func
        ReadUsersData();

    }

    //Service start if over Oreo
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("אתה כעת מחובר למחתרת.")
                .setSmallIcon(R.drawable.main)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

    }



    //Function that creates User and Tweet list, then activates notification func when there's a new tweet
    public void ReadUsersData(){


        mDatabase.child("Tweets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataBase.Tweets.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Tweet tweet = keyNode.getValue(Tweet.class);
                    DataBase.Tweets.add(tweet);}
                Collections.reverse(DataBase.Tweets);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child("Sum_of_tweets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataBase.Sum_of_tweets = Integer.parseInt(dataSnapshot.getValue().toString());
                if(DataBase.Tweets.size() != 0 &&
                        !DataBase.Tweets.get(0).getName().equals(DataBase.Currentuser.getName()))
                    notification(DataBase.Tweets.get(0).getCategory());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataBase.Users.clear();

                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    User user = keyNode.getValue(User.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("login details",MODE_PRIVATE);
                    String User = sharedPreferences.getString("User","");

                    if(User.equals(user.getName())){
                        System.out.println(user.getName());
                        DataBase.Currentuser = user;
                    }
                    DataBase.Users.add(user);
                }

                String ss =  DataBase.Users.get(0).getName();
                System.out.println(DataBase.Users.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    //Notification function
    private void notification(String type){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("n", "mainChannel", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        if(type.equals("bloch")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                    .setContentTitle("רב בלוך מגיע!")
                    .setAutoCancel(true)
                    .setLights(393207,1000,5000)
                    .setSmallIcon(R.drawable.bloch)
                    .setContentText("היה נעים להכיר :)")
                    .setSound(null);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(999, builder.build());
            if(!DataBase.isMuted) {
                MediaPlayer mp = MediaPlayer.create(context, R.raw.bloch);
                mp.start();

            }
        }


        if(type.equals("food")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                    .setContentTitle("אוכל הגיע!")
                    .setAutoCancel(true)
                    .setLights(393207,1000,5000)
                    .setSmallIcon(R.drawable.food)
                    .setContentText(DataBase.Tweets.get(0).getDescription())
                    .setSound(null);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(999, builder.build());
            if (!DataBase.isMuted) {
                MediaPlayer mp = MediaPlayer.create(context, R.raw.food);
                mp.start();
            }
        }

        if(type.equals("rishum")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                    .setContentTitle("יש רישום!")
                    .setAutoCancel(true)
                    .setLights(393207,1000,5000)
                    .setSmallIcon(R.drawable.rishum)
                    .setContentText("ד״ש מחיים שטיין")
                    .setSound(null);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(999, builder.build());
            if (!DataBase.isMuted) {
                MediaPlayer mp = MediaPlayer.create(context, R.raw.rishum);
                mp.start();
            }
        }

        if(type.equals("gross")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                    .setContentTitle("רב גרוס פה!")
                    .setAutoCancel(true)
                    .setLights(393207,1000,5000)
                    .setSmallIcon(R.drawable.gross)
                    .setContentText("רבוייתי. נו באמת.")
                    .setSound(null);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(999, builder.build());
            if (!DataBase.isMuted) {
                MediaPlayer mp = MediaPlayer.create(context, R.raw.gross);
                mp.start();
            }
        }

    }

    //Stack overflow code
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    //Stack Overflow code
    @Override
    public void onDestroy() {
        super.onDestroy();


        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}



