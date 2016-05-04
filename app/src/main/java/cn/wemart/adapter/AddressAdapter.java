package cn.wemart.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.wemart.pojo.AddressCollectionResponse;
import cn.wemart.pojo.AddressResponse2;
import cn.wemart.pojo.BaseResponse;
import cn.wemart.pojo.BuyOperateEntity;
import cn.wemart.pojo.BuyerEntity;
import cn.wemart.sdk.R;
import cn.wemart.sdk.activity.AddressEditActivity;
import cn.wemart.tools.HttpHelper;

/**
 * @author
 */
public class AddressAdapter extends BaseAdapter {

	private Context context;
	private List<AddressResponse2> guardNews;
	private LayoutInflater layoutInflater;
	private Gson gson = new Gson();
	private BuyOperateEntity buyOperate;
	private BuyerEntity buyer;
	private AddressResponse2 newAddress = null;
	private int maddrNo;
	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			notifyDataSetChanged();
			return false;
		}
	});

	public AddressAdapter(Context context, BuyOperateEntity buyOperate, List<AddressResponse2> mguardNews, int addrNo) {
		super();
		this.buyOperate = buyOperate;
		this.context = context;
		this.guardNews = mguardNews;
		this.maddrNo = addrNo;
	}

	public AddressAdapter() {
		super();
	}

	@Override
	public int getCount() {

		return guardNews.size();
	}

	@Override
	public Object getItem(int position) {

		return guardNews.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			layoutInflater = LayoutInflater.from(context);
			convertView = layoutInflater.inflate(R.layout.wemart_item_dizi, null);
			holder = new ViewHolder();
			holder.ltem_name = (TextView) convertView.findViewById(R.id.wemart_ltem_name);
			holder.liemtex = (TextView) convertView.findViewById(R.id.wemart_liemtex);
			holder.lte_sj = (TextView) convertView.findViewById(R.id.wemart_ltem_mobile);
			holder.lte_dz = (TextView) convertView.findViewById(R.id.wemart_lte_dz);
			holder.moren = (TextView) convertView.findViewById(R.id.wemart_moren);
			holder.textView1 = (TextView) convertView.findViewById(R.id.wemart_liemtex);
			holder.img_mr = (ImageView) convertView.findViewById(R.id.wemart_img_mr);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();

		holder.ltem_name.setText(guardNews.get(position).getname());
		holder.lte_sj.setText(guardNews.get(position).getmobileNo());
		holder.lte_dz.setText(guardNews.get(position).getprovince() + guardNews.get(position).getcity()
				+ guardNews.get(position).getdistrict() + guardNews.get(position).getstreet());
		if (guardNews.get(position).addrNo == maddrNo) {
			holder.img_mr.setVisibility(View.VISIBLE);
		} else {
			holder.img_mr.setVisibility(View.INVISIBLE);
		}
		Boolean readed = guardNews.get(position).getisDefault();
		if (!readed) {
			holder.moren.setVisibility(View.GONE);
		} else {
			holder.moren.setVisibility(View.VISIBLE);
		}
		holder.liemtex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// moren();

			}

			private void moren() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {

							JSONObject o = new JSONObject();
							buyer = buyOperate.buyer;
							o.put("buyerId", buyer.buyerId);
							o.put("scenType", buyer.scenType);
							o.put("scenId", buyer.scenId);
							o.put("sign", buyer.sign);
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("para", o.toString()));
							HttpHelper helper = new HttpHelper();
							String result = helper.executePost("http://uat.wemart.cn/api/shopping/buyer", params);
							BaseResponse entity = gson.fromJson(result, BaseResponse.class);
							Log.i("TAG1", entity.returnMsg + "|" + entity.returnValue);
							if (entity != null && entity.returnValue == 0) {

								params.clear();
								o = new JSONObject();
								o.put("addrNo", guardNews.get(position).getaddrNo());
								params.add(new BasicNameValuePair("para", o.toString()));
								result = helper.executePut("http://uat.wemart.cn/api/usermng/buyer/defaultaddr",
										params);
								BaseResponse list = gson.fromJson(result, BaseResponse.class);
								if (list.returnValue == 0) {

									guardNews.clear();
									result = helper.executeGet("http://uat.wemart.cn/api/usermng/buyer/address", null);
									AddressCollectionResponse list2 = gson.fromJson(result,
											AddressCollectionResponse.class);
									for (int i = 0; i < list2.data.length; i++) {
										AddressResponse2 a = new AddressResponse2(list2.data[i].addrNo,
												list2.data[i].city, list2.data[i].cityNo, list2.data[i].district,
												list2.data[i].isDefault, list2.data[i].mobileNo, list2.data[i].name,
												list2.data[i].province, list2.data[i].street);
										Log.i("TAG", list2.data[i].name);

										guardNews.add(a);

									}
									handler.sendEmptyMessage(0);

								}

							}

							// guardNews.clear();

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}).start();

			}
		});

		holder.textView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("TAG", "textView1");
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("xiugai", "1");
				bundle.putString("title", "修改收货地址");
				bundle.putString("mobileNo", guardNews.get(position).getmobileNo());
				bundle.putString("name", guardNews.get(position).getname());
				bundle.putBoolean("isDefault", guardNews.get(position).getisDefault());
				bundle.putString("streetAddr", guardNews.get(position).getstreet());
				bundle.putString("district", guardNews.get(position).getdistrict());
				bundle.putString("city", guardNews.get(position).getcity());
				bundle.putInt("addrNo", guardNews.get(position).getaddrNo());
				bundle.putString("province", guardNews.get(position).getprovince());
				bundle.putInt("cityNo", guardNews.get(position).getcityNo());
				bundle.putSerializable("buyOperate", buyOperate);

				intent.putExtras(bundle);
				intent.setClass(context, AddressEditActivity.class);
				context.startActivity(intent);
				guardNews.clear();
			}
		});

		/*
		 * convertView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * 
		 * 
		 * 
		 * newAddress = new AddressResponse2(); newAddress.province =
		 * guardNews.get(position).getprovince(); newAddress.city =
		 * guardNews.get(position).getcity(); newAddress.district =
		 * guardNews.get(position).getdistrict(); newAddress.streetAddr =
		 * guardNews.get(position).getstreet(); newAddress.mobileNo =
		 * guardNews.get(position).getmobileNo(); newAddress.name =
		 * guardNews.get(position).getname(); newAddress.isDefault =
		 * guardNews.get(position).getisDefault(); Intent intent = new Intent();
		 * intent.putExtra("buyOperate", (Serializable) buyOperate);
		 * intent.putExtra("newAddress", (Serializable) newAddress);
		 * intent.setClass(context, OrderConfirmActivity.class); // SouHuoDiZi
		 * activity=(SouHuoDiZi) context; // activity.setResult(123, intent);
		 * context.startActivity(intent);
		 * 
		 * 
		 * } });
		 */
		return convertView;
	}

	class ViewHolder {
		TextView ltem_name, lte_sj, lte_dz, moren, textView1, liemtex;
		ImageView img_mr;
	}

}
