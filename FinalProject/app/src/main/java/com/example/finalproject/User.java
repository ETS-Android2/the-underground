package com.example.finalproject;

import java.util.ArrayList;

public class User {
    private String ID="";
    private String Name="";
    private String Password="";
    private int Likes=0;
    private int Trolls=0;



    public User(String ID,int Likes, String Name, String Password,int Trolls){
        this.ID = ID;
        this.Name = Name;
        this.Password = Password;
        this.Trolls = Trolls;
        this.Likes = Likes;
    }
    public User(){

    }

    public void setName(String name) {
        Name = name;
    }

    public void setLikes(int likes) {
        Likes = likes;
    }

    public void setTrolls(int trolls) {
        Trolls = trolls;
    }

    public int getTrolls() { return Trolls; }

    public int getLikes() { return Likes; }

    public String getName() {
        return Name;
    }

    public String getPassword() { return Password; }

    public String getId(){
        return ID;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }
}
