package com.theartball.theartball;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ScrollView;

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
 * Created by Mario on 21.11.2015..
 */
public class CommentsActivity extends ActionBarActivity {
    final ArrayList<CommentItem> commentsArray = new ArrayList<CommentItem>();
    String databaseURL;

    String title;
    String content;
    String date;
    String category;
    String articleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Bundle articleData = getIntent().getExtras();
        articleID = articleData.getString("ID");
        title = articleData.getString("newsTitle");
        content = articleData.getString("newsContent");
        date = articleData.getString("newsDate");
        category = articleData.getString("newsCategory");

        databaseURL = "http://www.theartball.com/admin/iOS/getcomments.php?article_id=" + articleID;

        new CommentsAsyncTask().execute();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Log.d("TAG",title);
        intent.putExtra("newsTitle", title);
        intent.putExtra("newsContent", content);
        intent.putExtra("newsDate", date);
        intent.putExtra("newsCategory", category);
        intent.putExtra("ID", articleID);

        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    class CommentsAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog = new ProgressDialog(CommentsActivity.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    CommentsAsyncTask.this.cancel(true);
                }
            });
        }

        @Override
        protected void onPostExecute(String result) {

            result="{ \"Android\" :"+result+"}";
            progressDialog.dismiss();
            try {
                JSONObject allComments = new JSONObject(result);
                JSONArray allCommentsArray = allComments.optJSONArray("Android");
                commentsArray.clear();
                for(int i=0; i<allCommentsArray.length(); i++){
                    JSONObject commentItemJSON = allCommentsArray.getJSONObject(i);
                    CommentItem commentItem = new CommentItem();
                    commentItem.setAuthor(commentItemJSON.optString("author"));
                    commentItem.setDate(commentItemJSON.optString("time"));
                    commentItem.setComment(commentItemJSON.optString("comment"));

                    commentsArray.add(commentItem);
                }

                ListView commentsList = (ListView)findViewById(R.id.commentsList);
                commentsList.setAdapter(new CommentsAdapter(getApplicationContext(), commentsArray));
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
