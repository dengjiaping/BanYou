package com.quanliren.quan_one.activity.shop;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.custom.ProgressWebView;
import com.quanliren.quan_one.fragment.SetingMoreFragment;
import com.quanliren.quan_one.util.URL;

public class VipCardActivity extends BaseActivity {
	@ViewInject(id = R.id.webview)
	ProgressWebView wView;
	@ViewInject(id = R.id.back, click = "backs")
	View back;
	@ViewInject(id = R.id.refere, click = "refere")
	View refere;
	@ViewInject(id = R.id.go, click = "go")
	View go;
	String channelType = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		channelType = getIntent().getExtras().getString("channelType");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actinfo);
		setTitleTxt("购买会员");

		wView.setVerticalScrollBarEnabled(false);
		wView.setHorizontalScrollBarEnabled(false);
		WebSettings webSettings = wView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setBlockNetworkImage(false);
		CookieManager.getInstance().setAcceptCookie(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		wView.setWebViewClient(new MyWebViewClient());
		wView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
		// wView.loadUrl("http://192.168.1.15:8080/WS_WAP_PAYWAP-JAVA-UTF-8/alipayapi.jsp");

		LoginUser user = ac.getUser();
		if (user != null) {
			wView.loadUrl(URL.URL + "/client/pay/to_alipay.php?token="
					+ user.getToken());
		}
	}

	AtomicBoolean ab = new AtomicBoolean(false);
	String awid = "";

	final class MyWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (awid.equals("")) {
				if (url.indexOf("awid") > -1) {
					String str = url.substring(url.indexOf("awid=")).replace(
							"awid=", "");
					awid = str;
				}
				view.loadUrl(url);
				return true;
			}
			return false;
		}

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d("WebView", "onPageStarted");
			super.onPageStarted(view, url, favicon);
			
			if (awid.equals("")) {
				if (url.indexOf("awid") > -1) {
					String str = url.substring(url.indexOf("awid=")).replace(
							"awid=", "");
					awid = str;
				}
			}
		}

		public void onPageFinished(final WebView view, String url) {
			// view.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML);");
			super.onPageFinished(view, url);
			if (!awid.equals("") && ab.compareAndSet(false, true)) {
				view.loadUrl("https://wappaygw.alipay.com/cashier/cashier_gateway_pay.htm?channelType="
						+ channelType + "&awid=" + awid);
				awid = "";
			}
		}
		
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			// TODO Auto-generated method stub
			 handler.proceed();
		}
	}

	final class InJavaScriptLocalObj {
		@JavascriptInterface
		public void showSource(String html) {
			// try {
			// ReadWriteFile.creatTxtFile();
			// ReadWriteFile.writeTxtFile(html);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			Log.d("HTML", html);
		}

		@JavascriptInterface
		public void callBack(int powernum, String vipday) {
			sendb=true;
			Intent i = new Intent(SetingMoreFragment.UPDATE_USERINFO);
			sendBroadcast(i);
		}

		@JavascriptInterface
		public void close() {
			finish();
		}
	}

	boolean sendb=false;

	public void backs(View v) {
		wView.goBack();
	}

	public void refere(View v) {
		wView.reload();
	}

	public void go(View v) {
		wView.goForward();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(!sendb){
			Intent i = new Intent(SetingMoreFragment.UPDATE_USERINFO);
			sendBroadcast(i);
		}
	}
	
	@Override
	public void onBackPressed() {
		dialogFinish();
	}
	@Override
	public void back(View v) {
		dialogFinish();
	}
	
	public void dialogFinish() {
		new IosCustomDialog.Builder(VipCardActivity.this)
				.setMessage("您确定要取消本次交易吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).create().show();
	}
}
