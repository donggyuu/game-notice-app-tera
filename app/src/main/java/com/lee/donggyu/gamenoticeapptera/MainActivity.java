package com.lee.donggyu.gamenoticeapptera;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    NoticeService noticeService = new NoticeService();

    // contains all notice titles
    List<String> noticeTitles = new ArrayList<>();
    // contains all notice urls
    List<String> noticeURLs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-----------------------------------
        // action bar setting
        //-----------------------------------

        getSupportActionBar().setTitle("ACTIONBAR");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //hideActionBar();

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_la);


        WebCrawling wc = new WebCrawling();
        Log.i(this.getClass().getName(), "Now staring web crawling and get info from each site");

        // TODO 인터넷 연결이 안되었을때 에러 뜨는데.. 그냥 원래 화면 유지하도록 exception처리
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
    // TODO : make as independent class
    // TODO : add timer class here - crawing every one hour
    private class WebCrawling extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // get html data from URL using "Jsoup" library
        @Override
        protected Void doInBackground(Void... params) {

            // noticeTitles = noticeGames.getShowOkTitles(getStringArrayPref(buttonStatusKey));

            // getOKtitle이 null인 경우에 대해서 처리해줘야.
            noticeTitles = noticeService.getNoticeTitles();
            noticeURLs = noticeService.getNoticeURLs();

            // noticeURLs = noticeGames.getShowOkUrls(service.getStringArrayPref(getApplicationContext(), buttonStatusKey));

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

            // TODO should fix -> not work in NoticeURLsFIFA
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Toast.makeText(MainActivity.this, "You Clicked at " +web.get(position), Toast.LENGTH_SHORT).show();
                // Toast.makeText(MainActivity.this, "You Clicked at " +web.get(position), Toast.LENGTH_SHORT).show();

                System.out.println("clicked position : " + position);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(noticeURLs.get(position)));

                System.out.println("실행하려는 링크 일람 : " + Uri.parse(noticeURLs.get(position)));

                System.out.println("이 포지션의 링크릴 실행 : " + position);

                startActivity(intent);
            }
        });
    }

}