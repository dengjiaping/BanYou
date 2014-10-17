package com.quanliren.quan_one.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import com.google.gson.Gson;
import com.quanliren.quan_one.activity.Noti;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.message.MyLeaveMessageFragment;
import com.quanliren.quan_one.service.QuanPushService.ConnectionThread;
import com.quanliren.quan_one.util.BroadcastUtil;

public class ClientHandlerWord {
	public static final String TAG = "ClientHandlerWord";

	AppClass ac;
	Context c;
	NotificationManager nm;
	Notification noti;
	Uri alert;
	MediaPlayer player;
	AudioManager audioManager;
	User user;
	long time=0;

	public ClientHandlerWord(Context context) {
		ac = (AppClass) context.getApplicationContext();
		this.c = context;
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		noti = new Notification();
	}

	public void sessionConnected(ConnectionThread session) {
		try {
			JSONObject jo = new JSONObject();
			jo.put(SocketManage.ORDER, SocketManage.ORDER_CONNECT);
			jo.put(SocketManage.TOKEN, ac.getUser().getToken());
			jo.put(SocketManage.DEVICE_TYPE, "0");
			jo.put(SocketManage.DEVICE_ID, ac.cs.getDeviceId());
			session.write(jo.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void messageReceived(ConnectionThread session, Object message)
			throws Exception {
		Log.i(TAG, message.toString());
		user = ac.getUserInfo();
		JSONObject jo = new JSONObject(message.toString());
		String order = jo.getString(SocketManage.ORDER);
		if (order.equals(SocketManage.ORDER_SENDMESSAGE)) {
			getMessage(session, jo);
		}else if(order.equals(SocketManage.ORDER_SENDED)){
			sended(jo);
		}else if(order.equals(SocketManage.ORDER_OUTLINE)){
			Intent i = new Intent(BroadcastUtil.ACTION_OUTLINE);
			c.sendBroadcast(i);
		}else if(order.equals(SocketManage.ORDER_EXCHANGE)){
			exchange(jo);
		}
	}
	
	public void exchange(JSONObject jo){
		try {
			String msgid=jo.getString(SocketManage.MESSAGE_ID);
			List<DfMessage> list= ac.finalDb.findAllByWhere(DfMessage.class, "msgid='"+msgid+"'");
			if(list.size()>0){
				DfMessage m=list.get(0);
				m.setDownload(SocketManage.D_downloaded);
				ac.finalDb.update(m);
				Intent i = new Intent(ChatActivity.CHANGESEND);
				i.putExtra("bean", m);
				c.sendBroadcast(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void sended(JSONObject jo){
		try {
			String msgid=jo.getString(SocketManage.MESSAGE_ID);
			List<DfMessage> list= ac.finalDb.findAllByWhere(DfMessage.class, "msgid='"+msgid+"'");
			if(list.size()>0){
				DfMessage m=list.get(0);
				m.setDownload(SocketManage.D_downloaded);
				ac.finalDb.update(m);
				Intent i = new Intent(ChatActivity.CHANGESEND);
				i.putExtra("bean", m);
				c.sendBroadcast(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void getMessage(ConnectionThread session, JSONObject jo) {
		final DfMessage defMessage = new Gson().fromJson(
				jo.opt(SocketManage.MESSAGE).toString(), DfMessage.class);

		try {
			if (jo.opt(SocketManage.MESSAGE_ID) != null) {
				JSONObject jos = new JSONObject();
				jos.put(SocketManage.ORDER, SocketManage.ORDER_SENDED);
				jos.put(SocketManage.MESSAGE_ID, jo.opt(SocketManage.MESSAGE_ID));
				session.write(jos.toString());
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (ac.cs.getMSGOPEN() != 1) {
			return;
		}
		if(defMessage.getMsgtype()>0)
			defMessage.setDownload(SocketManage.D_nodownload);
		else
			defMessage.setDownload(SocketManage.D_downloaded);
		defMessage.setUserid(user.getId());

		ac.finalDb.saveBindId(defMessage);
		ac.finalDb.deleteByWhere(ChatListBean.class, "userid='" + user.getId()
				+ "' and friendid='" + defMessage.getSendUid() + "'");
		ChatListBean cb = new ChatListBean(user, defMessage);
		ac.finalDb.saveBindId(cb);
		
		Intent broad=new Intent(ChatActivity.ADDMSG);
		broad.putExtra("bean", defMessage);
		c.sendBroadcast(broad);
		
		broad=new Intent(MyLeaveMessageFragment.ADDMSG);
		broad.putExtra("bean", cb);
		c.sendBroadcast(broad);
		

		String content = null;
		if (defMessage.getMsgtype() == -2) {
			content = "[位置]";
		} else if (defMessage.getMsgtype() == 0) {
			content = defMessage.getContent();
		} else if (defMessage.getMsgtype() == 1) {
			content = "[图片]";
		} else if (defMessage.getMsgtype() == 2) {
			content = "[语音]";
		}

		noti.icon = R.drawable.icon;
		noti.tickerText = content;
		noti.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(c, Noti.class);
		intent.putExtra("activity", ChatActivity.class);
		
		User friend=new User();
		friend.setId(defMessage.getSendUid());
		friend.setNickname(defMessage.getNickname());
		friend.setAvatar(defMessage.getUserlogo());
		intent.putExtra("friend", friend);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("friendid", defMessage.getSendUid());
		PendingIntent contentIntent = PendingIntent.getActivity(c, 1, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		noti.setLatestEventInfo(c, defMessage.getNickname(), content,
				contentIntent);
		nm.notify(0, noti);

		if (ac.cs.getZHENOPEN() == 1) {
			long now = System.currentTimeMillis();
			long timeD = now - time;
			if (0 < timeD && timeD < 1500) {
			}else{
				Vibrator vib = (Vibrator) c
						.getSystemService(Service.VIBRATOR_SERVICE);
				long[] pattern = { 50, 300, 100, 200 };
				vib.vibrate(pattern, -1);
				time = now;
			}
		}
		if (ac.cs.getVIDEOOPEN() == 1&&player==null) {
			try {
				if (alert == null) {
					alert = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					audioManager = (AudioManager) c
							.getSystemService(Context.AUDIO_SERVICE);
				}
				if (audioManager
						.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
					player = new MediaPlayer();
					player.setDataSource(c, alert);
					player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
					player.setLooping(false);
					player.setOnCompletionListener(new OnCompletionListener() {

						public void onCompletion(MediaPlayer mp) {
							mp.stop();
							mp.release();
							player=null;
						}
					});
					player.prepare();
					player.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
