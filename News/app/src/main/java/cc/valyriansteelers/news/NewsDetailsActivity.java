package cc.valyriansteelers.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class NewsDetailsActivity extends AppCompatActivity {
    public static final String KEY_INDEX = "news_index";
    private ProgressBar progressBar;
    private WebView webView;

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.activity_newsDetail_webview);
        progressBar = (ProgressBar) findViewById(R.id.activity_newsDetail_progressbar);

        int index = getIntent().getIntExtra(KEY_INDEX,-1);
        if(index != -1){
            updateNewsdetails(index);
            Toast.makeText(NewsDetailsActivity.this,Integer.toString(NewsStore.getNewsArticles().get(index).getPriority()), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(NewsDetailsActivity.this, "Sorry, Incorrect index passed", Toast.LENGTH_SHORT).show();
        }

    }

    public void updateNewsdetails (int index){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NewsDetailsActivity.this, "Error in Loading Webpage", Toast.LENGTH_SHORT).show();

            }

        });
        webView.loadUrl(NewsStore.getNewsArticles().get(index).getUrl());
        getSupportActionBar().setTitle(NewsStore.getNewsArticles().get(index).getTitle());

    }

    public static void launch(Context context, int index){
        Intent intent = new Intent(context, NewsDetailsActivity.class);
        intent.putExtra(KEY_INDEX, index);
        context.startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share1:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String forsharingurl = "Why U Want To share Its URL!!!!";
                int index = getIntent().getIntExtra(KEY_INDEX, -1);

                if (index != -1) {
                    forsharingurl = NewsStore.getNewsArticles().get(index).getUrl();

                }
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, forsharingurl);

                startActivity(Intent.createChooser(sharingIntent, "Whom To Share"));
                return true;

            case R.id.addbookmark:
                ArrayList<Article> bk = readFromSd("ChooseUrNews/bookmark.dat");
                int indexi = getIntent().getIntExtra(KEY_INDEX, -1);
                if (isPresent(bk, NewsStore.getNewsArticles().get(indexi))) {
                    Toast.makeText(NewsDetailsActivity.this, "Already Present", Toast.LENGTH_SHORT).show();
                }
                else {
                    bk.add(NewsStore.getNewsArticles().get(indexi));
                    saveToSD(bk, "ChooseUrNews/bookmark.dat");
                    Toast.makeText(NewsDetailsActivity.this, "Bookmarked", Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;


            default:
            return super.onOptionsItemSelected(item);
        }
    }

}


