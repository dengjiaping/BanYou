package com.quanliren.quan_one.activity.user;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.gson.Gson;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseUserActivity;
import com.quanliren.quan_one.activity.group.QuanPersonalActivity;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.fragment.QuanPullListViewFragment;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class UserInfoActivity extends BaseUserActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);
		title.setText(R.string.my);
		setTitleRightTxt(R.string.edit);
		setListener();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void setListener() {
		user = ac.getUserInfo();
		ac.finalHttp.post(URL.GET_USER_INFO, getAjaxParams(),
				callBack);
		initViewByUser();
	}

	@Override
	public void rightClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, UserInfoEditActivity.class);
		startActivityForResult(i, 1);
	}

	public void info_tili_btn(View v) {
		// Intent i = new Intent(this, BuyPowerActivity.class);
		// startActivity(i);
	}

	public void care_btn(View v) {
		Intent i = new Intent(this, MyCareActivity.class);
		startActivity(i);
	}

	public void my_msg_btn(View v) {
		// Intent i = new Intent(this,MyLeaveMessageActivity.class);
		// startActivity(i);
	}

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog(1);
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};
		

		public void onSuccess(String t) {
			customDismissDialog();
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
					user = temp;
					initViewByUser();
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	public void personal_btn(View v) {
		Intent i = new Intent(this, QuanPersonalActivity.class);
		i.putExtra("type", QuanPullListViewFragment.ONCE);
		i.putExtra("otherid", "");
		startActivity(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1:
			switch (resultCode) {
			case 1:
				ac.finalHttp.post(URL.GET_USER_INFO,
						getAjaxParams("userid", user.getId()), callBack);
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		user = ac.getUserInfo();
		initViewByUser();
	}

	public void picSeting(int position) {
		menupop = new PopFactory(this, new String[] { "设为头像", "删除" },
				new menuClick(position), parent);
		menupop.toogle();
	}

	class menuClick implements OnClickListener{

		int position;
		
		public menuClick(int position){
			this.position=position;
		}
		
		@Override
		public void onClick(View v) {
			AjaxParams ap = getAjaxParams();
			switch (v.getId()) {
			case 0:
				ap.put("position", position+"");
				ac.finalHttp.post(URL.SET_USERLOGO, ap, new setLogoCallBack(position));
				break;
			case 1:
				JSONArray ja=new JSONArray();
				ja.put(""+position);
				ap.put("removeimg", ja.toString());
				ac.finalHttp.post(URL.DELETE_USERLOGO, ap, new deleteLogoCallBack(position));
				break;
			}
			menupop.closeMenu();
		}
	};
	
	class deleteLogoCallBack extends AjaxCallBack<String>{

		int position;
		
		public deleteLogoCallBack(int position){
			this.position=position;
		}
		
		public void onStart() {
			customShowDialog("正在删除头像");
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("删除成功");
					user.getImglist().remove(position);
					UserTable ut = new UserTable(user);
					ac.finalDb.delete(ut);
					ac.finalDb.save(ut);
					initViewByUser();
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
	
	class setLogoCallBack extends AjaxCallBack<String> {
		
		int position;
		
		public setLogoCallBack(int position){
			this.position=position;
		}
		
		public void onStart() {
			customShowDialog("正在设置头像");
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("修改成功");
					User user = ac.getUserInfo();
					if (user != null) {
						user.setAvatar(user.getImglist().get(position).imgpath);
						UserTable ut = new UserTable(user);
						ac.finalDb.delete(ut);
						ac.finalDb.save(ut);
						if(Util.isStrNotNull(user.getAvatar())){
							ImageLoader.getInstance().displayImage(
									user.getAvatar() + StaticFactory._320x320, userlogo);
						}
					}
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};
}
