package com.quanliren.quan_one.activity.reg;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.PropertiesActivity;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.user.LoginActivity;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.MoreLoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.util.URL;

public class ForgetPassWordActivity2 extends BaseActivity {

	@ViewInject(id = R.id.phone_txt)
	TextView phone_txt;
	@ViewInject(id = R.id.password)
	EditText password;
	@ViewInject(id = R.id.confirm_password)
	EditText confirm_password;
	@ViewInject(id = R.id.code)
	EditText code_txt;
	@ViewInject(id=R.id.ok,click="confirm")
	Button ok;
	String phone;
	//,code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		phone=getIntent().getExtras().getString("phone");
//		if(getIntent().getExtras().containsKey("code"))
//		code=getIntent().getExtras().getString("code");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_second);
		title.setText(R.string.findpassword);
		
		phone_txt.setText(phone);
//		code_txt.setText(code);
		
		receiveBroadcast(RegGetCode.GETCODE, handler);
	}
	
	Handler handler=new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			
			try {
				Intent i = (Intent) msg.obj;
				String action=i.getAction();
				if(action.equals(RegGetCode.GETCODE)){
					String code=i.getExtras().getString("code");
					code_txt.setText(code);
				}
			} catch (Exception e) {
			}
			super.dispatchMessage(msg);
		};
	};
	
	public void confirm(View v){
		String str_password=password.getText().toString().trim();
		String str_confirm_password=confirm_password.getText().toString().trim();
		String str_code=code_txt.getText().toString().trim();
		
		if(str_code.trim().length()!=6){
			showCustomToast("请输入正确的验证码！");
			return;
		}else	if(str_password.length()>16||str_password.length()<6){
			showCustomToast("密码长度为6-16个字符");
			return;
		}else if(!str_password.matches("^[a-zA-Z0-9 -]+$")){
			showCustomToast("密码中不能包含特殊字符");
			return;
		}else if(!str_confirm_password.equals(str_password)){
			showCustomToast("确认密码与密码不同");
			return;
		}
		
		AjaxParams ap=getAjaxParams();
		ap.put("mobile", phone);
		ap.put("authcode", str_code);
		ap.put("pwd", str_password);
		ap.put("repwd", str_confirm_password);
		
		User lou=new User();
		lou.setMobile(phone);
		lou.setPwd(str_password);
		ac.finalHttp.post(URL.FINDPASSWORD_SECOND,ap, new callBack(lou));
	}
	
	class callBack extends AjaxCallBack<String> {
		User u;
		
		public callBack(User u){
			this.u=u;
		}
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
					showCustomToast("修改成功");
					finish();
					break;
				default:
					showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(URL.INFO));
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};
}
