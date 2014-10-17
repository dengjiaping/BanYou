package com.quanliren.quan_one.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.activity.user.UserOtherInfoActivity;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.PinnedHeaderListView;
import com.quanliren.quan_one.custom.PinnedHeaderListView.PinnedHeaderAdapter;
import com.quanliren.quan_one.util.StaticFactory;

public class GroupMemberAdapter extends ParentsAdapter implements
		SectionIndexer, PinnedHeaderAdapter, OnScrollListener {

	private int mLocationPosition = -1;  
    // 首字母集  
    private List<String> mFriendsSections;  
    private List<Integer> mFriendsPositions;  
    public void setmFriendsSections(List<String> mFriendsSections) {
		this.mFriendsSections = mFriendsSections;
	}

	public void setmFriendsPositions(List<Integer> mFriendsPositions) {
		this.mFriendsPositions = mFriendsPositions;
	}

	public Handler handler;
	public GroupBean group;
	
	public GroupMemberAdapter(Context c, List list,List<String> sections,List<Integer> positions) {
		super(c, list);
		this.mFriendsSections=sections;
		this.mFriendsPositions=positions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		int section = getSectionForPosition(position); 
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(c, R.layout.group_member_list_item, null);
			holder.head = (TextView)convertView.findViewById(R.id.now_position_txt);
			holder.username = (TextView)convertView.findViewById(R.id.username);
			holder.sex = (TextView)convertView.findViewById(R.id.sex);
			holder.logo = (ImageView)convertView.findViewById(R.id.userlogo);
			holder.add = (Button)convertView.findViewById(R.id.add);
			holder.delete = (Button)convertView.findViewById(R.id.delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
        if (getPositionForSection(section) == position) {  
        	holder.head.setVisibility(View.VISIBLE);  
            holder.head.setText(mFriendsSections.get(section).toUpperCase());  
        } else {  
        	holder.head.setVisibility(View.GONE);  
        }  
		User user=(User) list.get(position);
		holder.username.setText(user.getNickname());
		if(user.getSex().equals("0")){
			holder.sex.setBackgroundResource(R.drawable.girl_icon);
		}else{
			holder.sex.setBackgroundResource(R.drawable.boy_icon);
		}
		holder.sex.setText(user.getUserAge());
		ImageLoader.getInstance().displayImage(user.getAvatar()+StaticFactory._160x160, holder.logo);
		holder.add.setTag(user);
		holder.delete.setTag(user);
		holder.add.setOnClickListener(btnClick);
		holder.delete.setOnClickListener(btnClick);
		if(user.getUserrole()==1){
			holder.add.setText("取消管理员");
		}else if(user.getUserrole()==0){
			holder.add.setText("添加管理员");
		}
		switch (group.getCrowdrole()) {
		case 2:
			holder.add.setVisibility(View.VISIBLE);
			holder.delete.setVisibility(View.VISIBLE);
			break;
		case 1:
			holder.add.setVisibility(View.GONE);
			holder.delete.setVisibility(View.VISIBLE);
			break;
		default:
			holder.add.setVisibility(View.GONE);
			holder.delete.setVisibility(View.GONE);
			break;
		}
		holder.logo.setTag(user);
		holder.logo.setOnClickListener(userlogoClick);
		return convertView;
	}

	class ViewHolder {
		TextView head,username,sex;
		ImageView logo;
		Button add,delete;
	}

	@Override  
    public void onScrollStateChanged(AbsListView view, int scrollState) {  
        // TODO Auto-generated method stub  
  
    }  
  
    @Override  
    public void onScroll(AbsListView view, int firstVisibleItem,  
            int visibleItemCount, int totalItemCount) {  
        // TODO Auto-generated method stub  
        if (view instanceof PinnedHeaderListView) {  
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);  
        }  
    }  
  
    @Override  
    public int getPinnedHeaderState(int position) {  
        int realPosition = position;  
        if (realPosition < 0  
                || (mLocationPosition != -1 && mLocationPosition == realPosition)) {  
            return PINNED_HEADER_GONE;  
        }  
        mLocationPosition = -1;  
        int section = getSectionForPosition(realPosition);  
        int nextSectionPosition = getPositionForSection(section + 1);  
        if (nextSectionPosition != -1  
                && realPosition == nextSectionPosition - 1) {  
            return PINNED_HEADER_PUSHED_UP;  
        }  
        return PINNED_HEADER_VISIBLE;  
    }  
  
    @Override  
    public void configurePinnedHeader(View header, int position, int alpha) {  
        // TODO Auto-generated method stub  
        int realPosition = position;  
        int section = getSectionForPosition(realPosition); 
        if(section<0){
        	return;
        }
        String title = (String) getSections()[section];  
        ((TextView) header.findViewById(R.id.list_header_text))  
                .setText(title.toUpperCase());  
    }  
  
    @Override  
    public Object[] getSections() {  
        // TODO Auto-generated method stub  
        return mFriendsSections.toArray();  
    }  
  
    @Override  
    public int getPositionForSection(int section) {  
        if (section < 0 || section >= mFriendsSections.size()) {  
            return -1;  
        }  
        return mFriendsPositions.get(section);  
    }  
  
    @Override  
    public int getSectionForPosition(int position) {  
        // TODO Auto-generated method stub  
        if (position < 0 || position >= getCount()) {  
            return -1;  
        }  
        int index = Arrays.binarySearch(mFriendsPositions.toArray(), position);  
        return index >= 0 ? index : -index - 2;  
    }  
    
    OnClickListener btnClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			User user=(User) v.getTag();
			Message msg=handler.obtainMessage();
			msg.obj=user;
			switch (v.getId()) {
			case R.id.add:
				if(user.getUserrole()==1){
					msg.what=3;
				}else if(user.getUserrole()==0){
					msg.what=1;
				}
				break;
			case R.id.delete:
				msg.what=2;
				break;
			default:
				break;
			}
			msg.sendToTarget();
		}
	};

	OnClickListener userlogoClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			User user=(User) v.getTag();
			Intent i = new Intent(c,user.getUserid().equals(ac.getUser().getId())?UserInfoActivity.class:UserOtherInfoActivity.class);
			i.putExtra("id", user.getUserid());
			c.startActivity(i);
		}
	};
}
