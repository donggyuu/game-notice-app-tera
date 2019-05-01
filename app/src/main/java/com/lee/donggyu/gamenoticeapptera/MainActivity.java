package com.lee.donggyu.gamenoticeapptera;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;


/**
 *  Set advertisement and execute crawling on background
 *
 */
public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    NoticeService noticeService = new NoticeService();

    // contains all notice titles
    List<String> noticeTitles = new ArrayList<>();
    // contains all notice urls
    List<String> noticeURLs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // for ad
        MobileAds.initialize(this,
                "ca-app-pub-9968881845594178~2464054961");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        // cannot guarantee that Ads always be loaded
        mAdView.loadAd(adRequest);

        // setting option for ActionBar
        // getSupportActionBar().setTitle("ACTIONBAR");
        // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // hideActionBar();
        // 추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_la);

        // get notice title from game site
        WebCrawling wc = new WebCrawling();
        Log.i(this.getClass().getName(), "Now staring web crawling and get info from each site");
        wc.execute();

        // Keep crawling even though application is being ended
        Intent intent = new Intent(MainActivity.this, NoticeRegularCrawling.class);
        startService(intent);
        System.out.println("백그라운드 작업 시작 ");
    }


    /**
     * get html data from url by asynchronous.
     * AsyncTask is widely used handling asynchronous taks in android.
     *
     */
    private class WebCrawling extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // get html data from URL using "Jsoup" library
        @Override
        protected Void doInBackground(Void... params) {

            noticeTitles = noticeService.getNoticeTitles();
            if (noticeTitles.size() == 0) {
                noticeTitles.add("인터넷 연결 상태를 확인해주세요");
                noticeTitles.add("인터넷 연결 상태를 확인해주세요");
                noticeTitles.add("인터넷 연결 상태를 확인해주세요");
                noticeTitles.add("인터넷 연결 상태를 확인해주세요");
                noticeTitles.add("인터넷 연결 상태를 확인해주세요");
            }

            noticeURLs = noticeService.getNoticeURLs();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(this.getClass().getName(), "starting onPostExeute");

            showNoticeTitleOnScreen();
        }
    }

    /**
     * get result data from WebCrawling class and show it on screen.
     *
     */
    private void showNoticeTitleOnScreen() {

        NoticeList adapter = new NoticeList(MainActivity.this, noticeTitles, noticeService.imageId);
        ListView list;

        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        // Toast message when click list in screen
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(noticeURLs.get(position)));

                startActivity(intent);
            }
        });
    }

}