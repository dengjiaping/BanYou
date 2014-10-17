package com.quanliren.quan_one.activity.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.core.Arrays;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.group.PhotoAlbumMainActivity;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.fragment.custom.AddPicFragment;
import com.quanliren.quan_one.fragment.custom.AddPicFragment.OnArticleSelectedListener;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class UserInfoEditActivity extends BaseActivity implements OnArticleSelectedListener{


	@ViewInject(id = R.id.nickname)
	EditText nickname;
	@ViewInject(id = R.id.age)
	TextView age;
//	@ViewInject(id = R.id.height)
//	TextView height;
	@ViewInject(id = R.id.love)
	TextView love;
	@ViewInject(id = R.id.work)
	TextView work;
	@ViewInject(id = R.id.signature)
	EditText signature;
//	@ViewInject(id = R.id.nature)
//	TextView nature;
//	@ViewInject(id = R.id.weight)
//	TextView weight;
//	@ViewInject(id = R.id.edu)
//	TextView edu;
	@ViewInject(id = R.id.place)
	EditText place;
//	@ViewInject(id = R.id.qq)
//	EditText qq;

	ImageView tempImageView = null;
	User user = null;
	JSONArray ja = new JSONArray();
	AddPicFragment fragment;

	public LinkedList<String> getHeightStr() {
		int min = 100;
		int max = 230;
		LinkedList<String> list = new LinkedList<String>();
		for (int i = min; i <= max; i++) {
			list.add(i + "cm");
		}
		list.addFirst("无");
		return list;
	}

	public LinkedList<String> getWeightStr() {
		int min = 35;
		int max = 200;
		LinkedList<String> list = new LinkedList<String>();
		for (int i = min; i <= max; i++) {
			list.add(i + "kg");
		}
		list.addFirst("无");
		return list;
	}

	public LinkedList<String> getNatureStr() {
		LinkedList<String> list = new LinkedList<String>();
		list.addFirst("无");
		list.add("成熟稳重");
		list.add("活泼开朗");
		list.add("温和细腻");
		list.add("外向有魄力");
		return list;
	}

	public LinkedList<String> getLoveStr() {
		LinkedList<String> list = new LinkedList<String>();
		list.add("泡吧");
		list.add("读书");
		list.add("运动");
		list.add("养宠物");
		list.add("音乐");
		list.add("网络游戏");
		list.add("电子产品");
		list.add("烹饪");
		list.add("购物");
		list.add("美食");
		list.add("炒股");
		list.add("看电影");
		list.add("摄影");
		list.add("旅游");
		list.add("品酒饮茶");
		list.add("跳舞");
		list.add("麻将棋牌");
		list.add("收藏");
		list.add("汽车");
		return list;
	}

	public LinkedList<String> getEduStr() {
		LinkedList<String> list = new LinkedList<String>();
		list.addFirst("无");
		list.add("大学");
		list.add("大专");
		list.add("中专");
		list.add("高中");
		list.add("博士");
		list.add("硕士");
		return list;
	}

	public LinkedList<String> getJobStr() {
		LinkedList<String> list = new LinkedList<String>();
		list.addFirst("无");
		list.add("白领");
		list.add("主管/经理");
		list.add("教授/教师");
		list.add("技术/科学/工程");
		list.add("服务人员");
		list.add("行政管理/秘书");
		list.add("销售/市场");
		list.add("艺术/音乐/作家");
		list.add("自由职业/自雇");
		list.add("演员/歌星");
		list.add("学生");
		list.add("失业");
		list.add("离/退休");
		list.add("主妇");
		list.add("普通职员");
		list.add("其他");
		return list;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ImageLoader.getInstance().stop();
		user = ac.getUserInfo();
		setContentView(R.layout.user_info_edit);
		setTitleTxt(R.string.edit);
		setTitleRightTxt(R.string.save);
		
		fragment=(AddPicFragment) getSupportFragmentManager().findFragmentById(R.id.picFragment);
		fragment.setMaxNum(user.getIsvip()==0?9:16);
		
		setListener();
		initViewByUser();
	}

	public void setListener() {

//		height.setOnClickListener(new choseDialog(getHeightStr()));
//		weight.setOnClickListener(new choseDialog(getWeightStr()));
//		edu.setOnClickListener(new choseDialog(getEduStr()));
//		nature.setOnClickListener(new choseDialog(getNatureStr()));
		work.setOnClickListener(new choseDialog(getJobStr()));
		love.setOnClickListener(new choseMDialog(getLoveStr()));
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		fragment.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode,resultCode, data);
	}

	public void add_pic_btn(View v) {
		tempImageView = (ImageView) v;
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
					showCustomToast("亲，请检查是否安装存储卡!");
				}
				break;
			case 1:
				if (Util.existSDcard()) {
					String messagepath = StaticFactory.APKCardPath;
					File fa = new File(messagepath);
					if (!fa.exists()) {
						fa.mkdirs();
					}
					Intent i = new Intent(UserInfoEditActivity.this,
							PhotoAlbumMainActivity.class);
					i.putExtra("maxnum", user.getIsvip()==0?8:16);
					i.putExtra("images", fragment.getSdibs());
					startActivityForResult(i, AddPicFragment.Album);
					/*Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent, AddPicFragment.Album);*/
				} else {
					showCustomToast("亲，请检查是否安装存储卡!");
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
				String url = tempImageView.getTag().toString();

				/** 删除图片的编号 **/
				if (url.startsWith("http://") && user.getImglist() != null) {
					for (ImageBean ibs : user.getImglist()) {
						if (ibs.imgpath.equals(url)) {
							ja.put(ibs.position);
						}
					}
				}

				fragment.removeByView(tempImageView);
				
				break;
			default:
				menupop.closeMenu();
				break;
			}
			menupop.closeMenu();
		}
	};


	@Override
	public void rightClick(View v) {
		String str_nickname = nickname.getText().toString().trim();
		String str_age = age.getText().toString().trim();
//		String str_height = height.getText().toString().trim();
		String str_love = love.getText().toString().trim();
		String str_work = work.getText().toString().trim();
		String str_signature = signature.getText().toString().trim();
//		String str_nature = nature.getText().toString().trim();
//		String str_edu = edu.getText().toString().trim();
//		String str_weight = weight.getText().toString().trim();
		String str_place = place.getText().toString().trim();
//		String str_qq = qq.getText().toString().trim();

		if (str_nickname.length() == 0) {
			showCustomToast("请输入昵称");
			return;
		} else if (str_age.length() == 0) {
			showCustomToast("请选择出生日期");
			return;
		}

		AjaxParams ap = getAjaxParams().put("userid", user.getId())
				.put("nickname", str_nickname).put("birthday", str_age)
				//.put("height", str_height.replace("cm", ""))
				.put("hobby", str_love).put("signature", str_signature)
				//.put("weight", str_weight.replace("kg", ""))
				//.put("nature", str_nature).put("education", str_edu)
				.put("hometown", str_place).put("removeimg", ja.toString()).put("job", str_work);
				//.put("qq", str_qq);

		ac.finalHttp.post(URL.EDIT_USER_INFO, ap, editInfoCallBack);
	}

	AjaxCallBack<String> editInfoCallBack = new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在更新信息");
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
					List<String> string=new ArrayList<String>();
					if (fragment.getCount()> 0){
						for (String string2 : fragment.getIbs()) {
							if(!string2.startsWith("http://")){
								string.add(string2);
							}
						}
					}
					if(string.size()>0)
						uploadImg(0,string);
					else {
						showCustomToast("上传成功");
						setResult(1);
						finish();
					}
					break;
				case -2:
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

	public void uploadImg(int i,List<String> list) {
		if (i == list.size()) {
			return;
		}
		try {
			AjaxParams ap = getAjaxParams()
					.put("file", new File(list.get(i)))
					.put("userid", user.getId())
					.put("position", fragment.getIbs().indexOf(list.get(i)) + "")
					.put("width", "0")
					.put("height", "0");
			ac.finalHttp.post(URL.UPLOAD_USER_LOGO, ap, new callBack(i,list));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	class callBack extends AjaxCallBack<String> {

		int index = 0;
		List<String> list;
		public callBack(int index,List<String> list) {
			this.index = index;
			this.list=list;
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
					if (index == (list.size()- 1)) {
						showCustomToast("上传成功");
						setResult(1);
						finish();
					} else {
						uploadImg(index + 1,list);
					}
					break;
				default:
					showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
							URL.INFO));
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		};
	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (menupop != null && menupop.isShow) {
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
		new IosCustomDialog.Builder(UserInfoEditActivity.this)
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
	protected void onDestroy() {
		super.onDestroy();

		for (String str: fragment.getIbs()) {
			File file = new File(str);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	public void initViewByUser() {
		nickname.setText(user.getNickname());

		if (user.getImglist() != null && user.getImglist().size() > 0) {
			for (int i = 0; i < user.getImglist().size(); i++) {
				fragment.getIbs().add(user.getImglist().get(i).imgpath);
				fragment.getSdibs().add(user.getImglist().get(i).imgpath);
			}
		}

		fragment.initSource(fragment.getIbs());
		
		age.setText(user.getBirthday());

//		if (Util.isStrNotNull(user.getHeight())) {
//			height.setText(user.getHeight() + "cm");
//		}
//
//		if (Util.isStrNotNull(user.getWeight())) {
//			weight.setText(user.getWeight() + "kg");
//		}
//
//		if (Util.isStrNotNull(user.getNature())) {
//			nature.setText(user.getNature());
//		}
//
		if (Util.isStrNotNull(user.getHobby())) {
			love.setText(user.getHobby());
		}
//
//		if (Util.isStrNotNull(user.getQq())) {
//			qq.setText(user.getQq());
//		}
//
//		if (Util.isStrNotNull(user.getEducation())) {
//			edu.setText(user.getEducation());
//		}

		if (Util.isStrNotNull(user.getJob())) {
			work.setText(user.getJob());
			String[] str = CustomFilterActivity.job;
			for (int i = 0; i < str.length; i++) {
				if (str[i].equals(user.getJob())) {
					work.setTag(i - 1);
				}
			}
		}

		if (Util.isStrNotNull(user.getHometown())) {
			place.setText(user.getHometown());
		}

		signature.setText(user.getSignature());
	}

	class choseDialog implements OnClickListener {

		LinkedList<String> str = null;

		public choseDialog(LinkedList<String> s) {
			str = s;
		}

		@Override
		public void onClick(final View v) {
			int index = 0;
			if(Util.isStrNotNull(((TextView)v).getText().toString())){
				index=str.indexOf(((TextView)v).getText().toString());
			}
			String[] ss = new String[str.size()];
			new AlertDialog.Builder(UserInfoEditActivity.this)
					.setTitle("选择")
					.setSingleChoiceItems(str.toArray(ss), index,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									((TextView) v).setText(str.get(item));
									dialog.cancel();
								}
							})
					.setPositiveButton("关闭",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();
		}

	}

	class choseMDialog implements OnClickListener {

		LinkedList<String> str = null;
		ListView lv;
		public choseMDialog(LinkedList<String> s) {
			str = s;
		}

		@Override
		public void onClick(final View v) {
			
			String[] ostr=love.getText().toString().split(",");
			
			String[] ss = new String[str.size()];
			final boolean[] selected = new boolean[ss.length];

			for (int i = 0; i < ss.length; i++) {
				selected[i] = false;
			}
			
			for(int i=0;i<ostr.length;i++){
				int ind=str.indexOf(ostr[i]);
				if(ind!=-1)
				selected[ind]=true;
			}
			
			AlertDialog ad=new AlertDialog.Builder(UserInfoEditActivity.this)
					.setTitle("选择")
					.setMultiChoiceItems(str.toArray(ss), selected,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,
										int item, boolean isChecked) {
									if(love.getText().toString().trim().equals("")){
										love.setText(str.get(item));
									}else{
										String[] ostr=love.getText().toString().split(",");
										List<String> strlist=Arrays.asList(ostr);
										List<String> newstrlist=new ArrayList<String>(strlist);
										if(isChecked){
											if(ostr.length>=4){
												showCustomToast("最能只能选择4个爱好");
												selected[item]=false;
												lv.setItemChecked(item, false);
												return;
											}
											newstrlist.add(str.get(item));
										}else{
											newstrlist.remove(str.get(item));
										}
										love.setText(Arrays.toString(newstrlist.toArray()).replace("[", "").replace("]", "").replace(" ", ""));
									}
								}
							})
					.setPositiveButton("关闭",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create();
			lv=ad.getListView();
			ad.show();
		}

	}


	@Override
	public void onArticleSelected(View articleUri) {
		if(articleUri.getId()==8&&user.getIsvip()==0){
			goVip();return;
		}
		add_pic_btn(articleUri);
	}
}
