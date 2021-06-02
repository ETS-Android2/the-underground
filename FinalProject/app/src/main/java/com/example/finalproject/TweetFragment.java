package com.example.finalproject;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.os.CancellationSignal;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TweetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TweetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TweetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TweetFragment newInstance(String param1, String param2) {
        TweetFragment fragment = new TweetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static void onClick(View v) {
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

    public void SetTweet(String Category,String Description){
        if(DataBase.Currentuser.getTrolls()<30)
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DateFormat day = new SimpleDateFormat("dd/MM/yy");
            DateFormat hour = new SimpleDateFormat("HH:mm");
            Date dateobj = new Date();
            if(DataBase.Tweets.size()>0){
                System.out.println();
                if(!day.format(dateobj).equals(DataBase.Tweets.get(DataBase.Tweets.size()-1).getDay())){
                    DataBase.Tweets.clear();
                    DataBase.Sum_of_tweets = 0;
                    mDatabase.child("Sum_of_tweets").setValue(0);
                    mDatabase.child("Tweets").setValue(0);
                    mDatabase.child("Likes").setValue(0);
                    mDatabase.child("Trolls").setValue(0);
                    mDatabase.child("Tweets").removeValue();
                    mDatabase.child("Likes").removeValue();
                    mDatabase.child("Trolls").removeValue();

                }
            }



            mDatabase.child("Tweets").child(String.valueOf(DataBase.Sum_of_tweets + 1)).child("Day").setValue(day.format(dateobj));
            mDatabase.child("Tweets").child(String.valueOf(DataBase.Sum_of_tweets + 1)).child("Hour").setValue(hour.format(dateobj));
            mDatabase.child("Tweets").child(String.valueOf(DataBase.Sum_of_tweets + 1)).child("Category").setValue(Category);
            mDatabase.child("Tweets").child(String.valueOf(DataBase.Sum_of_tweets + 1)).child("Description").setValue(Description);
            mDatabase.child("Tweets").child(String.valueOf(DataBase.Sum_of_tweets + 1)).child("From").setValue(DataBase.Currentuser.getId());
            mDatabase.child("Tweets").child(String.valueOf(DataBase.Sum_of_tweets + 1)).child("Name").setValue(DataBase.Currentuser.getName());
            mDatabase.child("Tweets").child(String.valueOf(DataBase.Sum_of_tweets + 1)).child("Likes").setValue(0);
            mDatabase.child("Tweets").child(String.valueOf(DataBase.Sum_of_tweets + 1)).child("Trolls").setValue(0);

            mDatabase.child("Sum_of_tweets").setValue(DataBase.Sum_of_tweets+1);

            Toast toast = Toast.makeText(getActivity(),"Tweeted successfully!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(getActivity(),"You're blocked because you trolled \uD83D\uDCA9!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.show();

        }


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this +fragment
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);

        view.findViewById(R.id.gross).setOnClickListener(v -> { SetTweet("gross","Rabbi Gross is in Yeshiva"); });
        view.findViewById(R.id.rishum).setOnClickListener(v -> { SetTweet("rishum","There is a rollcall now"); });
        view.findViewById(R.id.bloch).setOnClickListener(v -> { SetTweet("bloch","Rabbi Bloch is coming to the rooms"); });
        ImageButton food = view.findViewById(R.id.food);
        boolean IsTroll = DataBase.Currentuser.getTrolls()>30;





        food.setOnClickListener(v -> {
            if(DataBase.Currentuser.getTrolls()>=30){
                Toast toast = Toast.makeText(getActivity(),"You're blocked because you trolled \uD83D\uDCA9!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 150);
                toast.show();
                return;
            }
            final  View food_dialog = getLayoutInflater().inflate(R.layout.food_dialog,null);
            System.out.println("hiii");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(food_dialog);
            builder.setTitle("Sharing is Caring :)");


            int[] index = {2};

            int [] img_stroke = {R.drawable.yummystroke,R.drawable.smilestroke,R.drawable.neutralstroke,R.drawable.sadstroke,R.drawable.vomitingstroke};
            ImageView [] imageView = {food_dialog.findViewById(R.id.yummy),food_dialog.findViewById(R.id.smile),food_dialog.findViewById(R.id.neutral),food_dialog.findViewById(R.id.sad),food_dialog.findViewById(R.id.vomit)};
            int [] img = {R.drawable.yummy,R.drawable.smile,R.drawable.neutral,R.drawable.sad,R.drawable.vomiting};
            String [] emojis ={"\uD83D\uDE0B","\uD83D\uDE42","\uD83D\uDE10","\uD83D\uDE41","\uD83E\uDD2E"};
            View.OnClickListener PaintImg = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView current_img = food_dialog.findViewById(v.getId());

                    index[0] = java.util.Arrays.asList(imageView).indexOf(current_img);
                    for(int i = 0; i <img_stroke.length; i++){
                        imageView[i].setImageResource(img_stroke[i]);
                    }
                    imageView[index[0]].setImageResource(img[index[0]]);

                }
            };

            for (ImageView i: imageView) { i.setOnClickListener(PaintImg); }

            builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText editText = food_dialog.findViewById(R.id.description);
                    SetTweet("food",editText.getText()+" "+emojis[index[0]]);
                    dialog.dismiss();
                }
            });
            builder.show();

        });

        return  view;
    }
}