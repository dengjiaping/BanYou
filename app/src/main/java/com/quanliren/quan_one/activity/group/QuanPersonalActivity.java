package com.quanliren.quan_one.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.adapter.QuanAdapter;
import com.quanliren.quan_one.fragment.QuanPullListViewFragment;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;

public class QuanPersonalActivity extends BaseActivity{

	@ViewInject(id = R.id.listview)
	QuanAdapter adapter;
	ListView listview;
	int type=0;
	String otherid="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		type=getIntent().getExtras().getInt("type");
		otherid=getIntent().getExtras().getString("otherid");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quan_personal);
		setTitleTxt("个人动态");
		initAdapter();
		
	}
	
	public void initAdapter() {
		final QuanPullListViewFragment my=new QuanPullListViewFragment();
		if(!otherid.equals("")){
			Bundle b=new Bundle();
			b.putInt("type", type);
			b.putString("otherid", otherid);
			my.setArguments(b);
		}
		getSupportFragmentManager().beginTransaction().replace(R.id.content, my).commit();
		new Handler().post(new Runnable(){
			@Override
			public void run() {
				((LoaderImpl)my).refresh();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
}
