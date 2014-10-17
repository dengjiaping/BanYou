package com.quanliren.quan_one.custom.emoji;

import java.util.regex.Matcher;
import java.util.regex.Pattern;







import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.custom.HandyTextView;
import com.quanliren.quan_one.util.BitmapCache;
import com.quanliren.quan_one.util.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

public class EmoticonsTextView extends HandyTextView {

	public EmoticonsTextView(Context context) {
		super(context);
	}

	public EmoticonsTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EmoticonsTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		if (!TextUtils.isEmpty(text)) {
			super.setText(replace(text), type);
		} else {
			super.setText(text, type);
		}
	}

	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder(
				AppClass.mEmoticons.size() * 3);
		patternString.append('(');
		for (int i = 0; i < AppClass.mEmoticons.size(); i++) {
			String s = AppClass.mEmoticons.get(i);
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");
		return Pattern.compile(patternString.toString());
	}

	private CharSequence replace(CharSequence text) {
		try {
			SpannableStringBuilder builder = new SpannableStringBuilder(text);
			Pattern pattern = buildPattern();
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				if (AppClass.mEmoticonsId.containsKey(matcher.group())) {
					int id = AppClass.mEmoticonsId.get(matcher.group());
					Bitmap bitmap = BitmapCache.getInstance().getBitmap(id, getContext(),true);
//					int width = bitmap.getWidth();
//					int height = bitmap.getHeight();
//					// 设置想要的大小
//					int newWidth = ImageUtil.dip2px(getContext(), 14);
//					int newHeight =  ImageUtil.dip2px(getContext(), 14);
//					// 计算缩放比例
//					float scaleWidth = ((float) newWidth) / width;
//					float scaleHeight = ((float) newHeight) / height;
//					// 取得想要缩放的matrix参数
//					Matrix matrix = new Matrix();
//					matrix.postScale(scaleWidth, scaleHeight);
//					
//					bitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
					if (bitmap != null) {
						ImageSpan span = new ImageSpan(getContext(), bitmap,ImageSpan.ALIGN_BASELINE);
						builder.setSpan(span, matcher.start(), matcher.end(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
			return builder;
		} catch (Exception e) {
			return text;
		}
	}
}
