package cc.valyriansteelers.news.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhanshu on 28/9/17.
 */

public class NewsStore {
    public static List<Article> newsArticles = new ArrayList<>();

    public static List<Article> getNewsArticles() {
        return newsArticles;
    }

    public static void setArticle(List<Article> newsArticles) {
        NewsStore.newsArticles = newsArticles;
    }
    public static void addArticle(List<Article> newsArtticles){
       // for (int i = 0; i < newsArtticles.size(); i++) {
         //   NewsStore.newsArticles.add(i,newsArtticles.get(i));
        //}
        NewsStore.newsArticles.addAll(newsArtticles);
    }


}
