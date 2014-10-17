package com.quanliren.quan_one.activity.group;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.user.MyCareActivity;
import com.quanliren.quan_one.adapter.GroupMemberAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.BladeView;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.custom.PinnedHeaderListView;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.URL;

public class GroupMemberListActivity extends BaseActivity {

	public static final String TAG = "GroupMemberListActivity";
	private String CACHEKEY = "";
	private GroupBean groupBean;
	@ViewInject(id = R.id.member_list)
	PinnedHeaderListView listview;
	@ViewInject(id = R.id.letterlistview)
	BladeView letter;
	@ViewInject(id = R.id.edittext)
	EditText input;
	GroupMemberAdapter adapter;
	private static final String FORMAT = "^[a-z,A-Z].*$";
	// 首字母集
	private List<String> mSections;
	// 根据首字母存放数据
	private Map<String, List<User>> mMap;
	// 首字母位置集
	private List<Integer> mPositions;
	// 首字母对应的位置
	private Map<String, Integer> mIndexer;
	private List<User> list;
	private List<User> adapterList;
	private int p = 0;
	LinearLayout animateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		groupBean = (GroupBean) getIntent().getSerializableExtra("group");
		CACHEKEY = TAG + groupBean.getId();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_member_list);
		setTitleTxt("群成员");
		if(groupBean.getCrowdrole()>0){
			setTitleRightTxt("邀请");
		}
		initAdapter();
		setListener();
		init();
	}

	@Override
	public void rightClick(View v) {
		Intent i = new Intent(this,MyCareActivity.class);
		ArrayList<String> users=new ArrayList<String>();
		for (User user : list) {
			users.add(user.getUserid());
		}
		i.putStringArrayListExtra("user", users);
		i.putExtra("group", groupBean);
		startActivity(i);
	}
	
	public void initAdapter() {
		CacheBean cb = ac.finalDb.findById(CACHEKEY, CacheBean.class);
		list = new ArrayList<User>();
		if (cb != null) {
			list = new Gson().fromJson(cb.getValue(),
					new TypeToken<ArrayList<User>>() {
					}.getType());
		}
		adapter = new GroupMemberAdapter(this, initData(list), mSections,
				mPositions);
		adapter.handler = handler;
		adapter.group=groupBean;
		listview.setAdapter(adapter);
	}

	public void setListener() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				User user = (User) adapter.getItem(position);
				if (groupBean.getCrowdrole() > 0 && user.getUserrole() != 2&&!user.getUserid().equals(ac.getUser().getId())) {

					final LinearLayout managell = (LinearLayout) view
							.findViewById(R.id.manage_ll);
					if (animateView != null && !animateView.equals(managell)) {
						final LinearLayout temp = animateView;
						final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) temp
								.getLayoutParams();
						if (lp.height > 0) {
							ValueAnimator animator = ValueAnimator.ofInt(
									ImageUtil.dip2px(
											GroupMemberListActivity.this, 40),
									0).setDuration(200);
							animator.start();
							animator.addUpdateListener(new AnimatorUpdateListener() {

								@Override
								public void onAnimationUpdate(
										ValueAnimator animation) {
									lp.height = (Integer) animation
											.getAnimatedValue();
									temp.setLayoutParams(lp);
								}
							});
						}
					}
					animateView = managell;
					final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) managell
							.getLayoutParams();
					ValueAnimator animator;
					if (lp.height == 0) {
						animator = ValueAnimator.ofInt(
								0,
								ImageUtil.dip2px(GroupMemberListActivity.this,
										40)).setDuration(200);
					} else {
						animator = ValueAnimator.ofInt(
								ImageUtil.dip2px(GroupMemberListActivity.this,
										40), 0).setDuration(200);
					}
					animator.start();
					animator.addUpdateListener(new AnimatorUpdateListener() {

						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							lp.height = (Integer) animation.getAnimatedValue();
							managell.setLayoutParams(lp);
						}
					});
				}
			}
		});
		letter.setOnItemClickListener(new BladeView.OnItemClickListener() {

			@Override
			public void onItemClick(String s) {
				if (mIndexer.get(s) != null) {
					listview.setSelection(mIndexer.get(s));
				}
			}
		});
		listview.setOnScrollListener(adapter);
		listview.setPinnedHeaderView(LayoutInflater.from(this).inflate(
				R.layout.list_head, listview, false));
		input.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence text, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				List<User> lists = new ArrayList<User>();
				for (User a : list) {
					if (a.getNickname().indexOf(text.toString()) > -1) {
						lists.add(a);
					}
				}
				lists = initData(lists);
				adapter.setList(lists);
				adapter.setmFriendsPositions(mPositions);
				adapter.setmFriendsSections(mSections);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});

			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			public void afterTextChanged(Editable view) {
			}
		});
	}

	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
//		char[] nameChar = chines.toCharArray();
//		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
//		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//		for (int i = 0; i < nameChar.length; i++) {
//			if (nameChar[i] > 128) {
//				try {
//					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
//							nameChar[i], defaultFormat)[0].charAt(0);
//					return pinyinName;
//				} catch (BadHanyuPinyinOutputFormatCombination e) {
//					e.printStackTrace();
//				}
//			} else {
//				pinyinName += nameChar[i];
//				return pinyinName;
//			}
//		}
		return pinyinName;
	}

	public List<User> initData(List<User> list) {

		mSections = new ArrayList<String>();
		mMap = new HashMap<String, List<User>>();
		mPositions = new ArrayList<Integer>();
		mIndexer = new HashMap<String, Integer>();

		// Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
		Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
		String[] arrStrings = new String[list.size()];
		Map<String, User> tempMap = new HashMap<String, User>();
		for (int i = 0; i < list.size(); i++) {
			arrStrings[i] = list.get(i).getNickname() + i;
			tempMap.put(arrStrings[i], list.get(i));
		}
		// 使根据指定比较器产生的顺序对指定对象数组进行排序。
		Arrays.sort(arrStrings, comparator);
		List<User> arrayList = new ArrayList<User>();
		for (int i = 0; i < arrStrings.length; i++) {
			arrayList.add(tempMap.get(arrStrings[i]));
		}

		adapterList = arrayList;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getUserrole() > 0) {
				continue;
			}
			String firstName = converterToFirstSpell(list.get(i).getNickname());
			if (firstName.matches(FORMAT)) {
				if (mSections.contains(firstName)) {
					mMap.get(firstName).add(list.get(i));
				} else {
					mSections.add(firstName);
					List<User> lists = new ArrayList<User>();
					lists.add(list.get(i));
					mMap.put(firstName, lists);
				}
			} else {
				if (mSections.contains("#")) {
					mMap.get("#").add(list.get(i));
				} else {
					mSections.add("#");
					List<User> lists = new ArrayList<User>();
					lists.add(list.get(i));
					mMap.put("#", lists);
				}
			}
		}

		Collections.sort(mSections);

		List<User> list1 = new ArrayList<User>();
		for (User user : list) {
			if (user.getUserrole() == 2) {
				list1.add(user);
				break;
			}
		}
		if (list1.size() > 0) {
			mSections.add(0, "群主");
			mMap.put("群主", list1);
		}

		List<User> list2 = new ArrayList<User>();
		for (User user : list) {
			if (user.getUserrole() == 1) {
				list2.add(user);
			}
		}
		if (list2.size() > 0) {
			mSections.add(1, "管理员");
			mMap.put("管理员", list2);
		}

		int position = 0;
		for (int i = 0; i < mSections.size(); i++) {
			mIndexer.put(mSections.get(i), position);// 存入map中，key为首字母字符串，value为首字母在listview中位置
			mPositions.add(position);// 首字母在listview中位置，存入list中
			position += mMap.get(mSections.get(i)).size();// 计算下一个首字母在listview的位置
		}

		for (int i = 0; i < adapterList.size() - 1; i++) {
			for (int j = 1; j < adapterList.size() - i; j++) {
				Integer a = adapterList.get(j - 1).getUserrole();
				Integer b = adapterList.get(j).getUserrole();
				if (a.compareTo(b) < 0) { // 比较两个整数的大小
					User temp = adapterList.get(j - 1);
					adapterList.set((j - 1), adapterList.get(j));
					adapterList.set(j, temp);
				}
			}
		}
		return adapterList;
	}

	public void init() {
		AjaxParams ap = getAjaxParams();
		ap.put("p", "0");
		ap.put("crowdid", groupBean.getId());
		ac.finalHttp.post(URL.GETGROUPMEMBERLIST, ap, callBack);
	}

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					JSONObject response = jo.getJSONObject(URL.RESPONSE);
					List<User> users = new Gson().fromJson(
							response.getString(URL.LIST),
							new TypeToken<ArrayList<User>>() {
							}.getType());
					if (p == 0) {
						CacheBean cb = new CacheBean(CACHEKEY,
								response.getString(URL.LIST),
								new Date().getTime());
						ac.finalDb.deleteById(CacheBean.class, CACHEKEY);
						ac.finalDb.save(cb);
					}
					list = users;
					adapter.setList(initData(list));
					adapter.setmFriendsSections(mSections);
					adapter.setmFriendsPositions(mPositions);
					adapter.notifyDataSetChanged();
					p = response.getInt(URL.PAGEINDEX);
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

		public void onStart() {
			customShowDialog(1);
		};
	};

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:// 设置管理员
				amendUserDialog((User) msg.obj, 1);
				break;
			case 2:// 删除成员
				deleteUserDialog((User) msg.obj);
				break;
			case 3:// 取消管理员
				amendUserDialog((User) msg.obj, 2);
				break;
			}
			super.dispatchMessage(msg);
		};
	};

	public void amendUserDialog(final User user, final int type) {
		IosCustomDialog.Builder dialog = new IosCustomDialog.Builder(this);
		switch (type) {
		case 1:
			dialog.setMessage("您确定要将该成员设置为管理员吗？");
			break;
		case 2:
			dialog.setMessage("您确定要将移除该管理员吗？");
			break;
		}
		dialog.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				AjaxParams ap = getAjaxParams();
				ap.put("crowdid", groupBean.getId());
				ap.put("type", type + "");
				ap.put("memberid", user.getUserid());
				ac.finalHttp.post(URL.AMENTMEMBER, ap, new amendCallBack(user,
						type));
			}
		}).setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create().show();
	}

	public void deleteUserDialog(final User user) {
		new IosCustomDialog.Builder(this).setMessage("您确定要删除改成员吗？")
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						AjaxParams ap = getAjaxParams();
						ap.put("crowdid", groupBean.getId());
						ap.put("type", "0");
						ap.put("memberid", user.getUserid());
						ac.finalHttp.post(URL.AMENTMEMBER, ap,
								new deleteCallBack(user));
					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create().show();
	}

	class amendCallBack extends AjaxCallBack<String> {
		User user;
		int type;

		public amendCallBack(User user, int type) {
			this.user = user;
			this.type = type;
		}

		@Override
		public void onStart() {
			switch (type) {
			case 1:
				customShowDialog("正在设置管理员");
				break;
			case 2:
				customShowDialog("正在移除管理员");
				break;
			}
		}

		@Override
		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					switch (type) {
					case 1:
						user.setUserrole(1);
						break;
					case 2:
						user.setUserrole(0);
						break;
					}
					adapter.setList(initData(list));
					adapter.setmFriendsSections(mSections);
					adapter.setmFriendsPositions(mPositions);
					adapter.notifyDataSetChanged();
					if (animateView != null) {
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) animateView
								.getLayoutParams();
						lp.height = 0;
						animateView.setLayoutParams(lp);
					}
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		}
	}

	class deleteCallBack extends AjaxCallBack<String> {
		User user;

		public deleteCallBack(User user) {
			this.user = user;
		}

		public void onStart() {
			customShowDialog("正在删除群成员");
		};

		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					list.remove(user);
					adapter.setList(initData(list));
					adapter.setmFriendsSections(mSections);
					adapter.setmFriendsPositions(mPositions);
					adapter.notifyDataSetChanged();
					if (animateView != null) {
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) animateView
								.getLayoutParams();
						lp.height = 0;
						animateView.setLayoutParams(lp);
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

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};
	};
}
