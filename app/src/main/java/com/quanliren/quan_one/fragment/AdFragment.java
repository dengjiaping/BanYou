package com.quanliren.quan_one.fragment;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.AD;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.util.URL;

public class AdFragment extends MenuFragmentBase{
	ImageView adImg;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		adImg = new ImageView(getActivity());
		adImg.setVisibility(View.GONE);
		adImg.setScaleType(ScaleType.CENTER_CROP);
		adImg.setAdjustViewBounds(true);
		adImg.setBackgroundColor(getResources()
				.getColor(R.color.red_number));
		ac.finalHttp.post(URL.ADBANNER, getAjaxParams(), callback);
		return adImg;
	}
	
	AD ad;

	AjaxCallBack<String> callback = new AjaxCallBack<String>() {

		public void onStart() {
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					ad = new Gson().fromJson(jo.getString(URL.RESPONSE),
							new TypeToken<AD>() {
							}.getType());
					ImageLoader.getInstance().displayImage(ad.getImgpath(),
							adImg, new SimpleImageLoadingListener() {
								public void onLoadingComplete(String imageUri,
										View view,
										android.graphics.Bitmap loadedImage) {
									try {
										int swidth = getResources()
												.getDisplayMetrics().widthPixels;
										int widthScale = swidth
												/ loadedImage.getWidth();
										int height = widthScale
												* loadedImage.getHeight();
										AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
												swidth, height);
										adImg.setLayoutParams(lp);
										adImg.setVisibility(View.VISIBLE);
										adImg.setImageBitmap(loadedImage);
									} catch (Exception e) {
										e.printStackTrace();
									}
								};
							});
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
		};
	};
}
