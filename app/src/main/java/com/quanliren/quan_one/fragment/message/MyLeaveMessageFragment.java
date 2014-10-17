package com.quanliren.quan_one.fragment.message;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.db.sqlite.DbModel;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.adapter.LeaveMessageAdapter;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;

public class MyLeaveMessageFragment extends MenuFragmentBase implements IXListViewListener,LoaderImpl{

	public static final String TAG="MyLeaveMessageActivity";
	public static final String REFEREMSGCOUNT="com.quanliren.quan_one.MyLeaveMessageActivity.REFEREMSGCOUNT";
	public static final String ADDMSG="com.quanliren.quan_one.MyLeaveMessageActivity.ADDMSG";
	@ViewInject(id=R.id.listview)XXListView listview;
	@ViewInject(id=R.id.title_include)View title_include;
	LeaveMessageAdapter adapter;
//	TextView groupMessageCount;
	int p=0;
	LoginUser user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		user=ac.getUser();
		
	}
	
	public void onDestroy() {
		super.onDestroy();
	};
	
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view==null){
			view=inflater.inflate(R.layout.my_leavemessage_list, null);
			FinalActivity.initInjectedView(this, view);
			title_include.setVisibility(View.GONE);
		}else{
			ViewParent parent=view.getParent();
			if(parent!=null&&parent instanceof ViewGroup){
				((ViewGroup)parent).removeView(view);
			}
		}
		return view;
	}

	public void initAdapter() {
		adapter = new LeaveMessageAdapter(getActivity(), new ArrayList<ChatListBean>());
		adapter.handler=handler;
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if(position>0){
					ChatListBean mlb=(ChatListBean) adapter.getItem(position-1);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = mlb;
					msg.sendToTarget();
					return true;
				}
				return false;
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position>0){
					ChatListBean bean = (ChatListBean) adapter.getItem(position-1);
					User user = new User(bean.getFriendid(),bean.getUserlogo(),bean.getNickname());
					Intent i = new Intent(getActivity(), ChatActivity.class);
					i.putExtra("friend", user);
					startActivityForResult(i, 1);
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final List<ChatListBean> list=ac.finalDb.findAllByWhere(ChatListBean.class, "userid='"+user.getId()+"'", "id desc");
				if(list!=null&&list.size()>0){
					for (ChatListBean c : list) {
						DbModel model= ac.finalDb.findDbModelBySQL("select count(*) as num from "+DfMessage.TABLENAME+" where userid='"+user.getId()+"' and receiverUid ='"+user.getId()+"' and sendUid='"+c.getFriendid()+"' and isRead='0'");
						int num=model.getInt("num");
						c.setMsgCount(num);
					}
				}
				if(getActivity()!=null){
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if(list!=null&&list.size()>0){
								adapter.setList(list);
							}
							adapter.notifyDataSetChanged();
							listview.stop();
						}
					});
				}
			}
		}).start();
	}

	@Override
	public void onLoadMore() {
	}
	
	
	Handler handler=new Handler(){
		public void dispatchMessage(Message msg) {
			final ChatListBean bean=(ChatListBean) msg.obj;
			switch (msg.what) {
			case 1:
				new IosCustomDialog.Builder(getActivity()).setMessage("你确定要删除这条记录吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ac.finalDb.delete(bean);
						ac.finalDb.deleteByWhere(DfMessage.class, "userid='"+user.getId()+"' and (sendUid='"+bean.getFriendid()+"' or receiverUid='"+bean.getFriendid()+"')");
						final int position=adapter.getList().indexOf(bean);
						if(position>-1){
							final View view = listview.getChildAt((position+1)-listview.getFirstVisiblePosition());
							if(view != null){
								ViewPropertyAnimator.animate(view)  
			                    .alpha(0)  
			                    .setDuration(200)  
			                    .setListener(new AnimatorListenerAdapter() {  
			                        @Override  
			                        public void onAnimationEnd(Animator animation) {  
			                        	performDismiss(view,position);  
			                        }  
			                    });  
								
							}else{
								adapter.removeObj(position);
								adapter.notifyDataSetChanged();
							}
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).create().show();
				break;
			}
			super.dispatchMessage(msg);
		};
	};
	
	private void performDismiss(final View dismissView,final int position) {  
        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();//获取item的布局参数  
        final int originalHeight = dismissView.getHeight();//item的高度  
  
        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(200);  
        animator.start();  
  
        animator.addListener(new AnimatorListenerAdapter() {  
            @Override  
            public void onAnimationEnd(Animator animation) {  
            	adapter.removeObj(position);   
                ViewHelper.setAlpha(dismissView, 1f);  
                ViewGroup.LayoutParams lp = dismissView.getLayoutParams();  
                lp.height =  ViewGroup.LayoutParams.WRAP_CONTENT;  
                dismissView.setLayoutParams(lp);  
  
                adapter.notifyDataSetChanged();
            }  
        });  
  
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
            @Override  
            public void onAnimationUpdate(ValueAnimator valueAnimator) {  
                //这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果  
                lp.height = (Integer) valueAnimator.getAnimatedValue();  
                dismissView.setLayoutParams(lp);  
            }  
        });  
  
    } 
	
	Handler broadcast=new Handler(){
		public void dispatchMessage(Message msg) {
			Intent i =(Intent)msg.obj;
			String action=i.getAction();
			if (action.equals(REFEREMSGCOUNT)) {
				List<ChatListBean> list=adapter.getList();
				for (ChatListBean messageListBean : list) {
					if(messageListBean.getFriendid().equals(i.getStringExtra("id"))){
						DbModel model= ac.finalDb.findDbModelBySQL("select count(*) as num from "+DfMessage.TABLENAME+" where userid='"+user.getId()+"' and receiverUid ='"+user.getId()+"' and sendUid='"+messageListBean.getFriendid()+"' and isRead='0'");
						int num=model.getInt("num");
						messageListBean.setMsgCount(num);
					}
				}
				adapter.notifyDataSetChanged();
			}else if(action.equals(ADDMSG)){
				ChatListBean bean=(ChatListBean) i.getExtras().getSerializable("bean");
				DbModel model= ac.finalDb.findDbModelBySQL("select count(*) as num from "+DfMessage.TABLENAME+" where userid='"+user.getId()+"' and receiverUid ='"+user.getId()+"' and sendUid='"+bean.getFriendid()+"' and isRead='0'");
				int num=model.getInt("num");
				bean.setMsgCount(num);
				
				ChatListBean temp=null;
				List<ChatListBean> list=adapter.getList();
				for (ChatListBean messageListBean : list) {
					if(messageListBean.getFriendid().equals(bean.getFriendid())){
						temp=messageListBean;
					}
				}
				if(temp!=null){
					adapter.removeObj(temp);
				}
				adapter.addFirstItem(bean);
				adapter.notifyDataSetChanged();
			}
			super.dispatchMessage(msg);
		};
	};

	@Override
	public void refresh() {
		if(getActivity()!=null&&init.compareAndSet(false,true)){
			initAdapter();
			String[] str=new String[]{REFEREMSGCOUNT,ADDMSG};
			receiveBroadcast(str, broadcast);
		}
	}
}
