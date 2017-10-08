package cc.valyriansteelers.news.networking;

import java.util.List;

import cc.valyriansteelers.news.model.ArticlesResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sudhanshu on 30/9/17.
 */

public class NewsAPI {
    public static final String APIKEY = "47737bbc51334595a887f64082016928";
    public static final String APIPATH = "https://newsapi.org/v1/";

    public static NewsService newsService = null;
    public static NewsService newsService2 = null;
    public static NewsService getApi(){
        if (newsService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIPATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            newsService = retrofit.create(NewsService.class);
        }

        return newsService;

    }
    public static NewsService gettApi(){
        if (newsService2 == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIPATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            newsService2 = retrofit.create(NewsService.class);
        }

        return newsService2;

    }

    public interface NewsService {
        @GET("articles?apiKey=" + APIKEY)
        Call<ArticlesResponse> getArticles(@Query("source") String source, @Query("sortBy") String sortBy);
    }

}
