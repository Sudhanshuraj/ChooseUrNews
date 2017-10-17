package cc.valyriansteelers.news;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import cc.valyriansteelers.news.model.Article;

/**
 * Created by sudhanshu on 16/10/17.
 */


public class LikedNewsAdapter extends RecyclerView.Adapter<LikedNewsAdapter.LikedNewsViewHolder> {

    public ArrayList<Article> likedArticles = new ArrayList<>();

    public LikedNewsAdapter(ArrayList<Article> likedArticles) {

        this.likedArticles = likedArticles;
    }

/*    public void addLikedNewsAdapter(ArrayList<Article> likedArticles){
        for (int i = 0; i < likedArticles.size(); i++) {
            int ind=this.likedArticles.indexOf(likedArticles.get(i));
            if(ind==-1)
                this.likedArticles.add(likedArticles.get(i));
        }

    }*/

    @Override
    public LikedNewsAdapter.LikedNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_news, parent,false);
        LikedNewsAdapter.LikedNewsViewHolder likedNewsViewHolder = new LikedNewsAdapter.LikedNewsViewHolder(view);
        return likedNewsViewHolder;
    }

    @Override
    public void onBindViewHolder(LikedNewsAdapter.LikedNewsViewHolder holder, final int position) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Article likedArticle = likedArticles.get(position);
        Glide.with(holder.cardImageView.getContext()).load(likedArticle.getUrlToImage())
                .apply(options)
                .into(holder.cardImageView);
        holder.cardTitleTextView.setText(likedArticle.getTitle());
        holder.cardTimeTextView.setText(likedArticle.getPublishedAt());
        holder.cardDetailsTextView.setText(likedArticle.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailsActivity.launch(view.getContext(), position);

            }
        });

    }

    @Override
    public int getItemCount() {

        return likedArticles.size();
    }



    public static class LikedNewsViewHolder extends RecyclerView.ViewHolder{
        ImageView cardImageView;
        TextView cardTitleTextView;
        TextView cardTimeTextView;
        TextView cardDetailsTextView;


        public LikedNewsViewHolder(View itemView) {
            super(itemView);
            cardImageView = (ImageView) itemView.findViewById(R.id.news_image);
            cardTitleTextView = (TextView) itemView.findViewById(R.id.news_title);
            cardTimeTextView = (TextView) itemView.findViewById(R.id.news_time);
            cardDetailsTextView = (TextView) itemView.findViewById(R.id.news_description);

        }

    }

}
