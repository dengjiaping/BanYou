package com.quanliren.quan_one.activity.seting;

import android.os.Bundle;
import android.webkit.WebView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

public class FunctionActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serviceinfo);

		setTitleTxt("功能介绍");
		WebView wView = (WebView) findViewById(R.id.webview);
		wView.getSettings().setJavaScriptEnabled(true);
		wView.loadUrl("file:///android_asset/function.html");
	}
}
