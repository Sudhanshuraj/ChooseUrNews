package cc.valyriansteelers.news.networking;

import cc.valyriansteelers.news.article_object.ArticlesResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sudhanshu on 30/9/17.
 */

/**
 *Here first we store APIKEY as the key we get from newsapi.org
 *And store the urlpath by APIPATH in the form of string
 */

public class NewsAPI {
    /**
     *Here first we store APIKEY as the key we get from newsapi.org
     */
    public static final String APIKEY = "47737bbc51334595a887f64082016928";
    /**
     *here store the urlpath by APIPATH in the form of string
     */
    public static final String APIPATH = "https://newsapi.org/v1/";

    /**
     *First set the Newsservice as null.
     */
    public static NewsService newsService = null;

    /**
     *used for setting the newsservice.
     */
    public static NewsService getApi(){
        /**
         * first check if the newsservice is null
         *If null then getApi used for setting the newsservice by use of retrofit builder based upon apipath.
         *then return the newsService (which is not null).
         */
        if (newsService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIPATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            newsService = retrofit.create(NewsService.class);
        }

        return newsService;

    }
    /**
     *used in main activity for receiving news from net.
     */
    public interface NewsService {
        /**
         *Based upon two arguments source and sortby, it fetches the corresponding news.
         */
        @GET("articles?apiKey=" + APIKEY)
        Call<ArticlesResponse> getArticles(@Query("source") String source, @Query("sortBy") String sortBy);
    }

}

