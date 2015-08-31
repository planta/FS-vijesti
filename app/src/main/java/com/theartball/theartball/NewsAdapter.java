package com.theartball.theartball;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
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
        View gridView;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            gridView = new View(context);
            gridView = layoutInflater.inflate(R.layout.small_news_cell, null);
            TextView title = (TextView)gridView.findViewById(R.id.newsTitle);
            title.setText(listaVijesti.get(position).title.toString());
        } else {
            gridView = (View)convertView;
        }

        return gridView;
    }

    public static Drawable getImageFromWeb(String url) {
        try {
            InputStream inputStream = (InputStream) new URL(url).getContent();
            Drawable drawable = Drawable.createFromStream(inputStream, "src name");
            return drawable;
        } catch (Exception e) {
            return null;
        }
    }
}
