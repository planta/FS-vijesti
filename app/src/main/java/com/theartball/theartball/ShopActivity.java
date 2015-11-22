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
 * Created by Mario on 22.11.2015..
 */
public class ShopActivity extends ActionBarActivity {
    final ArrayList<ShopItem> shopArray = new ArrayList<ShopItem>();
    String databaseURL = "http://www.theartball.com/admin/iOS/getlistings.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        new ShopAsyncTask().execute();
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
            case R.id.action_shop:
                intent = new Intent(this, ShopActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ShopAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog = new ProgressDialog(ShopActivity.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    ShopAsyncTask.this.cancel(true);
                }
            });
        }

        @Override
        protected void onPostExecute(String result) {

            result="{ \"Android\" :"+result+"}";
            progressDialog.dismiss();
            try {
                JSONObject allItems = new JSONObject(result);
                JSONArray allItemsArray = allItems.optJSONArray("Android");
                shopArray.clear();
                for(int i=0; i<allItemsArray.length(); i++){
                    JSONObject commentItemJSON = allItemsArray.getJSONObject(i);
                    ShopItem shopItem = new ShopItem();
                    shopItem.setImageURL(commentItemJSON.optString("image"));
                    shopItem.setTitle(commentItemJSON.optString("title"));
                    shopItem.setDescription(commentItemJSON.optString("description"));
                    shopItem.setSeller(commentItemJSON.optString("seller"));
                    shopItem.setPrice(commentItemJSON.optString("price"));

                    shopArray.add(shopItem);
                }

                ListView shopList = (ListView)findViewById(R.id.shopList);
                shopList.setAdapter(new ShopAdapter(getApplicationContext(), shopArray));
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
