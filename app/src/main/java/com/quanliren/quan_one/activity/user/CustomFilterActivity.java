package com.quanliren.quan_one.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.adapter.CustomFilterAdapter;
import com.quanliren.quan_one.bean.CustomFilterBean;

public class CustomFilterActivity extends BaseActivity {

	@ViewInject(id = R.id.list)
	ListView listview;
	@ViewInject(id=R.id.start,click="start")Button start;
	private static final String[] sex = { "全部", "女", "男" };
	private String[] height = { "全部", "100cm以下", "100cm~120cm", "120cm~140cm",
			"140cm~160cm", "160cm~180cm", "180cm~200cm", "200cm以上" };
	private String[] age = { "全部", "18岁以下", "18岁~20岁", "20岁~30岁", "30岁~40岁",
			"40岁~50岁", "50岁以上" };
	private static final String[] xingzuo = { "全部", "水瓶座", "双鱼座", "白羊座", "金牛座",
			"双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };
	public static final String[] job = { "全部", "白领", "主管/经理", "教授/教师",
			"技术/科学/工程", "服务人员", "行政管理/秘书", "销售/市场", "艺术/音乐/作家", "自由职业/自雇",
			"演员/歌星", "学生", "失业", "离/退休", "主妇", "普通职员", "其他" };
	private CustomFilterAdapter adapter;
	List<String[]> strList = new ArrayList<String[]>();

	public static List<String> keys() {
		List<String> strs = new ArrayList<String>();
		strs.add("sex");
		strs.add("age");
		strs.add("height");
		strs.add("constellation");
		strs.add("job");
		return strs;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_filter);
		setTitleTxt("自定义");

		List<CustomFilterBean> cb = new ArrayList<CustomFilterBean>();

		cb.add(new CustomFilterBean("性别：", sex[0], "sex", -1));
		cb.add(new CustomFilterBean("年龄：", height[0], "age", -1));
		cb.add(new CustomFilterBean("身高：", age[0], "height", -1));
		cb.add(new CustomFilterBean("星座：", xingzuo[0], "constellation", -1));
		cb.add(new CustomFilterBean("职业：", job[0], "job", -1));

		strList.add(sex);
		strList.add(age);
		strList.add(height);
		strList.add(xingzuo);
		strList.add(job);

		adapter = new CustomFilterAdapter(this, cb);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(itemClick);

		for (String key : keys()) {
			CustomFilterBean dfb = ac.finalDb.findById(key,
					CustomFilterBean.class);
			if (dfb != null) {
				for (CustomFilterBean cb1 : cb) {
					if (cb1.key.equals(key)) {
						cb1.id = dfb.id;
						cb1.defaultValue = dfb.defaultValue;
					}
				}
			}
		}

		adapter.notifyDataSetChanged();
	}

	OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, final int position,
				long arg3) {
			final CustomFilterBean cfbp=(CustomFilterBean) adapter.getItem(position);
			final String[] items = strList.get(position);
			new AlertDialog.Builder(CustomFilterActivity.this)
					.setTitle("选择")
					.setSingleChoiceItems(items, (cfbp.id+1),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									CustomFilterBean cfb=(CustomFilterBean) adapter.getItem(position);
									cfb.setDefaultValue(items[item]);
									cfb.setId(item-1);
									adapter.notifyDataSetChanged();
									dialog.cancel();
								}
							}).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							}).show();
		}
	};
	
	public void start(View v){
		List<CustomFilterBean> list=adapter.getList();
		for (CustomFilterBean customFilterBean : list) {
			ac.finalDb.deleteById(CustomFilterBean.class, customFilterBean.key);
			if(customFilterBean.id>-1){
				ac.finalDb.save(customFilterBean);
			}
		}
		setResult(1);
		finish();
	}
}
