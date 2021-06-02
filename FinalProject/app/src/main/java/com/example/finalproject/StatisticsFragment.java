package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public class StatisticsFragment extends Fragment {

    ///////////////////////////chaim///////////////////////////
    private LineGraphSeries<DataPoint> series1;


    LineChart lineChart;
    Random random = new Random();
    //int rand = random.nextInt(5);
    //int rand2 = random.nextInt(100)*3;




    ///////////////////////////chaim///////////////////////////


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatisticsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void SaveData(String User, String Password){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login details",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("User",User);
        editor.putString("Password",Password);
        editor.commit();

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        Switch sw = (Switch)view.findViewById(R.id.notifications_switch);
        ImageButton logOutB = view.findViewById(R.id.logout);
        TextView mText= view.findViewById(R.id.muteText);
        if(DataBase.isMuted)
            sw.setChecked(true);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DataBase.isMuted = true;
                } else {
                    DataBase.isMuted = false;
                }
            }
        });

        logOutB.setOnClickListener(v -> {
            SaveData("","");
            openFragment(LoginFragment.newInstance("", ""));
        });
        final Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.bounch);
        ImageButton email=view.findViewById(R.id.emailBtn);
        email.setOnClickListener(v -> {
            email.startAnimation(animation);
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","bmdctwitter@gmail.com", null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(intent, "Choose an Email app to continue:"));
        });

/////////////////// the name //////////////////////


        TextView theName = (TextView) view.findViewById(R.id.theName);
        theName.setText(DataBase.Currentuser.getName());

        TextView theTrolls = (TextView) view.findViewById(R.id.numberTrolls);
        if (DataBase.Currentuser.getTrolls()>=999)
            theTrolls.setText("999");
        else if (DataBase.Currentuser.getTrolls()<=-1)
            theTrolls.setText("0");
        else
            theTrolls.setText(String.valueOf(DataBase.Currentuser.getTrolls()));

        TextView theLikes = (TextView) view.findViewById(R.id.numberLikes);
        if (DataBase.Currentuser.getLikes()>=999)
            theLikes.setText("999");
        else if (DataBase.Currentuser.getLikes()<=-1)
            theLikes.setText("0");
        else
            theLikes.setText(String.valueOf(DataBase.Currentuser.getLikes()));




/////////////////// the name //////////////////////

/////////////// graph ////////////////////

/////////////// graph ////////////////////

/////////////// lineGraph ////////////////////
        int countlikes=0;
        for(int i = 0; i<DataBase.Sum_of_tweets;i++)
        {
            DateFormat day = new SimpleDateFormat("dd");
            DateFormat hour = new SimpleDateFormat("HH:mm");
            Date dateobj = new Date();
//            while (Integer.valueOf(DataBase.Tweets.get(i).getDay().toString())==Integer.valueOf(day.format(dateobj).toString()))
//            {
////                countlikes += DataBase.Tweets.get(i).getLikes();
//            }
        }

        return view;
    }
}