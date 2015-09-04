package com.theartball.theartball;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Uki on 9/1/15.
 */
public class ReadArticleActivity extends ActionBarActivity {


    String title;
    String date;
    String content;
    String category;
    int imgWidth;
    ScrollView scrollView;
    TextView contentTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 11, 120, 228)));
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        Bundle articleData = getIntent().getExtras();
        title = articleData.getString("newsTitle");
        content = articleData.getString("newsContent");
        date = articleData.getString("newsDate");
        category = articleData.getString("newsCategory");

        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setText(title);
        TextView dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText("Date added: " + date);
        TextView categoryTextView = (TextView) findViewById(R.id.category);
        categoryTextView.setText("Category: " + category);
//        contentTextView=(TextView)findViewById(R.id.content);
        WebView contentTextView = (WebView) findViewById(R.id.content);


        String[] links = extractLinks(content);
        content = addTagsToLinks(content, links);
//        Spanned spanned = Html.fromHtml(content, this, null);
//        contentTextView.setText(spanned);
//        contentTextView.setText(Html.fromHtml(content));
//        contentTextView.setWebViewClient(new WebChromeClient());
        contentTextView.getSettings().setJavaScriptEnabled(true);
        contentTextView.setWebChromeClient(new WebChromeClient() {

        });
        contentTextView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("youtube") || url.contains("youtu.be")) {
                    Intent intent = new Intent(ReadArticleActivity.this, PlayVideoActivity.class);
                    intent.putExtra("Video-ID", url.substring(url.length() - 11));
                    intent.putExtra("autoplay", true);
                    startActivity(intent);
                    ReadArticleActivity.this.overridePendingTransition(R.anim.slide_up,R.anim.no_change);
                } else if(url.contains(".jpg") || url.contains(".png")){
                    Intent intent=new Intent(ReadArticleActivity.this, FullScreenImageActivity.class);
                    intent.putExtra("imageURL",url);
                    startActivity(intent);
                }
                else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }

            return true;
            }
        });

//        contentTextView.loadData(content, "text/html", null);
        contentTextView.loadDataWithBaseURL("",content,"text/html","UTF-8","");
//        contentTextView.setBackgroundColor(0x00000000);
        contentTextView.setBackgroundColor(ContextCompat.getColor(ReadArticleActivity.this, R.color.lighterGrey));
//
//        contentTextView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_archive:
                intent = new Intent(this, ArchiveActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_articles:
                intent = new Intent(this, ArticlesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String[] extractLinks(String text) {
        List<String> links = new ArrayList<>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            links.add(url);
        }

        return links.toArray(new String[links.size()]);
    }

    public String addTagsToLinks(String text, String[] links) {
        text="<span style='font-size:10pt; color:#444444'>"+text+"</span>";
        for (String link : links) {
            if (link.contains(".jpg") || link.contains(".png")) {
                text = text.replace(link, String.format("<br><a href='%s'><img src='%s' width='100%%'  /></a><br>", link, link));
            }  else if(link.contains(".youtube.com") || link.contains("youtu.be")){
                if(link.length()>43) link=link.substring(0,43);
                String videoID=link.substring(link.length()-11);
//                text = text.replace(link, String.format("<br><iframe src='https://www.youtube.com/embed/%s' width='100%%' height='%d' frameborder='0'></iframe><br>",videoID,(int)getResources().getDimension(R.dimen.embed_video),link));
                  text = text.replace(link, String.format("<br><div style='position:relative; top:0; left:0;'><a href='%s'><img src='http://img.youtube.com/vi/%s/0.jpg' style='position: relative; top: 0; left: 0;' width='100%%'/><img src='http://www.theartball.com/images/ytplayericon.png' style='position: absolute; left: 50%%; top: 50%%; margin: -40 0 0 -40;'/></a></div><br>",link,videoID));
            } else {
                text = text.replace(link, String.format("<br><a href='%s'> %s </a><br>", link, link));
            }

        }
        return text;

    }


}


