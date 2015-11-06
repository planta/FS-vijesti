package com.theartball.theartball;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pushbots.push.Pushbots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    ArrayList<NewsItem> newsArray = new ArrayList<NewsItem>();
    String databaseURL="http://www.theartball.com/admin/iOS/getnews.php";

    String currentTab;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                currentTab = data.getStringExtra("activeTab");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Pushbots.sharedInstance().init(this);
        // Set up the action bar.
//        Pushbots.sharedInstance().debug(true);
//        Pushbots.sharedInstance().setAlias("Test");
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab allTab = actionBar.newTab().setText(getString(R.string.title_section1));
        allTab.setTabListener(this);
        ActionBar.Tab compsTab = actionBar.newTab().setText(getString(R.string.title_section2));
        compsTab.setTabListener(this);
        ActionBar.Tab videosTab = actionBar.newTab().setText(getString(R.string.title_section3));
        videosTab.setTabListener(this);
        ActionBar.Tab otherTab = actionBar.newTab().setText(getString(R.string.title_section4));
        otherTab.setTabListener(this);

        actionBar.addTab(allTab);
        actionBar.addTab(compsTab);
        actionBar.addTab(videosTab);
        actionBar.addTab(otherTab);

        if(currentTab != null) {
            switch (currentTab) {
                case "all":
                    actionBar.selectTab(allTab);
                    break;
                case "comps":
                    actionBar.selectTab(compsTab);
                    break;
                case "videos":
                    actionBar.selectTab(videosTab);
                    break;
                case "other":
                    actionBar.selectTab(otherTab);
                    break;
            }
        }

        AsymmetricGridView gridView = (AsymmetricGridView)findViewById(R.id.newsGrid);
        gridView.setRequestedColumnCount(2);
        gridView.setAllowReordering(true);
        gridView.setRequestedHorizontalSpacing(Utils.dpToPx(this, 2));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem newsItem = newsArray.get(position);
                Intent intent;
                if(newsItem.category.equals("Videos")){
                    intent = new Intent(MainActivity.this,PlayVideoActivity.class);
                    if(newsItem.content.length()>43) newsItem.content=newsItem.content.substring(0,43);
                    String videoID=newsItem.content.substring(newsItem.content.length()-11);
                    intent.putExtra("Video-ID",videoID);
                    intent.putExtra("autoplay",false);

                    if(currentTab != null) {
                        intent.putExtra("currentTab", currentTab);
                    } else {
                        intent.putExtra("currentTab", "all");
                    }

                    startActivityForResult(intent, 1);
                    MainActivity.this.overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                }
                else {
                    intent = new Intent(MainActivity.this, ReadArticleActivity.class);

                    intent.putExtra("newsTitle", newsItem.title);
                    intent.putExtra("newsContent", newsItem.content);
                    intent.putExtra("newsDate", newsItem.date);
                    intent.putExtra("newsCategory", newsItem.category);
                    intent.putExtra("ID",newsItem.id);

                    if(currentTab != null) {
                        intent.putExtra("currentTab", currentTab);
                    } else {
                        intent.putExtra("currentTab", "all");
                    }

                    startActivityForResult(intent, 1);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_refresh).setVisible(true);
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
            case R.id.action_refresh:
                new NewsAsyncTask().execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        switch (tab.getText().toString()){
            case "Comps":
                databaseURL = "http://www.theartball.com/admin/iOS/getnews.php?category=competitions";
                currentTab = "comps";
                break;
            case "Videos":
                databaseURL = "http://www.theartball.com/admin/iOS/getnews.php?category=videos";
                currentTab = "videos";
                break;
            case "All":
                databaseURL="http://www.theartball.com/admin/iOS/getnews.php";
                currentTab = "all";
                break;
            case "Other":
                databaseURL="http://www.theartball.com/admin/iOS/getnews.php?category=other";
                currentTab = "other";
                break;
        }
        new NewsAsyncTask().execute();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    class NewsAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    NewsAsyncTask.this.cancel(true);
                }
            });
        }

        @Override
        protected void onPostExecute(String result) {

            result="{ \"Android\" :"+result+"}";
            progressDialog.dismiss();
            try {
                JSONObject allNews=new JSONObject(result);
                JSONArray allNewsArray=allNews.optJSONArray("Android");
                newsArray.clear();
                for(int i=0;i<allNewsArray.length();i++){
                    JSONObject newsItemJSON=allNewsArray.getJSONObject(i);
                    NewsItem newsItem=new NewsItem();
                    newsItem.setTitle(newsItemJSON.optString("title"));
                    newsItem.setContent(newsItemJSON.optString("content"));
                    newsItem.setCategory(newsItemJSON.optString("category"));
                    newsItem.setImageURL(newsItemJSON.optString("image"));
                    newsItem.setDate(newsItemJSON.optString("date"));
                    newsItem.setId(newsItemJSON.optString("id"));

                    if(newsItemJSON.optString("important").equals("1") || i==0){
                        newsItem.setImportant(true);
                    } else {
                        newsItem.setImportant(false);
                    }

                    newsArray.add(newsItem);

                }
                newsArray=gridAlgoritam(newsArray);
                int brNotImportant=0;
                int lastNotImportantIndex=0;
                for(int i=0;i<newsArray.size();i++){
                    if(!newsArray.get(i).important) {
                        brNotImportant++;
                        lastNotImportantIndex=i;
                    }
                }
                if(brNotImportant%2!=0) newsArray.remove(lastNotImportantIndex);

                AsymmetricGridView gridView = (AsymmetricGridView)findViewById(R.id.newsGrid);

                AsymmetricGridViewAdapter asymmetricAdapter =
                        new AsymmetricGridViewAdapter<>(MainActivity.this, gridView, new NewsAdapter(getApplicationContext(), newsArray));
                gridView.setAdapter(asymmetricAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private ArrayList<NewsItem> gridAlgoritam(ArrayList<NewsItem> vesti){
            int lastImportantIndex=0;
            for(int i=1;i<vesti.size();i++){
                if(vesti.get(i).important){
                    if((i-lastImportantIndex)%2==0){
                        ArrayList<NewsItem> tmpArray=vesti;
                        NewsItem previousNews=vesti.get(i-1);
                        NewsItem importantNews=vesti.get(i);

                        tmpArray.set(i,previousNews);
                        tmpArray.set(i-1,importantNews);

                        vesti=tmpArray;
                        lastImportantIndex=i-1;
                    }
                    else
                        lastImportantIndex=i;
                }
            }
        return vesti;
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