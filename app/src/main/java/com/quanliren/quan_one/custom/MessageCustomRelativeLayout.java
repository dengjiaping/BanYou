package com.quanliren.quan_one.custom;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MessageCustomRelativeLayout extends RelativeLayout {
	LinearLayout.LayoutParams lpt;

	public MessageCustomRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		lpt = new LinearLayout.LayoutParams(
				getResources().getDisplayMetrics().widthPixels,
				LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	public AtomicBoolean init = new AtomicBoolean(false);

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (init.compareAndSet(false, true))
			setLayoutParams(lpt);
	};
}