package com.example.finalproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  boolean FirstTime = true;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    private DatabaseReference mDatabase;

    private ArrayList<String> category = new ArrayList<String>();

    private SwipeRefreshLayout refreshLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //refreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.refresh);
        //refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        //    @Override
        //    public void onRefresh() {
        //        refreshLayout.setRefreshing(false);
        //    }
        //});




        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Tweets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataBase.Tweets.clear();
                category.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Tweet tweet = keyNode.getValue(Tweet.class);
                    DataBase.Tweets.add(tweet);

                }
                Collections.reverse(DataBase.Tweets);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(DataBase.Currentuser.getTrolls()>=30 && !DataBase.Currentuser.getName().contains("\uD83D\uDCA9")){
            mDatabase.child("Users").child(DataBase.Currentuser.getId()).child("Name").setValue(DataBase.Currentuser.getName()+"\uD83D\uDCA9");
            DataBase.Currentuser.setName(DataBase.Currentuser.getName()+"\uD83D\uDCA9");
        }
        if(DataBase.Currentuser.getLikes() >=150 && !DataBase.Currentuser.getName().contains("\uD83E\uDD47")){
            mDatabase.child("Users").child(DataBase.Currentuser.getId()).child("Name").setValue(DataBase.Currentuser.getName()+"\uD83E\uDD47");
            DataBase.Currentuser.setName(DataBase.Currentuser.getName()+"\uD83E\uDD47");
        }
        else if(DataBase.Currentuser.getLikes() >=100 && !DataBase.Currentuser.getName().contains("\uD83E\uDD48")){
            mDatabase.child("Users").child(DataBase.Currentuser.getId()).child("Name").setValue(DataBase.Currentuser.getName()+"\uD83E\uDD48");
            DataBase.Currentuser.setName(DataBase.Currentuser.getName()+"\uD83E\uDD48");
        }
        else if(DataBase.Currentuser.getLikes() >=50 && !DataBase.Currentuser.getName().contains("\uD83E\uDD49")) {
            mDatabase.child("Users").child(DataBase.Currentuser.getId()).child("Name").setValue(DataBase.Currentuser.getName() + "\uD83E\uDD49");
            DataBase.Currentuser.setName(DataBase.Currentuser.getName() + "\uD83E\uDD49");
        }


        ListView listView = view.findViewById(R.id.list);
        String [] category = new String[DataBase.Tweets.size()];
        String [] names = new String[DataBase.Tweets.size()];
        for(int i=0;i < category.length;i++){ category[i] = DataBase.Tweets.get(i).getDescription(); }
        for(int i=0;i < names.length;i++){ category[i] = DataBase.Tweets.get(i).getName(); }
        MyAdapter myAdapter = new MyAdapter(getContext(),category,names);
        listView.setAdapter(myAdapter);

        mDatabase.child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataBase.likes.clear();
                int [] like_array = new int[DataBase.Tweets.size()];
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    like_array[Integer.valueOf(keyNode.getValue().toString())-1]++;
                    if(keyNode.getKey().contains(DataBase.Currentuser.getId())){
                        DataBase.likes.add(String.valueOf(keyNode.getValue()));

                    }
                }
                System.out.print("like_array: ");
                for(int i = 0;i<like_array.length;i++){

                    DataBase.Tweets.get(i).setLikes(like_array[i]);
                    System.out.print(DataBase.Tweets.get(i).getLikes() + ", ");
                }
                System.out.println(".");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("Trolls").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int [] troll_array = new int[DataBase.Tweets.size()];
                DataBase.trolls.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    troll_array[Integer.valueOf(keyNode.getValue().toString())-1]++;
                    if(keyNode.getKey().contains(DataBase.Currentuser.getId())){
                        DataBase.trolls.add(String.valueOf(keyNode.getValue()));
                        System.out.println(DataBase.trolls);
                    }
                }
                System.out.print("troll_array: ");
                for(int i = 0;i<troll_array.length;i++){
                    System.out.print(troll_array[i] + ", ");
                    DataBase.Tweets.get(i).setTrolls(troll_array[i]);
                }
                System.out.println(".");
                if(FirstTime){
                    FirstTime =false;
                    ListView listView = view.findViewById(R.id.list);
                    String [] category = new String[DataBase.Tweets.size()];
                    String [] names = new String[DataBase.Tweets.size()];
                    for(int i=0;i < category.length;i++){ category[i] = DataBase.Tweets.get(i).getDescription(); }
                    for(int i=0;i < names.length;i++){ category[i] = DataBase.Tweets.get(i).getName(); }
                    MyAdapter myAdapter = new MyAdapter(getContext(),category,names);
                    listView.setAdapter(myAdapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }
}