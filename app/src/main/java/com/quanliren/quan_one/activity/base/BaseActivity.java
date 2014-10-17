package com.quanliren.quan_one.activity.base;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxParams;
import com.nineoldandroids.view.ViewHelper;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.shop.ShopVipDetail;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.activity.user.LoginActivity;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.custom.CustomProgressBar;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.util.URL;

public abstract class BaseActivity extends FinalActivity {

	public AppClass ac;

	@ViewInject(id = R.id.title)
	public TextView title;
	@ViewInject(id = R.id.title_right_txt)
	public TextView title_right_txt;
	@ViewInject(id = R.id.title_left_icon)
	public ImageView title_left_icon;
	@ViewInject(id = R.id.title_left_icon)
	public ImageView title_right_icon;
	@ViewInject(id = R.id.title_left_btn, click = "back")
	public View title_left_btn;
	@ViewInject(id = R.id.title_right_btn, click = "rightClick")
	public View title_right_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		AM.getActivityManager().pushActivity(this);
		ac = (AppClass) getApplication();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
		if (parent != null)
			ViewHelper.setAlpha(parent, 0);
		// animate(parent).setDuration(0).alpha(0).start();
	}

	public void setTitleTxt(String title) {
		if (this.title != null)
			this.title.setText(title);
	}

	public void setTitleTxt(int title) {
		if (this.title != null)
			this.title.setText(title);
	}

	public void setTitleRightTxt(String str) {
		if (this.title_right_btn != null)
			this.title_right_btn.setVisibility(View.VISIBLE);
		if (this.title_right_txt != null)
			this.title_right_txt.setText(str);
	}

	public void setTitleRightTxt(int str) {
		if (this.title_right_btn != null)
			this.title_right_btn.setVisibility(View.VISIBLE);
		if (this.title_right_txt != null)
			this.title_right_txt.setText(str);
	}

	public void back(View v) {
		closeInput();
		finish();
	}

	public void rightClick(View v) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (baseBroadcase != null) {
			unregisterReceiver(baseBroadcase);
		}
		closeInput();
		if(toast!=null)
			toast.cancel();
		AM.getActivityManager().popActivity(this);
	}

	public void closeInput() {
		if (getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(), 0);
		}
	}

	protected void showKeyBoard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	Toast toast = null;

	public void showCustomToast(String str) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(str);
		if(toast==null){
			toast = new Toast(getApplicationContext());
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.setView(layout);
		toast.show();
	}

	public void showToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void showIntentErrorToast() {
		showCustomToast("网络连接失败");
		// Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
	}

	public CustomProgressBar parentDialog;

	public void customDismissDialog() {
		if (parentDialog != null && parentDialog.isShowing())
			parentDialog.dismiss();
	}

	public void customShowDialog(String str) {
		parentDialog = new CustomProgressBar(this, R.style.dialog);
		parentDialog.setMessage(str);
		parentDialog.show();
	}

	String[] str = new String[] { "", "正在获取数据", "正在登录", "正在提交", "请稍等" };

	public void customShowDialog(int i) {
		parentDialog = new CustomProgressBar(this, R.style.dialog);
		parentDialog.setMessage(str[i]);
		parentDialog.show();
	}

	@ViewInject(id = R.id.parent, click = "closeMenull")
	public View parent;// menu弹出的透明背景
	public PopFactory menupop;// menu

	public void closeMenull(View v) {
		menupop.closeMenu();
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (menupop != null) {
				menupop.closeMenu();
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (menupop != null && menupop.isShow) {
			menupop.closeMenu();
			return;
		}
		super.onBackPressed();
	}

	public AjaxParams getAjaxParams() {
		LoginUser user = ac.getUser();
		AjaxParams ap = new AjaxParams()
				.put("versionName", ac.cs.getVersionName())
				.put("versionCode", String.valueOf(ac.cs.getVersionCode()))
				.put("channel", ac.cs.getChannel())
				.put("clientType", "android");
		if (user != null) {
			ap.put("token", user.getToken());
		}
		return ap;
	}

	public AjaxParams getAjaxParams(String str, String strs) {
		AjaxParams ap = getAjaxParams().put(str, strs);
		return ap;
	}

	public void showFailInfo(JSONObject jo) {
		try {
			int status=jo.getInt(URL.STATUS);
			switch (status) {
			case 1:
				showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(URL.INFO));
				break;
			case 2:
				loginOut();
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private BaseBroadcast baseBroadcase;
	private Handler broadcaseHandler;

	public void setBroadcaseHandler(Handler broadcaseHandler) {
		this.broadcaseHandler = broadcaseHandler;
	}

	class BaseBroadcast extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			if (broadcaseHandler != null) {
				Message msg = broadcaseHandler.obtainMessage();
				msg.obj = intent;
				msg.sendToTarget();
			}
		}
	}

	public void receiveBroadcast(String fileter, Handler handler) {
		registerReceiver(baseBroadcase = new BaseBroadcast(), new IntentFilter(
				fileter));
		this.broadcaseHandler = handler;
	}

	public void receiveBroadcast(String[] fileter, Handler handler) {
		IntentFilter filter = new IntentFilter();
		for (int i = 0; i < fileter.length; i++) {
			filter.addAction(fileter[i]);
		}
		registerReceiver(baseBroadcase = new BaseBroadcast(), filter);
		this.broadcaseHandler = handler;
	}

	public void loginOut(){
		ac.finalDb.deleteByWhere(LoginUser.class, "");
		startActivity(new Intent(BaseActivity.this,LoginActivity.class));
		AM.getActivityManager().popAllActivity();
	}
	
	public void goVip(){
		new IosCustomDialog.Builder(this)
		.setMessage("只有成为会员之后才可以使用哦~")
		.setTitle("提示")
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0,
							int arg1) {
					}
				})
		.setPositiveButton("成为会员",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						startActivity(new Intent(BaseActivity.this,
								ShopVipDetail.class));
					}
				}).create().show();
	}
}
