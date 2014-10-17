package com.quanliren.quan_one.activity.group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XListViewHeader;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.activity.user.UserOtherInfoActivity;
import com.quanliren.quan_one.adapter.QuanDetailReplyAdapter;
import com.quanliren.quan_one.adapter.QuanDetailReplyAdapter.IQuanDetailReplyAdapter;
import com.quanliren.quan_one.adapter.QuanPicAdapter;
import com.quanliren.quan_one.bean.DongTaiBean;
import com.quanliren.quan_one.bean.DongTaiBeanTable;
import com.quanliren.quan_one.bean.DongTaiReplyBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.custom.emoji.EmoteInputView;
import com.quanliren.quan_one.custom.emoji.EmoticonsEditText;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class DongTaiDetailActivity extends BaseActivity implements IQuanDetailReplyAdapter {
	public static final String TAG = "DongTaiDetailActivity";
	@ViewInject(id = R.id.listview)
	ListView listview;
	@ViewInject(id = R.id.reply_content)
	EmoticonsEditText reply_content;
	@ViewInject(id=R.id.emoji_btn,click="add_emoji_btn")
	View emoji_btn;
	@ViewInject(id = R.id.chat_eiv_inputview)
	EmoteInputView gridview;
	View vip;
	DongTaiBean bean;
	LinearLayout headView;
	ImageView userlogo;
	QuanDetailReplyAdapter adapter;
	GridView gridView;
	QuanPicAdapter picadapter;
	TextView username, sex, time, signature, reply_btn, location;
	LayoutParams lp;
	View content_rl;
	int imgWidth;
	XListViewHeader mHeaderView;
	RelativeLayout mHeaderViewContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		bean = (DongTaiBean) getIntent().getSerializableExtra("bean");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quan_detail);
		setTitleTxt("动态内容");

		listview.addHeaderView(headView = (LinearLayout) View.inflate(this,
				R.layout.quan_detail_head, null));
		mHeaderView = new XListViewHeader(this);
		headView.addView(mHeaderView);
		mHeaderView.setVisiableHeight(ImageUtil.dip2px(this, 60));
		mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
		mHeaderView.setVisibility(View.GONE);
		listview.addFooterView(new View(this));

		DongTaiBeanTable dtb = ac.finalDb.findById(bean.getDyid(),
				DongTaiBeanTable.class);

		if (dtb != null) {
			bean = dtb.getBean();
		}

		List<DongTaiReplyBean> list = new ArrayList<DongTaiReplyBean>();
		if (bean.getCommlist() != null) {
			list = bean.getCommlist();
		}
		listview.setAdapter(adapter = new QuanDetailReplyAdapter(this, list,this));

		findheadview();
		setHeadSource();

		if (bean.getUserid().equals(ac.getUser().getId())) {
			setTitleRightTxt("删除");
		}

		ac.finalHttp.post(URL.GETDONGTAI_DETAIL,
				getAjaxParams().put("dyid", bean.getDyid() + ""), callBack);
	}

	public void findheadview() {
		gridView = (GridView) headView.findViewById(R.id.pic_gridview);
		userlogo = (ImageView) headView.findViewById(R.id.userlogo);
		username = (TextView) headView.findViewById(R.id.nickname);
		sex = (TextView) headView.findViewById(R.id.sex);
		signature = (TextView) headView.findViewById(R.id.signature);
		time = (TextView) headView.findViewById(R.id.time);
		vip=headView.findViewById(R.id.vip);
		imgWidth = (getResources().getDisplayMetrics().widthPixels - ImageUtil
				.dip2px(this, 72)) / 3;

		picadapter = new QuanPicAdapter(this, new ArrayList<String>(), imgWidth);
		gridView.setAdapter(picadapter);

		lp = new LayoutParams(
				LayoutParams.FILL_PARENT, imgWidth);
		lp.addRule(RelativeLayout.BELOW, R.id.signature);
		location = (TextView) headView.findViewById(R.id.location);
		reply_btn = (TextView) headView.findViewById(R.id.reply_btn);
		gridView.setLayoutParams(lp);
		content_rl = headView.findViewById(R.id.content_rl);

		reply_content.setOnEditorActionListener(editListener);
		reply_content.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean b) {
				if(b){
					gridview.setVisibility(View.GONE);
				}
			}
		});
		reply_content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gridview.setVisibility(View.GONE);
			}
		});
		
		gridview.setEditText(reply_content);
	}

	public void setHeadSource() {
		if (bean.getImglist() == null || bean.getImglist().size() == 0) {
			gridView.setVisibility(View.GONE);
		} else {
			gridView.setVisibility(View.VISIBLE);
			picadapter.setList(bean.getImglist());
			picadapter.notifyDataSetChanged();
			lp = (LayoutParams) gridView.getLayoutParams();
			lp.height = imgWidth * Util.getLines(bean.getImglist().size(), 3);
			gridView.setLayoutParams(lp);
		}
		ImageLoader.getInstance().displayImage(
				bean.getAvatar() + StaticFactory._160x160, userlogo,ac.options_userlogo);
		username.setText(bean.getNickname());
		time.setText(Util.getTimeDateStr(bean.getCtime()));
		if (bean.getContent().trim().length() > 0) {
			signature.setVisibility(View.VISIBLE);
			signature.setText(bean.getContent());
		} else {
			signature.setVisibility(View.GONE);
		}
		switch (Integer.valueOf(bean.getSex())) {
		case 0:
			sex.setBackgroundResource(R.drawable.girl_icon);
			break;
		case 1:
			sex.setBackgroundResource(R.drawable.boy_icon);
			break;
		default:
			break;
		}
		sex.setText(bean.getAge());

		userlogo.setOnClickListener(userlogoClick);
		location.setText(bean.getArea());
		reply_btn.setText(bean.getCnum());
		
		if(bean.getIsvip()==1){
			vip.setVisibility(View.VISIBLE);
			username.setTextColor(getResources().getColor(R.color.vip_name));
		}else{
			vip.setVisibility(View.GONE);
		}
		
		adapter.setList(bean.getCommlist());
		adapter.notifyDataSetChanged();
	}


	OnClickListener userlogoClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(DongTaiDetailActivity.this, bean.getUserid()
					.equals(ac.getUser().getId()) ? UserInfoActivity.class
					: UserOtherInfoActivity.class);
			i.putExtra("id", bean.getUserid());
			startActivity(i);
		}
	};

	public void rightClick(View v) {
		new IosCustomDialog.Builder(this).setMessage("您确定要删除这条动态吗？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AjaxParams ap = getAjaxParams();
						ap.put("dyid", bean.getDyid() + "");
						ac.finalHttp.post(URL.DELETE_DONGTAI, ap,
								new AjaxCallBack<String>() {
									@Override
									public void onStart() {
										customShowDialog("正在删除");
									}

									@Override
									public void onSuccess(String t) {
										customDismissDialog();
										try {
											JSONObject jo = new JSONObject(t);
											int status = jo.getInt(URL.STATUS);
											switch (status) {
											case 0:
												showCustomToast("删除成功");
												Intent i = new Intent();
												i.putExtra("bean", bean);
												setResult(2, i);
												finish();
												break;
											default:
												showFailInfo(jo);
												break;
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

									@Override
									public void onFailure(Throwable t,
											int errorNo, String strMsg) {
										customDismissDialog();
										showIntentErrorToast();
									}
								});
					}
				}).create().show();
	};

	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
		public void onStart() {
			mHeaderView.setVisibility(View.VISIBLE);
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			mHeaderView.setVisibility(View.GONE);
		};

		public void onSuccess(String t) {
			mHeaderView.setVisibility(View.GONE);
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					bean = new Gson().fromJson(jo.getString(URL.RESPONSE),
							new TypeToken<DongTaiBean>() {
							}.getType());
					DongTaiBeanTable table = new DongTaiBeanTable(bean);
					ac.finalDb.delete(table);
					ac.finalDb.save(table);
					setHeadSource();
					break;
				case 2:
					finish();
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	OnEditorActionListener editListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
			if (arg1 == EditorInfo.IME_ACTION_SEND) {
				String content = reply_content.getText().toString().trim();
				if (content.length() == 0) {
					customShowDialog("请输入内容");
					return true;
				}
				AjaxParams ap = getAjaxParams();
				ap.put("dyid", bean.getDyid() + "").put("content", content);
				
				DongTaiReplyBean replayBean = new DongTaiReplyBean();
				
				Object obj=reply_content.getTag();
				if(obj!=null){
					DongTaiReplyBean rb=(DongTaiReplyBean) obj;
					ap.put("replyuid", rb.getUserid());
					replayBean.setReplyuid(rb.getUserid());
					replayBean.setReplyuname(rb.getNickname());
				}
				replayBean.setContent(content);
				User user=ac.getUserInfo();
				replayBean.setAvatar(user.getAvatar());
				replayBean.setNickname(user.getNickname());
				replayBean.setUserid(user.getId());
				replayBean.setCtime(Util.fmtDateTime.format(new Date()));
				
				ac.finalHttp.post(URL.REPLY_DONGTAI,ap, new replyCallBack(replayBean));
				
				reply_content.clearFocus();
				reply_content.setText("");
				reply_content.setHint("");
				reply_content.setTag(null);
				closeInput();
			}
			return false;
		}
	};
	
	class replyCallBack extends AjaxCallBack<String> {
		DongTaiReplyBean replayBean;
		public replyCallBack(DongTaiReplyBean replayBean){
			this.replayBean = replayBean;
		}
		public void onStart() {
			bean.getCommlist().add(replayBean);
			adapter.notifyDataSetChanged();listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		};
		public void onSuccess(String t) {
			try {
				DongTaiBeanTable table=new DongTaiBeanTable(bean);
				ac.finalDb.delete(table);
				ac.finalDb.save(table);
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					String id=jo.getJSONObject(URL.RESPONSE).getString("id");
					replayBean.setId(id);
					reply_btn.setText((Integer.valueOf(reply_btn.getText().toString())+1)+"");
					listview.setSelection((bean.getCommlist().size()+1));
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
			bean.getCommlist().remove(replayBean);
			adapter.notifyDataSetChanged();
		};
	}

	@Override
	public void contentClick(DongTaiReplyBean bean) {
		if(!bean.getUserid().equals(ac.getUser().getId())){
			reply_content.setTag(bean);
			reply_content.setHint("回复 "+bean.getNickname()+" :");
		}
		showKeyBoard();reply_content.requestFocus();
	}

	@Override
	public void logoCick(DongTaiReplyBean bean) {
		Intent i = new Intent(this, bean.getUserid()
				.equals(ac.getUser().getId()) ? UserInfoActivity.class
				: UserOtherInfoActivity.class);
		i.putExtra("id", bean.getUserid());
		startActivity(i);
	}
	
	public void add_emoji_btn(View v) {
		if (gridview.getVisibility() == View.VISIBLE) {
			gridview.setVisibility(View.GONE);
			reply_content.requestFocus();
			showKeyBoard();
		} else {
			closeInput();
			gridview.setVisibility(View.VISIBLE);
		}
	}
	
	public void onBackPressed() {
		if (gridview.getVisibility() == View.VISIBLE) {
			reply_content.requestFocus();
			gridview.setVisibility(View.GONE);
			showKeyBoard();
			return;
		} else if (menupop != null && menupop.isShow) {
			menupop.closeMenu();
			return;
		}
		super.onBackPressed();
	}


}
