package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;


public class DataBase {

    static List<User> Users = new ArrayList<>();
    static User Currentuser = new User();
    static int Sum_of_tweets = 1;
    static List<Tweet> Tweets = new ArrayList<>();
    static ArrayList<String> likes = new ArrayList<>() ;
    static ArrayList<String> trolls = new ArrayList<>() ;
    static Boolean isMuted = false;
}
