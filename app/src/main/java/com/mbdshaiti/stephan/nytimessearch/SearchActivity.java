package com.mbdshaiti.stephan.nytimessearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mbdshaiti.stephan.nytimessearch.adapters.ArticleArrayAdapter;
import com.mbdshaiti.stephan.nytimessearch.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.etQuery) EditText etQuery;
    @BindView(R.id.gvResults) GridView gvResults;
    @BindView(R.id.btnSearch) Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        articles=new ArrayList<>();
        adapter=new ArticleArrayAdapter(this,articles);
        gvResults.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        String query=etQuery.getText().toString();
        AsyncHttpClient client=new AsyncHttpClient();
        String url="https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params=new RequestParams();
        params.put("api-key","b9a8d31eed5a44148f8f359554e677dc");
        params.put("page",0);
        params.put("q",query);
        client.get(url,params,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONArray articleJSONResults=null;
                try{
                    articleJSONResults=response.getJSONObject("response").getJSONArray("docs");

                    adapter.addAll(Article.fromJSONArray(articleJSONResults));

                    Log.d("DEBUG",adapter.toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

        });
    }
}
