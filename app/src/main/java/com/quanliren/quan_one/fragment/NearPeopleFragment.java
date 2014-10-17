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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.location.ILocationImpl;
import com.quanliren.quan_one.activity.location.Location;
import com.quanliren.quan_one.activity.shop.ActActivity;
import com.quanliren.quan_one.activity.user.CustomFilterActivity;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.activity.user.UserOtherInfoActivity;
import com.quanliren.quan_one.adapter.NearPeopleAdapter;
import com.quanliren.quan_one.bean.AD;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.CustomFilterBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.util.URL;

public class NearPeopleFragment extends MenuFragmentBase implements IXListViewListener,ILocationImpl{

	private static final String TAG="NearPeopleFragment";
	int p=0;
	@ViewInject(id = R.id.listview)
	XXListView listview;
	NearPeopleAdapter adapter;
	@ViewInject(id = R.id.sex_ll)
	View sex_ll;
	@ViewInject(id = R.id.sex_ll_girl)
	View sex_ll_girl;
	@ViewInject(id = R.id.sex_girl)
	ImageView sex_girl;
	@ViewInject(id = R.id.sex_ll_boy)
	View sex_ll_boy;
	@ViewInject(id = R.id.sex_boy)
	ImageView sex_boy;
	AjaxParams ap=null;
	Location location;
	ImageView adImg;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.near_people, null);
		initInjectedView(v);
		initAdapter();
		setTitleTxt(R.string.near_people);
		setTitleLeftTxt("筛选");
		setListener();
		location=new Location(getActivity().getApplicationContext());
		location.setLocationListener(this);
		
		return v;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		location.destory();
	}
	
	@Override
	public void leftClick(View v) {
		if (menupop == null) {
			menupop = new PopFactory(getActivity(), new String[]{"全部","只看女♀","只看男♂"}, menuClick,parent);
		}
		menupop.toogle();
	}
	
	public void initAdapter() {
		List<User> list = new ArrayList<User>();
		CacheBean cb=ac.finalDb.findById(TAG, CacheBean.class);
		if(cb!=null){
			list=new Gson().fromJson(cb.getValue(), new TypeToken<ArrayList<User>>(){}.getType());
		}
		adapter = new NearPeopleAdapter(getActivity(), list);
		adImg = new ImageView(getActivity());
		adImg.setVisibility(View.GONE);
		adImg.setScaleType(ScaleType.CENTER_CROP);
		adImg.setAdjustViewBounds(true);
		listview.addHeaderView(adImg);
		
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);
		
	}

	public void setListener() {
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position>1){
					if(ac.getUserInfo().getIsvip()==0){
						goVip();return;
					}
					User user=adapter.getItem(position-1);
					Intent i = new Intent(getActivity(),user.getId().equals(ac.getUser().getId())?UserInfoActivity.class:UserOtherInfoActivity.class);
					i.putExtra("id", user.getId());
					startActivity(i);
				}
			}
		});
		
	}

	OnClickListener menuClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			CustomFilterBean cfb;
			switch (v.getId()) {
			case 0:
				ac.finalDb.deleteByWhere(CustomFilterBean.class, "");
				listview.startRefresh();
				break;
			case 1:
				cfb=new CustomFilterBean("性别", "女", "sex", 0);
				ac.finalDb.deleteById(CustomFilterBean.class, "sex");
				ac.finalDb.save(cfb);
				listview.startRefresh();
				break;
			case 2:
				cfb=new CustomFilterBean("性别", "男", "sex", 1);
				ac.finalDb.deleteById(CustomFilterBean.class, "sex");
				ac.finalDb.save(cfb);
				listview.startRefresh();
				break;
			case 3:
				Intent i = new Intent(getActivity(),CustomFilterActivity.class);
				startActivityForResult(i, 1);
				break;
			}
			menupop.closeMenu();
		}
	};

	@Override
	public void onRefresh() {
		p=0;
		location.startLocation();
		ac.finalHttp.post(URL.ADBANNER, getAjaxParams(), callback);
	}

	@Override
	public void onLoadMore() {
		onLocationSuccess();
	}
	
	public void initParam(){
		if(p==0){
			List<CustomFilterBean> listCB=ac.finalDb.findAll(CustomFilterBean.class);
			ap=getAjaxParams();
			
			if(listCB!=null)
			for (CustomFilterBean cfb : listCB) {
				ap.put(cfb.key, cfb.id+"");
			}
			
			ap.put("longitude", ac.cs.getLng());
			ap.put("latitude", ac.cs.getLat());
		}
	}
	
	AjaxCallBack<String> callBack=new AjaxCallBack<String>() {
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			listview.stop();
		};
		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					jo=jo.getJSONObject(URL.RESPONSE);
					List<User> list=new Gson().fromJson(jo.getString(URL.LIST), new TypeToken<ArrayList<User>>(){}.getType());
					if(p==0){
						CacheBean cb=new CacheBean(TAG, jo.getString(URL.LIST), new Date().getTime());
						ac.finalDb.deleteById(CacheBean.class, TAG);
						ac.finalDb.save(cb);
						adapter.setList(list);
					}else{
						adapter.addNewsItems(list);
					}
					adapter.notifyDataSetChanged();
					listview.setPage(p=jo.getInt(URL.PAGEINDEX));
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
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case 1:
				listview.startRefresh();
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
	public void onLocationSuccess() {
		initParam();
		ap.put("p", p+"");
		ac.finalHttp.post(URL.NearUserList,ap, callBack);
	}

	@Override
	public void onLocationFail() {
		listview.stop();
	};
	
	AD ad;

	AjaxCallBack<String> callback = new AjaxCallBack<String>() {

		public void onStart() {
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					ad = new Gson().fromJson(jo.getString(URL.RESPONSE),
							new TypeToken<AD>() {
							}.getType());
					ImageLoader.getInstance().displayImage(ad.getImgpath(),
							adImg, new SimpleImageLoadingListener() {
								public void onLoadingComplete(String imageUri,
										View view,
										android.graphics.Bitmap loadedImage) {
									try {
										int swidth=getResources().getDisplayMetrics().widthPixels;
										float widthScale=(float)swidth/(float)loadedImage.getWidth();
										int height=(int)(widthScale*loadedImage.getHeight());
										AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
												swidth, height);
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
	
	public void onResume() {
		super.onResume();
	};
}
