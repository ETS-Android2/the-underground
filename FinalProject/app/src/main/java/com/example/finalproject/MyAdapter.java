package com.example.finalproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter {
    String[] category;
    String[] names;
    ArrayList<String> likes = new ArrayList<String>();
    private Activity context;
    public MyAdapter(Context context ,String[] category,String[] names) {
        super(context,R.layout.single_item,R.id.textView4 ,category);
        this.category = category;
        this.names = names;

    }
    private DatabaseReference mDatabase;

    private void ReadData(){
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
                    mDatabase.child("Tweets").child(String.valueOf(i+1)).child("Likes").setValue(like_array[i]);
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
                    mDatabase.child("Tweets").child(String.valueOf(i+1)).child("Trolls").setValue(troll_array[i]);

                }
                System.out.println(".");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {




        View v = convertView;


        //If convertView is null create a new view, else use convert view
        if (v == null)
            v = ((LayoutInflater)getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.single_item, null);
        TextView textView2=  v.findViewById(R.id.textView2);
        TextView textView3=  v.findViewById(R.id.textView3);
        TextView textView4=  v.findViewById(R.id.textView4);
        TextView textView5=  v.findViewById(R.id.textView5);
        TextView textView6=  v.findViewById(R.id.textView6);
        TextView textView7=  v.findViewById(R.id.textView7);
        TextView textView8=  v.findViewById(R.id.textView8);
        ImageView imageView = v.findViewById(R.id.imageView);
        ImageButton like = v.findViewById(R.id.like);






        ImageButton dislike= v.findViewById(R.id.dislike);
        dislike.setTag(R.drawable.ic_dislikebc_alt_24);

        mDatabase = FirebaseDatabase.getInstance().getReference();





        like.setOnClickListener(view -> {
            User Tuser = new User();
            int current_tweet = Integer.valueOf(textView8.getText().toString());
            System.out.println("current tweet " + current_tweet);
            for (User user:DataBase.Users) {
                if(textView7.getText().equals(user.getId())){
                    Tuser = user;
                    break;

                }

            }


            if ((Integer) like.getTag() == R.drawable.ic_likebc_24){
                if ((Integer) dislike.getTag() != R.drawable.ic_dislikebc_alt_24){
                    DataBase.Tweets.get(current_tweet-1).setTrolls(DataBase.Tweets.get(current_tweet-1).getTrolls()-0);
                    ReadData();
                    if(Integer.valueOf(textView6.getText().toString()) >= 1)
                    {textView6.setText(String.valueOf(Integer.valueOf(textView6.getText().toString())-1));}
                    Tuser.setTrolls(Tuser.getTrolls()-1);
                    mDatabase.child("Trolls").child(DataBase.Currentuser.getId()+String.valueOf(Integer.valueOf(textView8.getText().toString()))).removeValue();
                }
                mDatabase.child("Likes").child(DataBase.Currentuser.getId()+String.valueOf(Integer.valueOf(textView8.getText().toString()))).setValue(textView8.getText().toString());
                DataBase.Tweets.get(current_tweet-1).setLikes(DataBase.Tweets.get(current_tweet-1).getLikes());

                Tuser.setLikes(Tuser.getLikes()+1);
                like.setImageResource(R.drawable.ic_like_after_click_24);
                like.setTag(R.drawable.ic_like_after_click_24);
                dislike.setImageResource(R.drawable.ic_dislikebc_alt_24);
                dislike.setTag(R.drawable.ic_dislikebc_alt_24);
                ReadData();
                textView5.setText(String.valueOf(Integer.valueOf(textView5.getText().toString())+1));
                System.out.println("Like: "+DataBase.Tweets.get(DataBase.Tweets.size() - current_tweet).getLikes() + "Currr:" + textView8.getText());

            }else{
                Tuser.setLikes(Tuser.getLikes()-1);

                DataBase.Tweets.get(current_tweet-1).setLikes(DataBase.Tweets.get(current_tweet-1).getLikes());
                System.out.println("Like: "+DataBase.Tweets.get(current_tweet-1).getLikes());
                mDatabase.child("Likes").child(DataBase.Currentuser.getId()+String.valueOf(Integer.valueOf(textView8.getText().toString()))).removeValue();
                like.setImageResource(R.drawable.ic_likebc_24);
                like.setTag(R.drawable.ic_likebc_24);
                ReadData();
                if(Integer.valueOf(textView5.getText().toString()) >= 1){}
                {textView5.setText(String.valueOf(Integer.valueOf(textView5.getText().toString())-1));}
            }
            mDatabase.child("Tweets").child(String.valueOf(Integer.valueOf(textView8.getText().toString()))).child("Trolls").setValue(DataBase.Tweets.get(current_tweet-1).getTrolls());
            mDatabase.child("Tweets").child(String.valueOf(Integer.valueOf(textView8.getText().toString()))).child("Likes").setValue(DataBase.Tweets.get(current_tweet-1).getLikes());
            mDatabase.child("Users").child(String.valueOf(Integer.valueOf(textView7.getText().toString()))).child("Trolls").setValue(Tuser.getTrolls());
            mDatabase.child("Users").child(String.valueOf(Integer.valueOf(textView7.getText().toString()))).child("Likes").setValue(Tuser.getLikes());

        });

        dislike.setOnClickListener(view -> {

            int current_tweet = Integer.valueOf(textView8.getText().toString());
            User Tuser = new User();
            for (User user:DataBase.Users) {
                if(textView7.getText().equals(user.getId())){
                    Tuser = user;
                    break;

                }

            }
            if ((Integer) dislike.getTag() == R.drawable.ic_dislikebc_alt_24) {
                if ((Integer) like.getTag() != R.drawable.ic_likebc_24){
                    DataBase.Tweets.get(current_tweet-1).setLikes(DataBase.Tweets.get(current_tweet-1).getLikes()-0);
                    ReadData();
                    if(Integer.valueOf(textView5.getText().toString()) >= 1)
                    {textView5.setText(String.valueOf(Integer.valueOf(textView5.getText().toString())-1));}
                    Tuser.setLikes(Tuser.getLikes()-1);
                    mDatabase.child("Likes").child(DataBase.Currentuser.getId()+String.valueOf(Integer.valueOf(textView8.getText().toString()))).removeValue();
                }
                Tuser.setTrolls(Tuser.getTrolls()+1);
                mDatabase.child("Trolls").child(DataBase.Currentuser.getId()+String.valueOf(Integer.valueOf(textView8.getText().toString()))).setValue(textView8.getText().toString());

                dislike.setImageResource(R.drawable.ic_dislike_after_click_24);
                dislike.setTag(R.drawable.ic_dislike_after_click_24);
                like.setImageResource(R.drawable.ic_likebc_24);
                like.setTag(R.drawable.ic_likebc_24);
                ReadData();
                textView6.setText(String.valueOf(Integer.valueOf(textView6.getText().toString())+1));

            } else {
                Tuser.setTrolls(Tuser.getTrolls()-1);
                mDatabase.child("Trolls").child(DataBase.Currentuser.getId()+String.valueOf(Integer.valueOf(textView8.getText().toString()))).removeValue();
                dislike.setImageResource(R.drawable.ic_dislikebc_alt_24);
                dislike.setTag(R.drawable.ic_dislikebc_alt_24);
                ReadData();
                if(Integer.valueOf(textView6.getText().toString()) >= 1){
                textView6.setText(String.valueOf(Integer.valueOf(textView6.getText().toString())-1));}
            }
            mDatabase.child("Tweets").child(String.valueOf(Integer.valueOf(textView8.getText().toString()))).child("Trolls").setValue(Integer.valueOf(textView6.getText().toString()));
            mDatabase.child("Tweets").child(String.valueOf(Integer.valueOf(textView8.getText().toString()))).child("Likes").setValue(Integer.valueOf(textView5.getText().toString()));
            mDatabase.child("Users").child(String.valueOf(Integer.valueOf(textView7.getText().toString()))).child("Trolls").setValue(Tuser.getTrolls());
            mDatabase.child("Users").child(String.valueOf(Integer.valueOf(textView7.getText().toString()))).child("Likes").setValue(Tuser.getLikes());


        });
        switch (DataBase.Tweets.get(position).getCategory()){
            case "gross": imageView.setImageResource(R.drawable.gross);
                break;
            case "bloch": imageView.setImageResource(R.drawable.bloch);
                break;
            case "rishum": imageView.setImageResource(R.drawable.rishum);
                break;
            case "food": imageView.setImageResource(R.drawable.food);
                break;
        }

        textView2.setText(DataBase.Tweets.get(position).getDescription());
        textView3.setText(DataBase.Tweets.get(position).getName());
        textView4.setText(DataBase.Tweets.get(position).getHour());
        textView5.setText(String.valueOf(DataBase.Tweets.get(DataBase.Tweets.size() - position-1).getLikes()));
        textView6.setText(String.valueOf(DataBase.Tweets.get(DataBase.Tweets.size() - position-1).getTrolls()));
        textView7.setText(String.valueOf(DataBase.Tweets.get(position).getFrom()));
        textView8.setText(String.valueOf(DataBase.Tweets.size() - position));




        if(DataBase.likes.contains(textView8.getText())){
            System.out.println("blahhh +" + DataBase.likes);
            like.setTag(R.drawable.ic_like_after_click_24);
            like.setImageResource(R.drawable.ic_like_after_click_24);

        }
        else
        {like.setTag(R.drawable.ic_likebc_24);
            like.setImageResource(R.drawable.ic_likebc_24);}

        if(DataBase.trolls.contains(textView8.getText())){
            dislike.setTag(R.drawable.ic_dislike_after_click_24);
            dislike.setImageResource(R.drawable.ic_dislike_after_click_24);

        }
        else
        {dislike.setTag(R.drawable.ic_dislikebc_alt_24);
            dislike.setImageResource(R.drawable.ic_dislikebc_alt_24);}

        //LayoutInflater inflater = context.getLayoutInflater();

        //if(convertView==null)
         //   row = inflater.inflate(R.layout.single_item, null, true);
        //TextView name = (TextView) row.findViewById(R.id.textView3);
        //name.setText(names[position]);
        return v;
    }
}
