package com.quanliren.quan_one.activity.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.ProgressWebView;

public class ActActivity extends BaseActivity {

	String url;
	String title;

	@ViewInject(id = R.id.back, click = "backs")
	View back;
	@ViewInject(id = R.id.refere, click = "refere")
	View refere;
	@ViewInject(id = R.id.go, click = "go")
	View go;
	@ViewInject(id = R.id.webview)
	ProgressWebView wView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		url = getIntent().getExtras().getString("url");
		title = getIntent().getExtras().getString("title");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actinfo);

		setTitleTxt(title);
		WebSettings webSettings = wView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);

		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		wView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				wView.loadUrl(url);
				return true;
			}
		});
		wView.addJavascriptInterface(new JavaScriptinterface(), "demo");
		wView.loadUrl(url);

	}

	class JavaScriptinterface {
		@JavascriptInterface
		public void clickOnAndroid(int num) {
			User user = ac.getUserInfo();
			user.setPowernum((Integer.valueOf(user.getPowernum()) - num) + "");
			UserTable ut = new UserTable(user);
			ac.finalDb.delete(ut);
			ac.finalDb.save(ut);
		}

		@JavascriptInterface
		public void buyVip() {
			startActivity(new Intent(ActActivity.this,
					ShopVipDetail.class));
		}
	}

	public void backs(View v) {
		wView.goBack();
	}

	public void refere(View v) {
		wView.reload();
	}

	public void go(View v) {
		wView.goForward();
	}
}
