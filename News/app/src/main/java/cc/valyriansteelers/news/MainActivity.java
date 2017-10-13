package cc.valyriansteelers.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
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

    private HomeNewsAdapter homeNewsAdapter=new HomeNewsAdapter();

    //
    private ArrayList<Article>  newsArticles;
    private Paint p=new Paint();
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews(){
        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //here it causes multiple layer of same news as previous news not deleted and new news are added everytime;
        // tried to do by the lower line but can also implement it by checking previous element in article

       // NewsStore.setArticle(new LinkedList<Article>());



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


        Call<ArticlesResponse> call2 = NewsAPI.getApi().getArticles("hacker-news", "top");
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

        Call<ArticlesResponse> call3 = NewsAPI.getApi().getArticles("bbc-news", "top");
        call3.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call3, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received bbc-news", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call3, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });





        Call<ArticlesResponse> call4 = NewsAPI.getApi().getArticles("google-news", "top");
        call4.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call4, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "google-news", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call4, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 4", Toast.LENGTH_SHORT).show();


            }
        });



        Call<ArticlesResponse> call5 = NewsAPI.getApi().getArticles("new-scientist", "top");
        call5.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call5, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received new-scientist", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call5, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });




/*        Call<ArticlesResponse> call6 = NewsAPI.getApi().getArticles("business-insider", "top");
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
        });*/




        Call<ArticlesResponse> call7 = NewsAPI.getApi().getArticles("the-times-of-india", "latest");
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


        Call<ArticlesResponse> call8 = NewsAPI.getApi().getArticles("national-geographic", "top");
        call8.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call8, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received national-geographic", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call8, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });


    initSwipe();

    }
    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    /*homeNewsAdapter = new HomeNewsAdapter(newsArticles);

                    String  s = newsArticles.get(position).getTitle();
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    newsArticles.remove(position);
*/
                    //newsArticles.get(position).setDescription(s);
                    //homeNewsAdapter.removeItem(position);
                    if(homeNewsAdapter != null){
                       // homeNewsAdapter.removeItem(position);
                        //homeNewsAdapter.notifyItemRemoved(position);
                        Toast.makeText(MainActivity.this, "yuplo", Toast.LENGTH_SHORT).show();}
                    else{
                        Toast.makeText(MainActivity.this, "yup", Toast.LENGTH_SHORT).show();
                    }
                    //HomeNewsAdapter newsAdapter = new HomeNewsAdapter(newsArticles);
                    //newsRecyclerView.setAdapter(newsAdapter);

                } else {
                    //removeView();
                    //edit_position = position;
                    //alertDialog.setTitle("Edit Country");
                    //et_country.setText(countries.get(position));
                    //alertDialog.show();
                }
            }

            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(newsRecyclerView);
    }
/*    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }*/


}
