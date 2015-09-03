package com.theartball.theartball;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * Created by Mario on 30.8.2015..
 */
public class AboutActivity extends ActionBarActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 11, 120, 228)));

        WebView aboutWebView=(WebView)findViewById(R.id.aboutText);
        String aboutText="<span style='color:#ffffff;font-size:10pt;'><img width='100%%' src='http://theartball.com/images/logo-vodoravno.png' /> " +
                "The Artball is application by freestylers for freestylers! <br> <br>It will help you stay in touch with the latest news and videos from the Freestyle Football world." +
                "<br> You will also be able to read great articles about trainings, equipment, freestyle thoughts and freestyle life in general. " +
                "All articles are written by freestylers themselves. <br><br> Application is developed by: <br><br> Uros Zivaljevic from Serbia <br> Mario Plantosar from Croatia <br><br>" +
                " If you are interested in helping The Artball by writing news, or you have article that you want to be published in application, please contact us on: " +
                "<br><br> contact@theartball.com <br><br> Follow us on other social networks: <div style='width:65%%; margin:0 auto'><br><br><a href='https://instagram.com/theartball'>" +
                "<img width='30%%' src='http://theartball.com/images/instagram-icon.png'></a><a href='https://www.facebook.com/theartball'>" +
                "<img width='30%%' src='http://theartball.com/images/facebook-icon.png'/></a><a href='https://twitter.com/TheArtball'>" +
                "<img width='30%%' src='http://theartball.com/images/twitter-icon.png'></a></div> <br><br><br>Open Source Libraries: <br><br><u>Picasso</u><br><br><div style='background-color:#6DAEEF;'><br>" +
                "Copyright 2013 Square, Inc.<br><br>\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at<br><br>\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0 <br><br>\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.<br><br></div><br><br></span>";

        aboutWebView.loadDataWithBaseURL("",aboutText,"text/html","UTF-8","");
        aboutWebView.setBackgroundColor(ContextCompat.getColor(this,R.color.mainBlue));
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
}
