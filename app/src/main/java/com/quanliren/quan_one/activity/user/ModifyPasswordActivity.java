package com.quanliren.quan_one.activity.user;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.PropertiesActivity;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.reg.RegSecond;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.MoreLoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.util.URL;

public class ModifyPasswordActivity extends BaseActivity{

	@ViewInject(id = R.id.oldpassword)
	EditText oldpassword;
	@ViewInject(id = R.id.password)
	EditText password;
	@ViewInject(id = R.id.confirm_password)
	EditText confirm_password;
	@ViewInject(id=R.id.modifyBtn,click="ok")Button modifyBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modifypassword);
		setTitleTxt(R.string.seting_modify_password);
	}
	
	public void ok(View v){
		String str_oldpassword=oldpassword.getText().toString().trim();
		String str_password=password.getText().toString().trim();
		String str_confirm_password=confirm_password.getText().toString().trim();
		
		if(str_password.length()>16||str_password.length()<6){
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
		ap.put("oldpwd", str_oldpassword);
		ap.put("pwd", str_password);
		ap.put("repwd", str_confirm_password);
		
		User lou=new User();
		lou.setMobile(ac.getUser().getMobile());
		lou.setPwd(str_password);
		ac.finalHttp.post(URL.MODIFYPASSWORD,ap, new callBack(lou));
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
					showCustomToast("注册成功");
					
					ac.finalDb.deleteByWhere(MoreLoginUser.class, "username='"+u.getMobile()+"'");
					ac.finalDb.save(new MoreLoginUser(u.getMobile(), u.getPwd()));
					
					User user=new Gson().fromJson(jo.getString(URL.RESPONSE), User.class);
					LoginUser lu=new LoginUser(user.getId(), u.getMobile(), u.getPwd(), user.getToken());
					
					//保存用户
					ac.finalDb.deleteById(UserTable.class, user.getId());
					ac.finalDb.save(new UserTable(user));
					
					//保存登陆用户
					ac.finalDb.deleteByWhere(LoginUser.class, "");
					ac.finalDb.save(lu);
					
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
