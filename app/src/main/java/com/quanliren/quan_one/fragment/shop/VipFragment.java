package com.quanliren.quan_one.fragment.shop;/*package com.quanliren.quan_one.fragment.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;

public class VipFragment extends MenuFragmentBase{

	View view;
	@ViewInject(id=R.id.btn1,click="btnClick")View btn1;
	@ViewInject(id=R.id.btn2,click="btnClick")View btn2;
	@ViewInject(id=R.id.btn3,click="btnClick")View btn3;
	
	@ViewInject(id=R.id.buy_btn1,click="buy_btnClick")View buy_btn1;
	@ViewInject(id=R.id.buy_btn2,click="buy_btnClick")View buy_btn2;
	@ViewInject(id=R.id.buy_btn3,click="buy_btnClick")View buy_btn3;
	
	@ViewInject(id=R.id.desc_ll1)View desc_ll1;
	@ViewInject(id=R.id.desc_ll2)View desc_ll2;
	@ViewInject(id=R.id.desc_ll3)View desc_ll3;
	List<View> list=new ArrayList<View>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		if(view==null){
			view=inflater.inflate(R.layout.shop_buy1, null);
//		}else{
//			ViewGroup vg=(ViewGroup) view.getParent();
//			vg.removeView(view);
//		}
			FinalActivity.initInjectedView(this,view);
			list.add(btn1);
			list.add(btn2);
			list.add(btn3);
			ViewHelper.setAlpha(desc_ll2, 0);
			ViewHelper.setAlpha(desc_ll3, 0);
			showing=desc_ll1;
		return view;
	}
	
	public void init(View clickView){
		for (View view : list) {
			View light=view.findViewById(R.id.light);
			View price=view.findViewById(R.id.price);
			TextView name=(TextView) view.findViewById(R.id.name);
			if(view.getId()!=clickView.getId()){
				light.setVisibility(View.INVISIBLE);
				price.setVisibility(View.INVISIBLE);
				name.setTextColor(getResources().getColor(R.color.title));
			}else{
				light.setVisibility(View.VISIBLE);
				price.setVisibility(View.VISIBLE);
				name.setTextColor(getResources().getColor(R.color.title_press_txt));
			}
		}
	}
	
	View showing;
	
	public void btnClick(View view){
		init(view);
		switch (view.getId()) {
		case R.id.btn1:
			doAnimate(desc_ll1);
			break;
		case R.id.btn2:
			doAnimate(desc_ll2);
			break;
		case R.id.btn3:
			doAnimate(desc_ll3);
			break;
		default:
			break;
		}
	}
	
	public void buy_btnClick(View view){
		switch (view.getId()) {
		case R.id.buy_btn1:
			showCustomToast("购买终生会员");
			break;
		case R.id.buy_btn2:
			showCustomToast("购买年会员");
			break;
		case R.id.buy_btn3:
			showCustomToast("购买季会员");
			break;
		default:
			break;
		}
	}
	
	public void doAnimate(final View view){
		if(showing!=null&&view.getId()!=showing.getId()){
			ViewHelper.setAlpha(showing, 1);
			ViewHelper.setAlpha(view, 0);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					ViewPropertyAnimator.animate(showing).alpha(0).setDuration(200).setListener(new AnimatorListenerAdapter() {
						public AtomicBoolean init=new AtomicBoolean(false);
						@Override
						public void onAnimationEnd(Animator animation) {
							if(init.compareAndSet(false, true)){
								showing.clearAnimation();
								showing.setVisibility(View.GONE);
								ViewPropertyAnimator.animate(view).alpha(1).setDuration(200).setListener(new AnimatorListenerAdapter() {
									public AtomicBoolean init1=new AtomicBoolean(false);
									public AtomicBoolean init2=new AtomicBoolean(false);
									@Override
									public void onAnimationStart(Animator animation) {
										if(init1.compareAndSet(false, true)){
											view.setVisibility(View.VISIBLE);
											showing=view;
										}
									}
									@Override
									public void onAnimationEnd(Animator animation) {
										if(init2.compareAndSet(false, true)){
											view.clearAnimation();
										}
									}
								}).start();
							}
						}
					}).start();
				}
			},2);
		}
	}
	
	class listener1 extends AnimatorListenerAdapter {
		public AtomicBoolean init=new AtomicBoolean(false);
		@Override
		public void onAnimationEnd(Animator animation) {
			if(init.compareAndSet(false, true)){
				showing.setVisibility(View.GONE);
				ViewPropertyAnimator.animate(view).alpha(1).setDuration(200).setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						showing=view;
						if(view.getVisibility()==View.GONE)
							view.setVisibility(View.VISIBLE);
					}
				}).start();
			}
		}
	};
	class listener2 extends AnimatorListenerAdapter{
		
	}
}
*/