package com.quanliren.quan_one.fragment.base;

import java.util.concurrent.atomic.AtomicBoolean;

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
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.quanliren.quan_one.activity.user.LoginActivity;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.custom.CustomProgressBar;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.util.URL;

public class MenuFragmentBase extends Fragment {

	public AppClass ac;

	@ViewInject(id = R.id.title_left_btn, click = "leftClick")
	public View leftBtn;
	@ViewInject(id = R.id.title)
	public TextView title;
	@ViewInject(id = R.id.title_right_btn, click = "rightClick")
	public View rightBtn;
	@ViewInject(id = R.id.title_left_txt)
	public TextView title_left_txt;
	@ViewInject(id = R.id.title_right_txt)
	public TextView title_right_txt;
	@ViewInject(id = R.id.title_right_icon)
	public ImageView title_right_icon;
	public AtomicBoolean init = new AtomicBoolean(false);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ac = (AppClass) getActivity().getApplication();
		super.onCreate(savedInstanceState);
	}

	public void initInjectedView(View v) {
		FinalActivity.initInjectedView(this, v);
		if (parent != null)
			ViewHelper.setAlpha(parent, 0);
	}

	public void leftClick(View v) {
		// ((PropertiesActivity) getActivity()).showLeft();
	}

	public void rightClick(View v) {
	}

	public void setTitleRightTxt(String str) {
		if (this.rightBtn != null)
			this.rightBtn.setVisibility(View.VISIBLE);
		if (this.title_right_txt != null)
			this.title_right_txt.setText(str);
	}

	public void setTitleRightTxt(int str) {
		if (this.rightBtn != null)
			this.rightBtn.setVisibility(View.VISIBLE);
		if (this.title_right_txt != null)
			this.title_right_txt.setText(str);
	}

	public void setTitleLeftTxt(String str) {
		if (this.leftBtn != null)
			this.leftBtn.setVisibility(View.VISIBLE);
		if (this.title_left_txt!= null)
			this.title_left_txt.setText(str);
	}

	public void setTitleRightIcon(int str) {
		if (this.rightBtn != null)
			this.rightBtn.setVisibility(View.VISIBLE);
		if (this.title_right_icon != null)
			this.title_right_icon.setImageResource(str);
	}

	public void setTitleTxt(String title) {
		if (this.title != null)
			this.title.setText(title);
	}

	public void setTitleTxt(int title) {
		if (this.title != null)
			this.title.setText(title);
	}

	@ViewInject(id = R.id.parent, click = "closeMenull")
	public View parent;// menu弹出的透明背景
	public PopFactory menupop;// menu

	public void closeMenull(View v) {
		menupop.closeMenu();
	}

	public void closeInput() {
		if (getActivity().getCurrentFocus() != null) {
			((InputMethodManager) getActivity().getSystemService(
					getActivity().INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(getActivity().getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			((InputMethodManager) getActivity().getSystemService(
					getActivity().INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(getActivity().getCurrentFocus()
							.getWindowToken(), 0);
		}
	}

	public void showKeyBoard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
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

	Toast toast = null;

	public void showCustomToast(String str) {
		if (getActivity() == null) {
			return;
		}
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// 根据指定的布局文件创建一个具有层级关系的View对象
		// 第二个参数为View对象的根节点，即LinearLayout的ID
		View layout = inflater.inflate(R.layout.custom_toast,
				(ViewGroup) getActivity().findViewById(R.id.toast_layout_root));

		// 查找ImageView控件
		// 注意是在layout中查找
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(str);

		toast = new Toast(getActivity().getApplicationContext());
		// 设置Toast的位置
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		// 让Toast显示为我们自定义的样子
		toast.setView(layout);
		toast.show();
	}

	public void showFailInfo(JSONObject jo) {
		try {
			int status = jo.getInt(URL.STATUS);
			switch (status) {
			case 2:
				loginOut();
			case 1:
				showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
						URL.INFO));
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void loginOut() {
		try {
			ac.finalDb.deleteByWhere(LoginUser.class, "");
			startActivity(new Intent(getActivity(), LoginActivity.class));
			AM.getActivityManager().popAllActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CustomProgressBar parentDialog;

	public void customDismissDialog() {
		if (parentDialog != null && parentDialog.isShowing())
			parentDialog.dismiss();
	}

	public void customShowDialog(String str) {
		if (getActivity() == null) {
			return;
		}
		parentDialog = new CustomProgressBar(getActivity(), R.style.dialog);
		parentDialog.setMessage(str);
		parentDialog.show();
	}

	String[] str = new String[] { "", "正在获取数据", "正在登录", "正在提交", "请稍等" };

	public void customShowDialog(int i) {
		if (getActivity() == null) {
			return;
		}
		parentDialog = new CustomProgressBar(getActivity(), R.style.dialog);
		parentDialog.setMessage(str[i]);
		parentDialog.show();
	}

	public void showIntentErrorToast() {
		showCustomToast("网络连接失败");
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
		getActivity().registerReceiver(baseBroadcase = new BaseBroadcast(), new IntentFilter(
				fileter));
		this.broadcaseHandler = handler;
	}

	public void receiveBroadcast(String[] fileter, Handler handler) {
		IntentFilter filter = new IntentFilter();
		for (int i = 0; i < fileter.length; i++) {
			filter.addAction(fileter[i]);
		}
		getActivity().registerReceiver(baseBroadcase = new BaseBroadcast(), filter);
		this.broadcaseHandler = handler;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (baseBroadcase != null) {
			getActivity().unregisterReceiver(baseBroadcase);
		}
	}
	
	public void goVip(){
		new IosCustomDialog.Builder(getActivity())
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
						startActivity(new Intent(getActivity(),
								ShopVipDetail.class));
					}
				}).create().show();
	}
}
