package cc.valyriansteelers.news;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import cc.valyriansteelers.news.model.Article;
import cc.valyriansteelers.news.model.ArticlesResponse;
import cc.valyriansteelers.news.model.NewsStore;
import cc.valyriansteelers.news.networking.NewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private RecyclerView newsRecyclerView;
    private ArrayList<Article>  newsArticles = new ArrayList<>();
    private HomeNewsAdapter homeNewsAdapter = new HomeNewsAdapter(newsArticles);
    private Paint p=new Paint();
    public static ArrayList<Article> test = null;
    //public static ArrayList<Article> ls = new ArrayList<>();

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Permission denied", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    void saveToSD(ArrayList<Article> articles) {

        if (isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();
            if (checkPermission()){
                try {
                    File dir = new File(String.valueOf(path));
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    FileOutputStream fos =
                            new FileOutputStream(
                                    new File(path, "like.dat")
                            );
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(articles);
                    os.close();
                    Toast.makeText(MainActivity.this, "Saved in External storage", Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                    Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
        }
        else
            requestPermission();
        }
    }

     public ArrayList<Article> readFromSd() {

        ArrayList<Article> savedArrayList = new ArrayList<>();
        if(isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();
            if(checkPermission()){

            try {
                File dir = new File(String.valueOf(path));
                if (!dir.exists()) {
                    dir.mkdir();
                }
                FileInputStream fis =
                        new FileInputStream(
                                new File(path, "like.dat")
                        );
                ObjectInputStream is = new ObjectInputStream(fis);
                savedArrayList = (ArrayList<Article>) is.readObject();
                is.close();
                fis.close();
                Toast.makeText(MainActivity.this, "read from external storage", Toast.LENGTH_SHORT).show();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return savedArrayList;
        }
            else {
                requestPermission();
                return savedArrayList;
            }

        }
        else {

            Toast.makeText(MainActivity.this, "Error in reading", Toast.LENGTH_SHORT).show();
            return savedArrayList;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!checkPermission()) {
            requestPermission();
            saveToSD(test);
        }
        initViews();
    }



    private void initViews(){
        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Call<ArticlesResponse> call = NewsAPI.getApi().getArticles("the-hindu", "top");

        call.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                homeNewsAdapter.notifyDataSetChanged();
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
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received hacker", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());

                newsRecyclerView.setAdapter(homeNewsAdapter);
                homeNewsAdapter.notifyDataSetChanged();

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
                homeNewsAdapter.notifyDataSetChanged();
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
                homeNewsAdapter.notifyDataSetChanged();
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
                homeNewsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call5, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });




        Call<ArticlesResponse> call6 = NewsAPI.getApi().getArticles("business-insider", "top");
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




        Call<ArticlesResponse> call7 = NewsAPI.getApi().getArticles("the-times-of-india", "latest");
        call7.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call7, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles());
                Toast.makeText(MainActivity.this, "Response Received times-of-india", Toast.LENGTH_SHORT).show();
                homeNewsAdapter.addHomeNewsAdapter(articlesResponse.getArticles());
                newsRecyclerView.setAdapter(homeNewsAdapter);
                homeNewsAdapter.notifyDataSetChanged();
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
                homeNewsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call8, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });


    initSwipe();

    }
    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    if(checkPermission()){

                        ArrayList<Article> ls = readFromSd();
                        ls.add(newsArticles.get(position));
                        saveToSD(ls);

                    }
                    else {
                        requestPermission();
                        saveToSD(test);

                    }
                    newsArticles.remove(newsArticles.get(position));
                    NewsStore.setArticle(newsArticles);
                    homeNewsAdapter.notifyItemRemoved(position);
                    homeNewsAdapter.notifyDataSetChanged();

                } else {

                }
            }

            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    p.setColor(Color.parseColor("#000000"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2*width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(newsRecyclerView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(MainActivity.this,
                        LikedActivity.class);
                startActivity(myIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
