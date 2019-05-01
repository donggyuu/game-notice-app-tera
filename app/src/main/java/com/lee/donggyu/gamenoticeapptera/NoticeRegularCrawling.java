package com.lee.donggyu.gamenoticeapptera;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NoticeRegularCrawling extends Service {

    NoticeService noticeService = new NoticeService();
    ServiceThread thread;

    // contains all notice titles
    List<String> noticeTitles = new ArrayList<>();
    // contains all notice urls
    List<String> noticeURLs = new ArrayList<>();

    // for notification message
    NotificationManager Notifi_M;
    Notification Notifi;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }

    // Job when service ended
    public void onDestroy() {
        thread.stopForever();
        thread = null;  // make garbage for fast retrieved
    }


    // Job - main service
    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            Log.i(this.getClass().getName(), "start getting notice titles regularly");

            // TODO exception handling when web connetion is not good
            // is already handled in NoticeService class?
            WebCrawling wc = new WebCrawling();
            wc.execute();
        }
    }


    // thread class for execute WebCrawling in thread
    public class ServiceThread extends Thread {
        Handler handler;
        boolean isRun = true;

        public ServiceThread(Handler handler) {
            this.handler = handler;
        }

        public void stopForever() {
            synchronized (this) {
                this.isRun = false;
            }
        }

        public void run() {
            // run Job which should be run regularly
            while (isRun) {
                // send mesage to handler whihc is in thread
                handler.sendEmptyMessage(0);
                try {
                    // TODO adjust tmies - now is crawling every 10 min
                    Thread.sleep(600000);
                } catch (Exception e) {
                }
            }
        }

    }


    private class WebCrawling extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // get notice titles and URLs from site using "Jsoup" library
        @Override
        protected Void doInBackground(Void... params) {

            noticeTitles = noticeService.getNoticeTitles();
            noticeURLs = noticeService.getNoticeURLs();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(this.getClass().getName(), "start onPostExecute for crawling");

            Intent intent = new Intent(NoticeRegularCrawling.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(NoticeRegularCrawling.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // user run first this app so there is no saved title
            if (getFirstNoticeTitle().equals("NoSavedTitle")) {
            // if (true) {

                Log.i(this.getClass().getName(), "first run this application. So print welcome message");

                Notifi = new Notification.Builder(getApplicationContext())
                        .setContentTitle("테라 공지사항")
                        .setContentText("새로운 공지가 뜨면 알려드립니다")
                        .setSmallIcon(R.drawable.image_sample)
                        .setTicker("새로운 공지가 떴어요")
                        .setContentIntent(pendingIntent)
                        .build();

                // Sound
                Notifi.defaults = Notification.DEFAULT_SOUND;

                // Notification alarm jus oneces
                Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

                // delete notification alarm automatically after user confirmed
                Notifi.flags = Notification.FLAG_AUTO_CANCEL;

                Notifi_M.notify(777, Notifi);

                // toast message
                Toast.makeText(NoticeRegularCrawling.this, "새로운 공지가 뜨면 알려드립니다", Toast.LENGTH_LONG).show();

                saveFirstNoticeTitle();

                // no official notice from site
            } else if (noticeTitles.isEmpty()) {

                Log.i(this.getClass().getName(), "there is no official notice message from game site");

                // new official notice message is updated
            } else if (!(noticeTitles.get(0).contains(getFirstNoticeTitle()))) {

                Log.i(this.getClass().getName(), "there is new notice message from game site");

                Notifi = new Notification.Builder(getApplicationContext())
                        .setContentTitle("테라 공지사항")
                        .setContentText(noticeTitles.get(0))
                        .setSmallIcon(R.drawable.image_sample)
                        .setTicker("새로운 공지가 떴어요")
                        .setContentIntent(pendingIntent)
                        .build();

                Notifi.defaults = Notification.DEFAULT_SOUND;

                Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

                Notifi.flags = Notification.FLAG_AUTO_CANCEL;

                Notifi_M.notify(777, Notifi);

                Toast.makeText(NoticeRegularCrawling.this, "새로운 공지가 떴어요", Toast.LENGTH_LONG).show();

                saveFirstNoticeTitle();
            }

        }
    }

    // get saved first notice title
    private String getFirstNoticeTitle() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString("key", "NoSavedTitle");
    }

    // save first notice title getting from game site
    private void saveFirstNoticeTitle() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        // there is no official notice in game site
        if (noticeTitles.isEmpty()) {
            editor.putString("key", "Official notice is not found");
        } else {
            editor.putString("key", noticeTitles.get(0));
        }

        editor.commit();
    }

}