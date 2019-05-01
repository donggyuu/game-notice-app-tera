package com.lee.donggyu.gamenoticeapptera;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by donggyu on 2019/03/23.
 */

public class NoticeService {

    // images showing showing on screen
    Integer[] imageId = {
            R.drawable.image_sample,
            R.drawable.image_sample,
            R.drawable.image_sample,
            R.drawable.image_sample,
            R.drawable.image_sample,
            R.drawable.image_sample,
            R.drawable.image_sample
    };

    // connected to game site and return notice titiles
    public List<String> getNoticeTitles() {

        String noticeURL = "http://tera.nexon.com/news/noticeTera/list.aspx";
        Elements noticeElements;                   // original elements getting from html
        List<String> noticeTitles = new ArrayList<>();  // original elements -> change to List<String>

        try {
            // get html document from site
            Document doc = getUrlConnection(noticeURL);

            // get titles only from html documents
            noticeElements = doc.select(".list_title");

        } catch (IOException e) {
            noticeElements = null;
        }

        if (noticeElements == null) {

            return noticeTitles;
        }

        // show 15 notice titles
        for (int i = 0; i < 15; i++) {
            // protect fron null - exception
            if (noticeElements.size() > i) {
                noticeTitles.add(elementToString(noticeElements).get(i));
            }
        }

        return noticeTitles;
    }

    // connected to game site and return notice URLs
    public List<String> getNoticeURLs() {

        String noticeURL = "http://tera.nexon.com/news/noticeTera/list.aspx";
        Elements noticeElements;                      // original elements getting from html
        List<String> noticeURLs = new ArrayList<>();  // original elements -> change to List<String>

        try {
            // get html document from site
            Document doc = getUrlConnection(noticeURL);

            // get titles only from html documents
            // noticeElements = doc.select(".list_title");

            Elements orgElements = doc.select(".list_common");
            noticeElements  = orgElements.select("a");

        } catch (IOException e) {
            noticeElements = null;
        }

        // protect fron null exeption - if null then return ""
        if (noticeElements == null) {

            return noticeURLs;
        }

        // show 15 notice titles
        for (int i = 0; i < 20; i++) {
            // protect fron null - exception
            if (noticeElements.size() > i) {
                noticeURLs.add(noticeElements.get(i).attr("abs:href"));
            }
        }

        return noticeURLs;
    }


    private Document getUrlConnection(String url) throws IOException {

        // SET url what I want - notice board page of game site
        String connUrl = url;
        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

        // make connection using Jsoup
        Connection conn = Jsoup
                .connect(connUrl)
                .header("Content-Type", "application/json;charset=UTF-8")
                .userAgent(USER_AGENT)
                .method(Connection.Method.GET)
                .ignoreContentType(true);

        // get HTML data
        // if wifi(lte) disconnected, application stop at this point
        Document doc = conn.get();
        Log.i(this.getClass().getName(), "connection done, got HTML document");

        return doc;
    }

    /**
     * change Elements to List<String>
     * @param elements
     * @return
     */
    private List<String> elementToString(Elements elements) {

        List<String> elementStringList = new ArrayList<String>();
        for (Element element : elements) {
            elementStringList.add(element.text());
        }
        return elementStringList;
    }

}
