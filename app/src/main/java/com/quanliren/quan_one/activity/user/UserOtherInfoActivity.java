package com.quanliren.quan_one.activity.user;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.view.ViewHelper;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseUserActivity;
import com.quanliren.quan_one.activity.group.QuanPersonalActivity;
import com.quanliren.quan_one.activity.shop.ShopVipDetail;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.CustomDialogEditText;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.fragment.QuanPullListViewFragment;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class UserOtherInfoActivity extends BaseUserActivity {

	@ViewInject(id = R.id.guanzhu_btn, click = "guanzhu_btn")
	View guanzhu_btn;
	@ViewInject(id = R.id.leavemsg_btn, click = "leavemsg_btn")
	View leavemsg_btn;
	@ViewInject(id = R.id.care_me_txt)
	TextView care_me_txt;
	 @ViewInject(id = R.id.bottom_btn_ll)
	 View bottom_btn_ll;
	boolean enable = false;
	PopFactory menupop1;
	@ViewInject(id = R.id.lx_ll)
	View lx_ll;
	@ViewInject(id = R.id.qq_ll)
	View qq_ll;
	@ViewInject(id = R.id.qq)
	TextView qq;
	@ViewInject(id = R.id.mobile_ll)
	View mobile_ll;
	@ViewInject(id = R.id.mobile)
	TextView mobile;

	String id = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		id = getIntent().getExtras().getString("id");
		super.onCreate(savedInstanceState);
		UserTable ut = ac.finalDb.findById(id, UserTable.class);
		if (ut != null) {
			user = ut.getUser();
		}
		setContentView(R.layout.user_other_info);
		initPicAdapter();
		setTitleTxt(R.string.other_userinfo);
		initViewByUser();
		isblack();
		pro_btn.setImageResource(R.drawable.userinfo_give_icon);
		pro_btn.setVisibility(View.VISIBLE);

		careText();
	}

	public void careText() {
		if (user != null && user.getAttenstatus() != null) {
			if (user.getAttenstatus().equals("0")) {
				care_me_txt.setText("关注我吧");
			} else {
				care_me_txt.setText("已关注");
			}
		}
	}

	public void initPicAdapter() {
		ac.finalHttp.post(URL.GET_USER_INFO, getAjaxParams("otherid", id),
				callBack);
	}

	public void relation_btn(View v) {
		ac.finalHttp.post(URL.GETCONTACT, getAjaxParams("otherid", id),
				getContactCallBack);
	}

	public void guanzhu_btn(View v) {
		if (!enable) {
			return;
		}
		if (user == null) {
			return;
		}
		String str = user.getAttenstatus().equals("0") ? "您确定要关注TA吗?"
				: "您确定要取消关注吗?";
		new IosCustomDialog.Builder(UserOtherInfoActivity.this)
				.setMessage(str)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						AjaxParams ap = getAjaxParams();
						ap.put("otherid", id);
						ap.put("type", user.getAttenstatus() + "");
						String url = user.getAttenstatus().equals("0") ? URL.CONCERN
								: URL.CANCLECONCERN;
						ac.finalHttp.post(url, ap, concernCallBack);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).create().show();
	}

	public void leavemsg_btn(View v) {
		if (!enable) {
			return;
		}
		Intent i = new Intent(this, ChatActivity.class);
		i.putExtra("friend", user);
		startActivity(i);
	}

	public void personal_btn(View v) {
		Intent i = new Intent(this, QuanPersonalActivity.class);
		i.putExtra("type", QuanPullListViewFragment.ONCE);
		i.putExtra("otherid", id);
		startActivity(i);
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
					if (temp != null) {
						user = temp;
						UserTable dbUser = new UserTable(temp);
						ac.finalDb.deleteById(UserTable.class, dbUser.getId());
						ac.finalDb.save(dbUser);
						initViewByUser();
						isblack();
					}
					enable = true;
					careText();
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
	AjaxCallBack<String> getContactCallBack = new AjaxCallBack<String>() {
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
					if (Util.isStrNotNull(temp.getMobile())) {
						mobile_ll.setVisibility(View.VISIBLE);
						mobile.setText(temp.getMobile());
					} else {
						mobile_ll.setVisibility(View.GONE);
					}
					if (Util.isStrNotNull(temp.getQq())) {
						qq_ll.setVisibility(View.VISIBLE);
						qq.setText(temp.getQq());
					} else {
						qq_ll.setVisibility(View.GONE);
					}
					ViewHelper.setAlpha(lx_ll, 0);
					animate(lx_ll).setDuration(200).alpha(1)
							.setListener(new AnimatorListener() {
								@Override
								public void onAnimationStart(Animator animation) {
									lx_ll.setVisibility(View.VISIBLE);
								}

								@Override
								public void onAnimationRepeat(Animator animation) {
								}

								@Override
								public void onAnimationEnd(Animator animation) {
								}

								@Override
								public void onAnimationCancel(Animator animation) {
								}
							}).start();
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
	};
	AjaxCallBack<String> concernCallBack = new AjaxCallBack<String>() {
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
					user.setAttenstatus(user.getAttenstatus().equals("0") ? "1"
							: "0");
					ac.finalDb.update(user);
					if (user.getAttenstatus().equals("1")) {
						user.setConnum(Integer.valueOf(
								Integer.valueOf(user.getConnum()) + 1)
								.toString());
						showCustomToast("已添加关注");
					} else {
						user.setConnum(Integer.valueOf(
								Integer.valueOf(user.getConnum()) - 1)
								.toString());
						showCustomToast("已取消关注");
					}
					// connum.setText(user.getConnum() + "");
					careText();
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
	CustomDialogEditText mdialog;

	public void pro_btn(View v) {
		if (!enable) {
			return;
		}
		mdialog = new CustomDialogEditText.Builder(this)
				.setTitle("请输入要赠送的体力个数")
				.setPositiveButton("赠送", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String num=mdialog.getMessage().trim();
						if(num.length()>0){
							if(Integer.valueOf(num)>Integer.valueOf(ac.getUserInfo().getPowernum())){
								dialogBuyVip();
							}else{
								AjaxParams ap=getAjaxParams();
								ap.put("otherid", user.getId());
								ap.put("powernum", num);
								ac.finalHttp.post(URL.GIVETILI, ap,giveCallBack);
							}
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		mdialog.show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				showKeyBoard();
			}
		}, 2);
	};
	
	AjaxCallBack<String> giveCallBack=new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在赠送");
		};
		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("赠送成功");
					int lastpower=jo.getJSONObject(URL.RESPONSE).getInt("lastpower");
					User user=ac.getUserInfo();
					user.setPowernum(lastpower+"");
					UserTable ut=new UserTable(user);
					ac.finalDb.deleteById(UserTable.class, user.getId());
					ac.finalDb.save(ut);
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				customDismissDialog();
			}
		};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();showIntentErrorToast();
		};
	};
	
	public void dialogBuyVip(){
		new IosCustomDialog.Builder(this).setTitle("体力不足").setMessage("立即获取？")
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).setPositiveButton("获取", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(UserOtherInfoActivity.this,ShopVipDetail.class);
				startActivity(i);
			}
		}).create().show();
	}
	
	public void isblack(){
		if(user==null){
			return;
		}
		if(user.getIsblacklist()==0){
			bottom_btn_ll.setVisibility(View.VISIBLE);
			title_right_txt.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ban), null, null, null);
			setTitleRightTxt("举报/拉黑");
		}else{
			bottom_btn_ll.setVisibility(View.GONE);
			title_right_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			setTitleRightTxt("取消拉黑");
		}
	}
	
	public void rightClick(View v) {
		if(user.getIsblacklist()==0){
			menupop = new PopFactory(this, new String[] { "加入黑名单", "举报并拉黑" },
					menuClick, parent);
		menupop.toogle();
		}else{
			menupop = new PopFactory(this,
					new String[] { "取消黑名单" }, new menuClick(), parent);
			menupop.toogle();
		}
			
	};
	class menuClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			AjaxParams ap = getAjaxParams();
			switch (v.getId()) {
			case 0:
				ap.put("otherid", user.getId());
				ac.finalHttp.post(URL.CANCLEBLACK, ap, new setLogoCallBack(
						));
				break;
			}
			menupop.closeMenu();
		}
	};
	OnClickListener menuClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case 0:
				new IosCustomDialog.Builder(UserOtherInfoActivity.this)
						.setMessage("您确定要拉黑该用户吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										AjaxParams ap = getAjaxParams();
										ap.put("otherid", user.getId());
										ac.finalHttp.post(URL.ADDTOBLACK, ap,
												addBlackCallBack);
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create().show();
				break;
			case 1:
				new IosCustomDialog.Builder(UserOtherInfoActivity.this)
						.setMessage("您确定要举报并拉黑该用户吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (menupop1 == null) {
											menupop1 = new PopFactory(
													UserOtherInfoActivity.this,
													new String[] { "骚扰信息",
															"个人资料不当", "盗用他人资料",
															"垃圾广告", "色情相关" },
													menuClick1, parent);
										}
										menupop1.toogle();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create().show();
				break;
			}
			menupop.closeMenu();
		}
	};
	AjaxCallBack<String> addBlackCallBack = new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在发送请求");
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("操作成功");
					user.setIsblacklist(1);
					user.setAttenstatus("0");
					UserTable ut=new UserTable(user);
					ac.finalDb.deleteById(UserTable.class, user.getId());
					ac.finalDb.save(ut);
					isblack();
					careText();
					Intent i = new Intent(BlackListActivity.ADDEBLACKLIST);
					i.putExtra("bean", user);
					sendBroadcast(i);
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				customDismissDialog();
			}
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};
	};
	
	OnClickListener menuClick1 = new OnClickListener() {
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.exit:
				break;
			default:
				AjaxParams ap = getAjaxParams();
				ap.put("otherid", user.getId());
				ap.put("type", arg0.getId() + "");
				ac.finalHttp.post(URL.JUBAOANDBLACK, ap, addBlackCallBack);
				break;
			}
			menupop1.closeMenu();
		};
	};
	class setLogoCallBack extends AjaxCallBack<String> {

		public void onStart() {
			customShowDialog("正在发送请求");
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
					showCustomToast("取消成功");
					user.setIsblacklist(0);
					UserTable ut=new UserTable(user);
					ac.finalDb.deleteById(UserTable.class, user.getId());
					ac.finalDb.save(ut);
					isblack();
					Intent i = new Intent(BlackListActivity.CANCLEBLACKLIST);
					i.putExtra("id", user.getId());
					sendBroadcast(i);
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
