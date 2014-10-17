package com.quanliren.quan_one.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.ChoseLocationActivity;
import com.quanliren.quan_one.activity.group.PublishActivity;
import com.quanliren.quan_one.bean.CustomFilterQuanBean;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.util.URL;

public class QuanFragment extends MenuFragmentBase {
	QuanPullListViewFragment pull;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.quan, null);
		FinalActivity.initInjectedView(this, v);
		setTitleTxt(R.string.quan);
		setTitleRightTxt(R.string.comment);
		setTitleLeftTxt("筛选");
		pull = new QuanPullListViewFragment();
		Bundle b = new Bundle();
		b.putInt("type", QuanPullListViewFragment.ALL);
		pull.setArguments(b);
		getChildFragmentManager().beginTransaction()
				.replace(R.id.content, pull).commit();
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				pull.refresh();
			}
		});
		
		return v;
	}

	@Override
	public void rightClick(View v) {
		Intent i = new Intent(getActivity(), PublishActivity.class);
		startActivityForResult(i, 1);
	}

	@Override
	public void leftClick(View v) {
		if (menupop == null) {
			menupop = new PopFactory(getActivity(), new String[] { "全部", "城市",
					"只看女♀", "只看男♂" }, menuClick, parent);
		}
		menupop.toogle();
	}

	OnClickListener menuClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			CustomFilterQuanBean cfb;
			switch (v.getId()) {
			case 0:
				ac.finalDb.deleteByWhere(CustomFilterQuanBean.class, "");
				ac.cs.setChoseLocation("全国");
				ac.cs.setChoseLocationID(-1);
				pull.refere();
				break;
			case 2:
				cfb = new CustomFilterQuanBean("性别", "女", "sex", 0);
				ac.finalDb.deleteById(CustomFilterQuanBean.class, "sex");
				ac.finalDb.save(cfb);
				pull.refere();
				break;
			case 3:
				cfb = new CustomFilterQuanBean("性别", "男", "sex", 1);
				ac.finalDb.deleteById(CustomFilterQuanBean.class, "sex");
				ac.finalDb.save(cfb);
				pull.refere();
				break;
			case 1:
				Intent i = new Intent(getActivity(), ChoseLocationActivity.class);
				startActivityForResult(i, 2);
				break;
			}
			menupop.closeMenu();
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 2:
			switch (resultCode) {
			case 1:
//				CustomFilterQuanBean cfb = new CustomFilterQuanBean("城市", ac.cs.getChoseLocation(), "cityid", Integer.valueOf(ac.cs.getChoseLocationID()));
//				ac.finalDb.deleteById(CustomFilterQuanBean.class, "cityid");
//				ac.finalDb.save(cfb);
				pull.refere();
				break;

			default:
				break;
			}
			break;
		default:
			pull.onActivityResult(requestCode, resultCode, data);
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
