package com.quanliren.quan_one.activity.group;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.PhotoAdappter;
import com.quanliren.quan_one.bean.PhotoAibum;
import com.quanliren.quan_one.bean.PhotoItem;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;

public class PhotoActivity extends MenuFragmentBase {
	@ViewInject(id = R.id.photo_gridview)
	GridView gv;
	private PhotoAibum aibum;
	private PhotoAdappter adapter;
	private int chooseNum = 0;

	private ArrayList<PhotoItem> gl_arr = new ArrayList<PhotoItem>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		aibum = (PhotoAibum) getArguments().get("aibum");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_photoalbum_gridview,
				null);
		FinalActivity.initInjectedView(this, view);

		/** 获取已经选择的图片 **/
		for (int i = 0; i < aibum.getBitList().size(); i++) {
			if (aibum.getBitList().get(i).isSelect()) {
				chooseNum++;
			}
		}
		adapter = new PhotoAdappter(getActivity(), aibum, null);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(gvItemClickListener);
		setTitleTxt("选择照片");
		setTitleRightTxt("确定");
		return view;
	}

	private ArrayList<String> paths = new ArrayList<String>();
	private ArrayList<String> ids = new ArrayList<String>();
	private OnItemClickListener gvItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			PhotoItem gridItem = aibum.getBitList().get(position);
			if (gridItem.isSelect()) {
				gridItem.setSelect(false);
				paths.remove(gridItem.getPath());
				ids.remove(gridItem.getPhotoID() + "");
				gl_arr.remove(gridItem);
				chooseNum--;
				((PhotoAlbumMainActivity) getActivity()).removePath(gridItem
						.getPath());
			} else {
				if (((PhotoAlbumMainActivity) getActivity()).paths.size() < ((PhotoAlbumMainActivity) getActivity()).maxnum) {
					gridItem.setSelect(true);
					ids.add(gridItem.getPhotoID() + "");
					paths.add(gridItem.getPath());
					gl_arr.add(gridItem);
					chooseNum++;
					((PhotoAlbumMainActivity) getActivity()).addPath(gridItem
							.getPath());
				} else {
					showCustomToast("最多只能添加"
							+ ((PhotoAlbumMainActivity) getActivity()).maxnum
							+ "张图片");
				}
			}
			adapter.notifyDataSetChanged();
		}
	};

	public void rightClick(View v) {
		ArrayList<String> strs = new ArrayList<String>();
		for (PhotoItem pi : gl_arr) {
			strs.add(pi.getPath());
		}
		Intent i = new Intent();
		i.putStringArrayListExtra("list", strs);
		getActivity().setResult(1, i);
		getActivity().finish();
	};
}
