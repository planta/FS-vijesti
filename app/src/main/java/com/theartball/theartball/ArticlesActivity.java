package com.theartball.theartball;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
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

/**
 * Created by Mario on 30.8.2015..
 */
public class ArticlesActivity extends ActionBarActivity {
    final ArrayList<ArticleItem> articlesArray = new ArrayList<ArticleItem>();
    String databaseURL="http://www.theartball.com/admin/iOS/getarticles.php";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final ArticleAdapter adapter = new ArticleAdapter(getApplicationContext(), articlesArray);
        final ListView articleList = (ListView)findViewById(R.id.articleList);
        articleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArticleItem articleItem = articlesArray.get(position);

                Intent intent;
                intent = new Intent(ArticlesActivity.this, ReadArticleActivity.class);

                intent.putExtra("newsTitle", articleItem.title);
                intent.putExtra("newsContent", articleItem.content);
                intent.putExtra("newsDate", articleItem.date);
                intent.putExtra("newsAuthor", articleItem.author);
                intent.putExtra("ImageURL",articleItem.imageURL);
                startActivity(intent);
            }
        });

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 11, 120, 228)));

        new ArticleAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    class ArticleAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog = new ProgressDialog(ArticlesActivity.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    ArticleAsyncTask.this.cancel(true);
                }
            });
        }

        @Override
        protected void onPostExecute(String result) {

            result="{ \"Android\" :"+result+"}";
            progressDialog.hide();
            try {
                JSONObject allArticles = new JSONObject(result);
                JSONArray allArticlesArray = allArticles.optJSONArray("Android");
                articlesArray.clear();
                for(int i=0;i<allArticlesArray.length();i++){
                    JSONObject articleItemJSON = allArticlesArray.getJSONObject(i);
                    ArticleItem articleItem = new ArticleItem();
                    articleItem.setTitle(articleItemJSON.optString("title"));
                    articleItem.setContent(articleItemJSON.optString("content"));
                    articleItem.setImageURL(articleItemJSON.optString("image"));
                    articleItem.setAuthor(articleItemJSON.optString("author"));
                    articleItem.setDate(articleItemJSON.optString("date"));

                    articlesArray.add(articleItem);
                }

                ListView articleList = (ListView)findViewById(R.id.articleList);
                articleList.setAdapter(new ArticleAdapter(getApplicationContext(), articlesArray));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL newsUrl = new URL(databaseURL);
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
