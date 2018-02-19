package com.mbdshaiti.stephan.nytimessearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mbdshaiti.stephan.nytimessearch.R;
import com.mbdshaiti.stephan.nytimessearch.activities.ArticleActivity;
import com.mbdshaiti.stephan.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Stephan on 2/18/2018.
 */

public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {
    private List<Article> articles;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ivImage) ImageView ivImage;
        @BindView(R.id.tvTitle) TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public ArticleArrayAdapter(Context context, List<Article> articles) {
      this.articles=articles;
      this.context=context;
    }

    private Context getContext()
    {
        return context;
    }

    @Override
    public ArticleArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleArrayAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        Article article = articles.get(position);

        // Set item views based on your views and data model
        viewHolder.tvTitle.setText(article.getHeadline());
        viewHolder.ivImage.setImageResource(0);
        String thumbnail=article.getThumbnail();
        if(!TextUtils.isEmpty(thumbnail))
            Picasso.with(getContext()).load(article.getThumbnail()).into(viewHolder.ivImage);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ArticleActivity.class);
                try {
                    Article article = articles.get(position);

                    intent.putExtra("article", Parcels.wrap(article));
                }
                catch (Exception e)
                {
                    Log.d("DEBUG",e.toString());
                }
                getContext().startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return articles.size();
    }
    /*@Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Article article=getItem(position);

        if(convertView==null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.item_article_result,parent,false);

        }

        ivImage.setImageResource(0);


        tvTitle.setText(article.getHeadline());
        String thumbnail=article.getThumbnail();
        if(!TextUtils.isEmpty(thumbnail))
            Picasso.with(getContext()).load(article.getThumbnail()).into(ivImage);
        return convertView;
    }*/
}
