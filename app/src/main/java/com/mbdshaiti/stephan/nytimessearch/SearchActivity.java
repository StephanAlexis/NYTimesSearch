package com.mbdshaiti.stephan.nytimessearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mbdshaiti.stephan.nytimessearch.adapters.ArticleArrayAdapter;
import com.mbdshaiti.stephan.nytimessearch.fragments.SettingsDialogFragment;
import com.mbdshaiti.stephan.nytimessearch.listener.EndlessRecyclerViewScrollListener;
import com.mbdshaiti.stephan.nytimessearch.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements SettingsDialogFragment.SettingsDialogListener {
      @BindView(R.id.rvResults)    RecyclerView rvResults;
    @BindView(R.id.toolbar) Toolbar toolBar;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    String query;
    private String beginDate;
    private String sort;
    private List<String> newsDeskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_search);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ButterKnife.bind(this);
            articles = new ArrayList<>();
            adapter = new ArticleArrayAdapter(this, articles);
            rvResults.setAdapter(adapter);
            StaggeredGridLayoutManager gridLayoutManager =
                    new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
            rvResults.setLayoutManager(gridLayoutManager);
            rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    customLoadMoreDataFromApi(page);
                }
            });
        }
        catch (Exception e)
        {
            Log.d("DEBUG",e.toString());
        }



    }
    private void customLoadMoreDataFromApi(int page) {
        onArticleSearch(query, page);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // search icon
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String quer) {
                // hold query
                query = quer;

                if (!isNetworkAvailable() ) {
                    Toast.makeText(getApplicationContext(),"No network",Toast.LENGTH_LONG).show();
                } else {
                    onArticleSearch(query, 0);

                    // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                    // see https://code.google.com/p/android/issues/detail?id=24599
                    searchView.clearFocus();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        settingsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showEditDialog();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
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

    public void onArticleSearch(String quer, int pageNumber) {
        if(isNetworkAvailable()) {
            query = quer;
            AsyncHttpClient client = new AsyncHttpClient();
            String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
            RequestParams params = new RequestParams();
            params.put("api-key", "b9a8d31eed5a44148f8f359554e677dc");
            params.put("page", 0);
            params.put("q", query);
            if (beginDate != null && !beginDate.trim().isEmpty())
                params.add("begin_date", beginDate);

            if (sort != null && !sort.trim().isEmpty())
                params.add("sort", sort.toLowerCase());

            if (pageNumber == 0) {
                articles.clear();
                adapter.notifyDataSetChanged();
            }
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    JSONArray articleJSONResults = null;
                    try {
                        articleJSONResults = response.getJSONObject("response").getJSONArray("docs");

                        articles.addAll(Article.fromJSONArray(articleJSONResults));
                        adapter.notifyDataSetChanged();

                        //  Log.d("DEBUG",adapter.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        }
        else
            Toast.makeText(this,"No network",Toast.LENGTH_LONG).show();
    }
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialogFragment settingsDialogFragment = SettingsDialogFragment.newInstance();
       settingsDialogFragment.setNewsDesk(newsDeskList);
        settingsDialogFragment.setSort(sort);
        settingsDialogFragment.show(fm, "fragment_edit_settings");
    }

    @Override
    public void onDone(String beginDate, List<String> newsDeskList, String sort) {
        this.beginDate = beginDate;
       this.newsDeskList = newsDeskList;
        this.sort = sort;
        onArticleSearch(query,0);
    }
    private String buildNewsDeskQuery() {
        String newsDesk = "";

        if (newsDeskList != null) {
            StringBuilder sb = new StringBuilder();
            for (String s : newsDeskList) {
                sb.append("\"").append(s).append("\"").append(" ");
            }
            newsDesk = sb.toString().trim();
        }
        if (!newsDesk.isEmpty())
            newsDesk = "news_desk" + ":(" + newsDesk + ")";
        return newsDesk;
    }

}
