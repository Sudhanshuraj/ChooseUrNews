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
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import cc.valyriansteelers.news.model.Article;
import cc.valyriansteelers.news.model.ArticlesResponse;
import cc.valyriansteelers.news.model.NewsStore;
import cc.valyriansteelers.news.networking.NewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    //variable declaration

    private static final int PERMISSION_REQUEST_CODE = 1;
    private RecyclerView newsRecyclerView;
    private Paint p=new Paint();
    public static ArrayList<Article> test = new ArrayList<>();
    public static String lis = null;
    private ArrayList<Article> newsArrticles = readFromSd("ChooseUrNews/data.dat");
    private HomeNewsAdapter homeNewsAdapter = new HomeNewsAdapter(newsArrticles);
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<String> stop = new ArrayList<String>(Arrays.asList("and", "at", "of", "the", "is", "in", "his", "her", "a", "there"));


    //--------------------------------------------------------

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


    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    void saveToSD(ArrayList<Article> articles,String dest) {

        if (isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();
                try {
                    File dir = new File(String.valueOf(path)+"/ChooseUrNews");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    FileOutputStream fos =
                            new FileOutputStream(
                                    new File(path, dest)
                            );
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(articles);
                    os.close();
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                }

        }
    }

    void saveToSDMap(Map<String, Integer> source,String dest) {

        if (isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();
                try {
                    File dir = new File(String.valueOf(path)+"/ChooseUrNews");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    FileOutputStream fos =
                            new FileOutputStream(
                                    new File(path, dest)
                            );
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(source);
                    os.close();
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                    
                }
        }
    }



     public static ArrayList<Article> readFromSd(String dest) {

        ArrayList<Article> savedArrayList = new ArrayList<>();
        if(isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();

            try {
                File dir = new File(String.valueOf(path)+"/ChooseUrNews");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                FileInputStream fis =
                        new FileInputStream(
                                new File(path, dest)
                        );
                ObjectInputStream is = new ObjectInputStream(fis);
                savedArrayList = (ArrayList<Article>) is.readObject();
                is.close();
                fis.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            return savedArrayList;


        }
        else {
            
            return savedArrayList;
        }
    }

    public static Map<String ,Integer> readFromSdMap(String dest) {

        Map<String,Integer> source = new HashMap<String, Integer>();
        if(isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();

                try {
                    File dir = new File(String.valueOf(path)+"/ChooseUrNews");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    FileInputStream fis =
                            new FileInputStream(
                                    new File(path, dest)
                            );
                    ObjectInputStream is = new ObjectInputStream(fis);
                    source = (Map<String, Integer>) is.readObject();
                    is.close();
                    fis.close();

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                return source;

        }
        else {
            return source;
        }
    }

    public static boolean isPresent(ArrayList<Article> list, Article toCheck){
        int n = list.size();
        for(int i = 0; i < n; i++){
            if(list.get(i).getUrl().equals(toCheck.getUrl())) {
                return true;
            }
        }

        return false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);

        if(!checkPermission()) {
            requestPermission();
            onCreate(savedInstanceState);
        }

        File path = Environment.getExternalStorageDirectory();
        File dir = new File(String.valueOf(path)+"/ChooseUrNews");
        if (!dir.exists()) {
            dir.mkdir();
        }

        if(newsArrticles.isEmpty()) {
            initViews();
        }
        else{
            newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
            newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            NewsStore.addArticle(newsArrticles);
            homeNewsAdapter.addHomeNewsAdapter(newsArrticles);
            homeNewsAdapter.notifyDataSetChanged();
            newsRecyclerView.setAdapter(homeNewsAdapter);
            Toast.makeText(MainActivity.this, "loaded from internal storage", Toast.LENGTH_SHORT).show();
            initSwipe();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsArrticles.clear();
                NewsStore.newsArticles.clear();
                initViews();

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });



    }





    private void initViews(){
        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Call<ArticlesResponse> call = NewsAPI.getApi().getArticles("the-hindu", "top");
        call.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"hindu");
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
                NewsStore.addArticle(articlesResponse.getArticles(),"hackernews");

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
                NewsStore.addArticle(articlesResponse.getArticles(),"bbc");
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
                NewsStore.addArticle(articlesResponse.getArticles(),"google");
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
                NewsStore.addArticle(articlesResponse.getArticles(),"scientist");
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
                NewsStore.addArticle(articlesResponse.getArticles(),"bussiness-insider");
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
                NewsStore.addArticle(articlesResponse.getArticles(),"timesofindia");
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
                NewsStore.addArticle(articlesResponse.getArticles(),"geographic");

                Map<String, Integer> sourcemap = readFromSdMap("ChooseUrNews/sources.dat");
                Map<String, Integer> frequency = readFromSdMap("ChooseUrNews/freq.dat");
                int sizeofnewsarticle = newsArrticles.size();
                //Toast.makeText(MainActivity.this,Integer.toString(sizeofnewsarticle), Toast.LENGTH_SHORT).show();

                Date currentdate = new Date();
                Date d1 = null;
                Date d2 = null;
                for(int prindex=0;prindex<sizeofnewsarticle;prindex++){

                    String srcname =newsArrticles.get(prindex).getSourcename();
                    String ttlname = newsArrticles.get(prindex).getTitle();
                    String time=newsArrticles.get(prindex).getPublishedAt();
                    int prior = 0;

                    if(time!=null) {
                        try {
                            String inputDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                            String outputDateFormat= "EEE, d MMM yyyy HH:mm";
                            SimpleDateFormat inputFormat = new SimpleDateFormat(inputDateFormat);
                            SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);

                            String sds=outputFormat.format(currentdate);
                            d1 = outputFormat.parse(sds);
                            d2 = inputFormat.parse(time);

                            long diff = d1.getTime() - d2.getTime();

                            int diffHours = (int) (diff / (60 * 60 * 1000) % 24);
                            int  diffDays = (int) diff / (24 * 60 * 60 * 1000);
                            if(diffDays>1){
                                prior= -20*diffDays;
                            }
                            else if(diffHours>12){
                                prior=-12;
                            }
                            else if(diffHours>1){
                                prior=-1*diffHours;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else prior= -5;
                    StringTokenizer ttltoken = new StringTokenizer(ttlname.toLowerCase()," ,.!-");
                    while (ttltoken.hasMoreTokens()) {
                        String nw = ttltoken.nextToken();
                        if (stop.contains(nw)) {
                            continue;
                        }
                        if (frequency.containsKey(nw)) {
                            prior+=frequency.get(nw);
                        }
                    }


                    if(sourcemap.containsKey(srcname)) {
                        prior += 2*sourcemap.get(srcname);
                    }

                    newsArrticles.get(prindex).setPriority(prior);

                }

                Collections.sort(newsArrticles, new Comparator<Article>() {
                    @Override
                    public int compare(Article article, Article t1) {
                        return t1.getPriority().compareTo(article.getPriority());
                    }
                });

                saveToSD(newsArrticles,"ChooseUrNews/data.dat");
                NewsStore.setArticle(newsArrticles);
                homeNewsAdapter.addHomeNewsAdapter(newsArrticles);
                homeNewsAdapter.notifyDataSetChanged();
                newsRecyclerView.setAdapter(homeNewsAdapter);

            }
            @Override
            public void onFailure(Call<ArticlesResponse> call8, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();

                Map<String, Integer> sourcemap = readFromSdMap("ChooseUrNews/sources.dat");
                Map<String, Integer> frequency = readFromSdMap("ChooseUrNews/freq.dat");
                int sizeofnewsarticle = newsArrticles.size();
                //Toast.makeText(MainActivity.this,Integer.toString(sizeofnewsarticle), Toast.LENGTH_SHORT).show();

                Date currentdate = new Date();
                Date d1 = null;
                Date d2 = null;
                for(int prindex=0;prindex<sizeofnewsarticle;prindex++){

                    String srcname =newsArrticles.get(prindex).getSourcename();
                    String ttlname = newsArrticles.get(prindex).getTitle();
                    String time=newsArrticles.get(prindex).getPublishedAt();
                    int prior = 0;

                    if(time!=null) {
                        try {
                            String inputDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                            String outputDateFormat= "EEE, d MMM yyyy HH:mm";
                            SimpleDateFormat inputFormat = new SimpleDateFormat(inputDateFormat);
                            SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);

                            String sds=outputFormat.format(currentdate);
                            d1 = outputFormat.parse(sds);
                            d2 = inputFormat.parse(time);

                            long diff = d1.getTime() - d2.getTime();

                            int diffHours = (int) (diff / (60 * 60 * 1000) % 24);
                            int  diffDays = (int) diff / (24 * 60 * 60 * 1000);
                            if(diffDays>1){
                                prior= -20*diffDays;
                            }
                            else if(diffHours>12){
                                prior=-12;
                            }
                            else if(diffHours>1){
                                prior=-1*diffHours;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else prior= -5;
                    StringTokenizer ttltoken = new StringTokenizer(ttlname.toLowerCase()," ,.!-");
                    while (ttltoken.hasMoreTokens()) {
                        String nw = ttltoken.nextToken();
                        if (stop.contains(nw)) {
                            continue;
                        }
                        if (frequency.containsKey(nw)) {
                            prior+=frequency.get(nw);
                        }
                    }


                    if(sourcemap.containsKey(srcname)) {
                        prior += 2*sourcemap.get(srcname);
                    }

                    newsArrticles.get(prindex).setPriority(prior);

                }

                Collections.sort(newsArrticles, new Comparator<Article>() {
                    @Override
                    public int compare(Article article, Article t1) {
                        return t1.getPriority().compareTo(article.getPriority());
                    }
                });

                saveToSD(newsArrticles,"ChooseUrNews/data.dat");
                NewsStore.setArticle(newsArrticles);
                homeNewsAdapter.addHomeNewsAdapter(newsArrticles);
                homeNewsAdapter.notifyDataSetChanged();
                newsRecyclerView.setAdapter(homeNewsAdapter);

            }
        });



        initSwipe();

    }



    public void afterSwipe(Article article){
        String src = article.getSourcename();
        Map<String, Integer> source = readFromSdMap("ChooseUrNews/sources.dat");
        Integer num = source.get(src);
        if (num == null) {
            source.put(lis, 1);
            saveToSDMap(source, "ChooseUrNews/sources.dat");
        } else {
            source.put(lis, num + 1);
            saveToSDMap(source, "ChooseUrNews/sources.dat");
        }

        String title = article.getTitle();
        StringTokenizer hello = new StringTokenizer(title.toLowerCase()," ,!-");
        Map<String, Integer> frequency = readFromSdMap("ChooseUrNews/freq.dat");

        while (hello.hasMoreTokens()) {
            String nw = hello.nextToken();
            if (stop.contains(nw)) {
                continue;
            }
            if (frequency.containsKey(nw)) {
                int i = frequency.get(nw);
                frequency.put(nw, i + 1);
            }
            else {
                frequency.put(nw, 1);
            }
        }

        saveToSDMap(frequency, "ChooseUrNews/freq.dat");
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

                if(direction == ItemTouchHelper.LEFT) {


                    ArrayList<Article> ls = readFromSd("ChooseUrNews/like.dat");
                    if (isPresent(ls, newsArrticles.get(position))) {
                        Toast.makeText(MainActivity.this, "Already Liked", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ls.add(newsArrticles.get(position));
                        saveToSD(ls, "ChooseUrNews/like.dat");
                        afterSwipe(newsArrticles.get(position));


                    }

                    newsArrticles.remove(newsArrticles.get(position));
                    saveToSD(newsArrticles,"ChooseUrNews/data.dat");
                    NewsStore.setArticle(newsArrticles);
                    homeNewsAdapter.notifyItemRemoved(position);
                    homeNewsAdapter.notifyDataSetChanged();
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
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.thumsup);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
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
        getMenuInflater().inflate(R.menu.mainact_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.bookmark:
                File path2 = Environment.getExternalStorageDirectory();
                File dir2 = new File(String.valueOf(path2)+"/ChooseUrNews/bookmark.dat");
                if(!dir2.exists()){
                    try {
                        dir2.createNewFile();
                        saveToSD(test,"ChooseUrNews/bookmark.dat");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Intent myIntent2 = new Intent(MainActivity.this,
                        BookMarkActivity.class);
                startActivity(myIntent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
