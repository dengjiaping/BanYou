package com.quanliren.quan_one.activity.group;

import android.os.Bundle;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.fragment.ChosePositionFragment;

public class ChoseLocationActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chose_position_actvitiy);
		setTitleTxt("选择城市");
		getSupportFragmentManager().beginTransaction().replace(R.id.content, new ChosePositionFragment()).commit();
	}
}
