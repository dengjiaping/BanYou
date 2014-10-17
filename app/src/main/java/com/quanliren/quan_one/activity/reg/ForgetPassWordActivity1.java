package com.quanliren.quan_one.activity.reg;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class ForgetPassWordActivity1 extends BaseActivity {

	@ViewInject(id = R.id.phone)
	EditText phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_first);
		title.setText(R.string.findpassword);
		this.setTitleRightTxt(R.string.next);
	}

	@Override
	public void rightClick(View v) {
		String pstr = phone.getText().toString();
		if (Util.isMobileNO(pstr)) {
			ac.finalHttp.post(URL.FINDPASSWORD_FIRST, getAjaxParams("mobile", pstr), callBack);
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
					Intent i = new Intent(ForgetPassWordActivity1.this, ForgetPassWordActivity2.class);
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
