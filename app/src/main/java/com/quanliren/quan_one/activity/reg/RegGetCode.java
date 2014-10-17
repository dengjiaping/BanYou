package com.quanliren.quan_one.activity.reg;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.util.URL;

public class RegGetCode extends BaseActivity{
	
	public static final String GETCODE="com.quanliren.quan_one.activity.reg.RegGetCode";
	
	String phone,code;
	@ViewInject(id=R.id.phone_txt)TextView phone_txt;
	@ViewInject(id=R.id.code)EditText code_edit;
	@ViewInject(id=R.id.second)TextView second;
	@ViewInject(id=R.id.second_ll)View second_ll;
	@ViewInject(id=R.id.resend_ll)View resend_ll;
	@ViewInject(id=R.id.resend_btn,click="resend")TextView resend_btn;
	int allSec=3*60;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		phone=getIntent().getExtras().getString("phone");
//		if(getIntent().getExtras().containsKey("code"))
//		code=getIntent().getExtras().getString("code");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_getcode);
		
		title.setText(R.string.reg);
		setTitleRightTxt(R.string.next);
		
		setListener();
		
		receiveBroadcast(GETCODE, handler);
	}

	@Override
	public void rightClick(View v) {
		String codes= code_edit.getText().toString();
		if(codes.trim().length()!=6){
			showCustomToast("请输入正确的验证码！");
			return;
		}
		AjaxParams ap=getAjaxParams();
		ap.put("mobile", phone);
		ap.put("authcode", codes);
		ac.finalHttp.post(URL.REG_SENDCODE,ap,sendCodeCallBack);
	}
	
	public void resend(View v){
		ac.finalHttp.post(URL.REG_FIRST, new AjaxParams("mobile", phone), callBack);
	}
	
	public void setListener(){
		phone_txt.setText(phone);
//		code_edit.setText(code);
		second.setText(allSec+"");
		handler.postDelayed(runable, 1000);
	}
	
	Runnable runable=new Runnable() {
		
		@Override
		public void run() {
			allSec--;
			second.setText(allSec+"");
			if(allSec>0){
				handler.postDelayed(runable, 1000);
			}else{
				second_ll.setVisibility(View.GONE);
				resend_ll.setVisibility(View.VISIBLE);
			}
		}
	};
	
	Handler handler=new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			
			try {
				Intent i = (Intent) msg.obj;
				String action=i.getAction();
				if(action.equals(GETCODE)){
					String code=i.getExtras().getString("code");
					code_edit.setText(code);
					
					title_right_btn.performClick();
				}
			} catch (Exception e) {
			}
			super.dispatchMessage(msg);
		};
	};
	
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
			showCustomToast("重新发送验证码成功！");
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				jo=jo.getJSONObject(URL.RESPONSE);
				switch (status) {
				case 0:
					code=jo.getString("authcode");
					allSec=180;
					second.setText(allSec+"");		
					second_ll.setVisibility(View.VISIBLE);
					resend_ll.setVisibility(View.GONE);
					handler.postDelayed(runable, 1000);
					break;
				case 1:
					String str=jo.getString(URL.INFO);
					showCustomToast(str);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};
	
	AjaxCallBack<String> sendCodeCallBack=new AjaxCallBack<String>() {
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
					Intent i =new Intent(RegGetCode.this,RegSecond.class);
					i.putExtra("phone", phone);
					startActivity(i);
					finish();
					break;
				case 1:
					showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(URL.INFO));
				break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};
	
	public void back(View v) {
		dialogFinish();
	};
	
	public void onBackPressed() {
		dialogFinish();
	};
	
	public void dialogFinish(){
		new IosCustomDialog.Builder(RegGetCode.this)
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
