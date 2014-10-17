package com.quanliren.quan_one.activity.user;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.PropertiesActivity;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.location.Location;
import com.quanliren.quan_one.activity.reg.ForgetPassWordActivity1;
import com.quanliren.quan_one.activity.reg.RegFirst;
import com.quanliren.quan_one.adapter.ParentsAdapter;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.MoreLoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.CustomRelativeLayout;
import com.quanliren.quan_one.custom.CustomRelativeLayout.OnSizeChangedListener;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class LoginActivity extends BaseActivity {

	@ViewInject(id = R.id.username)
	EditText username;
	@ViewInject(id = R.id.password)
	EditText password;
	@ViewInject(id = R.id.username_ll)
	View username_ll;
	@ViewInject(id = R.id.crl)
	CustomRelativeLayout crl;
	@ViewInject(id = R.id.title)
	TextView title;
	@ViewInject(id = R.id.forgetpassword, click = "findpassword")
	TextView forgetpassword;
	@ViewInject(id = R.id.loginBtn, click = "login")
	Button loginBtn;
	@ViewInject(id = R.id.regBtn, click = "reg")
	TextView regBtn;
	@ViewInject(id=R.id.margin_ll)LinearLayout margin_ll;
	@ViewInject(id = R.id.delete_username_btn, click = "delete_username_btn")
	View delete_username_btn;
	@ViewInject(id = R.id.delete_password_btn, click = "delete_password_btn")
	View delete_password_btn;
	@ViewInject(id = R.id.more_username_btn, click = "more_username_btn")
	View more_username_btn;
	boolean isShow = false; // 更多用户名是否展开
	private PopupWindow pop;
	private PopupAdapter adapter;
	private ListView listView;
	private List<MoreLoginUser> names = new ArrayList<MoreLoginUser>();
	String str_username, str_password;
	Location location;
	private int _oldh=-1;
	private boolean isOpenEdit=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		title.setText(R.string.login);
		// forgetpassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		setListener();
		location = new Location(getApplicationContext());
		location.startLocation();

		setSwipeBackEnable(false);

		crl.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void onSizeChanged(int w, final int h, final int oldw,
					final int oldh) {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						if (oldh == 0) {
							return;
						}
						if (_oldh == -1) {
							_oldh = oldh;
						}
						if (h >= _oldh) {
							isOpenEdit = false;

						} else if (h < _oldh) {
							isOpenEdit = true;
						}
						if (!isOpenEdit) {
							RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) margin_ll.getLayoutParams();
							lp.topMargin=ImageUtil.dip2px(LoginActivity.this, 80);
							margin_ll.setLayoutParams(lp);
							
							if (isShow && pop != null) {
								initUserNamePop();
							}
						} else {
							RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) margin_ll.getLayoutParams();
							lp.topMargin=0;
							margin_ll.setLayoutParams(lp);
							if (isShow && pop != null) {
								initUserNamePop();
							}
						}
					}
				});
			}
		});
	}

	public void setListener() {
		username.addTextChangedListener(usernameTW);
		password.addTextChangedListener(passwordTW);
	}

	public void reg(View v) {
		Intent i = new Intent(this, RegFirst.class);
		startActivity(i);
	}

	public void delete_username_btn(View v) {
		username.setText("");
	}

	public void delete_password_btn(View v) {
		password.setText("");
	}

	public void more_username_btn(View v) {
		if (!isShow) {
			isShow = true;
			initUserNamePop();
		} else {
			isShow = false;
			initUserNamePop();
		}
	}

	/**
	 * 用户名输入框的输入事件
	 */
	TextWatcher usernameTW = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().length() > 0) {
				delete_username_btn.setVisibility(View.VISIBLE);
			} else {
				delete_username_btn.setVisibility(View.GONE);
			}
		}
	};
	TextWatcher passwordTW = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().length() > 0) {
				delete_password_btn.setVisibility(View.VISIBLE);
			} else {
				delete_password_btn.setVisibility(View.GONE);
			}
		}
	};

	class PopupAdapter extends ParentsAdapter {

		public PopupAdapter(Context c, List list) {
			super(c, list);
		}

		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			final String name = ((MoreLoginUser) list.get(position))
					.getUsername();
			final String pass = ((MoreLoginUser) list.get(position))
					.getPassword();
			if (convertView == null) {
				convertView = View.inflate(c, R.layout.username_popup, null);
				holder = new ViewHolder();
				holder.tv = (TextView) convertView.findViewById(R.id.more_user);
				holder.iv = (ImageView) convertView
						.findViewById(R.id.more_clear);
				holder.ll = (LinearLayout) convertView
						.findViewById(R.id.more_user_ll);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(name);
			holder.ll.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					initUserNamePop();
					username.setText(name);
					password.setText(pass);
				}
			});

			holder.iv.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					names.remove(position);
					ac.finalDb.deleteByWhere(MoreLoginUser.class, "username='"
							+ name + "'");
					adapter.notifyDataSetChanged();
					if (names.size() == 0) {
						initUserNamePop();
					}
				}
			});
			if (position == (list.size() - 1)) {
				holder.ll.setBackgroundResource(R.drawable.input_btm_btn);
			} else {
				holder.ll.setBackgroundResource(R.drawable.input_mid_btn);
			}
			return convertView;
		}

	}

	static class ViewHolder {
		TextView tv;
		ImageView iv;
		LinearLayout ll;
	}

	public void initUserNamePop() {
		if (pop == null) {
			if (adapter == null) {
				names = ac.finalDb.findAllByWhere(MoreLoginUser.class, "",
						"id desc");
				adapter = new PopupAdapter(getApplicationContext(), names);
				listView = new ListView(LoginActivity.this);
				int width = username_ll.getWidth();
				pop = new PopupWindow(listView, width,
						LayoutParams.WRAP_CONTENT);
				pop.setOutsideTouchable(true);
				listView.setItemsCanFocus(false);
				listView.setDivider(null);
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listView.setAdapter(adapter);
				pop.setFocusable(false);
				pop.showAsDropDown(username_ll);
				isShow = true;
				animate(more_username_btn).setDuration(200).rotation(180)
						.start();
			}
		} else if (pop.isShowing()) {
			pop.dismiss();
			isShow = false;
			animate(more_username_btn).setDuration(200).rotation(0).start();
		} else if (!pop.isShowing()) {
			names = ac.finalDb.findAllByWhere(MoreLoginUser.class, "",
					"id desc");
			adapter.setList(names);
			adapter.notifyDataSetChanged();
			pop.showAsDropDown(username_ll);
			isShow = true;
			animate(more_username_btn).setDuration(200).rotation(180).start();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (isShow && pop != null) {
				initUserNamePop();
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isShow && pop != null) {
			initUserNamePop();
			return;
		}
		super.onBackPressed();
	}

	public void login(View v) {
		str_username = username.getText().toString().trim();
		str_password = password.getText().toString().trim();

		if (!Util.isMobileNO(str_username)) {
			showCustomToast("请输入正确的用户名");
			return;
		} else if (!Util.isPassword(str_password)) {
			showCustomToast("请输入正确的密码");
			return;
		}

		AjaxParams ap = getAjaxParams();
		ap.put("mobile", str_username);
		ap.put("pwd", str_password);
		ap.put("cityid", String.valueOf(ac.cs.getLocationID()));
		ap.put("longitude", ac.cs.getLng());
		ap.put("latitude", ac.cs.getLat());
		ap.put("area", ac.cs.getArea());
		ap.put("dtype", "0");
		ap.put("deviceid", ac.cs.getDeviceId());

		ac.finalHttp.post(URL.LOGIN, ap, callBack);
	}

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					// 登陆记录
					ac.finalDb.deleteByWhere(MoreLoginUser.class, "username='"
							+ str_username + "'");
					ac.finalDb.save(new MoreLoginUser(str_username,
							str_password));

					User u = new Gson().fromJson(jo.getString(URL.RESPONSE),
							User.class);
					LoginUser lu = new LoginUser(u.getId(), str_username,
							str_password, u.getToken());

					// 保存用户
					ac.finalDb.deleteById(UserTable.class, u.getId());
					ac.finalDb.save(new UserTable(u));

					// 保存登陆用户
					ac.finalDb.deleteByWhere(LoginUser.class, "");
					ac.finalDb.save(lu);

					ac.startServices();

					if (!AM.getActivityManager().contains(
							PropertiesActivity.class.getName())) {
						Intent i = new Intent(LoginActivity.this,
								PropertiesActivity.class);
						startActivity(i);
					}
					finish();
					break;
				default:
					showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
							URL.INFO));
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		public void onStart() {
			customShowDialog(1);
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};
	};

	protected void onDestroy() {
		super.onDestroy();
		location.destory();
	};

	public void findpassword(View v) {
		Intent intent = new Intent(this, ForgetPassWordActivity1.class);
		startActivity(intent);
	}
}
