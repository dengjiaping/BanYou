package com.quanliren.quan_one.activity.reg;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.seting.ServiceInfoActivity;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class RegFirst extends BaseActivity {

	@ViewInject(id = R.id.agreement2)
	TextView agreement2;
	@ViewInject(id = R.id.phone)
	EditText phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_first);
		title.setText(R.string.reg);
		this.setTitleRightTxt(R.string.next);
		agreement2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		
		agreement2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(RegFirst.this,ServiceInfoActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	public void rightClick(View v) {
		String pstr = phone.getText().toString();
		if (Util.isMobileNO(pstr)) {
			ac.finalHttp.post(URL.REG_FIRST, getAjaxParams("mobile", pstr), callBack);
		} else {
			showCustomToast("请输入正确的手机号码！");
			return;
		}
	}

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog(1);
		};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};
		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					Intent i = new Intent(RegFirst.this, RegGetCode.class);
//					if(!jo.isNull(URL.RESPONSE)){
//						jo=jo.getJSONObject(URL.RESPONSE);
//						i.putExtra("code", jo.getString("authcode"));
//					}
					i.putExtra("phone", phone.getText().toString());
					startActivity(i);
					finish();
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};
}
