package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.util.Hex;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

//main activity
public class MainActivity extends AppCompatActivity {

    //stuff to start the main service, which will also run in the background
    Intent mServiceIntent;
    private BackgroundService mYourService;

    //notifications muted flag
    Boolean isMuted = false;



    Context context = this;

    //for notification sounds
//    Uri gross_sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.gross);
//    Uri bloch_sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.bloch);
//    Uri food_sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.food);
//    Uri rishum_sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.rishum);


    //navigation bar
    BottomNavigationView bottomNavigation;

    //creating Firebase Database
    private DatabaseReference mDatabase;

    private boolean firsttime = true;

    //onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("The underground");





        mDatabase = FirebaseDatabase.getInstance().getReference();





        //starting the ReadUsers in background service
        mYourService = new BackgroundService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            startService(mServiceIntent);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ReadUsersData();

        //setting up navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, displayMetrics);
            // set your width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }


        //connecting fragments
        if(isConnected()){
            openFragment(HomeFragment.newInstance("", ""));
        }else{
            openFragment(LoginFragment.newInstance("", ""));
        }

        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    //Service running check, to make sure all runs at the right times
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    //itzik function
    public void SaveData(String User, String Password){
        SharedPreferences sharedPreferences = getSharedPreferences("login details",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("User",User);
        editor.putString("Password",Password);
        editor.commit();
    }

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

    //Fragment stuff, Itzik
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //Title setting
    public void setTitle(String title){
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = new TextView(this);
        textView.setText(title);




        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(),R.font.pacifico);
        textView.setTextSize(30);
        textView.setTypeface(typeface);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textView);
    }


    //Itzik Function
    public String CanConnect(String id, String password){

        for (int i = 0; i<DataBase.Users.size();i++) {
            User user = DataBase.Users.get(i);
            System.out.println(user.getId().equals(id));
            if(user.getId().equals(id)) {
                if(user.getPassword().equals(password)) {
                    //SaveData(user.getName(),password);
                    DataBase.Currentuser = user;
                    return user.getName();
                }
                else if(user.getPassword().equals("")){
                    //SaveData(user.getName(),password);
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Users").child(user.getId()).child("Password").setValue(password);
                    user.setPassword(password);
                    DataBase.Currentuser = user;
                    return  user.getName();
                }
            }
        }
        return "";
    }

    //Firebase check
    public boolean isConnected(){
        SharedPreferences sharedPreferences = getSharedPreferences("login details",MODE_PRIVATE);
        String User = sharedPreferences.getString("User","");
        System.out.println(User +  ":)))))))))))))))))))))))))");

        return !User.equals("");
    }

    //Initialize Navigation Bar
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            openFragment(HomeFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_sms:
                            openFragment(StatisticsFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_notifications:
                            openFragment(TweetFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };


    //Notification function
    private void notification(String type){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("n", "mainChannel", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        if(!firsttime && type.equals("bloch")){
            firsttime = false;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                    .setContentTitle("רב בלוך מגיע!")
                    .setAutoCancel(true)
                    .setLights(393207,1000,5000)
                    .setSmallIcon(R.drawable.bloch)
                    .setContentText("היה נעים להכיר :)")
                    .setSound(null);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(999, builder.build());
            if (!DataBase.isMuted) {
                MediaPlayer mp = MediaPlayer.create(context, R.raw.bloch);
                mp.start();
            }
        }

        if(!firsttime && type.equals("food")){
            firsttime = false;
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

        if(!firsttime && type.equals("rishum")){
            firsttime = false;
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

        if(!firsttime && type.equals("gross")){
            firsttime = false;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                    .setContentTitle("רב גרוס פה!")
                    .setAutoCancel(true)
                    .setLights(393207,1000,5000)
                    .setSmallIcon(R.drawable.gross)
                    .setContentText("רבוייתי. נו באמת.")
                    .setSound(null);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(999, builder.build());

            if(!DataBase.isMuted) {
                MediaPlayer mp = MediaPlayer.create(context, R.raw.gross);
                mp.start();
            }
        }

    }



}