package com.theartball.theartball;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

/**
 * Created by Uki on 8/30/15.
 */
public class NewsItem  implements AsymmetricItem{

    String id;
    String title;
    String content;
    String category;
    String imageURL;
    String date;
    boolean important;
    int columnSpan;

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

    public void setId(String id){
        this.id=id;
    }

    public void setImportant(boolean important){
        this.important=important;
    }

    @Override
    public int getColumnSpan() {
        return important?2:1;
    }

    public void setColumnSpan(int columnSpan){
        this.columnSpan=columnSpan;
    }

    @Override
    public int getRowSpan() {
        return important?2:1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(columnSpan);
    }


}
