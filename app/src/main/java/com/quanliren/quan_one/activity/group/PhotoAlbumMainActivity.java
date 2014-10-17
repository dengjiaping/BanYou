package com.quanliren.quan_one.activity.group;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.PhotoAibum;
import com.quanliren.quan_one.bean.PhotoItem;

public class PhotoAlbumMainActivity extends BaseActivity {

	int maxnum = 0;
	ArrayList<String> paths = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		maxnum = getIntent().getExtras().getInt("maxnum");
		if (getIntent().getExtras().containsKey("images")) {
			paths = getIntent().getStringArrayListExtra("images");
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_album_main);
		setTitleTxt("相册");
		setTitleRightTxt("确定(" + paths.size() + "/" + maxnum + ")");
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, new PhotoAlbumActivity()).commit();
	}

	PhotoActivity pa;

	public void replaceFragment(PhotoAibum i) {
		
		for (PhotoItem item : i.getBitList()) {
			if(paths.contains(item.getPath())){
				item.setSelect(true);
			}
		}
		
		Bundle b = new Bundle();
		b.putSerializable("aibum", i);
		pa = new PhotoActivity();
		pa.setArguments(b);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, pa).addToBackStack(null).commit();
	}

	@Override
	public void back(View v) {
		if (pa != null && pa.isVisible()) {
			onBackPressed();
		} else {
			finish();
		}
	}

	@Override
	public void rightClick(View v) {
		Intent i = new Intent();
		i.putStringArrayListExtra("images", paths);
		setResult(1,i);
		finish();
	}
	
	public void changeNum() {
		setTitleRightTxt("确定(" + paths.size() + "/" + maxnum + ")");
	}

	public void addPath(String path) {
		paths.add(path);
		changeNum();
	}

	public void removePath(String path) {
		paths.remove(path);
		changeNum();
	}
}
