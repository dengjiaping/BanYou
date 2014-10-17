package com.quanliren.quan_one.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.application.AM;

public class Noti extends Activity{
	private static final String TAG="Noti";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		if(!AM.getActivityManager().contains(PropertiesActivity.class.getName())){
			Intent intent = new Intent(this, PropertiesActivity.class);
			startActivity(intent);
		}
		if (getIntent() != null && getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey("activity")) {
			Class class1=(Class) getIntent().getExtras().get("activity");
			Intent i = new Intent(this,class1);
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			if(class1.getName().equals(ChatActivity.class.getName())){
				i.putExtra("friend", getIntent().getExtras().getSerializable("friend"));
			}
			startActivity(i);
		}
		finish();
	}
}
