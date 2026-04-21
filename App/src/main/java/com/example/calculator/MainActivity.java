package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        // Добавляем JavaScript Interface для шеринга
        webView.addJavascriptInterface(new ShareInterface(), "AndroidShare");

        webView.requestFocusFromTouch();
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("file:///android_asset/calculator.html");
    }

    // Класс для взаимодействия с JavaScript
    public class ShareInterface {

        @JavascriptInterface
        public void shareText(String title, String text, String url) {
            runOnUiThread(() -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                shareIntent.putExtra(Intent.EXTRA_TEXT, text + "\n" + url);

                Intent chooser = Intent.createChooser(shareIntent, "Поделиться через");
                startActivity(chooser);
            });
        }

        @JavascriptInterface
        public void copyToClipboard(String text) {
            runOnUiThread(() -> {
                android.content.ClipboardManager clipboard =
                        (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Calculator Link", text);
                clipboard.setPrimaryClip(clip);

                // Показываем Toast через JavaScript callback или можно через evaluateJavascript
                webView.evaluateJavascript(
                        "alert('✅ Ссылка скопирована!')",
                        null
                );
            });
        }
    }
}