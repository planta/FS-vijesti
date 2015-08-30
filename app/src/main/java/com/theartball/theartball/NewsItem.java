package com.theartball.theartball;

/**
 * Created by Uki on 8/30/15.
 */
public class NewsItem {

    String title;
    String content;
    String category;
    String imageURL;
    String date;
    boolean important;

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImportant(boolean important){
        this.important=important;
    }

}
