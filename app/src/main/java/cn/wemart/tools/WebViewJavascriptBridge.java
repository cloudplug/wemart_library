package cn.wemart.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import cn.wemart.pojo.AddressResponse2;
import cn.wemart.pojo.BaseResponse;
import cn.wemart.pojo.BuyOperateEntity;
import cn.wemart.pojo.BuyerEntity;
import cn.wemart.pojo.DefaultAddress;
import cn.wemart.pojo.DefaultAddressResponse;
import cn.wemart.pojo.MenuEntity;
import cn.wemart.pojo.OrderConfirmResponse1;
import cn.wemart.sdk.R;
import cn.wemart.sdk.activity.AddressEditActivity;
import cn.wemart.sdk.activity.MallActivity;
import cn.wemart.sdk.activity.OrderConfirmActivity;


public class WebViewJavascriptBridge {
	private  MallActivity mActivity;
	private Gson gson = new Gson();

	

	
	public WebViewJavascriptBridge(MallActivity activity) {
		this.mActivity = activity;
	}
	
	Handler handler= new Handler(){

		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 1: // 设置头部
				JSONObject entity =(JSONObject) msg.obj;
				try {
					mActivity.setHeader(entity.getString("head"), 
							entity.getString("headBgColor"), entity.getString("leftIcon"),
							entity.getString("rightIcon"), entity.getString("headtextcolor"),
							entity.getString("headheight"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			default:
				break;
			}
			
		};
	};

	/**
	 * 得到前端Javascript通知数据
	 * 
	 * @param handlerName
	 *            事件名称
	 * @param data
	 *            Json数据格式
	 */
	@JavascriptInterface
	public void callHandler(String handlerName, String data) {
		if (!TextUtils.isEmpty(handlerName)) {
			try {
				if ("shareWebPage".equals(handlerName)) {
					// 设置分享内容
					JSONObject entity = new JSONObject(data);
					if (entity.getString("title") != null) {
						this.mActivity.setShareContent(entity);
					}

				} else if ("setHeader".equals(handlerName)) {
					JSONObject entity = new JSONObject(data);
					
					Message msg = new Message();
					msg.obj = entity;
					msg.arg1 = 1;
					handler.sendMessage(msg);
					
//					// 设置头部
//					JSONObject entity = new JSONObject(data);
//					if (entity.isNull("headtextSize")) {
//						this.mActivity.setHeader(entity.getString("headtext"), entity.getString("headBgColor"),
//								entity.getString("leftIcon"), entity.getString("rightIcon"),
//								entity.getString("headtextcolor"), entity.getString("headheight"));
//					} else {
//						this.mActivity.setHeader(entity.getString("headtext"), entity.getString("headtextSize"),
//								entity.getString("headBgColor"), entity.getString("leftIcon"),
//								entity.getString("rightIcon"), entity.getString("headtextcolor"),
//								entity.getString("headheight"));
//					}
				} else if ("createMenu".equals(handlerName)) {
					// 设置底部菜单
					MenuEntity menuEntity = gson.fromJson(data, MenuEntity.class);
					mActivity.createMenu(menuEntity);
				} else if ("submitCarts".equals(handlerName)) {
					// 进入地址
					BuyOperateEntity entity = gson.fromJson(data, BuyOperateEntity.class);
					// 1表示需要新增；2 确认订单

					if (entity != null && "1".equals(entity.addressOperate)) {
						// 新增
						Bundle bundle = new Bundle();
						bundle.putString("title", "新增收货地址");
						Intent intent = new Intent(this.mActivity, AddressEditActivity.class);
						intent.putExtra("buyOperate", (Serializable) entity);
						intent.putExtras(bundle);
						this.mActivity.startActivity(intent);
					} else if (entity != null && "2".equals(entity.addressOperate)) {
						// 确认订单
						BuyerEntity buyer = entity.buyer;
						JSONObject oj = new JSONObject();
						oj.put("buyerId", buyer.buyerId);
						oj.put("scenType", buyer.scenType);
						oj.put("scenId", buyer.scenId);
						oj.put("sign", buyer.sign);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("para", oj.toString()));
						HttpHelper helper = new HttpHelper();
						String result = helper.executePost("http://uat.wemart.cn/api/shopping/buyer", params);
						BaseResponse entity2 = gson.fromJson(result, BaseResponse.class);
						if (entity != null && entity2.returnValue == 0) {
							params.clear();
							oj = new JSONObject();
							oj.put("isDefault", true);
							params.add(new BasicNameValuePair("para", oj.toString()));
							result = helper.executeGet("http://uat.wemart.cn/api/usermng/buyer/address", params);
							DefaultAddressResponse entity1 = gson.fromJson(result, DefaultAddressResponse.class);
							Log.e("entity1.returnValue", entity1.returnValue + "");
							if (entity != null && entity1.returnValue == 0) {
								DefaultAddress tempEntity = entity1.data[0];
								AddressResponse2 defalutAddress = new AddressResponse2();
								defalutAddress.addrNo = tempEntity.addrNo;
								defalutAddress.city = tempEntity.city;
								defalutAddress.cityNo = tempEntity.cityNo;
								defalutAddress.district = tempEntity.district;
								defalutAddress.streetAddr = tempEntity.street;
								defalutAddress.street = tempEntity.street;
								defalutAddress.isDefault = true;
								defalutAddress.mobileNo = tempEntity.mobileNo;
								defalutAddress.name = tempEntity.name;
								defalutAddress.province = tempEntity.province;
								Intent intent = new Intent(this.mActivity, OrderConfirmActivity.class);
								intent.putExtra("buyOperate", (Serializable) entity);
								intent.putExtra("newAddress", defalutAddress);
								this.mActivity.startActivity(intent);
							}
						}

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
