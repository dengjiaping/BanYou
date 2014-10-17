package com.quanliren.quan_one.fragment.shop;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.custom.CustomDialogEditText;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;

public class PowerFragment extends MenuFragmentBase{
	@ViewInject(id=R.id.other_num_click,click="otherNumClick")View otherNumClick;
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		if(view==null){
			view=inflater.inflate(R.layout.buy_tili, null);
			FinalActivity.initInjectedView(this, view);
//		}else{
//			ViewGroup group=(ViewGroup) view.getParent();
//			group.removeView(view);
//		}
		return view;
	}
	CustomDialogEditText mdialog;
	public void otherNumClick(View v){
		mdialog= new CustomDialogEditText.Builder(getActivity()).setPositiveButton("购买", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showCustomToast(mdialog.getMessage());
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
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
		
	}
}
