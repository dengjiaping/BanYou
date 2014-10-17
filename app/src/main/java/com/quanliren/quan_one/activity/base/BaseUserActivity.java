package com.quanliren.quan_one.activity.base;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.QuanPersonalActivity;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity;
import com.quanliren.quan_one.activity.user.UserInfoEditActivity;
import com.quanliren.quan_one.activity.user.UserPicFragment;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

public abstract class BaseUserActivity extends BaseActivity {

	@ViewInject(id = R.id.personal_ll, click = "personal_btn")
	public View personal_btn;
	@ViewInject(id=R.id.pro_btn,click="pro_btn")public ImageView pro_btn;
	@ViewInject(id = R.id.location_txt)
	public TextView location_txt;
	@ViewInject(id = R.id.xingzuo_txt)
	public TextView xingzuo_txt;
	@ViewInject(id = R.id.sex)
	public TextView sex;
	@ViewInject(id=R.id.vip)
	public View vip;
	@ViewInject(id = R.id.qq)
	public TextView qq;
	@ViewInject(id = R.id.signature)
	public TextView signature;
	@ViewInject(id = R.id.love)
	public TextView love;
	@ViewInject(id = R.id.work)
	public TextView work;
	@ViewInject(id = R.id.place)
	public TextView place;
	@ViewInject(id = R.id.userlogo)
	public ImageView userlogo;
	@ViewInject(id = R.id.power_num)
	public TextView power_num;
	@ViewInject(id=R.id.dycontent)
	public TextView dycontent;
	@ViewInject(id=R.id.dytime)
	public TextView dytime;
	@ViewInject(id=R.id.dyimg)
	public ImageView dyimg;
	@ViewInject(id=R.id.dongtai_no)
	public View dongtai_no;
	@ViewInject(id=R.id.personal_ll)
	public View personal_ll;

	public User user = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void rightClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, UserInfoEditActivity.class);
		startActivity(i);
	}

	public void personal_btn(View v) {
		Intent i = new Intent(this, QuanPersonalActivity.class);
		startActivity(i);
	}

	UserPicFragment fragment;

	public void initViewByUser() {

		if(user==null){
			return;
		}
		
		try {
			setTitleTxt(user.getNickname());
			if(Util.isStrNotNull(user.getAvatar())){
				ImageLoader.getInstance().displayImage(
						user.getAvatar() + StaticFactory._320x320, userlogo);
			}

			if(fragment==null){
				fragment=new UserPicFragment();
				if(user.getImglist()!=null&&user.getImglist().size()>0){
					MessageList list=new MessageList();
					list.imgList=user.getImglist();
					Bundle b = new Bundle();
					b.putSerializable("list", list);
					fragment.setArguments(b);
				}
				getSupportFragmentManager().beginTransaction().replace(R.id.pic_contents, fragment).commit();
			}else{
				if(user.getImglist()!=null&&user.getImglist().size()>0){
					fragment.setList(user.getImglist());
				}
			}
			
			if(user.getSex().equals("0")){
				sex.setBackgroundResource(R.drawable.girl_icon);
			}else{
				sex.setBackgroundResource(R.drawable.boy_icon);
			}
			sex.setText(user.getUserAge());


			if (Util.isStrNotNull(user.getHobby())) {
				love.setText(user.getHobby());
			}

			if (Util.isStrNotNull(user.getJob())) {
				work.setText(user.getJob());
			}

			if (Util.isStrNotNull(user.getHometown())) {
				place.setText(user.getHometown());
			} 
			if(power_num!=null)
				power_num.setText(user.getPowernum() + "");
			if (Util.isStrNotNull(user.getSignature())) {
				signature.setText(user.getSignature());
			} else {
				signature.setText("无");
			}

			if (Util.isStrNotNull(user.getConstellation())) {
				xingzuo_txt.setVisibility(View.VISIBLE);
//				Integer i = getxingzuoMap().get(user.getConstellation());
//				if (i != null)
				xingzuo_txt.setText(user.getConstellation());
			} else {
				xingzuo_txt.setVisibility(View.GONE);
			}

			if (Util.isStrNotNull(user.getCityname())) {
				location_txt.setVisibility(View.VISIBLE);
				location_txt.setText(user.getCityname());
			} else {
				location_txt.setVisibility(View.GONE);
			}
			
			
			if(!Util.isStrNotNull(user.getDyimgurl())&&!Util.isStrNotNull(user.getDycontent())){
				dongtai_no.setVisibility(View.VISIBLE);
				personal_ll.setVisibility(View.GONE);
			}else{
				dongtai_no.setVisibility(View.GONE);
				personal_ll.setVisibility(View.VISIBLE);
				if(Util.isStrNotNull(user.getDyimgurl())){
					dyimg.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(user.getDyimgurl()+StaticFactory._320x320, dyimg);
				}else{
					dyimg.setVisibility(View.GONE);
				}
				dycontent.setText(user.getDycontent());
				dytime.setText(Util.getTimeDateStr(user.getDytime()));
			}
			
			if(user.getIsvip()==1){
				vip.setVisibility(View.VISIBLE);
			}else{
				vip.setVisibility(View.GONE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Integer> getxingzuoMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
//		map.put("白羊座", R.drawable.x_baiyang);
//		map.put("狮子座", R.drawable.x_shizi);
//		map.put("处女座", R.drawable.x_chunv);
//		map.put("金牛座", R.drawable.x_jinniu);
//		map.put("巨蟹座", R.drawable.x_juxie);
//		map.put("摩羯座", R.drawable.x_mojie);
//		map.put("射手座", R.drawable.x_sheshou);
//		map.put("双子座", R.drawable.x_shuangzi);
//		map.put("水平座", R.drawable.x_shuiping);
//		map.put("天枰座", R.drawable.x_tianping);
//		map.put("天蝎座", R.drawable.x_tianxie);
//		map.put("双鱼座", R.drawable.x_shuangyu);
		return map;
	}
	public void pro_btn(View v){};
	
	public void viewPic(int position){
		Intent intent = new Intent(this, ImageBrowserActivity.class);
		intent.putExtra("position", position);
		MessageList ml=new MessageList();
		ml.imgList=user.getImglist();
		intent.putExtra("entity_profile", ml);
		startActivity(intent);
	}
}