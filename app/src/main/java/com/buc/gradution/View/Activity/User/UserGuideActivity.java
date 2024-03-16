package com.buc.gradution.View.Activity.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.buc.gradution.R;

public class UserGuideActivity extends AppCompatActivity {
    private WebView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);
        initComponents();
        String url = getIntent().getStringExtra("url");
        pdfView.loadUrl(url);
        pdfView.getSettings().setJavaScriptEnabled(true);
        pdfView.setWebViewClient(new WebViewClient());
    }
    private void initComponents(){
        pdfView = findViewById(R.id.pdf_view);
    }
}