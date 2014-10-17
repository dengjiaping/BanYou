package com.quanliren.quan_one.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.DongTaiDetailActivity;
import com.quanliren.quan_one.activity.shop.ActActivity;
import com.quanliren.quan_one.adapter.QuanAdapter;
import com.quanliren.quan_one.adapter.QuanAdapter.IQuanAdapter;
import com.quanliren.quan_one.bean.AD;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.CustomFilterQuanBean;
import com.quanliren.quan_one.bean.DongTaiBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;
import com.quanliren.quan_one.util.URL;

public class QuanPullListViewFragment extends MenuFragmentBase implements
		LoaderImpl, IXListViewListener, IQuanAdapter {

	public static final String TAG = "QuanPullListViewFragment";
	public String CACHEKEY = "";
	@ViewInject(id = R.id.listview)
	XXListView listview;
	QuanAdapter adapter;
	View mView;
	int type, p = 0;
	String otherid = "";
	String crowdid = "";
	AjaxParams ap;
	ImageView adImg;
	public static final int ONCE = 0;
	public static final int ALL = 1;
	public static final int MYCARE = 2;
	public static final int GROUP = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			type = getArguments().getInt("type");
			if (getArguments().containsKey("otherid")) {
				otherid = getArguments().getString("otherid");
			}
			if (getArguments().containsKey("crowdid")) {
				crowdid = getArguments().getString("crowdid");
			}
		}
		LoginUser user = ac.getUser();
		CACHEKEY = user.getId() + otherid + crowdid + TAG + type;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null) {
				parent.removeView(mView);
			}
		} else {
			mView = inflater.inflate(R.layout.quanpulllistview, null);
			FinalActivity.initInjectedView(this, mView);
		}
		// initAdapter();
		return mView;
	}

	public void initAdapter() {
		List<User> list = new ArrayList<User>();
		CacheBean cb = ac.finalDb.findById(CACHEKEY, CacheBean.class);
		if (cb != null) {
			list = new Gson().fromJson(cb.getValue(),
					new TypeToken<ArrayList<DongTaiBean>>() {
					}.getType());
		}
		switch (type) {
		case ALL:
			adImg = new ImageView(getActivity());
			adImg.setVisibility(View.GONE);
			adImg.setScaleType(ScaleType.CENTER_CROP);
			listview.addHeaderView(adImg);
			break;
		}
		adapter = new QuanAdapter(getActivity(), list, this);
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void refresh() {
		if (getActivity() != null && init.compareAndSet(false, true)) {
			initAdapter();
			setListener();
		}
	}

	public void setListener() {
	}

	@Override
	public void onRefresh() {
		ap = getAjaxParams("dytype", type + "");
		String url = URL.DONGTAI;
		switch (type) {
		case ONCE:
			url = URL.PERSONALDONGTAI;
			if (!otherid.equals(""))
				ap.put("otherid", otherid);
			break;
		case GROUP:
			ap.put("crowdid", crowdid);
			break;
		default:
			List<CustomFilterQuanBean> listCB = ac.finalDb
					.findAll(CustomFilterQuanBean.class);

			if (listCB != null){
				for (CustomFilterQuanBean cfb : listCB) {
					ap.put(cfb.key, cfb.id + "");
				}
			}
			if(!ac.cs.getChoseLocationID().equals("-1"))
				ap.put("cityid", ac.cs.getChoseLocationID());
			break;
		}
		p = 0;
		
		
		ac.finalHttp.post(url, ap.put("p", p + ""), callBack);
		
		switch (type) {
		case ALL:
			ac.finalHttp.post(URL.ADBANNER, getAjaxParams(), callback);
			break;
		}
	}

	@Override
	public void onLoadMore() {
		String url = URL.DONGTAI;
		switch (type) {
		case ONCE:
			url = URL.PERSONALDONGTAI;
			break;
		default:
			break;
		}
		ac.finalHttp.post(url, ap.put("p", p + ""), callBack);
	}

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			showIntentErrorToast();
			listview.stop();
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					jo = jo.getJSONObject(URL.RESPONSE);
					List<DongTaiBean> list = new Gson().fromJson(
							jo.getString(URL.LIST),
							new TypeToken<ArrayList<DongTaiBean>>() {
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
					refere();
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

	public void refere() {
		if (getActivity() != null) {
			listview.startRefresh();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case 1:
				refere();
				break;
			case 2:
				DongTaiBean bean = (DongTaiBean) data
						.getSerializableExtra("bean");
				List<DongTaiBean> beans = adapter.getList();
				int position = -1;
				for (DongTaiBean b : beans) {
					if (b.getDyid().equals(bean.getDyid())) {
						position = beans.indexOf(b);
						break;
					}
				}
				if (position != -1)
					deleteAnimate(position);
				break;
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
	public void detailClick(DongTaiBean bean) {
		Intent i = new Intent(getActivity(), DongTaiDetailActivity.class);
		i.putExtra("bean", bean);
		startActivityForResult(i, 1);
	}

	AD ad;
	
	AjaxCallBack<String> callback = new AjaxCallBack<String>() {

		public void onStart() {
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					ad=new Gson().fromJson(jo.getString(URL.RESPONSE), new TypeToken<AD>(){}.getType());
					ImageLoader.getInstance().displayImage(ad.getImgpath(), adImg,new SimpleImageLoadingListener(){
						public void onLoadingComplete(String imageUri, View view, android.graphics.Bitmap loadedImage) {
							try {
								int swidth=getResources().getDisplayMetrics().widthPixels;
								float widthScale=(float)swidth/(float)loadedImage.getWidth();
								int height=(int)(widthScale*loadedImage.getHeight());
								AbsListView.LayoutParams lp = new AbsListView.LayoutParams(swidth,height);
								adImg.setLayoutParams(lp);
								adImg.setVisibility(View.VISIBLE);
								adImg.setImageBitmap(loadedImage);
								adImg.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View arg0) {
										Intent i = new Intent(getActivity(),ActActivity.class);
										i.putExtra("url", ad.getUrl());
										i.putExtra("title", "活动");
										startActivity(i);
									}
								});
								} catch (Exception e) {
								e.printStackTrace();
							}
						};
					});
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
		};
	};
}
