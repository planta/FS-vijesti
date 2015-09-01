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
        NewsItem newsItem=listaVijesti.get(position);
        gridCellView = layoutInflater.inflate(R.layout.small_news_cell, null);
        TextView title = (TextView)gridCellView.findViewById(R.id.newsTitle);
        title.setText(newsItem.title);
        ImageView newsImage=(ImageView)gridCellView.findViewById(R.id.newsImage);
        newsImage.setImageResource(R.drawable.placeholder);
        ImageView playIcon=(ImageView)gridCellView.findViewById(R.id.playicon);
        playIcon.setImageResource(R.drawable.player);
        if(newsItem.category.equals("Videos")){
            playIcon.setVisibility(View.VISIBLE);
        }
        new ImageDownloadTask(newsImage).execute(newsItem.imageURL);
        return gridCellView;
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

    private class ImageDownloadTask extends AsyncTask<String, String, Bitmap>{


        ImageView tempImageView;

        public ImageDownloadTask(ImageView imageView){
            tempImageView=imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap imgBitmap=null;
            try {
                URL imageLink=new URL(params[0]);
                InputStream in = imageLink.openStream();
                imgBitmap=BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return imgBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            tempImageView.setImageBitmap(bitmap);
        }
    }
}
