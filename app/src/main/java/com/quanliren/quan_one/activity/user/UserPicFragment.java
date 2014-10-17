package com.quanliren.quan_one.activity.user;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.custom.CustomPicWall;
import com.quanliren.quan_one.custom.NoScrollGridView;
import com.quanliren.quan_one.util.BitmapCache;
import com.quanliren.quan_one.util.ImageUtil;

public class UserPicFragment extends Fragment {

	@ViewInject(id = R.id.viewpager)
	ViewPager viewpager;
	List<ImageBean> list = null;
	PicPageAdapter adapter;
	
	AtomicBoolean b=new AtomicBoolean(false);
	
	public void setList(List<ImageBean> list) {
		this.list = list;
		
		if (list != null && list.size() > 0) {
			viewpager.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, getResources().getDisplayMetrics().widthPixels/2));
			if(adapter!=null){
				adapter.notifyDataSetChanged();
			}else{
				viewpager.setAdapter(adapter=new PicPageAdapter());
			}
			
			if(b.compareAndSet(false, true)){
				if(adapter.getCount()>0){
					viewpager.setCurrentItem(adapter.getCount()-1);
				}
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						viewpager.setCurrentItem(0);
					}
				}, 1500);
			}
		}
		/*else {
			default_pic_wall.setVisibility(View.VISIBLE);
			default_pic_wall.setImageBitmap(BitmapCache.getInstance()
					.getBitmap(R.drawable.defalut_pic_wall, getActivity()));
			viewpager.setVisibility(View.GONE);
		}*/
	}

	@ViewInject(id = R.id.default_pic_wall)
	ImageView default_pic_wall;
	int lineNum=8;
	List<GridView> views=new ArrayList<GridView>();
	List<List<ImageBean>> imgLists=new ArrayList<List<ImageBean>>(); 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (getArguments() != null) {
			MessageList bean = (MessageList) getArguments().get("list");
			list = bean.imgList;
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_pic_fragment, null);
		FinalActivity.initInjectedView(this, view);
		default_pic_wall.setImageBitmap(BitmapCache.getInstance()
				.getBitmap(R.drawable.defalut_pic_wall, getActivity()));
		setList(list);
		return view;
	}
	
	public NoScrollGridView createGridView(){
		NoScrollGridView gridview=new NoScrollGridView(getActivity());
		ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, getResources().getDisplayMetrics().widthPixels/2);
		gridview.setGravity(Gravity.CENTER);
		gridview.setNumColumns(4);
		gridview.setPadding(ImageUtil.dip2px(getActivity(), 4), ImageUtil.dip2px(getActivity(), 4), ImageUtil.dip2px(getActivity(), 4), ImageUtil.dip2px(getActivity(), 4));
		gridview.setVerticalSpacing(ImageUtil.dip2px(getActivity(), 4));
		gridview.setHorizontalSpacing(ImageUtil.dip2px(getActivity(), 4));
		gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridview.setLayoutParams(lp);
		return gridview;
	}
	
	public CustomPicWall createLinearLayout(){
		CustomPicWall layout=new CustomPicWall(getActivity());
		ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(lp);
		return layout;
	}
	
	class PicPageAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			int lines = (int) (list.size() / lineNum);
			if (list.size() % lineNum > 0) {
				lines++;
			}
			return lines;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			CustomPicWall gridview=createLinearLayout();
			
			List<ImageBean> imgs=new ArrayList<ImageBean>();
			for (int j = position*lineNum; j < list.size(); j++) {
				imgs.add(list.get(j));
				if(imgs.size()==lineNum){
					break;
				}
			}
//			UserInfoPicAdapter adapter = new UserInfoPicAdapter(getActivity(), imgs);
//			gridview.setAdapter(adapter);
			gridview.setImgList(imgs);
			container.addView(gridview);
			return gridview;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		@Override  
		public int getItemPosition(Object object) {  
		    return POSITION_NONE;  
		}  
	}

}
