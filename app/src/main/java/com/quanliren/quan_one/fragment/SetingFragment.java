package com.quanliren.quan_one.fragment;/*package com.quanliren.quan_one.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.seting.AboutUsActivity;
import com.quanliren.quan_one.activity.seting.InviteFriendActivity;
import com.quanliren.quan_one.activity.user.LoginActivity;
import com.quanliren.quan_one.activity.user.ModifyPasswordActivity;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.URL;

public class SetingFragment extends MenuFragmentBase{

	@ViewInject(id=R.id.message_remind_btn,click="messageRemind")public ImageButton imgBtn;
//	@ViewInject(id=R.id.about_us_btn,click="about")public View about_us_btn;
	@ViewInject(id=R.id.invite_btn,click="invite")public View invite_btn;
	@ViewInject(id=R.id.modifypasswordbtn,click="modify") public View modifypasswordbtn;
	@ViewInject(id=R.id.loginout_btn,click="loginout") public View loginout_btn;
	CommonShared cs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.seting, null);
		
		FinalActivity.initInjectedView(this, v);
		
		setTitleTxt(R.string.seting);
		
		cs=new CommonShared(getActivity().getApplication());
		
		if(cs.getMessageRemind()==CommonShared.OPEN){
			imgBtn.setImageResource(R.drawable.seting_focus);
		}else{
			imgBtn.setImageResource(R.drawable.seting_unfocus);
		}
		return v;
	}
	
	public void setTitle(int str){
		title.setText(str);
	}
	
	public void about(View v){
		Intent i =  new Intent(getActivity(),AboutUsActivity.class);
		startActivity(i);
	}
	
	public void invite(View v){
		Intent i =  new Intent(getActivity(),InviteFriendActivity.class);
		startActivity(i);
	}
	
	public void modify(View v){
		Intent i = new Intent(getActivity(),ModifyPasswordActivity.class);
		startActivity(i);
	}
	
	public void messageRemind(View v){
		if(cs.getMessageRemind()==CommonShared.OPEN){
			cs.setMessageRemind(CommonShared.CLOSE);
			imgBtn.setImageResource(R.drawable.seting_unfocus);
		}else{
			cs.setMessageRemind(CommonShared.OPEN);
			imgBtn.setImageResource(R.drawable.seting_focus);
		}
	}
	
	public void loginout(View v){
		new IosCustomDialog.Builder(getActivity()).setMessage("您确定要残忍的离开吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				ac.finalHttp.post(URL.LOGOUT,getAjaxParams(), null);
				ac.finalDb.deleteByWhere(LoginUser.class, "");
				startActivity(new Intent(getActivity(),LoginActivity.class));
				getActivity().finish();				
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create().show();;
	}
	
}
*/