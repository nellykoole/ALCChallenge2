package com.example.alcchallenge2;


import android.net.Uri;
import android.widget.VideoView;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.stream.Stream;

public class BlogPost {

    private String price, desc, title, image;
    private Date time;

    public BlogPost() {}

    public BlogPost(String price, String desc, String title, Date time, String image) {
        this.price = price;
        this.image = image;
        this.desc = desc;
        this.title = title;
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date  timestamp) {
        this.time = timestamp;
    }


}
