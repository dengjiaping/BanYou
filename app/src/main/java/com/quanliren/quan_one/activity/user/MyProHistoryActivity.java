package com.quanliren.quan_one.activity.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.shop.ProInfoActivity;
import com.quanliren.quan_one.adapter.MyProNumAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.ProBean;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;
import com.quanliren.quan_one.util.URL;

public class MyProHistoryActivity extends MenuFragmentBase implements LoaderImpl, IXListViewListener,OnItemClickListener,OnItemLongClickListener{

	private static final String TAG="MyProHistoryActivity";
	private String CACHEKEY=TAG;
	@ViewInject(id=R.id.listview)XXListView listview;
	MyProNumAdapter adapter;
	LoginUser user;
	private int p=0;
	
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view==null){
			view=inflater.inflate(R.layout.my_pro_num, null);
			FinalActivity.initInjectedView(this, view);
		}else{
			ViewParent parent=view.getParent();
			if(parent!=null&&parent instanceof ViewGroup){
				((ViewGroup)parent).removeView(view);
			}
		}
		return view;
	}

	@Override
	public void rightClick(View v) {
		
	}
	
	@Override
	public void onRefresh() {
		p=0;
		ac.finalHttp.post(URL.PRONUM,getAjaxParams().put("p", p+""), callBack);
	}

	@Override
	public void onLoadMore() {
		ac.finalHttp.post(URL.PRONUM,getAjaxParams().put("p", p+""), callBack);
	};
	
	AjaxCallBack<String> callBack=new AjaxCallBack<String>() {
		public void onStart() {};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			listview.stop();
			showIntentErrorToast();
		};
		public void onSuccess(String t) {
			try {
				JSONObject jo= new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					jo = jo.getJSONObject(URL.RESPONSE);
					List<ProBean> list = new Gson().fromJson(
							jo.getString(URL.LIST),
							new TypeToken<ArrayList<ProBean>>() {
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
					if(jo.has(URL.PAGEINDEX))
						listview.setPage(p = jo.getInt(URL.PAGEINDEX));
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				listview.stop();
			}
		};
	};


	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		if(getActivity()!=null&&init.compareAndSet(false, true)){
			user=ac.getUser();
			CACHEKEY+=user.getId();
			
			CacheBean cb=ac.finalDb.findById(CACHEKEY, CacheBean.class);
			List<ProBean> users=new ArrayList<ProBean>();
			if(cb!=null){
				users=new Gson().fromJson(cb.getValue(), new TypeToken<ArrayList<ProBean>>(){}.getType());
			}
			adapter=new MyProNumAdapter(getActivity(), users);
			listview.setAdapter(adapter);
			listview.setXListViewListener(this);
			listview.setOnItemClickListener(this);
			listview.setOnItemLongClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(position>0&&position<=adapter.getCount()){
			ProBean pb=(ProBean) adapter.getItem(position-1);
			pb.setWinstate(1);
			Intent i = new Intent(getActivity(),ProInfoActivity.class);
			i.putExtra("bean", pb);
			startActivity(i);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position,
			long arg3) {
		if(position>0&&position<=adapter.getCount()){
			new IosCustomDialog.Builder(getActivity()).setMessage("你确定要删除这条记录吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ProBean pb=(ProBean) adapter.getItem(position-1);
					ac.finalHttp.post(URL.DELETENUM,getAjaxParams("codeid",pb.getCodeid()), new deleteCallBack(pb));
					}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			}).create().show();
			
			return true;
		}
		return false;
	}
	
	class deleteCallBack extends AjaxCallBack<String>{
		ProBean pb;
		public deleteCallBack(ProBean pb){
			this.pb=pb;
		}
		@Override
		public void onStart() {
			customShowDialog("正在删除");
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		}
		@Override
		public void onSuccess(String t) {
			try {
				JSONObject jo= new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					int num=adapter.getList().indexOf(pb);
					if(num>-1)
						deleteAnimate(adapter.getList().indexOf(pb));
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
		}
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
}
