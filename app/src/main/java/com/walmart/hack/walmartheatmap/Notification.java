package com.walmart.hack.walmartheatmap;

/**
 * Created by a0m019z on 6/29/17.
 */

public class Notification {
    public String title;
    public String body;

    public Notification(String title, String body){
        this.title = title;
        this.body = body;
    }

    public String toString(){
        return "Title: " + title + ", body: " + body;
    }
}
