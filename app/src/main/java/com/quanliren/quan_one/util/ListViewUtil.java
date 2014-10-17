package com.quanliren.quan_one.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XXListView;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_one.adapter.ParentsAdapter;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.MessageListBean;

public class ListViewUtil {
	
	public static int callBack(JSONObject jo,AppClass ac,int p,XXListView listview,ParentsAdapter adapter,String CacheKey){
		try {
			JSONObject response=jo.getJSONObject(URL.RESPONSE);
			String list=response.getString(URL.LIST);
			List<MessageListBean> users=new Gson().fromJson(list, new TypeToken<ArrayList<MessageListBean>>(){}.getType());
			if(p==0){
				ac.finalDb.deleteById(CacheBean.class,CacheKey);
				ac.finalDb.save(new CacheBean(CacheKey, list, new Date().getTime()));
				adapter.setList(users);
			}else{
				adapter.addNewsItems(users);
			}
			adapter.notifyDataSetChanged();
			listview.setPage(p=response.getInt(URL.PAGEINDEX));
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return p;
	}
}
