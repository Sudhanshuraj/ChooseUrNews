package cc.valyriansteelers.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import cc.valyriansteelers.news.model.NewsStore;

public class NewsDetailsActivity extends AppCompatActivity {
    public static final String KEY_INDEX = "news_index";
    private ProgressBar progressBar;
    private WebView webView;
    //Button bt;

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

    public void shareText(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBodyText = "Your shearing message goes here";
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject/Title");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(intent, "Choose sharing method"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share1:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String forsharingurl = "Why U Want To share Its URL!!!!";
                int index = getIntent().getIntExtra(KEY_INDEX,-1);

                if(index!=-1) {
                    forsharingurl = NewsStore.getNewsArticles().get(index).getUrl();
                   // imageuri=Uri.parse(NewsStore.getNewsArticles().get(index).getUrlToImage());
                }
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, forsharingurl);

                //sharingIntent.putExtra(Intent.EXTRA_STREAM,imageuri);

                startActivity(Intent.createChooser(sharingIntent, "Whom To Share"));
                return true;

            case android.R.id.home:
                finish();
                return true;

             
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }

}


