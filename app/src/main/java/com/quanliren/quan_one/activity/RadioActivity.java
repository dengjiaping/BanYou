package com.quanliren.quan_one.activity;

import java.util.Date;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.radio.AmrEncodSender;
import com.quanliren.quan_one.radio.AmrEngine;
import com.quanliren.quan_one.radio.MicRealTimeListener;
import com.quanliren.quan_one.util.StaticFactory;

public class RadioActivity extends BaseActivity {

	@ViewInject(id = R.id.start, click = "start")
	Button start;
	@ViewInject(id = R.id.stop, click = "stop")
	Button stop;
	@ViewInject(id = R.id.play, click = "play")
	Button play;
	@ViewInject(id = R.id.stop1, click = "stop1")
	Button stop1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.radio);
	}

	String fileName = "";
	private double voiceValue = 0.0; // 麦克风获取的音量值

	public void start(View v) {
		AmrEncodSender sender = new AmrEncodSender(StaticFactory.APKCardPath
				+ (fileName = String.valueOf((String.valueOf(new Date()
						.getTime()) + ".amr").hashCode())),
				new MicRealTimeListener() {

					@Override
					public void getMicRealTimeSize(double size, long time) {
						voiceValue = size;
					}
				});

		AmrEngine.getSingleEngine().startRecording();
		new Thread(sender).start();
	}

	public void stop(View v) {
		if (AmrEngine.getSingleEngine().isRecordRunning()) {
			AmrEngine.getSingleEngine().stopRecording();
			voiceValue = 0.0;
		}
	}

	public void play(View v) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		try {
			// 模拟器里播放传url，真机播放传getAmrPath()
			mediaPlayer.setDataSource(StaticFactory.APKCardPath+fileName);
			mediaPlayer.prepare();
			mediaPlayer.start();
			// 设置播放结束时监听
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
				}
			});
		} catch (Exception e) {
		}
	}

	public void stop1(View v) {

	}
}
