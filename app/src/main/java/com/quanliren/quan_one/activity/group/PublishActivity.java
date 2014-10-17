package com.quanliren.quan_one.activity.group;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.location.Location;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.custom.emoji.EmoteInputView;
import com.quanliren.quan_one.custom.emoji.EmoticonsEditText;
import com.quanliren.quan_one.fragment.custom.AddPicFragment;
import com.quanliren.quan_one.fragment.custom.AddPicFragment.OnArticleSelectedListener;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class PublishActivity extends BaseActivity implements
		OnArticleSelectedListener {


	@ViewInject(id = R.id.chat_eiv_inputview)
	EmoteInputView gridview;
	@ViewInject(id = R.id.text)
	EmoticonsEditText edittxt;
	AddPicFragment fragment;
	@ViewInject(id = R.id.add_emoji_btn, click = "add_emoji_btn")
	View add_emoji_btn;
	Location location;

	GroupBean group;
	ImageView tempImageView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (getIntent() != null && getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey("group")) {
			group = (GroupBean) getIntent().getExtras()
					.getSerializable("group");
		}
		super.onCreate(savedInstanceState);
		ImageLoader.getInstance().stop();
		setContentView(R.layout.publish);
		setTitleRightTxt("发表");
		setTitleTxt("发表");
		setListener();
		location=new Location(this);
		location.startLocation();
	}

	public void setListener() {

		gridview.setEditText(edittxt);

		edittxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gridview.setVisibility(View.GONE);
			}
		});
		fragment = (AddPicFragment) getSupportFragmentManager()
				.findFragmentById(R.id.picFragment);
	}

	public void add_pic_btn(View v) {
		tempImageView = (ImageView) v;
		gridview.setVisibility(View.GONE);
		if (v.getTag().toString().equals(AddPicFragment.DEFAULT)) {
			menupop = new PopFactory(this, new String[] { "相机", "从相册中选择" },
					menuClick, parent);
		} else {
			menupop = new PopFactory(this, new String[] { "删除" },
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
					Intent intent = new Intent(); // 调用照相机
					String messagepath = StaticFactory.APKCardPath;
					File fa = new File(messagepath);
					if (!fa.exists()) {
						fa.mkdirs();
					}
					fragment.cameraPath = messagepath + new Date().getTime();// 图片路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(fragment.cameraPath)));
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, AddPicFragment.Camera);
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
					Intent i = new Intent(PublishActivity.this,
							PhotoAlbumMainActivity.class);
					i.putExtra("maxnum", 6);
					i.putExtra("images", fragment.getSdibs());
					startActivityForResult(i, AddPicFragment.Album);
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
				fragment.removeByView(tempImageView);
				break;
			default:
				menupop.closeMenu();
				break;
			}
			menupop.closeMenu();
		}
	};

	

	public void add_emoji_btn(View v) {
		if (gridview.getVisibility() == View.VISIBLE) {
			gridview.setVisibility(View.GONE);
			edittxt.requestFocus();
			showKeyBoard();
		} else {
			closeInput();
			gridview.setVisibility(View.VISIBLE);
		}
	}

	public void onBackPressed() {
		if (gridview.getVisibility() == View.VISIBLE) {
			edittxt.requestFocus();
			gridview.setVisibility(View.GONE);
			showKeyBoard();
			return;
		} else if (menupop != null && menupop.isShow) {
			menupop.closeMenu();
			return;
		} else {
			dialogFinish();
		}
	}

	@Override
	public void back(View v) {
		dialogFinish();
	}

	public void dialogFinish() {
		new IosCustomDialog.Builder(PublishActivity.this)
				.setMessage("您确定要放弃本次编辑吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).create().show();
	}

	@Override
	public void rightClick(View v) {
		String content=edittxt.getText().toString().trim();
		if(!Util.isStrNotNull(content)&&fragment.getCount()==0){
			showCustomToast("请输入内容或添加图片");
			return;
		}
		LoginUser user = ac.getUser();

		if (user != null) {
			AjaxParams ap = getAjaxParams();
			ap.put("content", edittxt.getText().toString()).put("cityid", ac.cs.getLocationID()).put("area", ac.cs.getArea());
			if (group != null) {
				ap.put("crowdid", group.getId());
				ac.finalHttp.post(URL.PUBLISH_GROUP_TXT, ap, callBack);
			} else {
				ac.finalHttp.post(URL.PUBLISH_TXT, ap, callBack);
			}
		}
	}

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog(1);
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					if (fragment.getCount() > 0) {
						dyid = jo.getJSONObject(URL.RESPONSE).getString("dyid");
						uploadImg(0);
					} else {
						showCustomToast("发表成功");
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
	};

	private String dyid;

	public void uploadImg(int i) {
		if (i == fragment.getCount()) {
			return;
		}
		LoginUser user = ac.getUser();
		try {
			AjaxParams ap = getAjaxParams().put("file", new File(fragment.getItem(i)))
					.put("userid", user.getId()).put("dyid", dyid).put("position", i+"");
			ac.finalHttp.post(URL.PUBLISH_IMG, ap, new callBack(i));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	class callBack extends AjaxCallBack<String> {

		int index = 0;

		public callBack(int index) {
			this.index = index;
		}

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onStart() {
			customShowDialog("正在上传第" + (index + 1) + "张图片");
		};

		public void onSuccess(String t) {
			customDismissDialog();
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					if (index == (fragment.getCount() - 1)) {
						showCustomToast("上传成功");
						setResult(1);
						finish();
					} else {
						uploadImg(index + 1);
					}
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (String str : fragment.getIbs()) {
			File file = new File(str);
			if (file.exists()) {
				file.delete();
			}
		}
		location.destory();
	}

	@Override
	public void onArticleSelected(View articleUri) {
		add_pic_btn(articleUri);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		fragment.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
}
