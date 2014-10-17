package com.quanliren.quan_one.activity.seting;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

public class RemindMessageActivity extends BaseActivity implements
		OnCheckedChangeListener {

	@ViewInject(id = R.id.voice_cb)
	CheckBox voice_cb;
	@ViewInject(id = R.id.msg_cb)
	CheckBox msg_cb;
	@ViewInject(id = R.id.zhendong_cb)
	CheckBox zhendong_cb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remind_message);
		setTitleTxt("消息通知");
		initCb();
		voice_cb.setOnCheckedChangeListener(this);
		msg_cb.setOnCheckedChangeListener(this);
		zhendong_cb.setOnCheckedChangeListener(this);
		// voice_cb.
	}

	public void initCb() {
		int num = ac.cs.getMSGOPEN();
		if (num == 1) {
			msg_cb.setChecked(true);
			if (ac.cs.getVIDEOOPEN() == 1) {
				voice_cb.setChecked(true);
			}else{
				voice_cb.setChecked(false);
			}
			if (ac.cs.getZHENOPEN() == 1) {
				zhendong_cb.setChecked(true);
			}else{
				zhendong_cb.setChecked(false);
			}
		} else {
			msg_cb.setChecked(false);
			voice_cb.setChecked(false);
			zhendong_cb.setChecked(false);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.voice_cb:
			ac.cs.setVIDEOOPEN(isChecked?1:0);
			if(isChecked)
				ac.cs.setMSGOPEN(1);
			else{
				if(!zhendong_cb.isChecked()){
					ac.cs.setMSGOPEN(0);
				}
			}
			break;
		case R.id.msg_cb:
			ac.cs.setMSGOPEN(isChecked?1:0);
			break;
		case R.id.zhendong_cb:
			ac.cs.setZHENOPEN(isChecked?1:0);
			if(isChecked)
				ac.cs.setMSGOPEN(1);
			else{
				if(!voice_cb.isChecked()){
					ac.cs.setMSGOPEN(0);
				}
			}
			break;
		}
		initCb();
	}
}
