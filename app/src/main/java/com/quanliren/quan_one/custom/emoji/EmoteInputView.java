package com.quanliren.quan_one.custom.emoji;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.EmoteAdapter;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.util.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class EmoteInputView extends LinearLayout implements OnClickListener,
		OnCheckedChangeListener, OnItemClickListener {

	private GridView mGvDisplay;
	private RadioGroup mRgInner;
	private ImageView mIvDelete;

	private EmoteAdapter mDefaultAdapter;
	private EmoteAdapter mEmojiAdapter;

	private EmoticonsEditText mEEtView;

	private boolean mIsSelectedDefault;

	public EmoteInputView(Context context) {
		super(context);
		init();
	}

	@SuppressLint("NewApi")
	public EmoteInputView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public EmoteInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		inflate(getContext(), R.layout.common_emotionbar, this);
		mGvDisplay = (GridView) findViewById(R.id.emotionbar_gv_display);
		mRgInner = (RadioGroup) findViewById(R.id.emotionbar_rg_inner);
		mIvDelete = (ImageView) findViewById(R.id.emotionbar_iv_delete);

		mGvDisplay.setOnItemClickListener(this);
		mRgInner.setOnCheckedChangeListener(this);
		mIvDelete.setOnClickListener(this);

		mDefaultAdapter = new EmoteAdapter(getContext(),
				AppClass.mEmoticons_Zem);
		mEmojiAdapter = new EmoteAdapter(getContext(),
				AppClass.mEmoticons_Zemoji);
		mGvDisplay.setAdapter(mEmojiAdapter);
		mIsSelectedDefault = false;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
//		case R.id.emotionbar_rb_default:
//			mGvDisplay.setAdapter(mDefaultAdapter);
//			mIsSelectedDefault = true;
//			break;

		case R.id.emotionbar_rb_emoji:
			mGvDisplay.setAdapter(mEmojiAdapter);
			mIsSelectedDefault = false;
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.emotionbar_iv_delete:
			if (mEEtView != null) {
				int start = mEEtView.getSelectionStart();
				String content = mEEtView.getText().toString();
				if (TextUtils.isEmpty(content)) {
					return;
				}
				String startContent = content.substring(0, start);
				String endContent = content.substring(start, content.length());
				String lastContent = content.substring(start - 1, start);
				int last = startContent.lastIndexOf("[zem");
				int lastChar = startContent.substring(0,
						startContent.length() - 1).lastIndexOf("]");

				if ("]".equals(lastContent) && last > lastChar) {
					if (last != -1) {
						mEEtView.setText(startContent.substring(0, last)
								+ endContent);
						// 定位光标位置
						CharSequence info = mEEtView.getText();
						if (info instanceof Spannable) {
							Spannable spanText = (Spannable) info;
							Selection.setSelection(spanText, last);
						}
						return;
					}
				}
				mEEtView.setText(startContent.substring(0, start - 1)
						+ endContent);
				// 定位光标位置
				CharSequence info = mEEtView.getText();
				if (info instanceof Spannable) {
					Spannable spanText = (Spannable) info;
					Selection.setSelection(spanText, start - 1);
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String text = null;
		if (mIsSelectedDefault) {
			text = AppClass.mEmoticons_Zem.get(arg2);
		} else {
			text = AppClass.mEmoticons_Zemoji.get(arg2);
		}
		
		
		int nums=(int)(Util.getLengthString(mEEtView.getText().toString()+text)/2);
		if(Util.getLengthString(mEEtView.getText().toString()+text)%2>0){
			nums++;
		}
		if(nums>mEEtView.max_nickname_length){
			return;
		}
		
		if (mEEtView != null && !TextUtils.isEmpty(text)) {
			int start = mEEtView.getSelectionStart();
			CharSequence content = mEEtView.getText().insert(start, text);
			mEEtView.setText(content);
			// 定位光标位置
			CharSequence info = mEEtView.getText();
			if (info instanceof Spannable) {
				Spannable spanText = (Spannable) info;
				try {
					Selection.setSelection(spanText, start + text.length());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void setEditText(EmoticonsEditText editText) {
		mEEtView = editText;
	}
}
