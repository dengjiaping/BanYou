package com.quanliren.quan_one.activity.shop;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.alipay.Keys;
import com.alipay.Result;
import com.alipay.Rsa;
import com.alipay.android.app.sdk.AliPay;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.fragment.SetingMoreFragment;
import com.quanliren.quan_one.util.URL;

public class ShopVipDetail extends BaseActivity {
	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;
	@ViewInject(id = R.id.buybtn, click = "buy")
	Button buyBtn;
	@ViewInject(id = R.id.webview)
	WebView wView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vip_detail);
		setTitleTxt("会员中心");
		wView.getSettings().setJavaScriptEnabled(true);
		wView.loadUrl("file:///android_asset/vip.html");
	}

	public void buy(View view) {
		if (menupop == null) {
			menupop = new PopFactory(this, new String[] { "支付宝安全支付", "储蓄卡支付",
					"信用卡支付" }, menuClick, parent);
		}
		menupop.toogle();

	}

	OnClickListener menuClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case 0:
				startBao();
				break;
			case 1:
				Intent i = new Intent (ShopVipDetail.this,VipCardActivity.class);
				i.putExtra("channelType", "DEBIT_EXPRESS");
				startActivity(i);
				break;
			case 2:
				Intent i1 = new Intent (ShopVipDetail.this,VipCardActivity.class);
				i1.putExtra("channelType", "OPTIMIZED_MOTO");
				startActivity(i1);
				break;
			default:
				break;
			}
			menupop.closeMenu();
		}
	};
	
	public void startBao(){
		ac.finalHttp.post(URL.GETALIPAY, getAjaxParams(),
				new AjaxCallBack<String>() {
					@Override
					public void onStart() {
						customShowDialog("正在生成订单");
					}

					@Override
					public void onSuccess(String t) {
						customDismissDialog();
						try {
							JSONObject jo = new JSONObject(t);
							int status = jo.getInt(URL.STATUS);
							switch (status) {
							case 0:
								buy(t);
								break;
							default:
								showFailInfo(jo);
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
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
					AliPay alipay = new AliPay(ShopVipDetail.this, mHandler);
					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);

					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(ShopVipDetail.this, "remote_call_failed",
					Toast.LENGTH_SHORT).show();
		}
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Result result = new Result((String) msg.obj);
			switch (msg.what) {
			case RQF_PAY:
				if (result.getResultStatus().equals("9000")) {
					showCustomToast("购买成功");
					User user = ac.getUserInfo();
					user.setIsvip(1);
					user.setVipday(vipday);
					user.setViptime(viptime);
					user.setPowernum((Integer.valueOf(user.getPowernum()) + powernum)
							+ "");
					UserTable ut = new UserTable(user);
					ac.finalDb.delete(ut);
					ac.finalDb.save(ut);
					
					Intent i= new Intent(SetingMoreFragment.UPDATE_USERINFO);
					sendBroadcast(i);
					
				} else if (result.getResultStatus().equals("6001")) {
//					showCustomToast("取消购买");
				} else {
					showCustomToast("购买失败");
				}
				break;
			case RQF_LOGIN: {
				Toast.makeText(ShopVipDetail.this, result.getResult(),
						Toast.LENGTH_SHORT).show();
			}
				break;
			default:
				break;
			}
		};
	};

	String price;
	int powernum;
	String viptime;
	String vipday;

	private String getNewOrderInfo(String t) {
		String no = "";
		String url = "";
		try {
			JSONObject jo = new JSONObject(t);
			no = jo.getJSONObject(URL.RESPONSE).getString("order_no");
			url = jo.getJSONObject(URL.RESPONSE).getString("notify_url");
			price = jo.getJSONObject(URL.RESPONSE).getString("price");
			powernum = jo.getJSONObject(URL.RESPONSE).getInt("powernum");
			viptime = jo.getJSONObject(URL.RESPONSE).getString("viptime");
			vipday = jo.getJSONObject(URL.RESPONSE).getString("vipday");
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
		sb.append("伴游年会员");
		sb.append("\"&body=\"");
		sb.append("伴游年会员");
		sb.append("\"&total_fee=\"");
		sb.append(price);
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

}
