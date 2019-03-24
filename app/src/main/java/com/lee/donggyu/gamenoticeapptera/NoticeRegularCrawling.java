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


    Context context = this;

    NoticeService noticeService = new NoticeService();

    // contains all notice titles
    List<String> noticeTitles = new ArrayList<>();
    // contains all notice urls
    List<String> noticeURLs = new ArrayList<>();


    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi ;

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

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            // Intent intent = new Intent(CrawlingService.this, MainActivity.class);


            System.out.println("백그라운드에서 타이틀을 가져옵니다");
            WebCrawling wc = new WebCrawling();
            // TODO 인터넷 연결이 안되었을때 에러 뜨는데.. 그냥 원래 화면 유지하도록 exception처리
            wc.execute();

        }
    };


    public class ServiceThread extends Thread{
        Handler handler;
        boolean isRun = true;

        public ServiceThread(Handler handler){
            this.handler = handler;
        }

        public void stopForever(){
            synchronized (this) {
                this.isRun = false;
            }
        }

        public void run(){
            //반복적으로 수행할 작업을 한다.
            while(isRun){
                handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
                try{
                    Thread.sleep(10000); //10초씩 쉰다.
                }catch (Exception e) {}
            }
        }
    }




    private class WebCrawling extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // get html data from URL using "Jsoup" library
        @Override
        protected Void doInBackground(Void... params) {

            noticeTitles = noticeService.getNoticeTitles();
            noticeURLs = noticeService.getNoticeURLs();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(this.getClass().getName(), "starting onPostExeute");


            // TODO
            // 로직 : 맨 처음 타이트을 저장해둔다
            // 가져올 떄마다 저장된 타이틀과 비교
            // 일치하면 업뎃 아니니까 그냥 안띄움
            // 안 일치하면 업뎃으로, 알려준다


            // TODO
            // if 앱이 실행된 상태 -> showNoticeTitleOnScreen
            // else push alarm
            // send result data - notice title to showNoticeTitleOnScreen method
            // showNoticeTitleOnScreen();

            // String list를 하나씩 돌면서 안에 "공지" 단어가 있으면 그 라인을 출력

            // TODO : 무료 버전일 경우 여기에 광고 붙이기 유료일 경우는 안붙이기
            // 공지가 여러개 있으면 그거 다 보여줄 것인가? ㅇㅇ


//            String savedFirstTitle = getFirstNoticeTitle();
//
//            if (savedFirstTitle == "") {
//                System.out.println("saved Title is not existed");
//
//                saveFirstNoticeTitle();
//
//            } else {
//
//            }

            Intent intent = new Intent(NoticeRegularCrawling.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(NoticeRegularCrawling.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);


                if (getFirstNoticeTitle().equals("NoSavedTitle")) {

                    System.out.println("saved Title is not existed");

                    Notifi = new Notification.Builder(getApplicationContext())
                            .setContentTitle("공지알림")
                            .setContentText("아직은 공지가 없네여")
                            .setSmallIcon(R.drawable.image_sample)
                            .setTicker("처음 이용감사 여기에 공지가 뜰겁니다")
                            .setContentIntent(pendingIntent)
                            .build();

                    //소리추가
                    Notifi.defaults = Notification.DEFAULT_SOUND;

                    //알림 소리를 한번만 내도록
                    Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

                    //확인하면 자동으로 알림이 제거 되도록
                    Notifi.flags = Notification.FLAG_AUTO_CANCEL;


                    Notifi_M.notify( 777 , Notifi);

                    //토스트 띄우기
                    Toast.makeText(NoticeRegularCrawling.this, "처음이용! ", Toast.LENGTH_LONG).show();


                    saveFirstNoticeTitle();

                    System.out.println("111 : " + getFirstNoticeTitle() );


                    // 크롤링 해온게 빈 리스트일떄(아직 공지가 없을때)
                } else if (noticeTitles.isEmpty()) {
                    // 혹시모를 눌포 처리용 조건문임



                    System.out.println("homapge no Title existed");

                    System.out.println("222 : " + getFirstNoticeTitle() );

                // 크롤링 해온 데이터랑 저장된 데이터를 비교했는데 같지 않다 == 업데이트되어서 다르다
                // 만약 공지 자체가 없다면? 처음에 TryToFindButNoTitleExist 이 저장되었을거고, 앞에서 걸릴 것이다
                //
                } else if ( !(noticeTitles.get(0).contains(getFirstNoticeTitle()))) {

                    System.out.println("GM이 공지 안에 들어있습니다");

                    Notifi = new Notification.Builder(getApplicationContext())
                            .setContentTitle("공지알림")
                            .setContentText(noticeTitles.get(0))
                            .setSmallIcon(R.drawable.image_sample)
                            .setTicker("새로운 공지가 있습니다")
                            .setContentIntent(pendingIntent)
                            .build();

                    //소리추가
                    Notifi.defaults = Notification.DEFAULT_SOUND;

                    //알림 소리를 한번만 내도록
                    Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

                    //확인하면 자동으로 알림이 제거 되도록
                    Notifi.flags = Notification.FLAG_AUTO_CANCEL;


                    Notifi_M.notify( 777 , Notifi);

                    //토스트 띄우기
                    Toast.makeText(NoticeRegularCrawling.this, "공지체크 ", Toast.LENGTH_LONG).show();


                    saveFirstNoticeTitle();

                    System.out.println("333 : " + getFirstNoticeTitle() );

                }




        }
    }

    private String getFirstNoticeTitle(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        return pref.getString("key", "NoSavedTitle");
    }


    private void saveFirstNoticeTitle(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        if (noticeTitles.isEmpty()) {
            editor.putString("key", "TryToFindButNoTitleExist");

        } else {
            editor.putString("key", noticeTitles.get(0));

        }

        editor.commit();
    }


}
