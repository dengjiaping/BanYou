package com.quanliren.quan_one.activity.reg;

import java.util.Calendar;
import java.util.Locale;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
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
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class RegSecond extends BaseActivity{

	String phone;
	@ViewInject(id=R.id.age,click="editage")TextView age;
	@ViewInject(id=R.id.password)EditText password;
	@ViewInject(id=R.id.confirm_password)EditText confirm_password;
	@ViewInject(id=R.id.nickname)EditText nickname;
	@ViewInject(id=R.id.sex,click="editsex")TextView sex;
	@ViewInject(id=R.id.txt_num,click="editsex")TextView txt_num;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		phone=getIntent().getExtras().getString("phone");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_second);
		title.setText(R.string.reg);
		setTitleRightTxt(R.string.ok);
		
		setListener();
	}

	public void setListener(){
	}
	
	@Override
	public void rightClick(View v) {
		String str_nickname=nickname.getText().toString().trim();
		String str_sex=sex.getTag().toString().trim();
		String str_password=password.getText().toString().trim();
		String str_confirm_password=confirm_password.getText().toString().trim();
		String str_age=age.getText().toString().trim();
		
		if(str_nickname.length()==0){
			showCustomToast("请输入昵称");
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
		}else if(str_age.length()==0){
			showCustomToast("请选择出生日期");
			return;
		}
		
		CommonShared cs=new CommonShared(getApplicationContext());
		
		AjaxParams ap= getAjaxParams();
		ap.put("mobile", phone);
		ap.put("nickname", str_nickname);
		ap.put("pwd", str_password);
		ap.put("repwd", str_confirm_password);
		ap.put("birthday", str_age);
		ap.put("sex", str_sex);
		ap.put("cityid", String.valueOf(cs.getLocationID()));
		ap.put("longitude", ac.cs.getLng());
		ap.put("latitude", ac.cs.getLat());
		ap.put("area", cs.getArea());
		
		User lou=new User();
		lou.setMobile(phone);
		lou.setPwd(str_password);
		ac.finalHttp.post(URL.REG_THIRD, ap, new callBack(lou));
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
					
					AM.getActivityManager().popActivity(LoginActivity.class.getName());
					if(!AM.getActivityManager().contains(PropertiesActivity.class.getName())){
						startActivity(new Intent(RegSecond.this,PropertiesActivity.class));
					}
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
	
	Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

	public void editage(View v) {
		Calendar cal=Calendar.getInstance();
		new DatePickerDialog(RegSecond.this, d, (cal.get(Calendar.YEAR)),
				cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
	}

	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			
			
			Calendar cal = Calendar.getInstance();
			if (cal.getTime().before(dateAndTime.getTime())) {
				dateAndTime.set(Calendar.YEAR, cal.get(Calendar.YEAR));
				dateAndTime.set(Calendar.MONTH, cal.get(Calendar.MONTH));
				dateAndTime.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
			}

			updateLabel();
		}
	};

	private void updateLabel() {
		try {
			String age = Util.fmtDate.format(dateAndTime.getTime());
			this.age.setText(age);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editsex(View v) {
		final CharSequence[] items = { "男", "女" };
		AlertDialog.Builder builder = new AlertDialog.Builder(
				RegSecond.this);
		builder.setTitle("选择");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int position) {
				if (position == 0) {
					sex.setText("男");
					sex.setTag(1);
				} else {
					sex.setText("女");
					sex.setTag(0);
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void back(View v) {
		dialogFinish();
	};
	
	public void onBackPressed() {
		dialogFinish();
	};
	
	public void dialogFinish(){
		new IosCustomDialog.Builder(RegSecond.this)
		.setMessage("您确定要放弃本次注册吗？")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					public void onClick(
							DialogInterface dialog,
							int which) {
						finish();
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					public void onClick(
							DialogInterface arg0, int arg1) {
					}
				}).create().show();
	}
}
