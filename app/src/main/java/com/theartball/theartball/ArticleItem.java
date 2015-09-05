package com.theartball.theartball;

/**
 * Created by Mario on 5.9.2015..
 */
public class ArticleItem {
    String title;
    String content;
    String author;
    String imageURL;
    String date;

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setAuthor(String author) {
        this.author = author;
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
}
