package com.quanliren.quan_one.activity.group;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class CreateGroupActivity extends BaseActivity{
	
	@ViewInject(id=R.id.grouplogo,click="add_pic_btn")ImageView logo;
	@ViewInject(id=R.id.name)TextView name;
	@ViewInject(id=R.id.summary)TextView summary;
	
	final int Album = 2, Camera = 1;
	String fileName = "";
	List<ImageBean> ibs = new ArrayList<ImageBean>();
	GroupBean group;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(getIntent()!=null&&getIntent().getExtras()!=null){
			Bundle b=getIntent().getExtras();
			if(b.containsKey("group"))
				group=(GroupBean) b.getSerializable("group");
		}
		super.onCreate(savedInstanceState);
		ImageLoader.getInstance().stop();
		setContentView(R.layout.group_create);
		setTitleTxt("创建群");
		setTitleRightTxt(R.string.save);
		logo.setTag(DEFAULT);
		init();
	}
	
	@Override
	public void rightClick(View v) {
		String str_name=name.getText().toString().trim();
		String str_summary=summary.getText().toString().trim();
		if(logo.getTag().toString().equals(DEFAULT)){
			showCustomToast("请添加群头像");
			return;
		}
		if(!Util.isStrNotNull(str_name)){
			showCustomToast("请填写群名称");
			return;
		}
		AjaxParams ap=getAjaxParams();
		ap.put("crowdname", str_name);
		ap.put("summary", str_summary);
		if(group!=null){
			ap.put("crowdid", group.getId());	
		}
		ac.finalHttp.post(URL.CREATEGROUP,ap, callBack);
	}
	
	AjaxCallBack<String> callBack=new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在上传群信息");
		};
		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					if(ibs.size()>0){
						String crowid=jo.getJSONObject(URL.RESPONSE).getString("crowdid");
						AjaxParams ap=getAjaxParams();
						ap.put("crowdid", crowid);
						ap.put("file", new File(ibs.get(0).imgpath));
						ac.finalHttp.post(URL.CREATEGROUP_IMG,ap, imgCallBack);
					}else{
						showCustomToast("上传成功");
						setResult(1);
						finish();
					}
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};
	};
	
	AjaxCallBack<String> imgCallBack=new AjaxCallBack<String>() {

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onStart() {
			customShowDialog("正在上传群头像");
		};

		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("上传成功");
					setResult(1);
					finish();
					break;
				default:
					showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
							URL.INFO));
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Album:
			if (data == null) {
				return;
			}
			ContentResolver resolver = getContentResolver();
			Uri imgUri = data.getData();
			try {
				Cursor cursor = resolver.query(imgUri, null, null, null, null);
				cursor.moveToFirst();
				fileName = cursor.getString(1);
				int[] wh = ImageUtil.downsize(
						fileName,
						fileName = StaticFactory.APKCardPath
								+ new Date().getTime(), this);
				logo.setTag(fileName);
				ImageLoader.getInstance().displayImage(Util.FILE + fileName,
						logo);
				ibs.add(new ImageBean(0, fileName,wh));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case Camera:
			if (fileName != null) {
				File fi = new File(fileName);
				if (fi != null && fi.exists()) {
					int[] wh = ImageUtil.downsize(fileName, fileName, this);
					logo.setTag(fileName);
					ImageLoader.getInstance().displayImage(
							Util.FILE + fileName, logo);
					ibs.add(new ImageBean(0,
							fileName, wh));
				}
				fi = null;
			}
			break;
		default:
			break;
		}
	}
	
	final String DEFAULT = "default";
	
	public void add_pic_btn(View v) {
		if (v.getTag().toString().equals(DEFAULT)) {
			menupop = new PopFactory(this, new String[] { "相机", "从相册中选择" },
					menuClick, parent);
		} else {
			menupop = new PopFactory(this, new String[] { "更换","删除" },
					menuDeleteClick, parent);
		}
		closeInput();
		menupop.toogle();
	}
	
	OnClickListener menuClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case 0:
				if (Util.existSDcard()) {
					Intent intent = new Intent();
					String messagepath = StaticFactory.APKCardPath;
					File fa = new File(messagepath);
					if (!fa.exists()) {
						fa.mkdirs();
					}
					fileName = messagepath + new Date().getTime();
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(fileName)));
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, Camera);
				} else {
					Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				if (Util.existSDcard()) {
					String messagepath = StaticFactory.APKCardPath;
					File fa = new File(messagepath);
					if (!fa.exists()) {
						fa.mkdirs();
					}
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent, Album);
				} else {
					Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				menupop.closeMenu();
				break;
			}
			menupop.closeMenu();
		}
	};

	OnClickListener menuDeleteClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case 0:
				menupop.closeMenu();
				menupop = new PopFactory(CreateGroupActivity.this, new String[] { "相机", "从相册中选择" },
						menuClick, parent);
				closeInput();
				menupop.toogle();
				return;
			case 1:
				String url = logo.getTag().toString();

				/** 删除图片的编号 **/
				if (url.startsWith("http://")) {
				} else {
					ImageBean temp = null;
					for (ImageBean ib : ibs) {
						if (ib.imgpath.equals(url)) {
							temp = ib;
						}
					}
					if (temp != null) {
						ibs.remove(temp);
					}
				}

				File file = new File(logo.getTag().toString());
				if (file.exists()) {
					file.delete();
				}
				logo.setTag(DEFAULT);
				logo.setImageResource(R.drawable.publish_add_pic_icon_big);
				break;
			default:
				menupop.closeMenu();
				break;
			}
			menupop.closeMenu();
		}
	};

	public void init(){
		if(group==null){
			return;
		}
		name.setText(group.getCrowdname());
		summary.setText(group.getSummary());
		ImageLoader.getInstance().displayImage(group.getAvatar()+StaticFactory._320x320, logo);
		logo.setTag(group.getAvatar()+StaticFactory._320x320);
	}
}
