package com.quanliren.quan_one.activity.location;

import java.text.DecimalFormat;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.Area;
import com.quanliren.quan_one.fragment.ChosePositionFragment;

import android.content.Context;

public class Location {
	
	ILocationImpl locationListener;
	LocationClient mLocationClient = null;
	BDLocationListener myListener = new MyLocationListener();
	AppClass ac;
	
	public Location(Context context){
		ac=(AppClass) context.getApplicationContext();
		mLocationClient = new LocationClient(context.getApplicationContext()); // 声明LocationClient类
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		option.setNeedDeviceDirect(false);//返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
	}
	
	public void setLocationListener(ILocationImpl locationListener) {
		this.locationListener = locationListener;
	}
	
	public void startLocation(){
		mLocationClient.start();
		mLocationClient.requestLocation();
	}
	
	public class MyLocationListener implements BDLocationListener {
		public void onReceiveLocation(BDLocation location) {
			mLocationClient.stop();
			if (location == null){
				if(locationListener!=null)
					locationListener.onLocationFail();
				return;
			}
			DecimalFormat df = new DecimalFormat("#.##########");
//			System.out.println(location.getLatitude()+"--------------"+location.getLongitude());
			if(!df.format(location.getLatitude()).equals("0"))ac.cs.setLat(df.format(location.getLatitude()));
			if(!df.format(location.getLongitude()).equals("0"))ac.cs.setLng(df.format(location.getLongitude()));
			if(location.getDistrict()!=null)ac.cs.setArea(location.getDistrict());
			String city=location.getCity();
			if(city!=null){
				List<Area> areaList=ChosePositionFragment.getAreas();
				Area temp=null;
				for (Area area : areaList) {
					if(city.indexOf(area.name)>-1){
						temp=area;
						break;
					}
				}
				if(temp!=null){
					ac.cs.setLocation(city);
					ac.cs.setLocationID(temp.id);
				}
			}
			if(locationListener!=null)
				locationListener.onLocationSuccess();
		}
		@Override
		public void onReceivePoi(BDLocation arg0) {
		}

	}
	
	public void destory(){
		if(mLocationClient!=null){
			mLocationClient.unRegisterLocationListener(myListener);
			mLocationClient.stop();
			mLocationClient=null;
		}
	}
}
