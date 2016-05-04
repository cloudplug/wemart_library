package cn.wemart.sdk.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.wemart.adapter.AddressAdapter;
import cn.wemart.app.App;
import cn.wemart.pojo.AddressCollectionResponse;
import cn.wemart.pojo.AddressResponse2;
import cn.wemart.pojo.BaseResponse;
import cn.wemart.pojo.BuyOperateEntity;
import cn.wemart.pojo.BuyerEntity;
import cn.wemart.sdk.R;
import cn.wemart.tools.HttpHelper;
import cn.wemart.tools.VolleyLoadPicture;
import cn.wemart.widget.ListViewForScrollView;

public class AddressListActivity extends BaseActivity implements
		android.view.View.OnClickListener {
	private LinearLayout tianjina;
	private ListViewForScrollView listview;
	private Gson gson = new Gson();
	private List<AddressResponse2> addres = new ArrayList<AddressResponse2>();
	private AddressAdapter adapter;
	private BuyOperateEntity buyOperate = null;
	private BuyerEntity buyer = null;

	private int maddrNo;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			adapter.notifyDataSetChanged();
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemart_ac_addresslist);
//		setHead(HeadType.NORMAL, "选择收货地址", R.drawable.wemart_zuobiao, "", this);
		
		setHeader();
		
		
		templatedMethod();

		buyOperate = (BuyOperateEntity) getIntent().getExtras()
				.getSerializable("buyOperate");
		maddrNo = getIntent().getExtras().getInt("addrNo");
		buyer = buyOperate.buyer;
		// buyer = (BuyerEntity)

		// getIntent().getExtras().getSerializable("buyer");

		listview = (ListViewForScrollView) findViewById(R.id.wemart_list_dizi);
		adapter = new AddressAdapter(AddressListActivity.this, buyOperate,
				addres,maddrNo);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO 自动生成的方法存根
				
				AddressResponse2 response2 = addres.get(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("res", response2);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	
	private void setHeader() {
		String headBgColor  =	App.getInstance().getheadBgColor();
		String headtextcolor  =	App.getInstance().getheadtextcolor();
		String leftIcon = App.getInstance().getleftIcon();
		String headheight = App.getInstance().getheadheight();
		if(leftIcon!=null){
			ImageView btn_left = (ImageView) findViewById(R.id.btn_wemart_head_left);
			btn_left.setVisibility(View.VISIBLE);
			VolleyLoadPicture vlp = new VolleyLoadPicture(getApplicationContext(), btn_left);
			vlp.getmImageLoader().get(leftIcon, vlp.getOne_listener());		
			
			btn_left.setOnClickListener(this);
		}
		RelativeLayout bg_head = (RelativeLayout) findViewById(R.id.wemart_head);	
		if(headBgColor!=null){
			bg_head.setBackgroundColor(Color.parseColor(headBgColor));
		}
		
		TextView tv_title = (TextView) findViewById(R.id.tv_wemart_head_title);
		tv_title.setText("选择收货地址");
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
	
	
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根

		getaddress();
		super.onResume();
	}

	
	
	private void getaddress() {
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
					String result = helper.executePost(
							"http://uat.wemart.cn/api/shopping/buyer", params);
					BaseResponse entity = gson.fromJson(result,
							BaseResponse.class);
					Log.i("TAG", entity.returnMsg);
					if (entity != null && entity.returnValue == 0) {
						result = helper
								.executeGet(
										"http://uat.wemart.cn/api/usermng/buyer/address",
										null);
						AddressCollectionResponse list = gson.fromJson(result,
								AddressCollectionResponse.class);
						// AddressResponse2 maddres = null;

						addres.clear();
						for (int i = 0; i < list.data.length; i++) {
							
							AddressResponse2 a = new AddressResponse2(
									list.data[i].addrNo, list.data[i].city,
									list.data[i].cityNo, list.data[i].district,
									list.data[i].isDefault,
									list.data[i].mobileNo, list.data[i].name,
									list.data[i].province, list.data[i].street);
							Log.i("TAG", list.data[i].name);
							addres.add(a);
						}
						// adapter.notifyDataSethanged();

						handler.sendEmptyMessage(0);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected void findViewById() {
		tianjina = (LinearLayout) findViewById(R.id.wemart_tjdz);
	}

	@Override
	protected void setListeners() {
		tianjina.setOnClickListener(this);

	}

	@Override
	protected void initView() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onClick(View v) {
		int _id = v.getId();
		if (_id == R.id.btn_wemart_head_left) {
			finish();
		} else if (_id == R.id.wemart_tjdz) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("title", "收货地址");
			// bundle.putString("xiugai", "0");

			intent.putExtra("buyOperate", (Serializable) buyOperate);
			intent.putExtras(bundle);
			intent.setClass(AddressListActivity.this, AddressEditActivity.class);
			startActivity(intent);
			addres.clear();
		}

	}

}
