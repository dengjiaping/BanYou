package com.quanliren.quan_one.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.fragment.QuanPullListViewFragment;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;

public class QuanBarActivity extends BaseActivity{
	QuanPullListViewFragment my;
	GroupBean group;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		group=(GroupBean) getIntent().getExtras().getSerializable("group");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quan_personal);
		setTitleTxt("群动态");
		initAdapter();
		setTitleRightTxt(R.string.comment);
	}
	
	@Override
	public void rightClick(View v) {
		Intent i = new Intent(this, PublishActivity.class);
		i.putExtra("group", group);
		startActivityForResult(i, 1);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case 1:
				my.refere();
				break;
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void initAdapter() {
		my=new QuanPullListViewFragment();
		Bundle b=new Bundle();
		b.putInt("type", QuanPullListViewFragment.GROUP);
		b.putString("crowdid", group.getId());
		my.setArguments(b);
		getSupportFragmentManager().beginTransaction().replace(R.id.content, my).commit();
		new Handler().post(new Runnable(){
			@Override
			public void run() {
				((LoaderImpl)my).refresh();
			}
		});
	}
	
}
