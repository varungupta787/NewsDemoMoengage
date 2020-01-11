package com.news.demo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.news.demo.NewsApplication;
import com.news.demo.R;
import com.news.demo.data.database.entity.NewsArticleEntity;
import com.news.demo.models.NewsArticle;
import com.news.demo.ui.AppConstant;
import com.news.demo.ui.activity.AdapterCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {


    private final Context mContext;
    private List<NewsArticleEntity> newsEntityList;
    private AdapterCallback adapterCallback;

    public NewsAdapter(Context context, List<NewsArticleEntity> newsEntityList, AdapterCallback adapterCallback) {
        this.mContext = context;
        this.newsEntityList = newsEntityList;
        this.adapterCallback = adapterCallback;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View cityView = mInflater.inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(cityView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsArticleEntity entity = newsEntityList.get(position);
        NewsArticle article = entity.convertToNewsArticle();

        setTag(holder, position);
        holder.newsHeadline.setText(article.getTitle());
        holder.newsDescription.setText(article.getDescription());
        holder.newsAuthorAndDate.setText(
                (article.getAuthor() == null) ? "No Author" :
                        article.getAuthor() + "  |  " +
                                article.getPublishedAt().substring(0, 10));
        setListeners(holder);
        //setSaveButtonBackground(holder);
        if (entity.getNewsState() == AppConstant.NON_SAVE) {
            holder.newsSave.setText("Save");
        } else {
            holder.newsSave.setText("Saved");
        }
        Picasso.with(NewsApplication.getContext()).
                load(article.getUrlToImage()).into(holder.newsImage);
    }

    private void setTag(NewsViewHolder holder, int position) {
        holder.newsSave.setTag(position);
        holder.newsShare.setTag(position);
        holder.newsMore.setTag(position);
    }

    private void setListeners(NewsViewHolder holder) {
        holder.newsSave.setOnClickListener(saveListener);
        holder.newsShare.setOnClickListener(shareListener);
        holder.newsMore.setOnClickListener(moreListener);
    }

    View.OnClickListener saveListener = v -> {
        int tag = getTagValue(v);
        if (tag != -1) {
            NewsArticleEntity entity = newsEntityList.get(tag);
            entity.toggleNewsState();
            notifyItemChanged(tag);
            adapterCallback.saveNews(tag);
            }
    };

    View.OnClickListener shareListener = v -> {
        int tag = getTagValue(v);
        if (tag != -1) {
            adapterCallback.shareNews(tag);
        }
    };

    View.OnClickListener moreListener = v -> {
        int tag = getTagValue(v);
        if (tag != -1) {
            adapterCallback.navigateToNewsDetailScreen(tag);
        }
    };

    private int getTagValue(View v) {
        int tag = -1;
        Object obj = v.getTag();
        try {
            if (obj != null) {
                tag = (Integer) obj;
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }

    @Override
    public int getItemCount() {
        return (newsEntityList == null) ? 0 : newsEntityList.size();
    }

    public void setData(List<NewsArticleEntity> newsArticlesList) {
        this.newsEntityList = newsArticlesList;
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView newsHeadline;
        ImageView newsImage;
        TextView newsDescription;
        TextView newsAuthorAndDate;
        Button newsSave;
        Button newsShare;
        Button newsMore;

        public NewsViewHolder(View view) {
            super(view);
            newsHeadline = (TextView) view.findViewById(R.id.news_headline);
            newsImage = (ImageView) view.findViewById(R.id.news_image);
            newsDescription = (TextView) view.findViewById(R.id.news_description);
            newsAuthorAndDate = (TextView) view.findViewById(R.id.news_author_and_date);
            newsSave = (Button) view.findViewById(R.id.news_save);
            newsShare = (Button) view.findViewById(R.id.news_share);
            newsMore = (Button) view.findViewById(R.id.news_more);
        }
    }

}
