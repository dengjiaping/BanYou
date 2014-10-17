package com.quanliren.quan_one.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

import com.net.tsz.afinal.FinalDb;
import com.net.tsz.afinal.FinalHttp;
import com.net.tsz.afinal.http.PreferencesCookieStore;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.listener.DBUpdateListener;
import com.quanliren.quan_one.service.IQuanPushService;
import com.quanliren.quan_one.service.QuanPushService;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

public class AppClass extends Application {
	
	public FinalDb finalDb;
	public CommonShared cs=null;
	public FinalHttp finalHttp;
	public boolean hasNet=true;
	public static List<String> mEmoticons = new ArrayList<String>();
	public static Map<String, Integer> mEmoticonsId = new HashMap<String, Integer>();
	public static List<String> mEmoticons_Zem = new ArrayList<String>();
	public static List<String> mEmoticons_Zemoji = new ArrayList<String>();
	
	public AppClass() {
	}

	public static final DisplayImageOptions options_defalut = new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.image_group_qzl)
	.showImageForEmptyUri(R.drawable.image_group_qzl)
//	.displayer(new FadeInBitmapDisplayer(200))
	.showImageOnFail(R.drawable.image_group_load_f).cacheInMemory(true)
	.cacheOnDisc(true).build();
	
	public static final DisplayImageOptions options_chat = new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.ic_chat_def_pic)
	.showImageForEmptyUri(R.drawable.ic_chat_def_emote_failure)
	.showImageOnFail(R.drawable.ic_chat_def_emote_failure).cacheInMemory(true)
	.cacheOnDisc(true).build();
	
	public static final DisplayImageOptions options_userlogo = new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.defalut_logo)
	.showImageForEmptyUri(R.drawable.defalut_logo)
//	.displayer(new FadeInBitmapDisplayer(200))
	.showImageOnFail(R.drawable.defalut_logo).cacheInMemory(true)
	.cacheOnDisc(true).build();
	
	
	@Override
	public void onCreate() {
		
		cs=new CommonShared(getApplicationContext());
		cs.setVersionName(Util.getAppVersionName(this));
		cs.setVersionCode(Util.getAppVersionCode(this));
		cs.setChannel(Util.getChannel(this));
		
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
		try {
			cs.setDeviceId(tm.getDeviceId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(options_defalut)
				.build();
		ImageLoader.getInstance().init(config);
		
		for (int i = 1; i < 64; i++) {
			String emoticonsName = "[zem" + i + "]";
			int emoticonsId = getResources().getIdentifier("zem" + i,
					"drawable", getPackageName());
			mEmoticons.add(emoticonsName);
			mEmoticons_Zem.add(emoticonsName);
			mEmoticonsId.put(emoticonsName, emoticonsId);
		}
		for (int i = 1; i < 59; i++) {
			String emoticonsName = "[zemoji" + i + "]";
			int emoticonsId = getResources().getIdentifier("zemoji_e" + i,
					"drawable", getPackageName());
			mEmoticons.add(emoticonsName);
			mEmoticons_Zemoji.add(emoticonsName);
			mEmoticonsId.put(emoticonsName, emoticonsId);
		}
		
		finalHttp= new FinalHttp();
		finalHttp.configCookieStore(new PreferencesCookieStore(this));
		finalDb=FinalDb.create(getApplicationContext(), StaticFactory.DBName, true, StaticFactory.DBVersion, new DBUpdateListener());
	}
	
	public LoginUser getUser(){
		List<LoginUser> users=finalDb.findAll(LoginUser.class);
		if(users==null||users.size()==0){
			return null;
		}else{
			return users.get(0);
		}
	}
	public User getUserInfo(){
		LoginUser u=getUser();
		UserTable user=null;
		if(u!=null){
			user=finalDb.findById(u.getId(), UserTable.class);
			if(user!=null){
				return user.getUser();
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	public IQuanPushService remoteService = null; 
	public CounterServiceConnection conn = null; 

	public class CounterServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName name, IBinder service) {
			remoteService = IQuanPushService.Stub.asInterface(service);
		}

		public void onServiceDisconnected(ComponentName name) {
			remoteService = null;
		}
	}

	public boolean isConnectSocket() {
		try {
			if (remoteService != null && remoteService.getServerSocket()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void sendMessage(String str) {
		try {
			if (remoteService != null) {
				remoteService.sendMessage(str);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void startServices() {
		stopServices();
		Intent i = new Intent(com.quanliren.quan_one.util.BroadcastUtil.ACTION_CONNECT);
		startService(i);
		bindServices();
	}
	
	public void bindServices(){
		Intent i = new Intent(this,QuanPushService.class);
		if (conn == null)
			conn = new CounterServiceConnection();
		bindService(i, conn, Context.BIND_AUTO_CREATE);
	}

	public void stopServices() {
		try {
			Intent i = new Intent(this,QuanPushService.class);
			if (conn != null) {
				unbindService(conn);
				conn = null;
			}
			stopService(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void dispose() {
		finalDb.deleteByWhere(LoginUser.class, "");
		try {
			if (remoteService != null) {
				remoteService.closeAll();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		stopServices();
	}
}
