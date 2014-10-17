package com.quanliren.quan_one.activity.group;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.GroupBean.AdminBean;
import com.quanliren.quan_one.bean.GroupBeanTable;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.RoundAngleImageView;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;

public class GroupInfoActivity extends BaseActivity {

	@ViewInject(id = R.id.grouplogo)
	ImageView grouplogo;
	@ViewInject(id = R.id.groupname)
	TextView groupname;
	@ViewInject(id = R.id.groupid)
	TextView groupid;
	@ViewInject(id = R.id.groupdesc)
	TextView groupdesc;
	@ViewInject(id = R.id.manager_userlogo)
	ImageView manager_userlogo;
	@ViewInject(id = R.id.managers_rl)
	View managers_rl;
	@ViewInject(id = R.id.managers_num)
	TextView managers_num;
	@ViewInject(id = R.id.managers_logo_ll)
	LinearLayout managers_logo_ll;
	@ViewInject(id = R.id.num)
	TextView num;
	@ViewInject(id = R.id.maxnum)
	TextView maxnum;

	@ViewInject(id = R.id.group_memeber_list_ll, click = "startMemeberList")
	View group_memeber_list_ll;
	@ViewInject(id = R.id.group_memeber_list_txt, click = "startMemeberList")
	View group_memeber_list_txt;
	@ViewInject(id = R.id.gourp_bar_txt, click = "startQuanBar")
	View gourp_bar_txt;
	@ViewInject(id=R.id.group_pic_list_ll ,click="startPhoto")
	View group_pic_list_ll;
	GroupBean group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		group = (GroupBean) getIntent().getExtras().getSerializable("group");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_info);
		GroupBeanTable temp = ac.finalDb.findById(group.getId(),
				GroupBeanTable.class);
		if (temp != null) {
			group = temp.getGroupBean();
		}
		initView();
		initDate();
	}

	@Override
	public void rightClick(View v) {
		Intent i;
		switch (group.getCrowdrole()) {
		case 2:
			i=new Intent(this,CreateGroupActivity.class);
			i.putExtra("group", group);
			startActivityForResult(i, 1);
			break;
		case 1:
		case 0:
			exit();
			break;
		case -1:
			join();
			break;
		}
	}

	public void startMemeberList(View v) {
		if(group.getCrowdrole()==-1){
			showCustomToast("您还未加入该群");
			return;
		}
		Intent i = new Intent(GroupInfoActivity.this,
				GroupMemberListActivity.class);
		i.putExtra("group", group);
		startActivity(i);
	}

	public void startQuanBar(View v) {
		if(group.getCrowdrole()==-1){
			showCustomToast("您还未加入该群");
			return;
		}
		Intent i = new Intent(GroupInfoActivity.this, QuanBarActivity.class);
		i.putExtra("group", group);
		startActivity(i);
	}

	public void initView() {
		if (group == null) {
			return;
		}
		ImageLoader.getInstance().displayImage(
				group.getAvatar() + StaticFactory._160x160, grouplogo);
		groupname.setText(group.getCrowdname());
		setTitleTxt(group.getCrowdname());
		groupid.setText(group.getId());
		groupdesc.setText(group.getSummary());
		ImageLoader.getInstance().displayImage(
				group.getHostavatar() + StaticFactory._160x160,
				manager_userlogo);
		if (group.getAdmins() != null && group.getAdmins().size() > 0) {
			managers_num.setText(group.getAdmins().size() + "");
			managers_logo_ll.removeAllViews();

			for (AdminBean ab : group.getAdmins()) {
				RoundAngleImageView image = new RoundAngleImageView(this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						ImageUtil.dip2px(this, 50), ImageUtil.dip2px(this, 50));
				image.setLayoutParams(lp);
				image.setScaleType(ScaleType.CENTER_CROP);
				lp.rightMargin = ImageUtil.dip2px(this, 5);
				ImageLoader.getInstance().displayImage(
						ab.imgpath + StaticFactory._160x160, image);
			}
		} else {
			managers_rl.setVisibility(View.GONE);
		}
		num.setText(group.getNownum());
		maxnum.setText("/" + group.getMaxnum());
		switch (group.getCrowdrole()) {
		case 2:
			this.setTitleRightTxt("编辑");
			break;
		case 1:
		case 0:
			this.setTitleRightTxt("退出");
			break;
		case -1:
			this.setTitleRightTxt("加入");
			break;
		}
	}

	public void initDate() {
		ac.finalHttp.post(URL.GETGROUPDETAIL,
				getAjaxParams("crowdid", group.getId()), callBack);
	}

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在获取群信息");
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					GroupBean temp = new Gson().fromJson(
							jo.getString(URL.RESPONSE),
							new TypeToken<GroupBean>() {
							}.getType());
					if (temp != null) {
						group = temp;
						GroupBeanTable gbt = new GroupBeanTable(temp);
						ac.finalDb
								.deleteById(GroupBeanTable.class, gbt.getId());
						ac.finalDb.save(gbt);
						initView();
					}
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				customDismissDialog();
			}
		};
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case 1:
				initDate();
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	};
	
	public void join(){
		User user=ac.getUserInfo();
		if(Integer.valueOf(user.getLevelid())<2){
			showCustomToast("您还不是会员");
			return;
		}
		if(group.getNownum().equals(group.getMaxnum())){
			showCustomToast("该群已满");
			return;
		}
		AjaxParams ap=getAjaxParams();
		ap.put("crowdid", group.getId());
		ac.finalHttp.post(URL.JOINGROUP,ap, joinCallBack);
	}
	
	public void exit(){
		AjaxParams ap=getAjaxParams();
		ap.put("crowdid", group.getId());
		ac.finalHttp.post(URL.EXITGROUP,ap, exitCallBack);
	}
	
	AjaxCallBack<String> exitCallBack=new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在退出");
		};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};
		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("您已成功退出该群");
					group.setCrowdrole(-1);
					setTitleRightTxt("加入");
					GroupBeanTable gbt = new GroupBeanTable(group);
					ac.finalDb
							.deleteById(GroupBeanTable.class, gbt.getId());
					ac.finalDb.save(gbt);
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				customDismissDialog();
			}
		};
	};
	
	AjaxCallBack<String> joinCallBack=new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在申请加入");
		};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};
		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("您已申请加入该群\n请耐心等待群主回复");
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				customDismissDialog();
			}
		};
	};
	
	public void startPhoto(View v){
		if(group.getCrowdrole()==-1){
			showCustomToast("您还未加入该群");
			return;
		}
		Intent i =new Intent(this,GroupPhotoAlbumActivity.class);
		i.putExtra("group", group);
		startActivity(i);
	}
}
