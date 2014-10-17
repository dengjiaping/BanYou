package com.quanliren.quan_one.activity.image;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.adapter.ImageBrowserAdapter;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.PhotoTextView;
import com.quanliren.quan_one.custom.ScrollViewPager;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class ImageBrowserActivity extends BaseActivity implements
		OnPageChangeListener {

	private ScrollViewPager mSvpPager;
	private PhotoTextView mPtvPage;
	private ImageBrowserAdapter mAdapter;
	private int mPosition;
	private int mTotal;
	private List<ImageBean> mProfile;
	private boolean ismy = false;
	private int selectPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (getIntent() != null && getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey("ismy"))
			ismy = getIntent().getExtras().getBoolean("ismy");
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				            WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_imagebrowser);
		initViews();
		initEvents(); 
		init();
	}

	protected void initViews() {
		mSvpPager = (ScrollViewPager) findViewById(R.id.imagebrowser_svp_pager);
		mPtvPage = (PhotoTextView) findViewById(R.id.imagebrowser_ptv_page);
	}

	protected void initEvents() {
		mSvpPager.setOnPageChangeListener(this);
	}

	private void init() {
		mPosition = getIntent().getIntExtra("position", 0);
		mProfile = ((MessageList) getIntent().getSerializableExtra(
				"entity_profile")).imgList;
		mTotal = mProfile.size();
		if (mPosition > mTotal) {
			mPosition = mTotal - 1;
		}
		if (mTotal > 0) {
			mPosition += 1000 * mTotal;
			mPtvPage.setText((mPosition % mTotal) + 1 + "/" + mTotal);
			mAdapter = new ImageBrowserAdapter(mProfile,this);
			mSvpPager.setAdapter(mAdapter);
			mSvpPager.setCurrentItem(mPosition, false);
		}
//		if (ismy) {
//			setTitleRightTxt("设为头像");
//		} else {
//			setTitleRightTxt("保存本地");
//		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		mPosition = arg0;
		mPtvPage.setText((mPosition % mTotal) + 1 + "/" + mTotal);
	}

	public void rightClick(View v) {
		if (ismy) {
			AjaxParams ap = getAjaxParams();
			LoginUser user = ac.getUser();
			ap.put("userid", user.getId());
			ap.put("position", (mPosition % mTotal) + "");
			selectPosition = (mPosition % mTotal);
			ac.finalHttp.post(URL.SET_USERLOGO, ap, setLogoCallBack);
		} else {
			try {
				File fromFile = ImageLoader.getInstance().getDiskCache()
						.get(mProfile.get(mPosition % mTotal).imgpath);

				File toFile = new File(StaticFactory.SDCardPath
						+ "/DCIM/Camera/");
				if (!toFile.exists()) {
					toFile.mkdirs();
				}
				toFile = new File(toFile, new Date().getTime() + ".jpg");
				Util.doCopyFile(fromFile, toFile);
				Toast.makeText(ImageBrowserActivity.this,
						"图片已经保存到" + toFile.getParent() + "文件夹下",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(ImageBrowserActivity.this, "保存失败！",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	}

	OnClickListener menuMyClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case 0:
				AjaxParams ap = getAjaxParams();
				LoginUser user = ac.getUser();
				ap.put("userid", user.getId());
				ap.put("position", (mPosition % mTotal) + "");
				selectPosition = (mPosition % mTotal);
				ac.finalHttp.post(URL.SET_USERLOGO, ap, setLogoCallBack);
				break;
			case 1:
				try {
					File fromFile = ImageLoader.getInstance().getDiskCache()
							.get(mProfile.get(mPosition % mTotal).imgpath);

					File toFile = new File(StaticFactory.SDCardPath
							+ "/DCIM/Camera/");
					if (!toFile.exists()) {
						toFile.mkdirs();
					}
					toFile = new File(toFile, new Date().getTime() + ".jpg");
					Util.doCopyFile(fromFile, toFile);
					Toast.makeText(ImageBrowserActivity.this,
							"图片已经保存到" + toFile.getParent() + "文件夹下",
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(ImageBrowserActivity.this, "保存失败！",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				break;
			default:
				menupop.closeMenu();
				break;
			}
			menupop.closeMenu();
		}
	};
	OnClickListener menuClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case 0:
				break;
			case 1:
				break;
			default:
				menupop.closeMenu();
				break;
			}
			menupop.closeMenu();
		}
	};

	AjaxCallBack<String> setLogoCallBack = new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在设置头像");
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
					showCustomToast("修改成功");
					User user = ac.getUserInfo();
					if (user != null) {
						user.setAvatar(mProfile.get(selectPosition).imgpath);
						UserTable ut=new UserTable(user);
						ac.finalDb.delete(ut);
						ac.finalDb.save(ut);
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
	};
}
