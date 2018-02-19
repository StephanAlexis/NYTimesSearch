package com.mbdshaiti.stephan.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Stephan on 2/18/2018.
 */

public class Article {
    String webUrl;
    String headline;
    String thumbnail;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia=jsonObject.getJSONArray("multimedia");
            if(multimedia.length()>0) {
                JSONObject jsonMultimedia=multimedia.getJSONObject(0);
                this.thumbnail = "http://www.nytimes.com/"+jsonMultimedia.getString("url");
            }
            else
                this.thumbnail="";
        }
        catch (JSONException e)
        {

        }
    }
    public static ArrayList<Article> fromJSONArray (JSONArray array)
    {
        ArrayList<Article> results=new ArrayList<>();
        for(int x=0;x< array.length();x++)
        {
            try {
                results.add(new Article(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
