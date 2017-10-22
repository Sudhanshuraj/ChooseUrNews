package cc.valyriansteelers.news.model;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhanshu on 28/9/17.
 */

public class NewsStore {
    public static ArrayList<Article> newsArticles = new ArrayList<>();

    public static ArrayList<Article> getNewsArticles() {
        return newsArticles;
    }

    public static void setArticle(ArrayList<Article> newsArticles) {
        NewsStore.newsArticles = newsArticles;
    }
    public static void addArticle(ArrayList<Article> newsArtticles){
        for (int i = 0; i < newsArtticles.size(); i++) {
            int ind=newsArticles.indexOf(newsArtticles.get(i));
              if(ind==-1)
           NewsStore.newsArticles.add(newsArtticles.get(i));

        }
    }
    public static void addArticle(List<Article> newsArtticles,String source){
        Article n;
        for (int i = 0; i < newsArtticles.size(); i++) {
            n=newsArtticles.get(i);
            n.setSourcename(source);
            n.setEstimatedTime("Loading...");
            int ind=newsArticles.indexOf(newsArtticles.get(i));
            if(ind==-1)
                NewsStore.newsArticles.add(n);

        }
    }


}
