package com.quanliren.quan_one.activity.shop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.shop.ActActivity.JavaScriptinterface;
import com.quanliren.quan_one.bean.ProBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.ProgressWebView;

public class ProInfoActivity extends BaseActivity {

	ProBean bean;

	@ViewInject(id = R.id.use, click = "use")
	View use;
	@ViewInject(id = R.id.webview)
	ProgressWebView wView;
	@ViewInject(id = R.id.btomLL)
	View btomLL;
	@ViewInject(id = R.id.exchange, click = "exchange")
	View exchange;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		bean = (ProBean) getIntent().getExtras().getSerializable("bean");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.proinfo);

		setTitleTxt(bean.getTitle());
		WebSettings webSettings = wView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		wView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				wView.loadUrl(url);
				return true;
			}
		});
		wView.addJavascriptInterface(new JavaScriptinterface(), "demo");
		wView.loadUrl(bean.getActurl());

		if (bean.getWinstate() != 0) {
			btomLL.setVisibility(View.GONE);
		}
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
			startActivity(new Intent(ProInfoActivity.this, ShopVipDetail.class));
		}
	}

	public void exchange(View v) {
		Intent i = new Intent(this, ExchangeApplyActivity.class);
		i.putExtra("bean", bean);
		startActivityForResult(i, 1);
	}

	public void use(View v) {
		Intent i = new Intent(this, ExchangeUseActivity.class);
		i.putExtra("bean", bean);
		startActivityForResult(i, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case 1:
				bean.setWinstate(5);
				btomLL.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
