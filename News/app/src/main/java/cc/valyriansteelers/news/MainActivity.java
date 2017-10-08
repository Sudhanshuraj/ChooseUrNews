package cc.valyriansteelers.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.LinkedList;

import cc.valyriansteelers.news.model.Article;
import cc.valyriansteelers.news.model.ArticlesResponse;
import cc.valyriansteelers.news.model.NewsStore;
import cc.valyriansteelers.news.networking.NewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView newsRecyclerView;

    public static HomeNewsAdapter homeNewsAdapter=new HomeNewsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //here it causes multiple layer of same news as previous news not deleted and new news are added everytime;
        // tried to do by the lower line but can also implement it by checking previous element in article
        NewsStore.setArticle(new LinkedList<Article>());

        Call<ArticlesResponse> call = NewsAPI.getApi().getArticles("the-hindu", "top");

        call.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
               // NewsStore.setArticle(articlesResponse.getArticles());
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received", Toast.LENGTH_SHORT).show();
               // HomeNewsAdapter homeNewsAdapter = new HomeNewsAdapter(articlesResponse.getArticles());
               // newsRecyclerView.setAdapter(homeNewsAdapter);
               // HomeNewsAdapter homeNewsAdapter = new HomeNewsAdapter();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
            }

            @Override
            public void onFailure(Call<ArticlesResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received the-hindu", Toast.LENGTH_SHORT).show();


            }
        });
        Call<ArticlesResponse> call2 = NewsAPI.gettApi().getArticles("hacker-news", "top");



        call2.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call2, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                //NewsStore.setArticle(articlesResponse.getArticles());
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received hacker", Toast.LENGTH_SHORT).show();
               // HomeNewsAdapter homeNewsAdapter = new HomeNewsAdapter(articlesResponse.getArticles());
              //  HomeNewsAdapter homeNewsAdapter = new HomeNewsAdapter();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());

                newsRecyclerView.setAdapter(homeNewsAdapter);

            }

            @Override
            public void onFailure(Call<ArticlesResponse> call2, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received hacker-news", Toast.LENGTH_SHORT).show();


            }
        });

            //can delete espn as it creates only 1 news-article

        Call<ArticlesResponse> call3 = NewsAPI.gettApi().getArticles("espn-cric-info", "top");
        call3.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call3, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received 3-espn", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call3, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });





        Call<ArticlesResponse> call4 = NewsAPI.gettApi().getArticles("entertainment-weekly", "top");
        call4.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call4, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received entertainment", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call4, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 4", Toast.LENGTH_SHORT).show();


            }
        });



        Call<ArticlesResponse> call5 = NewsAPI.gettApi().getArticles("fortune", "top");
        call5.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call5, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received fortune", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call5, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });




        Call<ArticlesResponse> call6 = NewsAPI.gettApi().getArticles("business-insider", "top");
        call6.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call6, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received business-insider", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call6, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });




        Call<ArticlesResponse> call7 = NewsAPI.gettApi().getArticles("the-times-of-india", "top");
        call7.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call7, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received times-of-india", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call7, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });


    }
}
