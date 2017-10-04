package cc.valyriansteelers.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import cc.valyriansteelers.news.model.NewsStore;

public class NewsDetailsActivity extends AppCompatActivity {
    public static final String KEY_INDEX = "news_index";
    private ProgressBar progressBar;
    private WebView webView;


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
