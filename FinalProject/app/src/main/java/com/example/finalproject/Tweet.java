package com.example.finalproject;

public class Tweet{
    String Category;
    String Day;
    String Description;
    String From;
    String Hour;
    int Likes;
    String Name;
    int Trolls;

    public Tweet(String Category, String Day, String Description, String From, String Hour, int Likes, String Name, int Trolls){
        this.Category = Category;
        this.Day = Day;
        this.Description = Description;
        this.From = From;
        this.Hour = Hour;
        this.Likes = Likes;
        this.Name = Name;
        this.Trolls = Trolls;
    }

    public Tweet(){}

    public void setTrolls(int trolls) {
        Trolls = trolls;
    }

    public void setLikes(int likes) {
        Likes = likes;
    }

    public int getTrolls() { return Trolls; }

    public String getName() { return Name; }

    public String getHour() { return Hour; }

    public String getFrom() { return From; }

    public String getDescription() { return Description; }

    public String getDay() { return Day; }

    public int getLikes() { return Likes; }

    public String getCategory() { return Category; }

}
