package cc.valyriansteelers.news;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import cc.valyriansteelers.news.model.Article;
import cc.valyriansteelers.news.model.NewsStore;

/*
*
 * Created by sudhanshu on 16/10/17.

*/


public class LikedActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;



    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static ArrayList<Article> readFromSd() {
        ArrayList<Article> savedArrayList = null;
        if(isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();
            try {

                FileInputStream fis =
                        new FileInputStream(
                                new File(path, "like.dat")
                        );
                ObjectInputStream is = new ObjectInputStream(fis);
                savedArrayList = (ArrayList<Article>) is.readObject();
                is.close();
                fis.close();
                //Toast.makeText(LikedActivity.this, "read from external storage", Toast.LENGTH_SHORT).show();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                //Toast.makeText(LikedActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return savedArrayList;
        }
        else {

            //Toast.makeText(LikedActivity.this, "Error in reading", Toast.LENGTH_SHORT).show();
            return savedArrayList;
        }
    }


    //public static ArrayList<Article> likedArticles =new ArrayList<>();
    ArrayList<Article> likedArticles = readFromSd();
    private RecyclerView newsRecyclerView;
    private LikedNewsAdapter likedNewsAdapter = new LikedNewsAdapter(likedArticles) ;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //ArrayList<Article> likedArticles = readFromSd();
        NewsStore.setArticle(likedArticles);
        Toast.makeText(LikedActivity.this, "Yo", Toast.LENGTH_SHORT).show();
        likedNewsAdapter.notifyDataSetChanged();
        newsRecyclerView.setAdapter(likedNewsAdapter);
        int x = likedNewsAdapter.getItemCount();
        int y = likedArticles.size();
        String s = Integer.toString(y);
        Toast.makeText(LikedActivity.this, s, Toast.LENGTH_SHORT).show();

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
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
