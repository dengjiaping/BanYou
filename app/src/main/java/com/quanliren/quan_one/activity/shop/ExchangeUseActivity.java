package com.quanliren.quan_one.activity.shop;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.user.MyProActivity;
import com.quanliren.quan_one.activity.user.MyProFragment;
import com.quanliren.quan_one.bean.ProBean;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class ExchangeUseActivity extends BaseActivity{

	@ViewInject(id=R.id.name) EditText name;
	@ViewInject(id=R.id.phone) EditText phone;
	@ViewInject(id=R.id.email) EditText email;
	@ViewInject(id=R.id.address) EditText address;
	@ViewInject(id=R.id.remind) EditText remind;
	
	ProBean bean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		bean=(ProBean) getIntent().getExtras().getSerializable("bean");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exchange_use);
		setTitleTxt("使用");
		setTitleRightTxt("完成");
	}
	
	@Override
	public void rightClick(View v) {
		final String str_name=name.getText().toString().trim();
		final String str_phone=phone.getText().toString().trim();
		final String str_email=email.getText().toString().trim();
		final String str_address=address.getText().toString().trim();
		final String str_remind=remind.getText().toString().trim();
		
		if(!Util.isStrNotNull(str_name)){
			showCustomToast("请输入姓名");
			return;
		}else if(!Util.isMobileNO(str_phone)){
			showCustomToast("请输入正确的手机号码");
			return;
		}else if(!Util.isStrNotNull(str_address)){
			showCustomToast("请填写联系地址");
			return;
		}
		
		StringBuilder sb=new StringBuilder();
		sb.append("姓名："+str_name+"\n");
		sb.append("手机号码："+str_phone+"\n");
		sb.append("联系地址："+str_address+"\n");
		sb.append("邮箱："+str_email+"\n");
		sb.append("备注："+str_remind);
		
		new IosCustomDialog.Builder(this).setTitle("确认信息").setMessage(sb.toString()).setGravity(Gravity.LEFT).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AjaxParams ap=getAjaxParams();
				ap.put("actid", bean.getActid());
				ap.put("truename", str_name);
				ap.put("mobile", str_phone);
				ap.put("email", str_email);
				ap.put("address", str_address);
				ap.put("remark", str_remind);
				
				ac.finalHttp.post(URL.EXCHANGE_MONEY,ap, callBack);
			}
		}).create().show();
	}
	
	AjaxCallBack<String> callBack=new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在发送请求");
		};
		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("兑换申请已经提交请等待处理");
					Intent i = new Intent(MyProFragment.REFERE);
					sendBroadcast(i);
					setResult(1);
					finish();
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				customDismissDialog();
			}
		};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();showIntentErrorToast();
		};
	};
}
