package cn.wemart.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.wemart.pojo.AddressResponse2;
import cn.wemart.pojo.BaseResponse;
import cn.wemart.pojo.BuyerEntity;
import cn.wemart.pojo.OrderConfirmResponse1;
import cn.wemart.pojo.OrderConfirmSubResponse21;
import cn.wemart.pojo.OrderGoodSubIResponse;
import cn.wemart.sdk.R;
import cn.wemart.sdk.activity.OrderConfirmActivity;
import cn.wemart.tools.HttpHelper;
import cn.wemart.tools.VolleyLoadPicture;
import cn.wemart.widget.ListViewForScrollView;

public class OrderConfirmItemAdapter extends BaseAdapter {

	private OrderGoodSubIResponse[] mList;
	private Context context;
	private ListViewForScrollView lv;
	private String ss;
	private Gson gson = new Gson();
	private JSONObject checkGoodsIsSale;

	// private OrderConfirmSubResponse21[] mList2 = null;

	private BuyerEntity buyer;
	private AddressResponse2 newAddress;
	private OrderConfirmAdapter mAdapter;
	private String commSkuId;
	private int buyVol;
	private List<OrderConfirmSubResponse21> aList;
	private DecimalFormat moneyFormat = new DecimalFormat("0.00");

	//

	// private Handler handler = new Handler(new Callback() {

	//

	// @Override

	// public boolean handleMessage(Message msg) {

	//

	// OrderConfirmAdapter mAdapter = new OrderConfirmAdapter(context, mList2,

	// lv,buyer,newAddress);

	// // lv.setAdapter(mAdapter);

	// mAdapter.notifyDataSetChanged();

	// return false;

	// }

	// });

	public OrderConfirmItemAdapter(Context context, List<OrderConfirmSubResponse21> aList,
			OrderGoodSubIResponse[] mList, JSONObject checkGoodsIsSale, BuyerEntity buyer,
			AddressResponse2 newAddress) {
		this.context = context;
		this.mList = mList;
		this.checkGoodsIsSale = checkGoodsIsSale;
		this.newAddress = newAddress;
		this.buyer = buyer;
		this.aList = aList;
	}

	@Override
	public int getCount() {
		return mList.length;
	}

	@Override
	public Object getItem(int position) {
		return mList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.wemart_subitem_order_confirm, null);

			vh.tv_title = (TextView) convertView.findViewById(R.id.tv_wemart_title);
			vh.tv_size = (TextView) convertView.findViewById(R.id.tv_wemart_size);
			// vh.tv_color = (TextView)
			vh.tv_wemart_p = (TextView) convertView.findViewById(R.id.tv_wemart_p);
			// convertView.findViewById(R.id.tv_color);// 颜色

			vh.tv_price = (TextView) convertView.findViewById(R.id.tv_wemart_price);// 价格

			vh.iv_cut = (ImageView) convertView.findViewById(R.id.iv_wemart_cut);
			vh.tv_number = (TextView) convertView.findViewById(R.id.tv_wemart_number);
			vh.iv_add = (ImageView) convertView.findViewById(R.id.iv_wemart_add);
			vh.ll_order = (LinearLayout) convertView.findViewById(R.id.ll_wemart_order);
			vh.iv_p = (ImageView) convertView.findViewById(R.id.iv_wemart_p);

			convertView.setTag(vh);
			vh.iv_cut.setTag(vh);
			vh.iv_add.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final OrderGoodSubIResponse item = mList[position];

		vh.tv_number.setText(item.buyVol + "");
		vh.tv_title.setText(item.goodsName);
		try {
			// JSONObject sku = new JSONObject(item.skuContent);

			if (!item.skuContent.equals("{}")) {
				JSONArray jj = new JSONArray(item.skuContent);
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < jj.length(); i++) {
					JSONObject jo = jj.getJSONObject(i);
					builder.append(jo.getString("skuSetName"));
					builder.append(":");
					builder.append(jo.getString("skuValue"));
				}
				vh.tv_size.setText(builder.toString());
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		// vh.tv_color.setText(item.skuContent);

		vh.tv_price.setText("¥" + moneyFormat.format(Double.parseDouble(item.retailPrice + "") / 100));
		// 图片地址没有...判断商品是否可购买
		try {
			JSONObject goodsResult = checkGoodsIsSale.getJSONObject("goodsResult");
			Iterator<?> it = goodsResult.keys(); // ?
			String keyname = "";
			Boolean flag = false;
			String msg = null;
			while (it.hasNext()) {
				keyname = (String) it.next().toString();
				JSONObject temp = goodsResult.getJSONObject(keyname);
				flag = temp.getBoolean("sale");
				msg = temp.getString("msg");
				if (keyname.equals(item.commSkuId) && !flag) { // 如果返回的是false
					
					flag = true;
					break;
				}
			}

			if (flag) {
				// 不可购买。变灰
				
				vh.tv_wemart_p.setText(msg);
				vh.tv_wemart_p.getBackground().setAlpha(90);
				vh.tv_wemart_p.setVisibility(View.VISIBLE);
				
				
			}
		} catch (Exception ex) {

		}

		VolleyLoadPicture vlp = new VolleyLoadPicture(context, vh.iv_p);
		vlp.getmImageLoader().get(item.picUrl, vlp.getOne_listener());

		// x.image().bind(vh.iv_p, item.picUrl);
		if(buyVol>1){
			vh.iv_cut.setBackgroundResource(R.drawable.whitejian);
		}
		
		
		/**减的监听*/
		vh.iv_cut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder vh = (ViewHolder) v.getTag();

				vh.iv_add.setBackgroundResource(R.drawable.whitejia);
				int number = Integer.parseInt(vh.tv_number.getText().toString());
				if (number == 1) {
					vh.iv_cut.setEnabled(false);
					Toast.makeText(context, "数量不能少于1", Toast.LENGTH_SHORT).show();
					vh.iv_cut.setBackgroundResource(R.drawable.grayjian);
					return;
				} else if (!vh.iv_cut.isEnabled()) {
					vh.iv_cut.setEnabled(true);

				}

				vh.tv_number.setText(--number + "");
				commSkuId = mList[position].commSkuId;
				buyVol = number;
				initData();

			}

		});
		
		vh.iv_add.setBackgroundResource(R.drawable.whitejia);
		/**加的监听*/
		vh.iv_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder vh = (ViewHolder) v.getTag();
				vh.iv_cut.setBackgroundResource(R.drawable.whitejian);
				int number = Integer.parseInt(vh.tv_number.getText().toString());
				if(number >=98){
					vh.iv_add.setBackgroundResource(R.drawable.grayjia);
					vh.iv_add.setEnabled(false);
				}
				
				if (!vh.iv_cut.isEnabled()) {
					vh.iv_cut.setEnabled(true);
				}
				vh.tv_number.setText(++number + "");
				commSkuId = mList[position].commSkuId;
				buyVol = number;

				initData();
			}

		});

		return convertView;
	}

	private void initData() {
		// 订单确认

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
								newAddress.province + newAddress.city + newAddress.district + newAddress.streetAddr);
						receive.put("receiverPhone", newAddress.mobileNo);
						receive.put("cityNo", newAddress.cityNo);

						JSONArray goodsList = new JSONArray();
						for (int j = 0; j < aList.size(); j++) {
							OrderGoodSubIResponse[] mGoods = aList.get(j).goods;
							for (int i = 0; i < mGoods.length; i++) {
								JSONObject goods = new JSONObject();
								if (commSkuId.equals(mGoods[i].commSkuId)) {
									if (buyVol < 1) {
										buyVol = 1;
									}
									goods.put("buyVol", buyVol);
								} else {
									goods.put("buyVol", mGoods[i].buyVol);
								}
								goods.put("commSkuId", mGoods[i].commSkuId);
								goods.put("commsellerId", mGoods[i].commsellerId);
								goods.put("goodsName", mGoods[i].goodsName);
								goods.put("moneyUnit", mGoods[i].moneyUnit);
								goods.put("retailPrice", mGoods[i].retailPrice);
								goods.put("supplySellerId", mGoods[i].supplySellerId);
								goods.put("skuContent", mGoods[i].skuContent);
								goods.put("picUrl", mGoods[i].picUrl);
								goodsList.put(goods);

							}
						}
						o.put("receive", receive);
						o.put("goodsList", goodsList);
						params.add(new BasicNameValuePair("para", o.toString()));
						result = helper.executePost("http://uat.wemart.cn/api/shopping/order/confirm", params);

						OrderConfirmResponse1 list = gson.fromJson(result, OrderConfirmResponse1.class);

						if (list != null) {
							// OrderConfirmSubResponse21[] mList2 =

							// list.data.order;

							OrderConfirmActivity activity = (OrderConfirmActivity) context;
							activity.refreshOrder(list);
							// mAdapter.notifyDataSetChanged();

						}

					}
				} catch (Exception e) {
					Log.i("TAG1", e.getMessage());
				}
			}

		}).start();

	}

	private final class ViewHolder {
		TextView tv_title, tv_size, tv_color, tv_price, tv_number;
		TextView tv_wemart_p;
		LinearLayout ll_order;
		public ImageView iv_add, iv_cut, iv_p;

	}

}
