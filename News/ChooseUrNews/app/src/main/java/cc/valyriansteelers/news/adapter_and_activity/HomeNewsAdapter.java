package cc.valyriansteelers.news.adapter_and_activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;

import cc.valyriansteelers.news.R;
import cc.valyriansteelers.news.article_object.Article;
import cc.valyriansteelers.news.utility.DateUtils;

/**
 * Created by sudhanshu on 28/9/17.
 */

/**This class is used to create the news recycler view containing the cards of the articles*/

public class HomeNewsAdapter extends RecyclerView.Adapter<HomeNewsAdapter.HomeNewsViewHolder>{
    /**Variable storing the array of articles to be displayed in the recycler view*/
    private ArrayList<Article> newsArticles = new ArrayList<>();

    /**Constructor for the class*/
    public HomeNewsAdapter(ArrayList<Article> newsArticles) {
        this.newsArticles = newsArticles;
    }

    /**This function is used to add another arrylist of articles in the recycler view already containing an array*/
    public void addHomeNewsAdapter(ArrayList<Article> newsArtticles){
        for (int i = 0; i < newsArtticles.size(); i++) {
            int ind=this.newsArticles.indexOf(newsArtticles.get(i));
            if(ind==-1)
                this.newsArticles.add(newsArtticles.get(i));
        }

    }

    /**This function notifies the view that a particular article at a specified index has been changed which are passed as arguments*/
    public void modifyadapter(Article arc, Integer index){
        this.newsArticles.set(index, arc);
        notifyItemChanged(index);

    }
    /**Overriding the in-built function of creating an activity page to create news recycler view*/
    @Override
    public HomeNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_news, parent,false);
        HomeNewsViewHolder homeNewsViewHolder = new HomeNewsViewHolder(view);
        return homeNewsViewHolder;
    }

    /**This function initializes each field of the card with its contents*/
    @Override
    public void onBindViewHolder(final HomeNewsViewHolder holder, final int position) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        final Article newsArticle = newsArticles.get(position);
        Glide.with(holder.cardImageView.getContext()).load(newsArticle.getUrlToImage())
                .apply(options)
                .into(holder.cardImageView);
        holder.cardTitleTextView.setText(newsArticle.getTitle());

        holder.cardTimeTextView.setText(DateUtils.formatNewsApiDAte(newsArticle.getPublishedAt()));

        holder.cardDetailsTextView.setText(newsArticle.getDescription());
        holder.cardEstimatedTimeTextView.setText(newsArticle.getEstimatedTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailsActivity.launch(view.getContext(), position);


            }
        });



    }

    /**This function returns the number of newsarticles contained in the recycler view.*/
    @Override
    public int getItemCount() {
        return newsArticles.size();
    }


    /**This class is used to create card of the news article.
     * The card contains one image view and four text view.*/
    public static class HomeNewsViewHolder extends RecyclerView.ViewHolder{
        ImageView cardImageView;
        TextView cardTitleTextView;
        TextView cardTimeTextView;
        TextView cardDetailsTextView;
        TextView cardEstimatedTimeTextView;


        public HomeNewsViewHolder(View itemView) {
            super(itemView);
            cardImageView = (ImageView) itemView.findViewById(R.id.news_image);
            cardTitleTextView = (TextView) itemView.findViewById(R.id.news_title);
            cardTimeTextView = (TextView) itemView.findViewById(R.id.news_time);
            cardDetailsTextView = (TextView) itemView.findViewById(R.id.news_description);
            cardEstimatedTimeTextView = (TextView) itemView.findViewById(R.id.news_count);

        }
    }
}


