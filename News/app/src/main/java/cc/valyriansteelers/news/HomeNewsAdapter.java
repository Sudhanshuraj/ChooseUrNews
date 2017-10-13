package cc.valyriansteelers.news;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchUIUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cc.valyriansteelers.news.model.Article;
import cc.valyriansteelers.news.util.DateUtils;

/**
 * Created by sudhanshu on 28/9/17.
 */



public class HomeNewsAdapter extends RecyclerView.Adapter<HomeNewsAdapter.HomeNewsViewHolder>{
    private ArrayList<Article> newsArticles;
    public HomeNewsAdapter(){
        this.newsArticles= new ArrayList<Article>();
    }

    public HomeNewsAdapter(ArrayList<Article> newsArticles) {
        this.newsArticles = newsArticles;
    }
    public void addHomeNewsAdapter(ArrayList<Article> newsArtticles){
        for (int i = 0; i < newsArtticles.size(); i++) {
            int ind=this.newsArticles.indexOf(newsArtticles.get(i));
            //if(newsArticles.contains(newsArtticles.get(i)))
            if(ind==-1)
                this.newsArticles.add(newsArtticles.get(i));
            //Toast.makeText(HomeNewsAdapter.this, "Response Received", Toast.LENGTH_SHORT).show();
        }
        //this.newsArticles.addAll(newsArticles);

    }
    @Override
    public HomeNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_news, parent,false);
        HomeNewsViewHolder homeNewsViewHolder = new HomeNewsViewHolder(view);
        return homeNewsViewHolder;
    }

    @Override
    public void onBindViewHolder(HomeNewsViewHolder holder, final int position) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Article newsArticle = newsArticles.get(position);
        Glide.with(holder.cardImageView.getContext()).load(newsArticle.getUrlToImage())
                .apply(options)
                .into(holder.cardImageView);
        holder.cardTitleTextView.setText(newsArticle.getTitle());

        holder.cardTimeTextView.setText(DateUtils.formatNewsApiDAte(newsArticle.getPublishedAt()));

        holder.cardDetailsTextView.setText(newsArticle.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailsActivity.launch(view.getContext(), position);

            }
        });



    }

    @Override
    public int getItemCount() {
        return newsArticles.size();
    }


    public void removeItem(int position) {
        newsArticles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, newsArticles.size());
    }

    public static class HomeNewsViewHolder extends RecyclerView.ViewHolder{
        ImageView cardImageView;
        TextView cardTitleTextView;
        TextView cardTimeTextView;
        TextView cardDetailsTextView;


        public HomeNewsViewHolder(View itemView) {
            super(itemView);
            cardImageView = (ImageView) itemView.findViewById(R.id.news_image);
            cardTitleTextView = (TextView) itemView.findViewById(R.id.news_title);
            cardTimeTextView = (TextView) itemView.findViewById(R.id.news_time);
            cardDetailsTextView = (TextView) itemView.findViewById(R.id.news_description);

        }
    }
}


