package com.quanliren.quan_one.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.db.sqlite.DbModel;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.location.Location;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.activity.user.LoginActivity;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.fragment.MessageFragment;
//import com.quanliren.quan_one.fragment.HomeFragment;
import com.quanliren.quan_one.fragment.NearPeopleFragment;
import com.quanliren.quan_one.fragment.QuanFragment;
import com.quanliren.quan_one.fragment.SetingMoreFragment;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class PropertiesActivity extends BaseActivity {

	List<Fragment> addedList = new ArrayList<Fragment>(); // 已添加的窗体
	List<Fragment> unaddList = new ArrayList<Fragment>(); // 所有将要添加的窗体
	CommonShared cs;
	@ViewInject(id = R.id.nav_btn1, click = "setCurrentIndex")
	View btn1;
	@ViewInject(id = R.id.nav_btn2, click = "setCurrentIndex")
	View btn2;
	@ViewInject(id = R.id.nav_btn3, click = "setCurrentIndex")
	View btn3;
	@ViewInject(id = R.id.nav_btn4, click = "setCurrentIndex")
	View btn4;
	@ViewInject(id=R.id.messagecount)
	TextView messagecount;

	List<View> list = new ArrayList<View>();
	Location location;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.properties);

		cs = new CommonShared(getApplication());

		unaddList.add(new QuanFragment());
		unaddList.add(new NearPeopleFragment());
		unaddList.add(new MessageFragment());
		unaddList.add(new SetingMoreFragment());

		list.add(btn1);
		list.add(btn2);
		list.add(btn3);
		list.add(btn4);
		
//		btn4.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View arg0) {
//				new AlertDialog.Builder(PropertiesActivity.this)
//                .setTitle("选择IP")
//                .setItems(R.array.reboot_item, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        /* User clicked so do some stuff */
//                        String[] items = getResources().getStringArray(R.array.reboot_item);
//                        String[] items1 = getResources().getStringArray(R.array.reboot_item1);
//                       URL.setIP(items1[which]);
//                       URL.setURL(items[which]);
//                    }
//                })
//                .create().show();;
//				return false;
//			}
//		});

		// 显示第一页

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.content, unaddList.get(0),
						unaddList.get(0).getClass().getName()).commit();

		addedList.add(unaddList.get(0));

		setSwipeBackEnable(false);

		btn1.setSelected(true);
		
		if(ac.getUser()!=null){
			Util.setAlarmTime(this, System.currentTimeMillis(),BroadcastUtil.ACTION_CHECKCONNECT, 60 * 1000);
		}
		String[] string=new String[]{ChatActivity.ADDMSG,BroadcastUtil.ACTION_OUTLINE};
		receiveBroadcast(string,handler );
		
		location=new Location(this.getApplicationContext());
		location.startLocation();
		
		/**更新用户信息**/
		CacheBean cb=ac.finalDb.findById(USERINFOCACHE, CacheBean.class);
		if(cb==null||cb.isAfter()){
			ac.finalHttp.post(URL.GET_USER_INFO, getAjaxParams(),
					callBack);
		}
	}
	
	public static final String USERINFOCACHE="userinfo";
	
	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					User temp = new Gson().fromJson(jo.getString(URL.RESPONSE),
							User.class);
					UserTable ut = new UserTable(temp);
					ac.finalDb.delete(ut);
					ac.finalDb.save(ut);
					
					CacheBean cb=new CacheBean(USERINFOCACHE, "", new Date().getTime());
					ac.finalDb.deleteById(CacheBean.class, USERINFOCACHE);
					ac.finalDb.save(cb);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		location.destory();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setCount();
	}

	/**
	 * 切换页面
	 * 
	 * @param clazz
	 *            页面的Class
	 */
	public void switchContent(Class clazz) {
		if (clazz == null) {
			return;
		}
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		for (Fragment fg : addedList) {
			// if (!fg.isHidden()) {
			ft.hide(fg);
			// }
		}
		boolean showComplete = false;
		Fragment showFragment = null;// 将要显示的
		for (Fragment fg : addedList) {
			if (fg.getClass().getName().equals(clazz.getName())) {
				ft.show(fg);
				showFragment = fg;
				showComplete = true;
			}
		}
		if (!showComplete) {
			for (Fragment fg : unaddList) {
				if (fg.getClass().getName().equals(clazz.getName())) {
					ft.add(R.id.content, fg, fg.getClass().getName());
					showFragment = fg;
					addedList.add(fg);
				}
			}
		}
		ft.commit();
	}

	private long temptime;

	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			for (Fragment fg : addedList) {
				if (fg.isVisible()
						&& fg.getClass().getName()
								.equals(NearPeopleFragment.class.getName())) {
					if (((NearPeopleFragment) fg).menupop != null
							&& ((NearPeopleFragment) fg).menupop.isShow) {
						((NearPeopleFragment) fg).menupop.closeMenu();
						return true;
					}
				}
			}
			if (System.currentTimeMillis() - temptime > 3000) {
				Toast.makeText(this, "再按一次将退出程序", Toast.LENGTH_LONG).show();
				temptime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		} else if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			for (Fragment fg : addedList) {
				if (fg.isVisible()
						&& fg.getClass().getName()
								.equals(NearPeopleFragment.class.getName())) {
					if (((NearPeopleFragment) fg).menupop != null
							&& ((NearPeopleFragment) fg).menupop.isShow) {
						((NearPeopleFragment) fg).menupop.closeMenu();
					}
				}
			}
			temptime = 0;
			return true;
		}

		return super.dispatchKeyEvent(event);
	}

	public void setCurrentIndex(View view) {
		switchContent(unaddList.get(list.indexOf(view)).getClass());
		for (View v : list) {
			if (!v.equals(view))
				v.setSelected(false);
			else
				v.setSelected(true);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	Handler handler=new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			Intent i = (Intent) msg.obj;
			String action = i.getAction();
			if (action.equals(ChatActivity.ADDMSG)) {
				setCount();
			}else if(action.equals(BroadcastUtil.ACTION_OUTLINE)){
				AM.getActivityManager().popAllActivity();
				Intent in = new Intent(PropertiesActivity.this,LoginActivity.class);
				in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				startActivity(in);
			}
			super.dispatchMessage(msg);
		};
	};
	
	public void setCount(){
		LoginUser user=ac.getUser();
		try {
			ac.finalDb.checkTableExist(DfMessage.class);
			DbModel model=ac.finalDb.findDbModelBySQL("select count(*) as num from "+DfMessage.TABLENAME+" where userid='"+user.getId()+"' and receiverUid='"+user.getId()+"' and isRead='0'");
			int num=model.getInt("num");
			if(num>0){
				messagecount.setVisibility(View.VISIBLE);
				messagecount.setText(num+"");
			}else{
				messagecount.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
