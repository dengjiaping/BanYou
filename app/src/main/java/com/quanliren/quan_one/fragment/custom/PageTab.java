package com.quanliren.quan_one.fragment.custom;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.nineoldandroids.view.ViewHelper;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.custom.SanJiaoView;
import com.quanliren.quan_one.util.ImageUtil;

public class PageTab extends Fragment{

	ArrayList<String> titles=new ArrayList<String>();
	@ViewInject(id=R.id.tabs_ll)LinearLayout tabs_ll;
	@ViewInject(id=R.id.tab_icon)SanJiaoView tab_icon;
	int tabWidth=0;
	int width=0;
	OnPageTitleClickListener listener;
	
	
	public void setListener(OnPageTitleClickListener listener) {
		this.listener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		titles=getArguments().getStringArrayList("titles");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.page_tab, null);
		FinalActivity.initInjectedView(this, view);
		for (int i = 0; i < titles.size(); i++) {
			TextView tv=null;
			tabs_ll.addView(tv=createTextView(titles.get(i)));
			final int num=i;
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
//					listener=(OnPageTitleClickListener) getParentFragment();
					listener.click(num);
				}
			});
		}
		
		int screenW=getResources().getDisplayMetrics().widthPixels;
		tabWidth=(int)((float)screenW/titles.size());
		
		width=ImageUtil.dip2px(getActivity(), 12);
		
		ViewHelper.setTranslationX(tab_icon, now=((tabWidth-width)/2));
		return view;
	}
	
	public TextView createTextView(String str){
		TextView tv=new TextView(getActivity());
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT);
		lp.weight=1;
		tv.setGravity(Gravity.CENTER);
		tv.setLayoutParams(lp);
		tv.setTextSize(14);
		tv.setTextColor(getResources().getColor(R.color.nav_bar_text));
		tv.setText(str);
		return tv;
	}
	
	float now=0;
	public void setCurrendIndex(final int i){
		if(getActivity()!=null){
			for (int j = 0; j < titles.size(); j++) {
				if(i!=j){
					((TextView)(tabs_ll.getChildAt(j))).setTextColor(getResources().getColor(R.color.username_normal));
				}else{
					((TextView)(tabs_ll.getChildAt(j))).setTextColor(Color.WHITE);
				}
			}
		}
	}
	
	public interface OnPageTitleClickListener{
		void click(int index);
	}
	
	public void onPageScroll(int i, float bai, int x){
		ViewHelper.setTranslationX(tab_icon,tabWidth * i+now+tabWidth*bai);
	}
	
}
