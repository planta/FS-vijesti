package com.theartball.theartball;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
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
    String author;
    String currentTab;
    String articleID;

    ScrollView scrollView;
    String imageURL;

    String goHome;

    TextView titleTextView;
    WebView contentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 11, 120, 228)));
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        Bundle articleData = getIntent().getExtras();
        title = articleData.getString("newsTitle");
        content = articleData.getString("newsContent");
        date = articleData.getString("newsDate");
        category = articleData.getString("newsCategory");
        author = articleData.getString("newsAuthor");
        articleID = articleData.getString("ID");

        goHome = articleData.getString("goHome");

        imageURL = articleData.getString("ImageURL");

        currentTab = articleData.getString("currentTab");
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setText(title);
        TextView dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText("Date added: " + date);
        TextView categoryTextView = (TextView) findViewById(R.id.category);

        if(category == null) {
            categoryTextView.setText("Author: " + author);
            ImageView authorImage=(ImageView)findViewById(R.id.authorImage);
            authorImage.setVisibility(View.VISIBLE);
            Picasso.with(ReadArticleActivity.this).load(imageURL).placeholder(R.drawable.placeholder).resize(500, 500).centerCrop().into(authorImage);
        } else {
            categoryTextView.setText("Category: " + category);
        }

        contentTextView = (WebView) findViewById(R.id.content);

        String[] links = extractLinks(content);
        content = addTagsToLinks(content, links);
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

        contentTextView.loadDataWithBaseURL("",content,"text/html","UTF-8","");
        contentTextView.setBackgroundColor(ContextCompat.getColor(ReadArticleActivity.this, R.color.lighterGrey));
    }

    public void resetTitleAndContent(String newTitle, String newContent){
        String[] links = extractLinks(newContent);
        newContent = addTagsToLinks(newContent, links);
        contentTextView.loadDataWithBaseURL("",newContent,"text/html","UTF-8","");
        titleTextView.setText(newTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(category!=null) {
            menu.findItem(R.id.action_comments).setVisible(true);
            menu.findItem(R.id.action_refresh).setVisible(true);
        }
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
            case android.R.id.home:
                finish();
                return true;
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
            case R.id.action_comments:
                intent = new Intent(this, CommentsActivity.class);
                intent.putExtra("newsTitle", title);
                intent.putExtra("newsContent", content);
                intent.putExtra("newsDate", date);
                intent.putExtra("newsCategory", category);
                intent.putExtra("newsAuthor", author);
                intent.putExtra("currentTab", currentTab);
                intent.putExtra("ID", articleID);

                if(category == null) {
                    intent.putExtra("isArticle", "true");
                } else {
                    intent.putExtra("isArticle", "false");
                }

                startActivity(intent);
                return true;
            case R.id.action_refresh:
                new getTitleAndContentTask().execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("currentTab", currentTab);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
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
                text = text.replace(link, String.format("<br><br><a href='%s'><img src='%s' width='100%%'  /></a><p align='center' style='color:#777;font-size:7pt;'>+Click on image to see in fullscreen</p><br><br>", link, link));
            }  else if(link.contains(".youtube.com") || link.contains("youtu.be")){
                if(link.length()>43) link=link.substring(0,43);
                String videoID=link.substring(link.length()-11);
//                text = text.replace(link, String.format("<br><iframe src='https://www.youtube.com/embed/%s' width='100%%' height='%d' frameborder='0'></iframe><br>",videoID,(int)getResources().getDimension(R.dimen.embed_video),link));
                  text = text.replace(link, String.format("<br><br><div style='position:relative; top:0; left:0;'><a href='%s'><img src='http://img.youtube.com/vi/%s/0.jpg' style='position: relative; top: 0; left: 0;' width='100%%'/><img src='http://www.theartball.com/images/ytplay.jpg' style='position: absolute; left: 50%%; top: 50%%; margin: -40 0 0 -40;'/></a></div><br><br>",link,videoID));
            } else {
                int startUrl=text.indexOf(link);
                if(startUrl-1>=0)
                    if(text.charAt(startUrl-1)=='@'){

                    }
                else
                    text = text.replace(link, String.format("<br><a href='%s'> %s </a><br>", link, link));
            }
        }

        return text;
    }

    private class getTitleAndContentTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog = new ProgressDialog(ReadArticleActivity.this);
        InputStream inputStream = null;
        String result = "";

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    getTitleAndContentTask.this.cancel(true);
                }
            });
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
//            s="{ \"Android\" :"+s+"}";
            try {
                JSONObject newArticle = new JSONObject(s);
                String newTitle=newArticle.optString("title");
                String newContent=newArticle.optString("content");
                resetTitleAndContent(newTitle,newContent);
            } catch (JSONException e){
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL newsUrl = new URL("http://theartball.com/admin/iOS/get-single-article.php?id=" + articleID);
                URLConnection urlConnection = newsUrl.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = null;

                StringBuilder stringBuilder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                result = stringBuilder.toString();
            } catch (UnsupportedEncodingException e1) {
//                Log.e("UnsupportedEncodingException", e1.toString());
                e1.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }

            return result;
        }
    }
}


