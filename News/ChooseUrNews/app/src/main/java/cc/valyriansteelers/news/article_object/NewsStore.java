package cc.valyriansteelers.news.article_object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhanshu on 28/9/17.
 */

/**
 * It is the main Arraylist, which we used for reference i.e. every detail for any news.
 */

public class NewsStore {
    /**
     * newsArticle is the private data which contains all the news in the form of arraylist.
     */
    public static ArrayList<Article> newsArticles = new ArrayList<>();

    /**
     * getNewsArticle return all the news in the form of arraylist.
     */
    public static ArrayList<Article> getNewsArticles() {
        return newsArticles;
    }

    /**
     * setArticle is used for updating the newsarticles, as it set the newsArticle to the arguments.
     */
    public static void setArticle(ArrayList<Article> newsArticles) {
        NewsStore.newsArticles = newsArticles;
    }

    /**
     * It takes an arraylist of article(news) and add every article to newsArticle depending upon they are previously in the newsArticle or not.
     */
    public static void addArticle(ArrayList<Article> newsArtticles){
        for (int i = 0; i < newsArtticles.size(); i++) {
            int ind=newsArticles.indexOf(newsArtticles.get(i));
            if(ind==-1)
                NewsStore.newsArticles.add(newsArtticles.get(i));

        }
    }

    /**
     *This  addArticle takes two arguments, one list of article and 2nd the source through which they are coming.
     * It again add the article to the newsArticle depending upon they are previously in the newsArticle or not.
     * But this time it sets the source as the given arguments and estimated time as loading....
     */
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

    /**
     * Used for modification of index corresponding article
     */
    public static void modify(Article arc, int index){
        newsArticles.set(index,arc);
    }


}

