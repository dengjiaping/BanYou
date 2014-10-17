package com.quanliren.quan_one.util;

import android.content.Context;
import android.widget.Toast;

public class Log {
	public static final boolean DEBUG = true;
    
    public static void toast(Context context,String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
    
    public static void v(String tag,String msg){
        if(DEBUG){
            android.util.Log.v(tag, msg);
        }
    }
     
    public static void d(String tag,String msg){
        if(DEBUG){
        	android.util.Log.d(tag, msg);
        }
    }
    
    public static void i(String tag,String msg){
        if(DEBUG){
        	android.util.Log.i(tag, msg);
        }
    }
    
    public static void w(String tag,String msg){
        if(DEBUG){
        	android.util.Log.w(tag, msg);
        }
    }
    
    public static void e(String tag,String msg){
        if(DEBUG){
        	android.util.Log.e(tag, msg);
        }
    }    
}
