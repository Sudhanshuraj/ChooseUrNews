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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(LikedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(LikedActivity.this,
                            "Permission accepted", //Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(LikedActivity.this,
                            "Permission denied", //Toast.LENGTH_LONG).show();

                }
                break;
        }
    }*/


    public static ArrayList<Article> readFromSd(String dest) {

        ArrayList<Article> savedArrayList = null;
        if(isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();
            try {
                File dir = new File(String.valueOf(path) + "/ChooseUrNews");
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


    ArrayList<Article> likedArticles = readFromSd("ChooseUrNews/like.dat");
    private RecyclerView newsRecyclerView;
    private LikedNewsAdapter likedNewsAdapter = new LikedNewsAdapter(likedArticles) ;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!checkPermission())
            requestPermission();
        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        NewsStore.setArticle(likedArticles);
        likedNewsAdapter.notifyDataSetChanged();
        newsRecyclerView.setAdapter(likedNewsAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.like_bookmark, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
