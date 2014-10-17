package com.quanliren.quan_one.activity.seting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

public class AboutUsActivity extends BaseActivity{

	@ViewInject(id=R.id.phone1,click="callPhone")TextView phone1;
	@ViewInject(id=R.id.phone2,click="callPhone")TextView phone2;
	@ViewInject(id=R.id.email,click="sendMailByIntent")TextView email;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutus);
		setTitleTxt(R.string.seting_about_us);
	}
	
	public void callPhone(View v){
		String mobile=((TextView)v).getText().toString();
		Intent intent = new Intent(
				Intent.ACTION_DIAL, Uri
						.parse("tel:"
								+ mobile));
		startActivity(intent);
	}
	
	public void sendMailByIntent(View v) {  
        String[] reciver = new String[] { ((TextView)v).getText().toString() };  
        String[] mySbuject = new String[] { "" };  
        String myCc = "cc";  
        String mybody = "";  
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("plain/text");  
        myIntent.putExtra(Intent.EXTRA_EMAIL, reciver);
        myIntent.putExtra(Intent.EXTRA_CC, myCc);
        myIntent.putExtra(Intent.EXTRA_SUBJECT, mySbuject);
        myIntent.putExtra(Intent.EXTRA_TEXT, mybody);
        startActivity(Intent.createChooser(myIntent, "mail test"));  
    }  
}
