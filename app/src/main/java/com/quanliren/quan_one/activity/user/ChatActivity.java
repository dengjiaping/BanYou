package com.quanliren.quan_one.activity.user;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.db.sqlite.SqlInfo;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity;
import com.quanliren.quan_one.adapter.MessageAdapter;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.bean.MessageListBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.custom.emoji.EmoteInputView;
import com.quanliren.quan_one.custom.emoji.EmoticonsEditText;
import com.quanliren.quan_one.fragment.custom.AddPicFragment;
import com.quanliren.quan_one.fragment.message.MyLeaveMessageFragment;
import com.quanliren.quan_one.radio.AmrEncodSender;
import com.quanliren.quan_one.radio.AmrEngine;
import com.quanliren.quan_one.radio.MicRealTimeListener;
import com.quanliren.quan_one.service.SocketManage;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.EmojiFilter;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class ChatActivity extends BaseActivity implements IXListViewListener,
		OnFocusChangeListener, OnEditorActionListener, OnTouchListener,
		SensorEventListener {
	public static final String ADDMSG = "com.quanliren.quan_one.ChatActivity.ADDMSG";
	public static final String CHANGESEND = "com.quanliren.quan_one.ChatActivity.CHANGESEND";
	@ViewInject(id = R.id.text)
	EmoticonsEditText text;
	@ViewInject(id = R.id.chat_borad_btn, click = "boradClick")
	View chat_borad_btn;
	@ViewInject(id = R.id.chat_eiv_inputview)
	EmoteInputView gridview;
	@ViewInject(id = R.id.chat_voice_btn, click = "voiceClick")
	View chat_voice_btn;
	@ViewInject(id = R.id.chat_add_btn, click = "addClick")
	View chat_add_btn;
	@ViewInject(id = R.id.chat_radio_btn)
	Button chat_radio_btn;
	@ViewInject(id = R.id.chat_radio_panel)
	View chat_radio_panel;
	@ViewInject(id = R.id.chat_face_btn, click = "faceClick")
	View chat_face_btn;
	@ViewInject(id = R.id.edit_ll)
	View edit_ll;
	@ViewInject(id = R.id.list)
	XXListView listview;
	@ViewInject(id = R.id.voicesize)
	ImageView voicesize;
	@ViewInject(id = R.id.loading)
	View loading;
	@ViewInject(id = R.id.delete)
	ImageView delete;
	MessageAdapter adapter;
	User friend;
	User user;
	String maxid = "0";
	AjaxParams ap;
	PopFactory menupop1;
	String filename;
	public PopFactory addpop;
	private NotificationManager nm;
	AudioManager audioManager;
	SensorManager mSensorManager;
	Sensor mSensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		friend = (User) getIntent().getSerializableExtra("friend");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(0);
		user = ac.getUserInfo();
		adapter = new MessageAdapter(this, new ArrayList<MessageListBean>(),
				friend, itemHandler);
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);
		listview.setPullLoadEnable(false);
		
		UserTable temp=ac.finalDb.findById(friend.getId(),UserTable.class);
		if(temp!=null){
			friend=temp.getUser();
		}else{
			ac.finalHttp.post(URL.GET_USER_INFO, getAjaxParams("otherid", friend.getId()),
					callBack);
		}
		
		setTitleTxt(friend.getNickname());

		text.setOnFocusChangeListener(this);

		text.setOnEditorActionListener(this);

		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				gridview.setVisibility(View.GONE);
			}
		});

		chat_radio_btn.setOnTouchListener(this);

		/*title_right_txt.setCompoundDrawablesWithIntrinsicBounds(getResources()
				.getDrawable(R.drawable.ban), null, null, null);
		setTitleRightTxt("举报/拉黑");*/

		listview.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				closeInput();
				gridview.setVisibility(View.GONE);
				return false;
			}
		});

		gridview.setEditText(text);

		String[] str = new String[] { ADDMSG, CHANGESEND };
		receiveBroadcast(str, broadcast);

		audioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		if(user!=null){
			Util.setAlarmTime(this, System.currentTimeMillis(),BroadcastUtil.ACTION_CHECKCONNECT, 60 * 1000);
		}
	}
	
	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					User temp = new Gson().fromJson(jo.getString(URL.RESPONSE),
							User.class);
					if (temp != null) {
						friend = temp;
						UserTable dbUser = new UserTable(temp);
						ac.finalDb.deleteById(UserTable.class, dbUser.getId());
						ac.finalDb.save(dbUser);
						adapter.setFriend(friend);
						adapter.notifyDataSetChanged();
						setTitleTxt(friend.getNickname());
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

	@Override
	public void onRefresh() {
		if (friend == null) {
			return;
		}
		listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
		new Thread(new Runnable() {

			@Override
			public void run() {
				int maxid = -1;
				if (adapter.getList().size() > 0) {
					maxid = ((DfMessage) (adapter.getList().get(0))).getId();
				}
				String where = "userid='" + user.getId() + "' and (sendUid='"
						+ friend.getId() + "' or receiverUid='"
						+ friend.getId() + "')";
				if (maxid > -1) {
					where += " and id<" + maxid;
				}
				final List<DfMessage> list = ac.finalDb.findAllByWhere(
						DfMessage.class, where, "id desc limit 0,15");
				final List<DfMessage> downlist = new ArrayList<DfMessage>();
				List<Integer> ids = new ArrayList<Integer>();
				for (DfMessage dfMessage : list) {
					if (dfMessage.getIsRead() == 0) {
						ids.add(dfMessage.getId());
					}
					if (dfMessage.getMsgtype() == 2
							&& (dfMessage.getDownload() == SocketManage.D_nodownload || dfMessage
									.getDownload() == SocketManage.D_downloading)) {
						downlist.add(dfMessage);
					}
				}
				if (ids.size() > 0) {
					SqlInfo si = new SqlInfo();
					StringBuffer strSQL = new StringBuffer("update "
							+ DfMessage.TABLENAME
							+ " set isRead='1' where id in ");
					strSQL.append(" ( ");

					for (int i = 0; i < ids.size(); i++) {
						strSQL.append("?");
						strSQL.append(",");
					}
					strSQL.deleteCharAt(strSQL.length() - 1);
					strSQL.append(" )");

					si.setSql(strSQL.toString());
					for (Integer integer : ids) {
						si.addValue(integer);
					}

					ac.finalDb.exeSqlInfo(si);
				}
				if (list.size() > 0) {
					try {
						for (int i = list.size() - 1; i >= 0; i--) {
							if (i < list.size() - 1 && i > 0) {
								if (Util.fmtDateTime.parse(
										list.get(i).getCtime()).getTime() - 60 * 1000 > Util.fmtDateTime
										.parse(list.get(i - 1).getCtime())
										.getTime()) {
									list.get(i).setShowTime(true);
								}
							} else {
								list.get(i).setShowTime(true);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				Intent broad = new Intent(MyLeaveMessageFragment.REFEREMSGCOUNT);
				broad.putExtra("id", friend.getId());
				sendBroadcast(broad);

				runOnUiThread(new Runnable() {
					public void run() {
						for (DfMessage dfMessage : list) {
							adapter.addFirstItem(dfMessage);
						}
						adapter.notifyDataSetChanged();
						listview.stop();
						if(list.size()>0){
							listview.setSelection(adapter.getList().indexOf(list.get(0))+1);
						}
						for (DfMessage dfMessage : downlist) {
							ac.finalHttp.download(
									dfMessage.getContent(),
									StaticFactory.APKCardPathChat
											+ dfMessage.getContent().hashCode(),
									true, new fileDownload(dfMessage));
						}
					}
				});
			}
		}).start();
	}

	@Override
	public void onLoadMore() {
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AddPicFragment.Album:
			if (data == null) {
				return;
			}
			ContentResolver resolver = getContentResolver();
			Uri imgUri = data.getData();
			try {
				Cursor cursor = resolver.query(imgUri, null, null, null, null);
				cursor.moveToFirst();
				filename = cursor.getString(1);
				ImageUtil.downsize(
						filename,
						filename = StaticFactory.APKCardPath
								+ new Date().getTime(), this);
				new sendFile(new File(filename), 1).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case AddPicFragment.Camera:
			if (filename != null) {
				File fi = new File(filename);
				if (fi != null && fi.exists()) {
					ImageUtil.downsize(filename, filename, this);
					new sendFile(fi, 1).start();
				}
				fi = null;
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	};

	Handler broadcast = new Handler() {
		public void dispatchMessage(Message msg) {
			Intent i = (Intent) msg.obj;
			String action = i.getAction();
			if (action.equals(ADDMSG)) {
				DfMessage bean = (DfMessage) i.getExtras().getSerializable(
						"bean");
				if (bean.getSendUid().equals(friend.getId())) {
					bean.setIsRead(1);
					ac.finalDb.update(bean);

					Intent broad = new Intent(
							MyLeaveMessageFragment.REFEREMSGCOUNT);
					broad.putExtra("id", friend.getId());
					sendBroadcast(broad);

					adapter.addNewsItem(bean);
					adapter.notifyDataSetChanged();

					listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

					if (bean.getMsgtype() == 2) {
						ac.finalHttp.download(bean.getContent(),
								StaticFactory.APKCardPathChat
										+ bean.getContent().hashCode(), true,
								new fileDownload(bean));
					}
					nm.cancel(0);
				}
			} else if (action.equals(CHANGESEND)) {
				DfMessage bean = (DfMessage) i.getExtras().getSerializable(
						"bean");
				List<DfMessage> list = adapter.getList();
				for (DfMessage dfMessage : list) {
					if (dfMessage.getId() == bean.getId()) {
						dfMessage.setDownload(bean.getDownload());
					}
				}
				adapter.notifyDataSetChanged();
			}
			super.dispatchMessage(msg);
		};
	};

	class sendFile extends Thread {
		private File file;
		private int msgtype;

		public sendFile(File file, int msgtype) {
			this.file = file;
			this.msgtype = msgtype;
		}

		@Override
		public void run() {
			try {
				JSONObject msg = DfMessage.getMessage(user, file.getPath(),
						friend, msgtype, (int) recodeTime);
				
				JSONObject jo = new JSONObject();
				jo.put(SocketManage.ORDER, SocketManage.ORDER_SENDMESSAGE);
				jo.put(SocketManage.SEND_USER_ID, user.getId());
				jo.put(SocketManage.RECEIVER_USER_ID, friend.getId());
				jo.put(SocketManage.MESSAGE, msg);
				jo.put(SocketManage.MESSAGE_ID,
						msg.getString(SocketManage.MESSAGE_ID));

				AjaxParams ap = getAjaxParams();
				ap.put("file", file);
				ap.put("msgattr", jo.toString());
				ap.put("devicetype", "0");

				ac.finalHttp.post(
						URL.SENDFILE,
						ap,
						new sendfileCallBack((DfMessage) new Gson().fromJson(
								msg.toString(), new TypeToken<DfMessage>() {
								}.getType())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class sendfileCallBack extends AjaxCallBack<String> {
		DfMessage msg;

		public sendfileCallBack(DfMessage msg) {
			this.msg = msg;
		}

		@Override
		public void onStart() {
			msg.setDownload(SocketManage.D_downloading);

			ac.finalDb.saveBindId(msg);

			ac.finalDb.deleteByWhere(
					ChatListBean.class,
					"userid='" + user.getId() + "' and friendid='"
							+ friend.getId() + "'");
			ChatListBean cb = new ChatListBean(user, msg, friend);
			ac.finalDb.saveBindId(cb);

			Intent broad = new Intent(MyLeaveMessageFragment.ADDMSG);
			broad.putExtra("bean", cb);
			sendBroadcast(broad);

			listview.setSelection(adapter.getCount() - 1);
			listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			adapter.addNewsItem(msg);
			adapter.notifyDataSetChanged();
			text.setText("");
			
			chat_radio_btn.setEnabled(true);
		}

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					msg.setDownload(SocketManage.D_downloaded);
					ac.finalDb.update(msg);
					adapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			fail();
		}

		public void fail() {
			msg.setDownload(SocketManage.D_destroy);
			ac.finalDb.update(msg);
			adapter.notifyDataSetChanged();
		}
	};

	public void rightClick(View v) {
		if (menupop == null) {
			menupop = new PopFactory(this, new String[] { "加入黑名单", "举报并拉黑" },
					menuClick, parent);
		}
		menupop.toogle();
	};

	OnClickListener menuClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case 0:
				new IosCustomDialog.Builder(ChatActivity.this)
						.setMessage("您确定要拉黑该用户吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										AjaxParams ap = getAjaxParams();
										ap.put("otherid", friend.getId());
										ac.finalHttp.post(URL.ADDTOBLACK, ap,
												addBlackCallBack);
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create().show();
				break;
			case 1:
				new IosCustomDialog.Builder(ChatActivity.this)
						.setMessage("您确定要举报并拉黑该用户吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (menupop1 == null) {
											menupop1 = new PopFactory(
													ChatActivity.this,
													new String[] { "骚扰信息",
															"个人资料不当", "盗用他人资料",
															"垃圾广告", "色情相关" },
													menuClick1, parent);
										}
										menupop1.toogle();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create().show();
				break;
			}
			menupop.closeMenu();
		}
	};

	AjaxCallBack<String> addBlackCallBack = new AjaxCallBack<String>() {
		public void onStart() {
			customShowDialog("正在发送请求");
		};

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("操作成功");
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

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			customDismissDialog();
			showIntentErrorToast();
		};
	};

	OnClickListener menuClick1 = new OnClickListener() {
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.exit:
				break;
			default:
				AjaxParams ap = getAjaxParams();
				ap.put("otherid", friend.getId());
				ap.put("type", arg0.getId() + "");
				ac.finalHttp.post(URL.JUBAOANDBLACK, ap, addBlackCallBack);
				break;
			}
			menupop1.closeMenu();
		};
	};

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		edit_ll.setSelected(hasFocus);
		if (hasFocus) {
			gridview.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEND) {
			String t = text.getText().toString();
			if (Util.isStrNotNull(t)) {
				t=EmojiFilter.filterEmoji(t);
				new Thread(new sendTextThread(t)).start();
			} else {
				showCustomToast("请输入内容");
			}
			return true;
		}
		return false;
	}
	class sendTextThread implements Runnable{
		String t;
		public sendTextThread(String t){
			this.t=t;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
				try {
					JSONObject msg = DfMessage.getMessage(user, t, friend, 0,
							(int) recodeTime);
					final JSONObject jo = new JSONObject();
					jo.put(SocketManage.ORDER, SocketManage.ORDER_SENDMESSAGE);
					jo.put(SocketManage.SEND_USER_ID, user.getId());
					jo.put(SocketManage.RECEIVER_USER_ID, friend.getId());
					jo.put(SocketManage.MESSAGE, msg);
					jo.put(SocketManage.MESSAGE_ID,
							msg.getString(SocketManage.MESSAGE_ID));

					recodeTime = 0.0f;
					final DfMessage msgs = new Gson().fromJson(msg.toString(),
							new TypeToken<DfMessage>() {
							}.getType());
					msgs.setDownload(SocketManage.D_downloading);
					ac.finalDb.saveBindId(msgs);
					
					Util.setAlarmTime(ChatActivity.this, System.currentTimeMillis()
							+ (10 * 1000), BroadcastUtil.ACTION_CHECKMESSAGE,
							10 * 1000);

					ac.finalDb.deleteByWhere(ChatListBean.class,
							"userid='" + user.getId() + "' and friendid='"
									+ friend.getId() + "'");
					ChatListBean cb = new ChatListBean(user, msgs, friend);
					ac.finalDb.saveBindId(cb);

					Intent broad = new Intent(MyLeaveMessageFragment.ADDMSG);
					broad.putExtra("bean", cb);
					sendBroadcast(broad);

					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							listview.setSelection(adapter.getCount() - 1);
							listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
							adapter.addNewsItem(msgs);
							adapter.notifyDataSetChanged();
							text.setText("");
							
							ac.sendMessage(jo.toString());
						}
					});
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
	}
	

	Handler imgHandle = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (AmrEngine.getSingleEngine().isRecordRunning()) {
					AmrEngine.getSingleEngine().stopRecording();
					hideall();
					voiceValue = 0.0;
					chat_radio_btn.setText(R.string.normaltalk);
					chat_radio_btn.setEnabled(false);
					if (recodeTime < MIX_TIME) {
						showCustomToast("太短了");
						File o = new File(filename);
						if (o.exists()) {
							o.delete();
						}
						new Handler().postDelayed(new Runnable() {

							public void run() {
								hideall();
								chat_radio_btn.setEnabled(true);
							}
						}, 1000);
					} else {
						new sendFile(new File(filename), 2).start();
					}
				}
				break;
			case 1:
				setDialogImage();
				break;
			default:
				break;
			}

		}
	};

	public void boradClick(View v) {
		chat_voice_btn.setVisibility(View.VISIBLE);
		edit_ll.setVisibility(View.VISIBLE);
		text.requestFocus();
		showKeyBoard();
		chat_radio_btn.setVisibility(View.GONE);
		chat_borad_btn.setVisibility(View.GONE);
	}

	public void voiceClick(View v) {
		User u=ac.getUserInfo();
		if(u.getIsvip()==0){
			goVip();
			return;
		}
		chat_voice_btn.setVisibility(View.GONE);
		edit_ll.setVisibility(View.GONE);
		closeInput();
		text.clearFocus();
		gridview.setVisibility(View.GONE);
		chat_radio_btn.setVisibility(View.VISIBLE);
		chat_borad_btn.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!AmrEngine.getSingleEngine().isRecordRunning()) {
				
				showVoiceLoading();
				
				chat_radio_panel.setVisibility(View.VISIBLE);
				chat_radio_btn.setText(R.string.pressedtalk);

				File file = new File(StaticFactory.APKCardPathChat);
				if (!file.exists()) {
					file.mkdirs();
				}
				AmrEncodSender sender = new AmrEncodSender(
						filename = (StaticFactory.APKCardPathChat + String.valueOf((String
								.valueOf(new Date().getTime()) + ".amr")
								.hashCode())), new MicRealTimeListener() {

							@Override
							public void getMicRealTimeSize(double size,
									long time) {
								voiceValue = size;
							}
						});

				AmrEngine.getSingleEngine().startRecording();
				new Thread(sender).start();
				showVoiceStart();
				mythread();
			}

			break;
		case MotionEvent.ACTION_UP:
			if (AmrEngine.getSingleEngine().isRecordRunning()) {
				AmrEngine.getSingleEngine().stopRecording();
				chat_radio_panel.setVisibility(View.GONE);
				chat_radio_btn.setText(R.string.normaltalk);

				voiceValue = 0.0;
				chat_radio_btn.setEnabled(false);
				if (recodeTime < MIX_TIME) {
					showCustomToast("太短了");
					
					new Handler().postDelayed(new Runnable() {

						public void run() {
							hideall();
							chat_radio_btn.setEnabled(true);
						}
					}, 1000);
				} else {
					showVoiceLoading();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							hideall();
							if (isCanle) {
								File o = new File(filename);
								if (o.exists()) {
									o.delete();
								}
								chat_radio_btn.setEnabled(true);
							} else {
								new sendFile(new File(filename), 2).start();
							}
						}
					}, 500);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			chat_radio_btn.getLocationOnScreen(location);
			chat_radio_panel.getLocationOnScreen(location1);
			if (event.getRawY() < location[1]) {
				showVoiceCancle();
				isCanle = true;
				if (event.getRawY() <= location1[1]
						+ ImageUtil.dip2px(this, 150)
						&& event.getRawY() >= location1[1]
						&& event.getRawX() <= location1[0]
								+ ImageUtil.dip2px(this, 150)
						&& event.getRawX() >= location1[0]) {
					delete.setSelected(true);
				} else {
					delete.setSelected(false);
				}
			} else {
				showVoiceStart();
				isCanle = false;
			}
			break;
		}
		return false;
	}

	int[] location = new int[2];
	int[] location1 = new int[2];
	boolean isCanle = false;
	private MediaPlayer mediaPlayer;
	// Button player;
	private Thread recordThread;

	private static int MAX_TIME = 60; // 最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1

	private static float recodeTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值

	private static boolean playState = false; // 播放状态

	// 录音计时线程
	public void mythread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	private Runnable ImgThread = new Runnable() {

		public void run() {
			recodeTime = 0.0f;
			while (AmrEngine.getSingleEngine().isRecordRunning()) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0);
				} else {
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						if (AmrEngine.getSingleEngine().isRecordRunning()) {
							// voiceValue = mr.getAmplitude();
							imgHandle.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	// 录音Dialog图片随声音大小切换
	void setDialogImage() {
		if (voiceValue <= 11.0) {
			voicesize.setImageResource(R.drawable.hua1);
		} else if (voiceValue > 15.0 && voiceValue <= 18.0) {
			voicesize.setImageResource(R.drawable.hua2);
		} else if (voiceValue > 18.0 && voiceValue <= 21.0) {
			voicesize.setImageResource(R.drawable.hua3);
		} else if (voiceValue > 21.0 && voiceValue <= 25.0) {
			voicesize.setImageResource(R.drawable.hua4);
		} else if (voiceValue > 25.0 && voiceValue <= 28.0) {
			voicesize.setImageResource(R.drawable.hua5);
		} else if (voiceValue > 28.0 && voiceValue <= 31.0) {
			voicesize.setImageResource(R.drawable.hua6);
		} else if (voiceValue > 31.0 && voiceValue <= 36.0) {
			voicesize.setImageResource(R.drawable.hua7);
		} else if (voiceValue > 36.0) {
			voicesize.setImageResource(R.drawable.hua7);
		}
	}

	public void hideall() {
		chat_radio_panel.setVisibility(View.GONE);
		delete.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		voicesize.setVisibility(View.VISIBLE);
	}

	public void showVoiceLoading() {
		voicesize.setVisibility(View.GONE);
		delete.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
	}

	public void showVoiceCancle() {
		loading.setVisibility(View.GONE);
		voicesize.setVisibility(View.GONE);
		delete.setVisibility(View.VISIBLE);
	}

	public void showVoiceStart() {
		loading.setVisibility(View.GONE);
		voicesize.setVisibility(View.VISIBLE);
		delete.setVisibility(View.GONE);
	}

	public void addClick(View view) {
		User u=ac.getUserInfo();
		if(u.getIsvip()==0){
			goVip();
			return;
		}
		closeInput();
		addpop = new PopFactory(this, new String[] { "相机", "从相册中选择" },
				addClick, parent);
		addpop.toogle();
	}

	OnClickListener addClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case 0:
				if (Util.existSDcard()) {
					Intent intent = new Intent(); // 调用照相机
					String messagepath = StaticFactory.APKCardPathChat;
					File fa = new File(messagepath);
					if (!fa.exists()) {
						fa.mkdirs();
					}
					filename = messagepath + new Date().getTime();// 图片路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(filename)));
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, AddPicFragment.Camera);
				} else {
					Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				if (Util.existSDcard()) {
					Intent intent = new Intent();
					String messagepath = StaticFactory.APKCardPathChat;
					File fa = new File(messagepath);
					if (!fa.exists()) {
						fa.mkdirs();
					}
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, AddPicFragment.Album);
				} else {
					Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
			addpop.closeMenu();
		}
	};

	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (addpop != null && addpop.isShow) {
			addpop.closeMenu();
			return;
		}
		super.onBackPressed();
	}

	public void faceClick(View v) {
		if (gridview.getVisibility() == View.VISIBLE) {
			gridview.setVisibility(View.GONE);
			text.requestFocus();
			showKeyBoard();
		} else {
			closeInput();
			gridview.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void closeMenull(View v) {
		if (addpop != null)
			addpop.closeMenu();
		super.closeMenull(v);
	}

	Handler itemHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0:

				break;
			case 1:
				startImage((DfMessage) msg.obj);
				break;
			case 2:
				playVoice((DfMessage) msg.obj);
				break;
			case 3:
				addpop = new PopFactory(ChatActivity.this, new String[] {
						"复制文本", "删除消息" },
						new content_click((DfMessage) msg.obj), parent);
				addpop.toogle();
				break;
			case 4:
				addpop = new PopFactory(ChatActivity.this,
						new String[] { "删除消息" }, new img_click(
								(DfMessage) msg.obj), parent);
				addpop.toogle();
				break;
			case 5:
				addpop = new PopFactory(ChatActivity.this,
						new String[] { "删除消息" }, new img_click(
								(DfMessage) msg.obj), parent);
				addpop.toogle();
				break;
			case 6://消息重发
				reSend((DfMessage) msg.obj);
				break;
			}
			super.dispatchMessage(msg);
		};
	};

	public void reSend(final DfMessage msg){
		new IosCustomDialog.Builder(this).setMessage("您确定要重发这条信息吗？")
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.setPositiveButton("确定",new  DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				ac.finalDb.delete(msg);
				adapter.removeObj(msg);
				adapter.notifyDataSetChanged();
				switch (msg.getMsgtype()) {
				case 0:
					new Thread(new sendTextThread(msg.getContent())).start();
					break;
				case 1:
					new sendFile(new File(msg.getContent()), 1).start();
					break;
				case 2:
					new sendFile(new File(msg.getContent()), 2).start();
					break;
				}
			}
		}).create().show();;
	}
	
	class content_click implements OnClickListener {
		DfMessage msg;

		public content_click(DfMessage msg) {
			this.msg = msg;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case 0:
				copy(msg.getContent(), ChatActivity.this);
				break;
			case 1:
				deleteMsg(msg);
				break;
			}
			addpop.closeMenu();
		}
	};

	public void deleteMsg(DfMessage msg) {
		ac.finalDb.delete(msg);
		try {
			if (msg.getMsgtype() > 0) {
				File file = new File(msg.getContent());
				if (file.exists()) {
					file.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		adapter.removeObj(msg);
		adapter.notifyDataSetChanged();
	}

	public void copy(String content, Context context) {
		ClipboardManager c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		c.setText(content);
		Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show();
	}

	class img_click implements OnClickListener {
		DfMessage msg;

		public img_click(DfMessage msg) {
			this.msg = msg;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case 0:
				deleteMsg(msg);
				break;
			}
			addpop.closeMenu();
		}
	};

	DfMessage playingMsg;

	public void playVoice(DfMessage msg) {
		if (playState) {
			stopArm(playingMsg);
			if (playingMsg.getId() != msg.getId()) {
				playArm(msg);
			}
		} else {
			playArm(msg);
		}
	}

	public void playArm(final DfMessage dm) {
		File file = new File(dm.getContent());
		if (!file.exists()) {
			return;
		}

		mediaPlayer = new MediaPlayer();
		try {
			playingMsg = dm;
			mediaPlayer.setDataSource(dm.getContent());
			mediaPlayer.prepare();
			mediaPlayer.start();
			dm.setPlaying(true);
			adapter.notifyDataSetChanged();
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					playState = false;
					dm.setPlaying(false);
					adapter.notifyDataSetChanged();
					mediaPlayer = null;
				}
			});
			playState = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopArm(DfMessage msg) {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if (msg != null) {
			msg.setPlaying(false);
			adapter.notifyDataSetChanged();
		}
		playState = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopArm(null);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopArm(null);
		mSensorManager.unregisterListener(this);
	}

	public void startImage(DfMessage msg) {
		List<DfMessage> imgDf = new ArrayList<DfMessage>();
		List<DfMessage> list = adapter.getList();
		for (DfMessage dfMessage : list) {
			if (dfMessage.getMsgtype() == 1) {
				imgDf.add(dfMessage);
			}
		}
		int position = imgDf.indexOf(msg);
		List<ImageBean> beans = new ArrayList<ImageBean>();
		for (DfMessage dfMessage : imgDf) {
			ImageBean ib = new ImageBean();
			ib.imgpath = dfMessage.getContent();
			beans.add(ib);
		}
		Intent i = new Intent(this, ImageBrowserActivity.class);
		MessageList beanlist = new MessageList();
		beanlist.imgList = beans;
		i.putExtra("position", position);
		i.putExtra("entity_profile", beanlist);
		startActivity(i);
	}

	class fileDownload extends AjaxCallBack<File> {
		DfMessage msg;

		public fileDownload(DfMessage msg) {
			this.msg = msg;
		}

		public void onStart() {
			File file = new File(StaticFactory.APKCardPathChat);
			if (!file.exists()) {
				file.mkdirs();
			}
			msg.setDownload(SocketManage.D_downloading);
			ac.finalDb.update(msg);
			adapter.notifyDataSetChanged();
		};

		public void onSuccess(File t) {
			msg.setDownload(SocketManage.D_downloaded);
			msg.setContent(t.getPath());
			ac.finalDb.update(msg);
			adapter.notifyDataSetChanged();
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			msg.setDownload(SocketManage.D_destroy);
			ac.finalDb.update(msg);
			adapter.notifyDataSetChanged();
		};
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float range = event.values[0];
		if (range >= mSensor.getMaximumRange()) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
		} else {
			audioManager.setMode(AudioManager.MODE_IN_CALL);
		}
	};
}
