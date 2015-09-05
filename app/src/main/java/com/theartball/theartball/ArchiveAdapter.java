package com.theartball.theartball;

import android.content.Context;
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
public class ArchiveAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NewsItem> arhivaVijesti;

    public ArchiveAdapter(Context c, ArrayList<NewsItem> newsList) {
        context = c;
        arhivaVijesti = newsList;
    }

    @Override
    public int getCount() {
        return arhivaVijesti.size();
    }

    @Override
    public Object getItem(int position) {
        return arhivaVijesti.get(position);
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
        NewsItem newsItem = arhivaVijesti.get(position);
        archiveCellView = layoutInflater.inflate(R.layout.archive_news_cell, null);
        TextView title = (TextView)archiveCellView.findViewById(R.id.archiveTitle);
        title.setText(newsItem.title);
        TextView subtitle = (TextView)archiveCellView.findViewById(R.id.archiveSubtitle);
        subtitle.setText(newsItem.date + " - " + newsItem.category);

        return archiveCellView;
    }
}
