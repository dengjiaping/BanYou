package com.quanliren.quan_one.activity.seting;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

public class HtmlActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serviceinfo);

		setTitleTxt("用户协议");
		WebView wView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = wView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		wView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				System.out.println(url);
				return true;
			}
		});
		wView.loadUrl("file:///android_asset/html/html.html");
	}
}
