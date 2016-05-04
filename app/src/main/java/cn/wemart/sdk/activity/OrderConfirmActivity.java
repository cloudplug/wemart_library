package cn.wemart.sdk.activity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.wemart.adapter.OrderConfirmAdapter;
import cn.wemart.app.App;
import cn.wemart.pojo.AddressResponse2;
import cn.wemart.pojo.BaseResponse;
import cn.wemart.pojo.BuyOperateEntity;
import cn.wemart.pojo.BuyerEntity;
import cn.wemart.pojo.GoodEntity;
import cn.wemart.pojo.OrderConfirmResponse1;
import cn.wemart.pojo.OrderConfirmSubResponse21;
import cn.wemart.pojo.OrderCreateResponse;
import cn.wemart.pojo.PayResult;
import cn.wemart.sdk.R;
import cn.wemart.tools.HttpHelper;
import cn.wemart.tools.VolleyLoadPicture;
import cn.wemart.widget.ListViewForScrollView;

public class OrderConfirmActivity extends BaseActivity implements OnClickListener {
	/** 确认订单 适配器 */
	private OrderConfirmAdapter mAdapter;
	/** 商品订单的列表控件 */
	private ListViewForScrollView lv_order;
	// 微信和支付宝被选中时候的提示控件
	private ImageView img_alipay, img_wx;
	/** 立即支付和总金额 */
	private TextView tv_pay, tv_Allprice;
	/** 名字 电话 和地址 */
	private TextView tv_name, tv_phone, tv_address;
	// private boolean isClick = false;
	// private IWXAPI api;
	private Gson gson = new Gson();
	public static final int SDK_PAY_FLAG = 1;
	/** 确认订单 返回的实体 */
	private OrderConfirmResponse1 list = new OrderConfirmResponse1();
	/** 商品实体 */
	private GoodEntity[] mDatas;
	// 三个可点击跳转的ViewGroup
	private RelativeLayout dizi, rl_wechat, rl_alipay;
	/** 用户购买的实体 */
	private BuyOperateEntity buyOperate = null;
	/** 用户实体 */
	private BuyerEntity buyer = null;
	/** 地址实体 */
	private AddressResponse2 newAddress = null;
	// private CreateGoodEntity[] createMDatas;
	/** 操作钱数的工具类 */
	private DecimalFormat moneyFormat = new DecimalFormat("0.00");
	/** 确认订单 子实体 */
	private OrderConfirmSubResponse21[] mList = null;
	// private OrderConfirmSubResponse1 checkGoodsIsSale;// 检查商品集合
	/** 确认订单 子实体的集合 */
	private List<OrderConfirmSubResponse21> mDatas2 = new ArrayList<OrderConfirmSubResponse21>();
	/** 地址的编号 */
	private int maddrNo;

	private Handler handler = new Handler(new Callback() {

		@SuppressLint("ShowToast")
		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.arg1) {
			case 1:
				list = (OrderConfirmResponse1) msg.obj;
				tv_Allprice.setText(moneyFormat.format(Double.parseDouble(list.data.totalMoney + "") / 100) + "");// 总价
				mAdapter.notifyDataSetChanged();
				break;
			case 2:
				StringBuilder errmsg = (StringBuilder) msg.obj;
				Toast.makeText(getApplicationContext(), errmsg, 0).show();
				tv_pay.setBackgroundColor(R.drawable.wemart_button_order_pay_gray);
				break;
			default:
				
				list = (OrderConfirmResponse1) msg.obj;
				tv_Allprice.setText(moneyFormat.format(Double.parseDouble(list.data.totalMoney + "") / 100) + "");// 总价
				mAdapter.notifyDataSetChanged();
				
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.wemart_ac_order_confirm);
//		setHead(HeadType.NORMAL, "订单确认", R.drawable.wemart_back, "", this);
		
		setHeader();
		
		
		// mList = new OrderConfirmSubResponse21[0];
		// 取得传值
		buyOperate = (BuyOperateEntity) getIntent().getExtras().getSerializable("buyOperate");
		buyer = buyOperate.buyer;
		newAddress = (AddressResponse2) getIntent().getExtras().getSerializable("newAddress");
		maddrNo = newAddress.addrNo;
		mDatas = buyOperate.goodsList;
		templatedMethod();
		mAdapter = new OrderConfirmAdapter(OrderConfirmActivity.this, mDatas2, buyer, newAddress);
		lv_order.setAdapter(mAdapter);

		// 微信支付
		// api = WXAPIFactory.createWXAPI(this, "wxc8f6b243eb4f43bf");
		Data();
		// createOrder();
	}

	
	private void setHeader() {
		String headBgColor  =	App.getInstance().getheadBgColor();
		String headtextcolor  =	App.getInstance().getheadtextcolor();
		String leftIcon = App.getInstance().getleftIcon();
		String headheight = App.getInstance().getheadheight();
		if(leftIcon!=null){
			ImageView btn_left = (ImageView) findViewById(R.id.btn_wemart_head_left);
			VolleyLoadPicture vlp = new VolleyLoadPicture(getApplicationContext(), btn_left);
			vlp.getmImageLoader().get(leftIcon, vlp.getOne_listener());		
			btn_left.setVisibility(View.VISIBLE);
			
			btn_left.setOnClickListener(this);
		}
		RelativeLayout bg_head = (RelativeLayout) findViewById(R.id.wemart_head);	
		if(headBgColor!=null){
			bg_head.setBackgroundColor(Color.parseColor(headBgColor));
		}
		
		TextView tv_title = (TextView) findViewById(R.id.tv_wemart_head_title);
		tv_title.setText("确认订单");
		if(headtextcolor!=null){
			tv_title.setTextColor(Color.parseColor(headtextcolor));
		}
		
		if (headheight!= null && !"".equals(headheight)) {
			String[] sss = headheight.split("/");
			Float a1 = Float.parseFloat(sss[0]);
			Float a2 = Float.parseFloat(sss[1]);
			Float scale = a1 / a2;
			WindowManager wm = this.getWindowManager();
			int width = wm.getDefaultDisplay().getWidth();
			float widthdp = width * scale;
			android.widget.LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) bg_head
					.getLayoutParams();
			layoutParams.height = (int) widthdp;
			bg_head.setLayoutParams(layoutParams);

		}
	}
	
	private void Data() {
		// 订单确认页面绑定
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject o = new JSONObject();
					o.put("buyerId", buyer.buyerId);
					o.put("scenType", buyer.scenType);
					o.put("scenId", buyer.scenId);
					o.put("sign", buyer.sign);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("para", o.toString()));

					HttpHelper helper = new HttpHelper();
					String result = helper.executePost("http://uat.wemart.cn/api/shopping/buyer", params);
					BaseResponse entity = gson.fromJson(result, BaseResponse.class);
					if (entity != null && entity.returnValue == 0) {
						params.clear();
						o = new JSONObject();
						JSONObject receive = new JSONObject();

						receive.put("receiver", newAddress.name);
						receive.put("receiverAddr",
								newAddress.province + newAddress.city + newAddress.district + newAddress.street);
						receive.put("receiverPhone", newAddress.mobileNo);
						receive.put("cityNo", newAddress.cityNo);

						JSONArray goodsList = new JSONArray();
						for (int i = 0; i < mDatas.length; i++) {
							JSONObject goods = new JSONObject();
							goods.put("buyVol", mDatas[i].buyVol);
							goods.put("commSkuId", mDatas[i].commSkuId);
							goods.put("commsellerId", mDatas[i].commsellerId);
							goods.put("goodsName", mDatas[i].goodsName);
							goods.put("moneyUnit", mDatas[i].moneyUnit);
							goods.put("retailPrice", mDatas[i].retailPrice);
							goods.put("supplySellerId", mDatas[i].supplySellerId);
							goods.put("skuContent", mDatas[i].skuContent);
							goods.put("picUrl", mDatas[i].picUrl);
							goodsList.put(goods);

						}

						o.put("receive", receive);
						o.put("goodsList", goodsList);
						params.add(new BasicNameValuePair("para", o.toString()));
						result = helper.executePost("http://uat.wemart.cn/api/shopping/order/confirm", params);

						// JSONObject jobject = new JSONObject(result);

						list = gson.fromJson(result, OrderConfirmResponse1.class);

						if (list != null) {
							JSONObject ttt = new JSONObject(result);
							mAdapter.setCheckGoodsIsSale(ttt.getJSONObject("data").getJSONObject("checkGoodsIsSale"));
							mList = list.data.order;
							mDatas2.clear();
							for (int i = 0; i < mList.length; i++) {
								mDatas2.add(mList[i]);
							}

							Message msg = new Message();
							msg.obj = list;
							msg.arg1 = 1;
							handler.sendMessage(msg);
							// mAdapter.notifyDataSetChanged();

						}

					}
				} catch (Exception e) {
					Log.i("TAG1", e.getMessage() + "Exception");
				}
			}

		}).start();

	}

	public void refreshOrder(OrderConfirmResponse1 list) {
		mDatas2.clear();
		for (int i = 0; i < list.data.order.length; i++) {
			mDatas2.add(list.data.order[i]);
		}
		mList = list.data.order;
		Message msg = new Message();
		msg.obj = list;
		handler.sendMessage(msg);
	}

	@Override
	protected void findViewById() {
		tv_Allprice = (TextView) findViewById(R.id.tv_wemart_orderprice);// 总价

		tv_name = (TextView) findViewById(R.id.tv_wemart_name);
		tv_phone = (TextView) findViewById(R.id.tv_wemart_phone);
		tv_address = (TextView) findViewById(R.id.tv_wemart_address);
		lv_order = (ListViewForScrollView) findViewById(R.id.lv_wemart_order);
		img_alipay = (ImageView) findViewById(R.id.img_wemart_alipay);
		img_wx = (ImageView) findViewById(R.id.img_wemart_wx);
		tv_pay = (TextView) findViewById(R.id.tv_wemart_pay);
		// tv_head_share = (TextView) findViewById(R.id.tv_wemart_head_share);
		dizi = (RelativeLayout) findViewById(R.id.wemart_tdizi);
		rl_wechat = (RelativeLayout) findViewById(R.id.rl_wemart_wechat);
		rl_alipay = (RelativeLayout) findViewById(R.id.rl_wemart_alipay);

		// 设置默认选择
		setDefaultPay();
	}

	private void setDefaultPay() {
		img_alipay.setImageResource(R.drawable.wemart_order_pay);
		// isClick = true;
		imgId = 1;
	}

	@Override
	protected void setListeners() {
		img_alipay.setOnClickListener(this);
		img_wx.setOnClickListener(this);
		dizi.setOnClickListener(this);
		tv_pay.setOnClickListener(this);
		// tv_head_share.setOnClickListener(this);
		rl_wechat.setOnClickListener(this);
		rl_alipay.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		tv_name.setText(newAddress.name);
		tv_phone.setText(newAddress.mobileNo);
		tv_address.setText(newAddress.province + newAddress.city + newAddress.district + newAddress.street);
	}

	private int imgId = 0;

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		img_alipay.setImageResource(R.drawable.wemart_order_nopay);
		img_wx.setImageResource(R.drawable.wemart_order_nopay);

		int _id = v.getId();
		if (_id == R.id.btn_wemart_head_left) {
			finish();
		} else if (_id == R.id.wemart_tdizi) {
			// 选择地址
			Intent i2 = new Intent();
			i2.setClass(getApplicationContext(), AddressListActivity.class);
			i2.putExtra("buyOperate", (Serializable) buyOperate);
			i2.putExtra("addrNo", maddrNo);
			startActivityForResult(i2, 123);

		} else if (_id == R.id.rl_wemart_alipay) { // 支付宝
			if (imgId != 1) {
				// 选中
				setDefaultPay();
			} else {
				img_alipay.setImageResource(R.drawable.wemart_order_nopay);
				// isClick = false;
				imgId = -1;
			}
		} else if (_id == R.id.rl_wemart_wechat) {
			// 微信

			if (imgId != 2) {
				// 选中

				img_wx.setImageResource(R.drawable.wemart_order_pay);
				// isClick = true;
				imgId = 2;
			} else {
				img_wx.setImageResource(R.drawable.wemart_order_nopay);
				// isClick = false;
				imgId = -2;
			}
		} else if (_id == R.id.tv_wemart_pay) { // 立即支付 按钮
			if (imgId == 1) {// 支付宝
				new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							JSONObject o = new JSONObject();
							o.put("buyerId", buyer.buyerId);
							o.put("scenType", buyer.scenType);
							o.put("scenId", buyer.scenId);
							o.put("sign", buyer.sign);
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("para", o.toString()));

							HttpHelper helper = new HttpHelper();
							String result = helper.executePost("http://uat.wemart.cn/api/shopping/buyer", params);
							BaseResponse entity = gson.fromJson(result, BaseResponse.class);
							if (entity != null && entity.returnValue == 0) {
								params.clear();
								o = new JSONObject();

								JSONArray ordersList = new JSONArray();
								for (int i = 0; i < list.data.order.length; i++) {
									JSONObject order = new JSONObject();
									order.put("fareMoney", list.data.order[i].fareMoney);
									// 商品列表

									JSONArray goodsList = new JSONArray();
									for (int j = 0; j < list.data.order[i].goods.length; j++) {
										JSONObject goods = new JSONObject();
										goods.put("buyVol", list.data.order[i].goods[j].buyVol);
										goods.put("commSkuId", list.data.order[i].goods[j].commSkuId);
										goods.put("commsellerId", list.data.order[i].goods[j].commsellerId);
										goods.put("goodsName", list.data.order[i].goods[j].goodsName);
										goods.put("moneyUnit", list.data.order[i].goods[j].moneyUnit);
										goods.put("retailPrice", list.data.order[i].goods[j].retailPrice);
										goods.put("fareMoney", list.data.order[i].goods[j].fareMoney);// 运费

										goods.put("supplySellerId", list.data.order[i].goods[j].supplySellerId);
										goods.put("skuContent", list.data.order[i].goods[j].skuContent);

										goodsList.put(goods);
									}
									order.put("goods", goodsList);
									order.put("goodsNum", list.data.order[i].goodsNum);
									order.put("groupName", list.data.order[i].groupName);
									order.put("supplySellerId", list.data.order[i].supplySellerId);
									order.put("totalPrice", list.data.order[i].totalPrice);

									ordersList.put(order);
								}

								// 买家备注信息

								String remarkComment = "";
								JSONObject buyerComment = new JSONObject();
								for (int i = 0; i < list.data.order.length; i++) {
									remarkComment = list.data.order[i].remarkContent;
									if (TextUtils.isEmpty(remarkComment))
										remarkComment = "";
									JSONObject comments = new JSONObject();
									comments.put("remarkComment", remarkComment);
									// comments.put("remarkComment", "买家备注" +
									// i);
									comments.put("radioComment", "下拉选择信息");
									buyerComment.put(
											list.data.order[i].commSellerId + "_" + list.data.order[i].supplySellerId,
											comments);
								}

								// 支付方式

								JSONObject pays = new JSONObject();
								pays.put("isDefault", 1);
								pays.put("paySubType", 3);// 3手机
								pays.put("payType", 1);// 1支付宝 3微信

								JSONArray payTypeLL = new JSONArray();
								payTypeLL.put(pays);
								// 收货信息

								JSONObject receive = new JSONObject();
								receive.put("receiver", newAddress.name);
								receive.put("receiverAddr", newAddress.province + newAddress.city + newAddress.district
										+ newAddress.street);
								receive.put("receiverPhone", newAddress.mobileNo);

								o.put("order", ordersList);
								o.put("buyerComment", buyerComment);
								o.put("payTypeList", payTypeLL);
								o.put("receive", receive);

								params.add(new BasicNameValuePair("para", o.toString()));
								result = helper.executePost("http://uat.wemart.cn/api/shopping/order/create", params);

								// JSONObject jobject=new JSONObject(result);

								OrderCreateResponse lista = gson.fromJson(result, OrderCreateResponse.class);
								// 如果订单提交成功
								if (lista != null && lista.returnValue == 0 && lista.data.checkGoodsIsSale.status) {
									PayTask alipay = new PayTask(OrderConfirmActivity.this);
									String result1 = alipay.pay(lista.data.paymentUrl, true);
									Message msg = new Message();
									msg.what = SDK_PAY_FLAG;
									msg.obj = result1;
									mHandler.sendMessage(msg);
								} else if (!lista.data.checkGoodsIsSale.status && lista.returnValue == 0) {
									/** 解析提交失败的信息 */
//									tv_pay.setBackgroundColor(Color.parseColor("#DCDCDC"));
									
									JSONObject jsonResult = new JSONObject(result);
									JSONObject goodsResultData = jsonResult.getJSONObject("data");
									JSONObject goodsResultData1 = goodsResultData.getJSONObject("checkGoodsIsSale");
									JSONObject goodsResult = goodsResultData1.getJSONObject("goodsResult");
									// checkGoodsIsSale
									Iterator<?> it = goodsResult.keys(); // ?
									String keyname = "";
									Boolean flag = false;
									StringBuilder errmsg = new StringBuilder();
									while (it.hasNext()) {
										keyname = (String) it.next().toString();
										JSONObject temp = goodsResult.getJSONObject(keyname);
										flag = temp.getBoolean("sale");
										if (!flag) { // 如果返回的是false
											errmsg.append(temp.getString("msg"));
											String str = temp.getString("msg");
											Log.e("提交订单返回的信息", temp.getString("msg"));
											Message msg = new Message();
											msg.obj = errmsg;
											msg.arg1 = 2;
											handler.sendMessage(msg);
											Toast.makeText(getApplicationContext(), str, 0).show();
										}
									}
									Looper.prepare();
									Toast.makeText(getApplicationContext(), errmsg.toString(), Toast.LENGTH_SHORT)
											.show();
									Looper.loop();
								} else if (lista.returnValue != 0) {
									Looper.prepare();
									Toast.makeText(getApplicationContext(), lista.returnMsg, Toast.LENGTH_SHORT).show();
									Looper.loop();
								}
							} else {
								Toast.makeText(getApplicationContext(), "提交失败", 0).show();
							}
						} catch (Exception ex) {
							// TODO: handle exception
							System.out.println(ex);
						}
					}
				}).start();

			} else if (imgId == 2) {
				// 微信

				// new Thread(new Runnable() {
				//
				// @Override
				// public void run() {
				// try {
				// JSONObject o = new JSONObject();
				// o.put("buyerId", buyer.buyerId);
				// o.put("scenType", buyer.scenType);
				// o.put("scenId", buyer.scenId);
				// o.put("sign", buyer.sign);
				// List<NameValuePair> params = new ArrayList<NameValuePair>();
				// params.add(new BasicNameValuePair("para", o
				// .toString()));
				//
				// HttpHelper helper = new HttpHelper();
				// String result = helper.executePost(
				// "http://uat.wemart.cn/api/shopping/buyer",
				// params);
				// BaseResponse entity = gson.fromJson(result,
				// BaseResponse.class);
				// if (entity != null && entity.returnValue == 0) {
				// params.clear();
				// o = new JSONObject();
				//
				// JSONArray ordersList = new JSONArray();
				// for (int i = 0; i < list.data.order.length; i++) {
				// JSONObject order = new JSONObject();
				// order.put("fareMoney",
				// list.data.order[i].fareMoney);
				// // 商品列表
				//
				// JSONArray goodsList = new JSONArray();
				// for (int j = 0; j < list.data.order[i].goods.length; j++) {
				// JSONObject goods = new JSONObject();
				// goods.put(
				// "buyVol",
				// list.data.order[i].goods[j].buyVol);
				// goods.put(
				// "commSkuId",
				// list.data.order[i].goods[j].commSkuId);
				// goods.put(
				// "commsellerId",
				// list.data.order[i].goods[j].commsellerId);
				// goods.put(
				// "goodsName",
				// list.data.order[i].goods[j].goodsName);
				// goods.put(
				// "moneyUnit",
				// list.data.order[i].goods[j].moneyUnit);
				// goods.put(
				// "retailPrice",
				// list.data.order[i].goods[j].retailPrice);
				// goods.put(
				// "fareMoney",
				// list.data.order[i].goods[j].fareMoney);// 运费
				//
				// goods.put(
				// "supplySellerId",
				// list.data.order[i].goods[j].supplySellerId);
				// goods.put(
				// "skuContent",
				// list.data.order[i].goods[j].skuContent);
				//
				// goodsList.put(goods);
				// }
				// order.put("goods", goodsList);
				// order.put("goodsNum",
				// list.data.order[i].goodsNum);
				// order.put("groupName",
				// list.data.order[i].groupName);
				// order.put("supplySellerId",
				// list.data.order[i].supplySellerId);
				// order.put("totalPrice",
				// list.data.order[i].totalPrice);
				//
				// ordersList.put(order);
				// }
				//
				// // // 买家备注信息
				//
				// JSONObject buyerComment = new JSONObject();
				// for (int i = 0; i < list.data.order.length; i++) {
				//
				// JSONObject comments = new JSONObject();
				// comments.put("remarkComment", "买家备注" + i);
				// comments.put("radioComment", "下拉选择信息");
				// buyerComment
				// .put(list.data.order[i].commSellerId
				// + "_"
				// + list.data.order[i].supplySellerId,
				// comments);
				// }
				//
				// // // 支付方式
				//
				// JSONObject pays = new JSONObject();
				// pays.put("isDefault", 1);
				// pays.put("paySubType", 3);// 3手机
				//
				// pays.put("payType", 3);// 1支付宝 3微信
				//
				// JSONArray payTypeLL = new JSONArray();
				// payTypeLL.put(pays);
				// // // 收货信息
				//
				// JSONObject receive = new JSONObject();
				// receive.put("receiver", newAddress.name);
				// receive.put("receiverAddr", newAddress.province
				// + newAddress.city + newAddress.district
				// + newAddress.streetAddr);
				// receive.put("receiverPhone",
				// newAddress.mobileNo);
				//
				// o.put("order", ordersList);
				// o.put("buyerComment", buyerComment);
				// o.put("payTypeList", payTypeLL);
				// o.put("receive", receive);
				//
				// params.add(new BasicNameValuePair("para", o
				// .toString()));
				// result = helper
				// .executePost(
				// "http://uat.wemart.cn/api/shopping/order/create",
				// params);
				//
				// // JSONObject jobject=new JSONObject(result);
				//
				// OrderCreateResponse lista = gson.fromJson(
				// result, OrderCreateResponse.class);
				// if (lista != null && lista.returnValue == 0) {
				// String url =
				// "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
				//
				// // Toast.makeText(OrderConfirmActivity.this,
				//
				// // "获取订单中...",
				//
				// // Toast.LENGTH_SHORT).show();
				//
				// byte[] buf = Util.httpGet(url);
				// if (buf != null && buf.length > 0) {
				// String content = new String(buf);
				// Log.e("get server pay params:", content);
				// JSONObject json = new JSONObject(
				// content);
				// if (null != json
				// && !json.has("retcode")) {
				// PayReq request = new PayReq();
				// request.appId = "wxc8f6b243eb4f43bf";
				// request.partnerId = "1293524701";
				// request.prepayId = "wx20160325150935ed3a7262080716825125";
				// request.packageValue = "Sign=WXPay";
				// request.nonceStr = "1458889775563";
				// request.timeStamp = "1458889775";
				// request.sign = "82B02363E60C675F7D2ADEE759F30775";
				//
				// api.sendReq(request);
				//
				// Log.i("TAG1", "正常调起支付");
				// Looper.prepare();
				// Toast.makeText(
				// OrderConfirmActivity.this,
				// "正常调起支付",
				// Toast.LENGTH_SHORT).show();
				// Looper.loop();
				// // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
				//
				// api.sendReq(request);
				// } else {
				// Log.i("TAG1",
				// "返回错误"
				// + json.getString("retmsg"));
				// Looper.prepare();
				// Toast.makeText(
				// OrderConfirmActivity.this,
				// "返回错误"
				// + json.getString("retmsg"),
				// Toast.LENGTH_SHORT).show();
				// Looper.loop();
				//
				// }
				// } else {
				// Log.i("TAG1", "服务器请求错误");
				// Looper.prepare();
				// Toast.makeText(
				// OrderConfirmActivity.this,
				// "服务器请求错误", Toast.LENGTH_SHORT)
				// .show();
				// Looper.loop();
				// }
				// }
				// }
				//
				// } catch (Exception e) {
				// Log.i("TAG1", "异常：" + e.getMessage());
				// Looper.prepare();
				// Toast.makeText(OrderConfirmActivity.this,
				// "异常：" + e.getMessage(), Toast.LENGTH_SHORT)
				// .show();
				// Looper.loop();
				// }
				//
				// }
				// }).start();
			} else {
				Toast.makeText(OrderConfirmActivity.this, "请选择支付方式", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		// TODO 自动生成的方法存根
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:

			Bundle bundle = arg2.getExtras();
			newAddress = (AddressResponse2) bundle.getSerializable("res");
			maddrNo = newAddress.addrNo;
			tv_name.setText(newAddress.name);
			tv_phone.setText(newAddress.mobileNo);

			tv_address.setText(newAddress.province + newAddress.city + newAddress.district + newAddress.street);

			break;
		default:
			break;
		}

	}

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * 
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * 
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档

				if (TextUtils.equals(resultStatus, "9000")) {

					Toast.makeText(OrderConfirmActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败

					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）

					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(OrderConfirmActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误

						Toast.makeText(OrderConfirmActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}

			default:
				break;
			}
		};
	};

}
