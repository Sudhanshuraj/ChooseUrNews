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
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import cc.valyriansteelers.news.model.Article;
import cc.valyriansteelers.news.model.NewsStore;

/**
 * Created by sudhanshu on 18/10/17.
 */

public class BookMarkActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;



    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(BookMarkActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }


    public static ArrayList<Article> readFromSd(String dest) {

        ArrayList<Article> savedArrayList = null;
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

    void saveToSD(ArrayList<Article> articles,String dest) {

        if (isExternalStorageWritable()) {
            File path = Environment.getExternalStorageDirectory();
            if (checkPermission()){
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
                    //Toast.makeText(BookMarkActivity.this, "Saved in External storage", //Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                    //Toast.makeText(BookMarkActivity.this, ex.getMessage(), //Toast.LENGTH_LONG).show();
                }
            }
            else
                requestPermission();
        }
    }
    
    ArrayList<Article> bookmarkList = readFromSd("ChooseUrNews/bookmark.dat");
    private RecyclerView newsRecyclerView;
    private HomeNewsAdapter bkNewsAdapter = new HomeNewsAdapter(bookmarkList);
    private Paint p=new Paint();
    public static ArrayList<Article> test = new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_main);
        if(!checkPermission())
            requestPermission();
        initViews();
        

    }

    private void initViews() {
        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        NewsStore.setArticle(bookmarkList);
        bkNewsAdapter.notifyDataSetChanged();
        newsRecyclerView.setAdapter(bkNewsAdapter);
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

                    bookmarkList.remove(bookmarkList.get(position));
                    saveToSD(bookmarkList,"ChooseUrNews/bookmark.dat");
                    NewsStore.setArticle(bookmarkList);
                    bkNewsAdapter.notifyItemRemoved(position);
                    bkNewsAdapter.notifyDataSetChanged();

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
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.bin);
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
        getMenuInflater().inflate(R.menu.like_bookmark, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                ArrayList<Article> ls = readFromSd("ChooseUrNews/data.dat");
                NewsStore.setArticle(ls);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
