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
import android.view.ViewGroup;

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
import com.quanliren.quan_one.adapter.GroupMessageAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.MessageListBean;
import com.quanliren.quan_one.util.URL;

public class MyGroupMessageActivity extends BaseActivity implements
		IXListViewListener {

	public static final String TAG = "MyGroupMessageActivity";
	public static final String BROADCAST = "com.quanliren.quan_one.MyLeaveMessageActivity";
	@ViewInject(id = R.id.listview)
	XXListView listview;
	GroupMessageAdapter adapter;
	String maxid = "0";
	LoginUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_leavemessage_list);
		setTitleTxt("群消息");
		user = ac.getUser();
		initAdapter();

		receiveBroadcast(BROADCAST, broadcast);
	}

	public void initAdapter() {
		List<MessageListBean> list = new ArrayList<MessageListBean>();
		CacheBean cb = ac.finalDb.findById(TAG + user.getId(), CacheBean.class);
		if (cb != null) {
			list = new Gson().fromJson(cb.getValue(),
					new TypeToken<ArrayList<MessageListBean>>() {
					}.getType());
		}
		adapter = new GroupMessageAdapter(this, list);
		adapter.handler = handler;
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);

	}

	@Override
	public void onRefresh() {
		maxid = "0";
		ac.finalHttp.post(URL.GROUPMESSAGELIST, getAjaxParams("maxid", maxid),
				callBack);
	}

	@Override
	public void onLoadMore() {
		ac.finalHttp.post(URL.GROUPMESSAGELIST, getAjaxParams("maxid", maxid),
				callBack);
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
					JSONObject response = jo.getJSONObject(URL.RESPONSE);
					List<MessageListBean> users = new Gson().fromJson(
							response.getString(URL.LIST),
							new TypeToken<ArrayList<MessageListBean>>() {
							}.getType());
					if (maxid.equals("0")) {
						ac.finalDb.deleteById(CacheBean.class,
								TAG + user.getId());
						ac.finalDb.save(new CacheBean(TAG + user.getId(),
								response.getString(URL.LIST), new Date()
										.getTime()));
						adapter.setList(users);
					} else {
						adapter.addNewsItems(users);
					}
					adapter.notifyDataSetChanged();
					listview.setPage(response.getInt(URL.PAGEINDEX));
					break;
				case -1:
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

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			AjaxParams ap;
			MessageListBean bean = (MessageListBean) msg.obj;
			switch (msg.what) {
			case 1:
				ap = getAjaxParams();
				ap.put("msgid", bean.getId());
				ac.finalHttp.post(URL.DELETEGROUPMESSAGE, ap,
						new deleteCallBack(bean));
				break;
			case 2:
				ap = getAjaxParams();
				ap.put("msgid", bean.getId());
				ap.put("isconsent", "0");
				ac.finalHttp.post(URL.AGREEORNOT, ap, new agree(bean, 0));
				break;
			case 3:
				ap = getAjaxParams();
				ap.put("msgid", bean.getId());
				ap.put("isconsent", "1");
				ac.finalHttp.post(URL.AGREEORNOT, ap, new agree(bean, 1));
				break;
			}
			super.dispatchMessage(msg);
		};
	};

	class agree extends AjaxCallBack<String> {
		MessageListBean bean;
		int type;

		public agree(MessageListBean bean, int type) {
			this.bean = bean;
			this.type = type;
		}

		public void onStart() {
			customShowDialog("正在处理");
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					switch (type) {
					case 0:
						showCustomToast("已同意");
						bean.setApplystatus("1");
						break;
					case 1:
						showCustomToast("已拒绝");
						bean.setApplystatus("2");
						break;
					}
					adapter.notifyDataSetChanged();
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
	};

	class deleteCallBack extends AjaxCallBack<String> {
		MessageListBean bean;

		public deleteCallBack(MessageListBean bean) {
			this.bean = bean;
		}

		public void onStart() {
			customShowDialog("正在删除");
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("删除成功");
					final int position = adapter.getList().indexOf(bean);
					if (position > -1) {
						final View view = listview.getChildAt((position + 1)
								- listview.getFirstVisiblePosition());
						if (view != null) {
							ViewPropertyAnimator.animate(view).alpha(0)
									.setDuration(200)
									.setListener(new AnimatorListenerAdapter() {
										@Override
										public void onAnimationEnd(
												Animator animation) {
											performDismiss(view, position);
										}
									});

						} else {
							adapter.removeObj(position);
							adapter.notifyDataSetChanged();
						}
					}
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
				ViewHelper.setAlpha(dismissView, 1f);
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

	Handler broadcast = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			Intent i = (Intent) msg.obj;
			String action = i.getAction();
			if (action.equals(BROADCAST)) {
				List<MessageListBean> list = adapter.getList();
				for (MessageListBean messageListBean : list) {
					if (messageListBean.getSenduid().equals(
							i.getStringExtra("id"))) {
						messageListBean.setCnt("0");
					}
				}
				adapter.notifyDataSetChanged();
			}
			super.dispatchMessage(msg);
		};
	};
}
