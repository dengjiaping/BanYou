package com.quanliren.quan_one.activity.group;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.adapter.GroupPhotoAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.GroupPhotoBean;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class GroupPhotoAlbumActivity extends BaseActivity implements
		IXListViewListener {

	private static final String TAG = "GroupPhotoAlbumActivity";
	private String CACHEKEY = "";
	@ViewInject(id = R.id.photo_list)
	XXListView listview;
	GroupPhotoAdapter adapter;
	@ViewInject(id = R.id.top_btn_ll)
	View topBtnLL;
	@ViewInject(id = R.id.bottom_btn_ll)
	View bottomBtnLL;
	@ViewInject(id = R.id.delete, click = "deleteClick")
	TextView delete;
	@ViewInject(id = R.id.select, click = "selectClick")
	TextView select;
	@ViewInject(id = R.id.photo, click = "photoClick")
	TextView photo;
	@ViewInject(id = R.id.camera,click="cameraClick")
	TextView camera;
	int p = 0;
	GroupBean group;
	AjaxParams ap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		group = (GroupBean) getIntent().getSerializableExtra("group");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_photo_list);
		CACHEKEY = TAG + group.getId();
		List<GroupPhotoBean> list = new ArrayList<GroupPhotoBean>();
		CacheBean cb = ac.finalDb.findById(CACHEKEY, CacheBean.class);
		if (cb != null) {
			list = new Gson().fromJson(cb.getValue(),
					new TypeToken<ArrayList<GroupPhotoBean>>() {
					}.getType());
		}
		adapter = new GroupPhotoAdapter(this, list);
		
		switch (group.getCrowdrole()) {
		case 2:
			setTitleRightTxt("编辑");
			bottomBtnLL.setVisibility(View.VISIBLE);
			View btmView = new View(this);
			btmView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					ImageUtil.dip2px(this, 50)));
			listview.addFooterView(btmView);
			break;
		}
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);
		setTitleTxt("群相册");
		select.setTag("0");
	}

	@Override
	public void onRefresh() {
		p = 0;
		AjaxParams ap = getAjaxParams("crowdid", group.getId());
		ap.put("p", p + "");
		ac.finalHttp.post(URL.GROUPPHOTOLIST, ap, callBack);
	}

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			listview.stop();
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					jo = jo.getJSONObject(URL.RESPONSE);
					List<GroupPhotoBean> list = new Gson().fromJson(
							jo.getString(URL.LIST),
							new TypeToken<ArrayList<GroupPhotoBean>>() {
							}.getType());
					if (p == 0) {
						CacheBean cb = new CacheBean(CACHEKEY,
								jo.getString(URL.LIST), new Date().getTime());
						ac.finalDb.deleteById(CacheBean.class, CACHEKEY);
						ac.finalDb.save(cb);
						adapter.setList(list);
					} else {
						adapter.addNewsItems(list);
					}
					adapter.notifyDataSetChanged();
					listview.setPage(p = jo.getInt(URL.PAGEINDEX));
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				listview.stop();
			}
		};
	};

	@Override
	public void onLoadMore() {
		ap.put("p", p + "");
		ac.finalHttp.post(URL.GROUPPHOTOLIST, ap, callBack);
	}

	@Override
	public void rightClick(View v) {
		if (adapter.isEdit())
			adapter.setEdit(false);
		else
			adapter.setEdit(true);
		adapter.notifyDataSetChanged();
		ValueAnimator animator = null;
		final android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) topBtnLL
				.getLayoutParams();
		if (adapter.isEdit()) {
			setTitleRightTxt("取消");
			animator = ValueAnimator.ofInt(0, ImageUtil.dip2px(this, 50))
					.setDuration(200);
		} else {
			animator = ValueAnimator.ofInt(ImageUtil.dip2px(this, 50), 0)
					.setDuration(200);
			setTitleRightTxt("编辑");
		}
		animator.start();
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				lp.height = (Integer) animation.getAnimatedValue();
				topBtnLL.setLayoutParams(lp);
			}
		});
	}

	public void selectClick(View v) {
		List<GroupPhotoBean> gps = adapter.getList();
		if ("0".equals(v.getTag().toString())) {
			select.setText("反选");
			adapter.selectIds.clear();
			for (GroupPhotoBean groupPhotoBean : gps) {
				groupPhotoBean.setSelected(true);
				adapter.selectIds.add(groupPhotoBean);
			}
			v.setTag("1");
		} else {
			select.setText("全选");
			adapter.selectIds.clear();
			for (GroupPhotoBean groupPhotoBean : gps) {
				groupPhotoBean.setSelected(false);
			}
			v.setTag("0");
		}
		adapter.notifyDataSetChanged();
	}

	public void photoClick(View v) {
		if (Util.existSDcard()) {
			Intent i = new Intent(this, PhotoAlbumActivity.class);
			startActivityForResult(i, ALBUM);
		}else {
			showCustomToast("亲，请检查是否安装存储卡!");
		}
	}
	
	String fileName="";
	
	public void cameraClick(View v){
		if (Util.existSDcard()) {
			Intent intent = new Intent(); // 调用照相机
			String messagepath = StaticFactory.APKCardPath;
			File fa = new File(messagepath);
			if (!fa.exists()) {
				fa.mkdirs();
			}
			fileName = messagepath + new Date().getTime();// 图片路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(fileName)));
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, CAMERA);
		} else {
			showCustomToast("亲，请检查是否安装存储卡!");
		}
	}

	public static final int ALBUM = 1, CAMERA = 2;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ALBUM:
			switch (resultCode) {
			case 1:
				ArrayList<String> list = data.getStringArrayListExtra("list");
				if (list.size() > 0) {
					uploadImg(list, 0);
				}
				break;
			default:
				break;
			}
			break;
		case CAMERA:
			if (fileName != null) {
				File fi = new File(fileName);
				if (fi != null && fi.exists()) {
					ImageUtil.downsize(fileName, fileName, this);
					ArrayList<String> list=new ArrayList<String>();
					list.add(fileName);
					uploadImg(list, 0);
				} 
				fi = null;
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void uploadImg(final ArrayList<String> list, final int num) {
		if (num >= list.size()) {
			showCustomToast("上传完成");
			return;
		}
		try {
			File file = new File(list.get(num));
			if (file.exists()) {
				String fileName = "";
				ImageUtil.downsize(
						file.getPath(),
						fileName = StaticFactory.APKCardPath
								+ new Date().getTime(), this);
				file = new File(fileName);
				if (file.exists()) {
					AjaxParams ap = getAjaxParams("crowdid", group.getId());
					ap.put("file", file);
					ac.finalHttp.post(URL.UPLOADGROUPPHOTO, ap,
							new AjaxCallBack<String>() {
								@Override
								public void onStart() {
									customShowDialog("正在上传第" + (num + 1)
											+ "张图片");
								}

								@Override
								public void onSuccess(String t) {
									try {
										JSONObject jo = new JSONObject(t);
										int status = jo.getInt(URL.STATUS);
										switch (status) {
										case 0:
											GroupPhotoBean gb=new Gson().fromJson(jo.getString(URL.RESPONSE), new TypeToken<GroupPhotoBean>(){}.getType());
											adapter.addNewsItem(gb);
											adapter.notifyDataSetChanged();
											uploadImg(list, num + 1);
											break;
										default:
											showFailInfo(jo);
											break;
										}
									} catch (JSONException e) {
										e.printStackTrace();
									} finally {
										customDismissDialog();
									}
								}

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									showIntentErrorToast();
									customDismissDialog();
								}
							});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteClick(View v) {
		final ArrayList<GroupPhotoBean> ids = adapter.selectIds;
		if (ids.size() > 0) {
			new IosCustomDialog.Builder(this).setMessage("您确定要删除吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					JSONArray ja = new JSONArray();
					for (GroupPhotoBean groupPhotoBean : ids) {
						ja.put(groupPhotoBean.getId());
					}
					AjaxParams ap = getAjaxParams();
					ap.put("crowdid", group.getId());
					ap.put("imgids", ja.toString());
					ac.finalHttp
							.post(URL.DELETEGROUPPHOTO, ap, new deleteCallBack(ids));
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).create().show();
		}
	}

	class deleteCallBack extends AjaxCallBack<String> {
		ArrayList<GroupPhotoBean> ids;

		public deleteCallBack(ArrayList<GroupPhotoBean> list) {
			this.ids = list;
		}

		public void onStart() {
			customShowDialog("正在删除");
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
					for (GroupPhotoBean gp : ids) {
						adapter.removeObj(gp);
					}
					adapter.selectIds.remove(ids);
					adapter.notifyDataSetChanged();
					showCustomToast("删除成功");
					if(adapter.getCount()==0){
						listview.startRefresh();
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
}
