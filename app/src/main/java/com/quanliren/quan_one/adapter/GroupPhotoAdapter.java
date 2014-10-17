package com.quanliren.quan_one.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity;
import com.quanliren.quan_one.bean.GroupPhotoBean;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.custom.PhotoGridItem;
import com.quanliren.quan_one.custom.RoundProgressBar;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class GroupPhotoAdapter extends ParentsAdapter {

	private int columnCount = 3;
	private int lineHeight = 0;
	private boolean edit = false;
	public ArrayList<GroupPhotoBean> selectIds=new ArrayList<GroupPhotoBean>();
	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
		initLineHeight(columnCount);
	}

	public void initLineHeight(int num) {
		int swidth = c.getResources().getDisplayMetrics().widthPixels;
		lineHeight = (swidth - (num + 1) * ImageUtil.dip2px(c, 4)) / num;
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
		int len = 0;
		if (list.size() % columnCount > 0) {
			len = 1;
		}
		return list.size() / columnCount + len;
	}

	public GroupPhotoAdapter(Context c, List list) {
		super(c, list);
		initLineHeight(columnCount);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		
		if (convertView == null) {
			holder = new ViewHolder();
			holder.imgs = new ArrayList<PhotoGridItem>();
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT, lineHeight);
			convertView = new LinearLayout(c);
			convertView.setPadding(ImageUtil.dip2px(c, 4), 0, 0, 0);
			convertView.setLayoutParams(lp);
			for (int i = 0; i < columnCount; i++) {
				PhotoGridItem item = new PhotoGridItem(c);
				item.setLayoutParams(new LayoutParams(lineHeight, lineHeight));
				LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(
						lineHeight, lineHeight);
				imgLp.rightMargin = ImageUtil.dip2px(c, 4);
				item.setLayoutParams(imgLp);
//				item.getmImageView().setImageResource(R.drawable.user_logo);
				item.getmImageView().setScaleType(ScaleType.CENTER_CROP);
				holder.imgs.add(item);
				((LinearLayout) convertView).addView(item);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		for (int i = 0; i < holder.imgs.size(); i++) {
			PhotoGridItem item=holder.imgs.get(i);
			int beanIndex=position*columnCount+i;
			if(beanIndex>=list.size()){
				item.setVisibility(View.GONE);
			}else{
				item.setVisibility(View.VISIBLE);
				GroupPhotoBean bean=(GroupPhotoBean) list.get(beanIndex);
				if (edit)
					item.showSelect();
				else
					item.hideSelect();
				item.setOnClickListener(imgClick);
				item.setTag(bean);
				item.setChecked(bean.isSelected());
				final RoundProgressBar progress=item.getProgress();
				ImageLoader.getInstance().displayImage(bean.getImgurl()+StaticFactory._320x320, item.getmImageView(),null,null,new ImageLoadingProgressListener() {
					
					@Override
					public void onProgressUpdate(String imageUri, View view, int current,
							int total) {
						if (current == total) {
							progress.setVisibility(View.GONE);
						} else {
							progress.setVisibility(View.VISIBLE);
							progress.setMax(total);
							progress.setProgress(current);
						}
					}
				});
			}
		}
		return convertView;
	}

	class ViewHolder {
		List<PhotoGridItem> imgs;
	}

	OnClickListener imgClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			GroupPhotoBean item = (GroupPhotoBean) v.getTag();
			if (edit) {
				item.setSelected(!item.isSelected());
				((PhotoGridItem)v).setChecked(item.isSelected());
				notifyDataSetChanged();
				if(item.isSelected()){
					selectIds.add(item);
				}else{
					selectIds.remove(item);
				}
			}else{
				Intent intent = new Intent(c, ImageBrowserActivity.class);
				intent.putExtra("position", getList().indexOf(item));
				List<ImageBean> list=new ArrayList<ImageBean>();
				for (Object i : getList()) {
					GroupPhotoBean it = (GroupPhotoBean) i;
					list.add(new ImageBean(0, it.getImgurl()));
				}
				MessageList ml=new MessageList();
				ml.imgList=list;
				intent.putExtra("entity_profile", ml);
				c.startActivity(intent);
			}
		}
	};
}
