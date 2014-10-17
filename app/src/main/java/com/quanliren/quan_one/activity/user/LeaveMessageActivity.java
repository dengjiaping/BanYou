package com.quanliren.quan_one.activity.user;

import android.os.Bundle;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

public class LeaveMessageActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leave_message);
		setTitleTxt(R.string.leave_msg);
		setTitleRightTxt(R.string.confirm);
	}
}
