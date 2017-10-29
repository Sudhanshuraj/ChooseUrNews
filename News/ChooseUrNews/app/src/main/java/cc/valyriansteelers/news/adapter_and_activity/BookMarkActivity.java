package cc.valyriansteelers.news.adapter_and_activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
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
import java.util.ArrayList;

import cc.valyriansteelers.news.R;
import cc.valyriansteelers.news.article_object.Article;
import cc.valyriansteelers.news.article_object.NewsStore;

/**
 * Created by sudhanshu on 18/10/17.
 */

/** This is the class for creating the the page in which all the bookmarked articles come in*/

public class BookMarkActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;

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

    /**Function for checking whether external storage is readable and writeable */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**Function for checking whether permission of reading and writing on the external storage is granted or not*/
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(BookMarkActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

    /**Function for reading an arraylist of news articles from external storage at given destination*/
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

    /**Function for saving an arraylist of news articles on external storage at specified destination*/
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

    /**Variable storing the bookmarked news articles*/
    ArrayList<Article> bookmarkList = readFromSd("ChooseUrNews/bookmark.dat");
    private RecyclerView newsRecyclerView;
    private HomeNewsAdapter bkNewsAdapter = new HomeNewsAdapter(bookmarkList);
    private Paint p=new Paint();
    public static ArrayList<Article> test = new ArrayList<>();


    /**Overriding the in-built function of creating an activity page to display the list of bookmarked articles in recycler view*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_main);
        if(!checkPermission())
            requestPermission();
        initViews();
    }

    /**Function responsible for creating the view of the page.
     * It creates cards for all the bookmarked news articles and put them in the recycler view*/
    private void initViews() {
        newsRecyclerView =  (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        NewsStore.setArticle(bookmarkList);
        bkNewsAdapter.notifyDataSetChanged();
        newsRecyclerView.setAdapter(bkNewsAdapter);
        for(int i = 0 ;i < NewsStore.newsArticles.size(); i++){
            new BookMarkActivity.MyTask().execute(NewsStore.newsArticles.get(i));
            NewsStore.modify(bookmarkList.get(i),i);
            bkNewsAdapter.modifyadapter(NewsStore.newsArticles.get(i),i);
            bkNewsAdapter.notifyItemChanged(i);
            bkNewsAdapter.notifyDataSetChanged();
            newsRecyclerView.setAdapter(bkNewsAdapter);
        }
        initSwipe();
    }

    /**Function for adding the swipe feature to the card in the recycler view.
     * Here on left swipe news article gets deleted and updated list of bookmarked news gets saved on external storage*/
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

    /** Overriding the in built function to create icons at the title bar for navigation to different pages
     * and to perform different functions*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.like_bookmark, menu);
        return true;
    }

    /**Function to determine the action to be taken when different icons are pressed on the title bar.
     * Here only one icon is available for navigating to the main page*/
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
