package com.quanliren.quan_one.fragment;/*package com.quanliren.quan_one.fragment;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.Keys;
import com.alipay.Result;
import com.alipay.Rsa;
import com.alipay.android.app.sdk.AliPay;
import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.URL;

public class ShopFragment extends MenuFragmentBase {

	private static final String TAG = "ShopFragment";
	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;

	// @ViewInject(id = R.id.tab1)
	// TextView tab1;
	// @ViewInject(id = R.id.tab2)
	// TextView tab2;
	//
	// @ViewInject(id = R.id.tab_icon)
	// ImageView tab_icon;
	// @ViewInject(id = R.id.viewpager)
	// ViewPager viewPager;
	// PageAdapter adapter;
	// List<Fragment> views;
	// List<TextView> tabs;
	// int offset;
	// int currentIndex;
	@ViewInject(id = R.id.name)
	TextView name;
	@ViewInject(id = R.id.buy, click = "buy")
	Button buy;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.shop, null);
		FinalActivity.initInjectedView(this, v);
		setTitleTxt(R.string.shop);
		initView();
		return v;
	}

	public void buy(View view) {
		ac.finalHttp.post(URL.GETALIPAY, getAjaxParams(),
				new AjaxCallBack<String>() {
					@Override
					public void onStart() {
						customShowDialog("正在生成订单");
					}

					@Override
					public void onSuccess(String t) {
						customDismissDialog();
						buy(t);
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						customDismissDialog();
						showIntentErrorToast();
					}
				});

	}

	public void buy(String t) {
		try {
			String info = getNewOrderInfo(t);
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();

			final String orderInfo = info;
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(getActivity(), mHandler);

					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);

					Log.i(TAG, "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(getActivity(), "remote_call_failed",
					Toast.LENGTH_SHORT).show();
		}
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);
			switch (msg.what) {
			case RQF_PAY:
				showCustomToast(result.getResultStatus());
				break;
			case RQF_LOGIN: {
				Toast.makeText(getActivity(), result.getResult(),
						Toast.LENGTH_SHORT).show();

			}
				break;
			default:
				break;
			}
		};
	};

	private String getNewOrderInfo(String t) {
		String no = "";
		String url = "";
		try {
			JSONObject jo = new JSONObject(t);
			no = jo.getJSONObject(URL.RESPONSE).getString("order_no");
			url = jo.getJSONObject(URL.RESPONSE).getString("notify_url");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");

		sb.append(no);

		sb.append("\"&subject=\"");
		sb.append("小白");
		sb.append("\"&body=\"");
		sb.append("小白的姐姐");
		sb.append("\"&total_fee=\"");
		sb.append("0.01");
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode(url));

		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String key = format.format(date);

		java.util.Random r = new java.util.Random();
		key += r.nextInt();
		key = key.substring(0, 15);
		Log.d(TAG, "outTradeNo: " + key);
		return key;
	}

	public void initView() {
		Shader shader = new LinearGradient(0, 0, 0, ImageUtil.dip2px(
				getActivity(), 25), Color.WHITE, getResources().getColor(
				R.color.gold), TileMode.CLAMP);
		name.getPaint().setShader(shader);
		// getChildFragmentManager().beginTransaction().replace(R.id.content,
		// new VipFragment()).commit();
		
		 * int width = getResources().getDisplayMetrics().widthPixels; offset =
		 * (int) ((float) width / 2); RelativeLayout.LayoutParams iconlp = new
		 * RelativeLayout.LayoutParams( offset,
		 * RelativeLayout.LayoutParams.WRAP_CONTENT);
		 * iconlp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.tab_ll);
		 * tab_icon.setLayoutParams(iconlp); tabs = new ArrayList<TextView>();
		 * tabs.add(tab2); tabs.add(tab1);
		 * 
		 * views = new ArrayList<Fragment>(); views.add(new PowerFragment());
		 * views.add(new VipFragment()); adapter = new
		 * PageAdapter(getChildFragmentManager());
		 * 
		 * viewPager.setAdapter(adapter);
		 * viewPager.setOnPageChangeListener(changeListener);
		 * 
		 * for (final TextView tv : tabs) { tv.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * viewPager.setCurrentItem(tabs.indexOf(tv)); } }); }
		 
	}

	
	 * class PageAdapter extends FragmentPagerAdapter {
	 * 
	 * public PageAdapter(FragmentManager fm) { super(fm); }
	 * 
	 * @Override public Fragment getItem(int position) { return
	 * views.get(position); }
	 * 
	 * @Override public int getCount() { return views.size(); }
	 * 
	 * }
	 * 
	 * OnPageChangeListener changeListener = new OnPageChangeListener() {
	 * 
	 * @Override public void onPageSelected(int position) {
	 * ViewPropertyAnimator.animate(tab_icon).translationX(offset * position)
	 * .setDuration(200); }
	 * 
	 * @Override public void onPageScrolled(int position, float positionOffset,
	 * int positionOffsetPixels) { }
	 * 
	 * @Override public void onPageScrollStateChanged(int state) { } };
	 
}
*/