package com.quanliren.quan_one.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.Util;

public class BootCompletedAlarmReceiver extends BroadcastReceiver {
	public static final String TAG="BootCompletedAlarmReceiver";
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, intent.getAction());
		AppClass ac = (AppClass) context.getApplicationContext();
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			if (ac.getUser()!=null) {
				Util.setAlarmTime(context, System.currentTimeMillis(),BroadcastUtil.ACTION_CHECKCONNECT, 60 * 1000);
			}
		}
	}
}