package io.nbos.starterapp.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.nbos.starterapp.R;

/**
 * Created by vivekkiran on 7/15/16.
 */

public class SocialWebViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        String url = intent.getStringExtra("url");
        getSupportActionBar().setTitle(name);
        toolbar.setTitleTextColor(ContextCompat.getColor(SocialWebViewActivity.this, R.color.white));
        if (name.contains("linkedIn")) {
            toolbar.setBackgroundColor(ContextCompat.getColor(SocialWebViewActivity.this, R.color.linkedIn));
        }
        if (name.contains("instagram")) {
            toolbar.setBackgroundColor(ContextCompat.getColor(SocialWebViewActivity.this, R.color.instagram));
        }
        if (name.contains("github")) {
            toolbar.setBackgroundColor(ContextCompat.getColor(SocialWebViewActivity.this, R.color.github));
        }
        final WebView vw = (WebView) findViewById(R.id.webView);
        vw.getSettings().setLoadsImagesAutomatically(true);
        vw.getSettings().setJavaScriptEnabled(true);
        //  vw.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        vw.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println(" redirect to: " + url);
                if (url.startsWith("http://api.qa1")) {
                    Uri uri = Uri.parse(url);
                    String code = uri.getQueryParameter("code");
                    String state = uri.getQueryParameter("state");
                    System.out.println("Now call our API:" + code + state);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("service", name);
                    resultIntent.putExtra("code", code);
                    resultIntent.putExtra("state", state);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                    return true;
                }
                return false;
            }

//
        });
        vw.loadUrl(url);

    }
}