package cc.valyriansteelers.news.adapter_and_activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.jsoup.Jsoup;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
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

import cc.valyriansteelers.news.R;
import cc.valyriansteelers.news.article_object.Article;
import cc.valyriansteelers.news.article_object.ArticlesResponse;
import cc.valyriansteelers.news.article_object.NewsStore;
import cc.valyriansteelers.news.networking.NewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.String.valueOf;

/**
 * Created by sudhanshu on 18/10/17.
 */

/**The main activity page where all the news articles from different sources are shown in a recycler view
 * arranged in a way convenient for the user to read.
 */

public class MainActivity extends AppCompatActivity {
    //variable declaration

    private static final int PERMISSION_REQUEST_CODE = 1;
    int count =1;
    private RecyclerView newsRecyclerView;
    private Paint p = new Paint();
    public static ArrayList<Article> test = new ArrayList<>();
    public static String lis = null;
    private ArrayList<Article> newsArrticles = new ArrayList<>();
    private HomeNewsAdapter homeNewsAdapter = new HomeNewsAdapter(newsArrticles);
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<String> stop = new ArrayList<String>(Arrays.asList("and", "at", "of", "the", "is", "in", "his", "her", "a", "there"));


    //--------------------------------------------------------

    /**Function for checking whether internet connection is available or not*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null) && activeNetworkInfo.isConnected();
    }

    /**Function which runs in the background asynchronously which returns
     *  the html source code of the webpage as a string whose URL is passed as the argument*/
    private class MyTask extends AsyncTask<Article, Void, Article> {
        String textResult;

        @Override
        protected Article doInBackground(Article... params) {
            URL textUrl;
            Article arc = params[0];
            String textSource = arc.getUrl();
            try {
                textUrl = new URL(textSource);
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(textUrl.openStream()));

                String stringBuffer;
                String stringText = "";

                while ((stringBuffer = bufferedReader.readLine()) != null) {
                    stringText += stringBuffer;
                }
                bufferedReader.close();
                textResult = stringText;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                textResult = e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                textResult = e.toString();
            }
            float count = 0;
            try {
                count = countWords(textResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (count < 300) {
                arc.setEstimatedTime("1 min read");
            } else {
                int tont = Math.round(count / 300);
                arc.setEstimatedTime(Integer.toString(tont) + " min read");
            }

            return arc;

        }

        @Override
        protected void onPostExecute(Article aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /**Function which takes string of html source code as argument and returns the exact number of words
     * which appears in the webview of the article, removing all the tags and unnecessary css and javascript code */
    private int countWords(String html) throws Exception {
        org.jsoup.nodes.Document dom = Jsoup.parse(html);
        String text = dom.text();

        return text.split(" ").length;
    }

    /**Function for checking whether permission of reading and writing on the external storage is granted or not*/
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**Function for requesting permission of reading and writing on the external storage if already nat granted*/
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    /**Function for checking whether external storage is readable and writeable */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**Function for saving an arraylist of news articles on external storage at specified destination*/
    void saveToSD(ArrayList<Article> articles, String dest) {

        if (isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();
            try {
                File dir = new File(valueOf(path) + "/ChooseUrNews");
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

    /**Function for saving a map with key as string and value as integer on external storage at specified destination*/
    void saveToSDMap(Map<String, Integer> source, String dest) {

        if (isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();
            try {
                File dir = new File(valueOf(path) + "/ChooseUrNews");
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

    /**Function for reading an arraylist of news articles from external storage at given destination*/
    public static ArrayList<Article> readFromSd(String dest) {

        ArrayList<Article> savedArrayList = new ArrayList<>();
        if (isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();

            try {
                File dir = new File(valueOf(path) + "/ChooseUrNews");
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


        } else {

            return savedArrayList;
        }
    }

    /**Function for reading a map from external storage at given destination*/
    public static Map<String, Integer> readFromSdMap(String dest) {

        Map<String, Integer> source = new HashMap<String, Integer>();
        if (isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();

            try {
                File dir = new File(valueOf(path) + "/ChooseUrNews");
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

        } else {
            return source;
        }
    }

    /**Function for checking whether an article is present is an arraylist of news articles*/
    public static boolean isPresent(ArrayList<Article> list, Article toCheck) {
        int n = list.size();
        for (int i = 0; i < n; i++) {
            if (list.get(i).getUrl().equals(toCheck.getUrl())) {
                return true;
            }
        }

        return false;
    }

    /**Variable for containg the cuss words to be removed from title of news article while tokenizing it*/
    final CharSequence[] items = {"Sports","India","Entertainment","Technology","Business","World","Scientific"};

    final ArrayList<Integer> seletedItems = new ArrayList();

    /**Function for creating the dialog box for selecting the type of news the user want to read*/
    public void buildDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type Of News You Like");
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                            //Toast.makeText(MainActivity.this, Integer.toString(indexSelected), Toast.LENGTH_SHORT).show();
                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addToProperPlace(seletedItems);
                        newsArrticles.clear();
                        initViews();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**Function for distributing news sources among different heads like sports, finance, etc.*/
    public void addToProperPlace(ArrayList<Integer> seleted){
        Map<String, Integer> source = readFromSdMap("ChooseUrNews/sources.dat");
        int maxValueInMap = 0;
        if(!source.isEmpty()) {
            maxValueInMap = (Collections.max(source.values()));
        }
        for (int i = 0; i< seleted.size();i++){
            if(seleted.get(i) == 0){
                lis = "espn";
                Integer num = source.get(lis);
                if (num == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                }
                lis = "the-sports-bible";
                Integer num2 = source.get(lis);
                if (num2 == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num2 + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                }
            }

            if(seleted.get(i) == 1){
                lis = "hindu";
                Integer num = source.get(lis);
                if (num == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                }
                lis = "timesofindia";
                Integer num2 = source.get(lis);
                if (num2 == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num2 + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                }
            }

            if(seleted.get(i) == 2){
                lis = "entertainment-weekly";
                Integer num = source.get(lis);
                if (num == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                } else {
                    source.put(lis, num + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                }
                lis = "buzzfeed";
                Integer num2 = source.get(lis);
                if (num2 == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num2 + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                }
            }

            if(seleted.get(i)==3){
                lis = "hackernews";
                Integer num = source.get(lis);
                if (num == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                }
                lis = "techradar";
                Integer num2 = source.get(lis);
                if (num2 == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num2 + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                }
                lis = "recode";
                Integer num3 = source.get(lis);
                if (num3 == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num3 + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                }
            }

            if(seleted.get(i)==4){
                lis = "the-wall-street-journal";
                Integer num = source.get(lis);
                if (num == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                }
            }
            if(seleted.get(i)==5){
                lis = "bbc";
                Integer num = source.get(lis);
                if (num == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                }

            }

            if(seleted.get(i)==6){
                lis = "scientist";
                Integer num = source.get(lis);
                if (num == null) {
                    source.put(lis, 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                } else {
                    source.put(lis, num + 10*count + maxValueInMap);
                    saveToSDMap(source, "ChooseUrNews/sources.dat");
                    //Toast.makeText(MainActivity.this,lis, Toast.LENGTH_SHORT).show();
                }
            }
        }
        saveToSDMap(source,"ChooseUrNews/sources.dat");
        seleted.clear();
    }

    /**Overriding the in-built function of creating an activity page to display the list of news articles in recycler view*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);

        if(!checkPermission()) {
            requestPermission();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            buildDialogBox();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

        if(!isNetworkAvailable()){
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Internet");
            alert.setMessage("Not Available");
            alert.setPositiveButton("OK",null);
            alert.show();
        }

        File path = Environment.getExternalStorageDirectory();
        File dir = new File(valueOf(path)+"/ChooseUrNews");
        if (!dir.exists()) {
            dir.mkdir();
            initViews();
        }
        newsArrticles.clear();
        newsArrticles.addAll(readFromSd("ChooseUrNews/data.dat"));

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
            Toast.makeText(MainActivity.this, "loaded saved news", Toast.LENGTH_SHORT).show();
            initSwipe();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isNetworkAvailable()){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("Internet");
                    alert.setMessage("Not Available");
                    alert.setPositiveButton("OK",null);
                    alert.show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                else {
                    newsArrticles.clear();
                    NewsStore.newsArticles.clear();
                    initViews();

                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        });



    }


    /**Function for getting response from sources and setting up the UI*/
    private void initViews(){
        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Call<ArticlesResponse> call = NewsAPI.getApi().getArticles("the-hindu", "latest");
        call.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"hindu");
            }

            @Override
            public void onFailure(Call<ArticlesResponse> call, Throwable t) {
                //Toast.makeText(MainActivity.this, "Error Received the-hindu", Toast.LENGTH_SHORT).show();


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
                //Toast.makeText(MainActivity.this, "Error Received hacker-news", Toast.LENGTH_SHORT).show();


            }
        });

        Call<ArticlesResponse> call3 = NewsAPI.getApi().getArticles("bbc-news", "top");
        call3.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call3, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"bbc");
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call3, Throwable t) {
                //Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


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
                //Toast.makeText(MainActivity.this, "Error Received 4", Toast.LENGTH_SHORT).show();


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
               // Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });

        Call<ArticlesResponse> call6 = NewsAPI.getApi().getArticles("espn-cric-info", "latest");
        call6.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call6, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"espn");
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call6, Throwable t) {
               // Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


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
                //Toast.makeText(MainActivity.this, "Error Received", Toast.LENGTH_SHORT).show();


            }
        });

        Call<ArticlesResponse> call9 = NewsAPI.getApi().getArticles("techradar", "latest");
        call9.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call9, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"techradar");
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call9, Throwable t) {
                //Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });

        Call<ArticlesResponse> call10 = NewsAPI.getApi().getArticles("the-sport-bible", "top");
        call10.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call10, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"the-sport-bible");
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call10, Throwable t) {
               // Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });

        Call<ArticlesResponse> call11 = NewsAPI.getApi().getArticles("entertainment-weekly", "top");
        call11.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call11, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"entertainment-weekly");
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call11, Throwable t) {
                //Toast.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });

        Call<ArticlesResponse> call12 = NewsAPI.getApi().getArticles("the-wall-street-journal", "top");
        call12.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call12, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"the-wall-street-journal");
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call12, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received", Toast.LENGTH_SHORT).show();


            }
        });

        Call<ArticlesResponse> call13 = NewsAPI.getApi().getArticles("buzzfeed", "top");
        call13.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call13, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"buzzfeed");
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call13, Throwable t) {
                //.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


            }
        });

        Call<ArticlesResponse> call14 = NewsAPI.getApi().getArticles("recode", "top");
        call14.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call14, Response<ArticlesResponse> response) {
                ArticlesResponse articlesResponse = response.body();
                NewsStore.addArticle(articlesResponse.getArticles(),"recode");
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call14, Throwable t) {
                //.makeText(MainActivity.this, "Error Received 3", Toast.LENGTH_SHORT).show();


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
                ArrayList<Article> readArticles = readFromSd("ChooseUrNews/read.dat");
                int sizeofnewsarticle = newsArrticles.size();

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
                            if(time.length()>20){
                                time = time.substring(0,19)+"Z";
                            }
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

                    if(isPresent(readArticles,newsArrticles.get(prindex))){
                        prior-= 40;
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
                        for(int i = 0 ;i < NewsStore.newsArticles.size(); i++){
                            new MyTask().execute(NewsStore.newsArticles.get(i));
                            NewsStore.modify(newsArrticles.get(i),i);
                            homeNewsAdapter.modifyadapter(NewsStore.newsArticles.get(i),i);
                            homeNewsAdapter.notifyItemChanged(i);
                            homeNewsAdapter.notifyDataSetChanged();
                            newsRecyclerView.setAdapter(homeNewsAdapter);
                        }


            }
            @Override
            public void onFailure(Call<ArticlesResponse> call8, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Received", Toast.LENGTH_SHORT).show();

                Map<String, Integer> sourcemap = readFromSdMap("ChooseUrNews/sources.dat");
                Map<String, Integer> frequency = readFromSdMap("ChooseUrNews/freq.dat");
                ArrayList<Article> readArticles = readFromSd("ChooseUrNews/read.dat");
                int sizeofnewsarticle = newsArrticles.size();

                Date currentdate = new Date();
                Date d1 = null;
                Date d2 = null;
                for (int prindex = 0; prindex < sizeofnewsarticle; prindex++) {

                    String srcname = newsArrticles.get(prindex).getSourcename();
                    String ttlname = newsArrticles.get(prindex).getTitle();
                    String time = newsArrticles.get(prindex).getPublishedAt();
                    int prior = 0;

                    if (time != null) {
                        try {
                            if(time.length()>20){
                                time = time.substring(0,19)+"Z";
                            }
                            String inputDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                            String outputDateFormat = "EEE, d MMM yyyy HH:mm";
                            SimpleDateFormat inputFormat = new SimpleDateFormat(inputDateFormat);
                            SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);

                            String sds = outputFormat.format(currentdate);
                            d1 = outputFormat.parse(sds);
                            d2 = inputFormat.parse(time);

                            long diff = d1.getTime() - d2.getTime();

                            int diffHours = (int) (diff / (60 * 60 * 1000) % 24);
                            int diffDays = (int) diff / (24 * 60 * 60 * 1000);
                            if (diffDays > 1) {
                                prior = -20 * diffDays;
                            } else if (diffHours > 12) {
                                prior = -12;
                            } else if (diffHours > 1) {
                                prior = -1 * diffHours;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else prior = -5;
                    StringTokenizer ttltoken = new StringTokenizer(ttlname.toLowerCase(), " ,.!-");
                    while (ttltoken.hasMoreTokens()) {
                        String nw = ttltoken.nextToken();
                        if (stop.contains(nw)) {
                            continue;
                        }
                        if (frequency.containsKey(nw)) {
                            prior += frequency.get(nw);
                        }
                    }


                    if (sourcemap.containsKey(srcname)) {
                        prior += 2 * sourcemap.get(srcname);
                    }

                    if (isPresent(readArticles, newsArrticles.get(prindex))) {
                        prior -= 40;
                    }

                    newsArrticles.get(prindex).setPriority(prior);

                }

                Collections.sort(newsArrticles, new Comparator<Article>() {
                    @Override
                    public int compare(Article article, Article t1) {
                        return t1.getPriority().compareTo(article.getPriority());
                    }
                });

                saveToSD(newsArrticles, "ChooseUrNews/data.dat");
                NewsStore.setArticle(newsArrticles);
                homeNewsAdapter.addHomeNewsAdapter(newsArrticles);
                homeNewsAdapter.notifyDataSetChanged();
                newsRecyclerView.setAdapter(homeNewsAdapter);
                for (int i = 0; i < NewsStore.newsArticles.size(); i++) {
                    new MyTask().execute(NewsStore.newsArticles.get(i));
                    homeNewsAdapter.modifyadapter(NewsStore.newsArticles.get(i), i);
                    homeNewsAdapter.notifyItemChanged(i);
                    homeNewsAdapter.notifyDataSetChanged();
                    newsRecyclerView.setAdapter(homeNewsAdapter);
                }
            }
        });

        initSwipe();

    }

    /**This Function performs actions after the user likes any article
     * It records the source from where the news have come and also records the title of the news article */
    public void afterLike(Article article){
        String lis = article.getSourcename();
        Map<String, Integer> source = readFromSdMap("ChooseUrNews/sources.dat");
        Integer num = source.get(lis);
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

    /**This Function performs actions after the user dislikes any article
     * It records the source from where the news have come and also records the title of the news article */
    public void afterDislike(Article article){
        String lis = article.getSourcename();
        Map<String, Integer> source = readFromSdMap("ChooseUrNews/sources.dat");
        Integer num = source.get(lis);
        if (num == null) {
            source.put(lis, -1);
            saveToSDMap(source, "ChooseUrNews/sources.dat");
        } else {
            source.put(lis, num - 1);
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
                frequency.put(nw, i - 1);
            }
            else {
                frequency.put(nw, -1);
            }
        }

        saveToSDMap(frequency, "ChooseUrNews/freq.dat");
    }

    /**Function for adding the swipe feature to the card in the recycler view.
     * Here on right swipe news article gets liked and afterLike function is called and
     * on left swipe news article gets disliked and afterDislike function is called */
    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if(direction == ItemTouchHelper.RIGHT) {


                    ArrayList<Article> ls = readFromSd("ChooseUrNews/like.dat");
                    if (isPresent(ls, newsArrticles.get(position))) {
                        Toast.makeText(MainActivity.this, "Already Liked", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ls.add(newsArrticles.get(position));
                        saveToSD(ls, "ChooseUrNews/like.dat");
                        afterLike(newsArrticles.get(position));

                    }

                    newsArrticles.remove(newsArrticles.get(position));
                    saveToSD(newsArrticles,"ChooseUrNews/data.dat");
                    NewsStore.setArticle(newsArrticles);
                    homeNewsAdapter.notifyItemRemoved(position);
                    homeNewsAdapter.notifyDataSetChanged();
                }

                if(direction == ItemTouchHelper.LEFT){
                    ArrayList<Article> ls = readFromSd("ChooseUrNews/like.dat");
                    if (isPresent(ls, newsArrticles.get(position))) {
                        Toast.makeText(MainActivity.this, "Already DisLiked", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ls.add(newsArrticles.get(position));
                        saveToSD(ls, "ChooseUrNews/like.dat");
                        afterDislike(newsArrticles.get(position));

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


                    if(dX < 0) {

                        p.setColor(Color.parseColor("#000000"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.thumsdown);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                    else {
                        p.setColor(Color.parseColor("#000000"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.thumbsup);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(newsRecyclerView);
    }


    /** Overriding the in built function to create icons at the title bar for navigation to different pages
     * and to perform different functions*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainact_menu, menu);
        return true;
    }

    /**Function to determine the action to be taken when different icons are pressed on the title bar.
     * Here only one icon is available for navigating to the bookmark page*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookmark:
                if(!checkPermission())
                    requestPermission();
                File path2 = Environment.getExternalStorageDirectory();
                File dir2 = new File(valueOf(path2)+"/ChooseUrNews/bookmark.dat");
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

            case R.id.settings:
                buildDialogBox();
                count++;
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
