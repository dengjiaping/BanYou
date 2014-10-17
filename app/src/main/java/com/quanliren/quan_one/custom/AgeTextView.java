package com.quanliren.quan_one.custom;

import java.util.Calendar;
import java.util.Locale;

import com.quanliren.quan_one.activity.user.UserInfoEditActivity;
import com.quanliren.quan_one.util.Util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class AgeTextView extends TextView{
	
	Context context;
	
	public AgeTextView(Context context) {
		super(context);
		this.context=context;
		init();
	}
	
	public AgeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		init();
	}
	
	public void init(){
		setOnClickListener(ageClick);
	}
	
	OnClickListener ageClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Calendar cal=Calendar.getInstance();
			new DatePickerDialog(context, d, (cal.get(Calendar.YEAR)),
					cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
		}
	};
	
	Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			
			
			Calendar cal = Calendar.getInstance();
			if (cal.getTime().before(dateAndTime.getTime())) {
				dateAndTime.set(Calendar.YEAR, cal.get(Calendar.YEAR));
				dateAndTime.set(Calendar.MONTH, cal.get(Calendar.MONTH));
				dateAndTime.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
			}

			updateLabel();
		}
	};

	private void updateLabel() {
		try {
			String age = Util.fmtDate.format(dateAndTime.getTime());
			setText(age);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
