package cc.valyriansteelers.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cc.valyriansteelers.news.model.NewsArticle;
import cc.valyriansteelers.news.model.NewsStore;

public class MainActivity extends AppCompatActivity {
    private RecyclerView newsRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<NewsArticle> newsArticles = new ArrayList<>();

        newsArticles.add(new NewsArticle("dummy title1", "dummy details", "https://www.w3schools.com/css/trolltunga.jpg", "dummy time 521+2613", "https://www.facebook.com"));
        newsArticles.add(new NewsArticle("dummy title2", "dummy details2", "https://imagejournal.org/wp-content/uploads/bb-plugin/cache/23466317216_b99485ba14_o-panorama.jpg", "dumm25y time 521+2613", "https://www.google.com"));
        newsArticles.add(new NewsArticle("dummy title3", "dummy details", "https://static.pexels.com/photos/20974/pexels-photo.jpg","time ", "https://www.instagram.com"));

        NewsStore.setNewsArticle(newsArticles);

        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        HomeNewsAdapter homeNewsAdapter = new HomeNewsAdapter(NewsStore.getNewsArticle());
        newsRecyclerView.setAdapter(homeNewsAdapter);

    }
}
