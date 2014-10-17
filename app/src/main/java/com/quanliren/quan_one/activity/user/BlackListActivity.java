package com.quanliren.quan_one.activity.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.adapter.BlackPeopleAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class BlackListActivity extends BaseActivity implements
		IXListViewListener {
	private static final String TAG = "BlackListActivity";
	public static final String CANCLEBLACKLIST = "com.quanliren.quan_one.activity.user.BlackListActivity.CANCLEBLACKLIST";
	public static final String ADDEBLACKLIST = "com.quanliren.quan_one.activity.user.BlackListActivity.ADDBLACKLIST";
	private String CACHEKEY = "";
	int p = 0;
	@ViewInject(id = R.id.listview)
	XXListView listview;
	BlackPeopleAdapter adapter;
	AjaxParams ap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.black_user);
		CACHEKEY = TAG + ac.getUser().getId();
		initAdapter();
		setTitleTxt("黑名单");
		setListener();

		String[] filter=new String[]{CANCLEBLACKLIST,ADDEBLACKLIST};
		receiveBroadcast(filter, handler);
	}

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			Intent i = (Intent) msg.obj;
			String action = i.getAction();
			if (action.equals(CANCLEBLACKLIST)) {
				String id= i.getExtras().getString("id");
				List<User> user=adapter.getList();
				User temp=null;
				for (User user2 : user) {
					if(user2.getId().equals(id)){
						temp=user2;
					}
				}
				if(temp!=null){
					adapter.removeObj(temp);
					adapter.notifyDataSetChanged();
				}
			}else if(action.equals(ADDEBLACKLIST)){
				User user=(User) i.getExtras().getSerializable("bean");
				user.setCtime(Util.fmtDateTime.format(new Date()));
				adapter.addFirstItem(user);
				adapter.notifyDataSetChanged();
			}
			super.dispatchMessage(msg);
		};
	};

	public void initAdapter() {
		List<User> list = new ArrayList<User>();
		CacheBean cb = ac.finalDb.findById(CACHEKEY, CacheBean.class);
		if (cb != null) {
			list = new Gson().fromJson(cb.getValue(),
					new TypeToken<ArrayList<User>>() {
					}.getType());
		}
		adapter = new BlackPeopleAdapter(this, list);
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);

	}

	public void setListener() {

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position > 0 && position <= adapter.getCount()) {
					User user = (User) adapter.getItem(position - 1);
					Intent i = new Intent(
							BlackListActivity.this,
							user.getId().equals(ac.getUser().getId()) ? UserInfoActivity.class
									: UserOtherInfoActivity.class);
					i.putExtra("id", user.getId());
					startActivity(i);
				}
			}
		});

		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position > 0) {
					menupop = new PopFactory(BlackListActivity.this,
							new String[] { "取消黑名单" }, new menuClick(
									position - 1), parent);
					menupop.toogle();
					return true;
				}
				return false;
			}
		});
	}

	class menuClick implements OnClickListener {

		int position;

		public menuClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			AjaxParams ap = getAjaxParams();
			switch (v.getId()) {
			case 0:
				ap.put("otherid", ((User) adapter.getItem(position)).getId());
				ac.finalHttp.post(URL.CANCLEBLACK, ap, new setLogoCallBack(
						position));
				break;
			}
			menupop.closeMenu();
		}
	};

	class setLogoCallBack extends AjaxCallBack<String> {

		int position;

		public setLogoCallBack(int position) {
			this.position = position;
		}

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
					deleteAnimate(position);
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

	private void performDismiss(final View dismissView, final int position) {
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();// 获取item的布局参数
		final int originalHeight = dismissView.getHeight();// item的高度

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
				.setDuration(200);
		animator.start();

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				adapter.removeObj(position);
				// 这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
				// 所以我们在动画执行完毕之后将item设置回来
				ViewHelper.setAlpha(dismissView, 1f);
				ViewHelper.setTranslationX(dismissView, 0);
				ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
				lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				dismissView.setLayoutParams(lp);

				adapter.notifyDataSetChanged();

				if (adapter.getCount() == 0) {
					listview.startRefresh();
				}
			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// 这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

	}

	public void deleteAnimate(final int position) {
		final View view = listview.getChildAt((position + 1)
				- listview.getFirstVisiblePosition());
		if (view != null) {
			ViewPropertyAnimator.animate(view).alpha(0).setDuration(200)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							performDismiss(view, position);
						}
					});

		} else {
			adapter.removeObj(position);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRefresh() {
		p = 0;
		onLocationSuccess();
	}

	@Override
	public void onLoadMore() {
		onLocationSuccess();
	}

	public void initParam() {
		if (p == 0) {
			ap = getAjaxParams();
			ap.put("p", p + "");
		}
	}

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			listview.stop();
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					jo = jo.getJSONObject(URL.RESPONSE);
					List<User> list = new Gson().fromJson(
							jo.getString(URL.LIST),
							new TypeToken<ArrayList<User>>() {
							}.getType());
					if (p == 0) {
						CacheBean cb = new CacheBean(CACHEKEY,
								jo.getString(URL.LIST), new Date().getTime());
						ac.finalDb.deleteById(CacheBean.class, CACHEKEY);
						ac.finalDb.save(cb);
						adapter.setList(list);
					} else {
						adapter.addNewsItems(list);
					}
					adapter.notifyDataSetChanged();
					listview.setPage(p = jo.getInt(URL.PAGEINDEX));
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				listview.stop();
			}
		};
	};

	public void onLocationSuccess() {
		initParam();
		ap.put("p", p + "");
		ac.finalHttp.post(URL.BLACKLIST, ap, callBack);
	}

}
