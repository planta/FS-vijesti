package com.theartball.theartball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by MarioP on 31/08/15.
 */
public class NewsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NewsItem> listaVijesti;

    public NewsAdapter(Context c, ArrayList<NewsItem> newsList) {
        context = c;
        listaVijesti = newsList;
    }

    @Override
    public int getCount() {
        return listaVijesti.size();
    }

    @Override
    public Object getItem(int position) {
        return listaVijesti.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridCellView;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            gridCellView = new View(context);
        } else {
            gridCellView = (View)convertView;
        }
        NewsItem newsItem = listaVijesti.get(position);
        Boolean important=newsItem.important;

        gridCellView = layoutInflater.inflate(important?R.layout.big_news_cell:R.layout.small_news_cell, null);
        TextView title;
        ImageView newsImage;
        ImageView playIcon;
        if(important) {
            title = (TextView) gridCellView.findViewById(R.id.newsTitleBig);
            newsImage = (ImageView) gridCellView.findViewById(R.id.newsImageBig);
            playIcon = (ImageView)gridCellView.findViewById(R.id.playiconBig);
        }else {
            title = (TextView) gridCellView.findViewById(R.id.newsTitle);
            newsImage = (ImageView) gridCellView.findViewById(R.id.newsImage);
            playIcon = (ImageView)gridCellView.findViewById(R.id.playicon);
        }
        title.setText(newsItem.title);
        if(important)
            Picasso.with(context).load(newsItem.imageURL).placeholder(R.drawable.placeholder).resize(500,500).centerCrop().into(newsImage);
        else
            Picasso.with(context).load(newsItem.imageURL).placeholder(R.drawable.placeholder).resize(300,300).centerCrop().into(newsImage);
        playIcon.setImageResource(R.drawable.player);
        if(newsItem.category.equals("Videos")){
            playIcon.setVisibility(View.VISIBLE);
        }
        return gridCellView;
    }
}
