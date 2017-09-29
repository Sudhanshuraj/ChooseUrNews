package cc.valyriansteelers.news.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhanshu on 28/9/17.
 */

public class NewsStore {
    public static List<NewsArticle> newsArticle = new ArrayList<>();

    public static List<NewsArticle> getNewsArticle() {
        return newsArticle;
    }

    public static void setNewsArticle(List<NewsArticle> newsArticle) {
        NewsStore.newsArticle = newsArticle;
    }
}
