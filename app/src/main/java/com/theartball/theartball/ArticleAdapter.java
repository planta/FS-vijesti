package com.theartball.theartball;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mario on 5.9.2015..
 */
public class ArticleAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ArticleItem> articleList;

    public ArticleAdapter(Context c, ArrayList<ArticleItem> articleList) {
        context = c;
        this.articleList = articleList;
    }

    @Override
    public int getCount() {
        return articleList.size();
    }

    @Override
    public Object getItem(int position) {
        return articleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View archiveCellView;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            archiveCellView = new View(context);
        } else {
            archiveCellView = (View)convertView;
        }
        ArticleItem articleItem = articleList.get(position);
        archiveCellView = layoutInflater.inflate(R.layout.article_cell, null);
        ImageView image = (ImageView)archiveCellView.findViewById(R.id.articleImage);
        Picasso.with(context).load(articleItem.imageURL).placeholder(R.drawable.placeholder).resize(300,300).centerCrop().into(image);
        TextView title = (TextView)archiveCellView.findViewById(R.id.articleTitle);
        title.setText(articleItem.title);
        TextView subtitle = (TextView)archiveCellView.findViewById(R.id.articleSubtitle);
        subtitle.setText("By: " + articleItem.author);

        return archiveCellView;
    }
}
