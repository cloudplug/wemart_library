package cn.wemart.sdk.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.wemart.app.App;
import cn.wemart.pojo.AddressCollectionResponse;
import cn.wemart.pojo.AddressResponse2;
import cn.wemart.pojo.BaseResponse;
import cn.wemart.pojo.BuyOperateEntity;
import cn.wemart.pojo.BuyerEntity;
import cn.wemart.sdk.R;
import cn.wemart.tools.HttpHelper;
import cn.wemart.tools.VolleyLoadPicture;
import cn.wemart.widget.PickDialog;
import cn.wemart.widget.PickDialogListener;

@SuppressLint("ShowToast")
public class AddressEditActivity extends BaseActivity implements android.view.View.OnClickListener, TextWatcher {
	private Button queding;
	private LinearLayout sancu;
	private EditText name, souji, dizi;
	private String nae, sj, dz;
	private ToggleButton moren;
	private TextView result1, result2, result3;
	private Gson gson = new Gson();
//	private String results; 
	
	private String province_Name = null;
	int cityNos = 0;
	private int nf = 0;
	private PickDialog pickDialog;
	private ArrayList<String> lists = new ArrayList<String>();
	private int cod = -1;
	private AddressCollectionResponse mlist;
	public String d = "0";

	private BuyOperateEntity buyOperate = null;
	private BuyerEntity buyer = null;
	private AddressResponse2 newAddress = null;
//	private String addr;
	private int addrNo, cityNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemart_ac_addressedit);
//		setHead(HeadType.NORMAL, "收货地址", // getIntent().getStringExtra("title")
//				R.drawable.wemart_zuobiao, "", this);
		
	setHeader();
	
	
		templatedMethod();

		addrNo = getIntent().getIntExtra("addrNo", -1);
		cityNo = getIntent().getIntExtra("cityNo", -1);
		buyOperate = (BuyOperateEntity) getIntent().getExtras().getSerializable("buyOperate");
		buyer = buyOperate.buyer;
		// buyer = (BuyerEntity)

		// getIntent().getExtras().getSerializable("buyer");

		if (getIntent().getStringExtra("xiugai") != null) {
			if (getIntent().getStringExtra("xiugai").equals("1")) {
				sancu.setVisibility(View.VISIBLE);
				getss();
				d = "1";
			}
		}

//		initData();
		// if
		// (name.getText().toString().equals("")||souji.getText().toString().equals("")||dizi.getText().toString().equals("")||result1.getText().toString().equals("")||result2.getText().toString().equals("")||result3.getText().toString().equals(""))
		// {

		// queding.setBackgroundColor(R.drawable.btn_shape_off2);

		// }else {

		// queding.setBackgroundColor(R.drawable.but_queding);

		// }

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
		tv_title.setText("收货地址");
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

	private void getss() {
		name.setText(getIntent().getStringExtra("name"));
		dizi.setText(getIntent().getStringExtra("streetAddr"));
		result2.setText(getIntent().getStringExtra("city"));
		result1.setText(getIntent().getStringExtra("province"));
		result3.setText(getIntent().getStringExtra("district"));
		souji.setText(getIntent().getStringExtra("mobileNo"));
		moren.setChecked(getIntent().getBooleanExtra("isDefault", false));
	}

	@Override
	protected void findViewById() {
		name = (EditText) findViewById(R.id.wemart_edname);
		souji = (EditText) findViewById(R.id.wemart_edsouji);
		dizi = (EditText) findViewById(R.id.wemart_eddizi);
		queding = (Button) findViewById(R.id.wemart_butqd);
		moren = (ToggleButton) findViewById(R.id.wemart_moren);
		result1 = (TextView) findViewById(R.id.wemart_result1);
		result2 = (TextView) findViewById(R.id.wemart_result2);
		result3 = (TextView) findViewById(R.id.wemart_result3);

		sancu = (LinearLayout) findViewById(R.id.wemart_sancudizi);

		
		SpannableString namehint = new SpannableString("名字");
		
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12, true);
		
		namehint.setSpan(ass, 0, namehint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		name.setHint(new SpannedString(namehint)); 
		
		SpannableString phoneHint = new SpannableString("11位手机号");
		AbsoluteSizeSpan ass1 = new AbsoluteSizeSpan(12, true);
		phoneHint.setSpan(ass1, 0, phoneHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		souji.setHint(new SpannableString(phoneHint));
		
		SpannableString addressHint = new SpannableString("详细地址，门牌号");
		AbsoluteSizeSpan ass2 = new AbsoluteSizeSpan(12, true);
		addressHint.setSpan(ass2, 0, addressHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		dizi.setHint(new SpannableString(addressHint));

	}

	@Override
	protected void onResume() {
		
		super.onResume();

	}

	@Override
	protected void setListeners() {
		queding.setOnClickListener(this);
		result1.setOnClickListener(this);
		result2.setOnClickListener(this);
		result3.setOnClickListener(this);
		sancu.setOnClickListener(this);

		name.addTextChangedListener(this);
		souji.addTextChangedListener(this);
		result1.addTextChangedListener(this);
		result2.addTextChangedListener(this);
		result3.addTextChangedListener(this);
		dizi.addTextChangedListener(this);

	}

	@Override
	protected void initView() {
	

	}

	private void getCity() {
		getprovince();
		pickDialog = new PickDialog(AddressEditActivity.this, "请选择", new PickDialogListener() {

			@Override
			public void onRightBtnClick() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onListItemLongClick(int position, String string) {

			}

			@Override
			public void onLeftBtnClick() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onListItemClick(int position, String string) {
				String a = result1.getText().toString().trim();
				String b = result2.getText().toString().trim();
				if (nf == 0) {
				

					if (!a.equals(lists.get(position))) {
						result2.setText("");
						result3.setText("");
					}
					result1.setText(lists.get(position));
				} else if (nf == 1) {
					

					if (!b.equals(lists.get(position))) {
						result3.setText("");
					}
					cod = mlist.data[position].cityNo;
					result2.setText(lists.get(position));
				} else if (nf == 2) {
					

					result3.setText(lists.get(position));
				}

			}

		});
		pickDialog.show();

	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.arg1) {
			case 1:
				
				Toast.makeText(AddressEditActivity.this, "添加收货地址成功", 3000).show();
				// queding.setClickable(true);
				Intent intent = new Intent(getApplicationContext(), OrderConfirmActivity.class);
				intent.putExtra("buyOperate", (Serializable) buyOperate);
				intent.putExtra("newAddress", (Serializable) newAddress);

				AddressEditActivity.this.startActivity(intent);
				finish();
				break;
			case 7:
				String msgs = (String) msg.obj;
				Toast.makeText(AddressEditActivity.this, msgs, 3000).show();
				queding.setClickable(true);
				break;
			case 8:
				queding.setClickable(true);
				break;
				
			case 6:
				String str0 =  (String)msg.obj;
				result1.setText(str0);
				break;
			case 9:
				String str =  (String)msg.obj;
				result2.setText(str);
				break;
			case 10:
				String str1 =(String) msg.obj;
				result3.setText(str1);
				
				break;
			default:
				lists.clear();

				for (int i = 0; i < mlist.data.length; i++) {
					lists.add(mlist.data[i].name);
				}

				pickDialog.initListViewData(lists);
				break;
			}

			return false;
		}
	});

	private void initData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpHelper helper = new HttpHelper();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					JSONObject o = new JSONObject();

					
					String result = helper.executeGet("http://uat.wemart.cn/api/configmng/geo", null);
					mlist = gson.fromJson(result, AddressCollectionResponse.class);

					
					if (mlist != null && mlist.data != null && mlist.data.length > 0) {
						province_Name = mlist.data[0].name;
//						result1.setText(province_Name);

						Message msg0 = new Message();
						msg0.obj = mlist.data[0].name;
						msg0.arg1 = 6;
						handler.sendMessage(msg0);
						
						
						o.put("provinceName", province_Name);
						params.add(new BasicNameValuePair("para", o.toString()));
						result = helper.executeGet("http://uat.wemart.cn/api/configmng/geo", params);
						mlist = gson.fromJson(result, AddressCollectionResponse.class);

						
						if (mlist != null && mlist.data != null && mlist.data.length > 0) {
//							result2.setText(mlist.data[0].name);
							
							Message msg = new Message();
							msg.obj = mlist.data[0].name;
							msg.arg1 = 9;
							handler.sendMessage(msg);
							
							cod = mlist.data[0].cityNo;
							params.clear();
							o.put("cityNo", cod);
							params.add(new BasicNameValuePair("para", o.toString()));
							result = helper.executeGet("http://uat.wemart.cn/api/configmng/geo", params);
							mlist = gson.fromJson(result, AddressCollectionResponse.class);
							if (mlist != null && mlist.data != null && mlist.data.length > 0) {
//								result3.setText(mlist.data[0].name);
								
								Message msg1 = new Message();
								msg1.obj = mlist.data[0].name;
								msg1.arg1 = 10;
								handler.sendMessage(msg1);
							}
						}
					}
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		}).start();
	}

	private void getprovince() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject o = new JSONObject();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					String result;
					HttpHelper helper = new HttpHelper();
					o = new JSONObject();
					if (nf == 1) {
						

						params.clear();
						o.put("provinceName", province_Name);
						params.add(new BasicNameValuePair("para", o.toString()));
						result = helper.executeGet("http://uat.wemart.cn/api/configmng/geo", params);
					} else if (nf == 2) {
						

						params.clear();
						o.put("cityNo", cod);
						params.add(new BasicNameValuePair("para", o.toString()));
						result = helper.executeGet("http://uat.wemart.cn/api/configmng/geo", params);
					} else {
						

						params.clear();
						result = helper.executeGet("http://uat.wemart.cn/api/configmng/geo", null);
					}
					mlist = gson.fromJson(result, AddressCollectionResponse.class);
					Message msg = new Message();
					msg.obj = null;
					handler.sendMessage(msg);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();

	}

	@Override
	public void onClick(View v) {
		String r1 = result1.getText().toString().trim();
		String r2 = result2.getText().toString().trim();

		int _id = v.getId();
		if (_id == R.id.wemart_butqd) {
			
			queding.setClickable(false);
			ifo();
		} else if (_id == R.id.btn_wemart_head_left) {
			AddressEditActivity.this.finish();
		} else if (_id == R.id.wemart_result1) {
			
			if (nf != 0) {
				nf = 0;
			}
			getCity();

		} else if (_id == R.id.wemart_result2) {
			

			if (r1.equals("")) {
				Toast.makeText(getApplicationContext(), "请先选择省份", 3000).show();
			} else {
				nf = 1;
				province_Name = result1.getText().toString();
				getCity();
			}
		} else if (_id == R.id.wemart_result3) {
			

			if (r2.equals("")) {
				Toast.makeText(getApplicationContext(), "请先选择城市", 3000).show();
			} else {
				nf = 2;
				getCity();
			}
		} else if (_id == R.id.wemart_sancudizi) {
			new AlertDialog.Builder(AddressEditActivity.this).setMessage("是否确认删除地址")
					.setPositiveButton("是", new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							delete();

						}
					}).setNegativeButton("否", null).show();

		}

	}

	
	private void delete() {
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
					Log.i("TAG1", entity.returnMsg + "|" + entity.returnValue);
					if (entity != null && entity.returnValue == 0) {
						params.clear();
						o = new JSONObject();
						o.put("addrNo", addrNo);

						// params.add(new BasicNameValuePair("para",

						// o.toString()));

						result = helper.executeDelete("http://uat.wemart.cn/api/usermng/buyer/address", o);
						BaseResponse list = gson.fromJson(result, BaseResponse.class);
						Log.i("TAG", list.returnMsg + "||" + list.returnValue);
						if (list.returnValue == 0) {
							finish();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();

	}

	
	private void modify() {
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
					// Log.i("TAG1", entity.returnMsg + "|" +

					// entity.returnValue);

					if (entity != null && entity.returnValue == 0) {
						params.clear();
						o = new JSONObject();
						o.put("addrNo", addrNo);
						// o.put("cityNo", cityNo);

						if (cod == -1) {
							o.put("cityNo", cityNo);
						} else {
							o.put("cityNo", cod);
						}

						o.put("district", result3.getText().toString());
						o.put("streetAddr", dizi.getText());
						o.put("mobileNo", souji.getText());
						o.put("name", name.getText());
						o.put("isDefault", moren.isChecked());
						params.add(new BasicNameValuePair("para", o.toString()));
						result = helper.executePut("http://uat.wemart.cn/api/usermng/buyer/address", params);
						BaseResponse list = gson.fromJson(result, BaseResponse.class);
						Log.i("TAG", list.returnMsg + "|" + list.returnValue);
						if (list.returnValue == 0) {
							finish();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	
	private void ifo() {
		String nae = name.getText().toString().trim();
		String sj = souji.getText().toString().trim();
		String dz = dizi.getText().toString().trim();
		if (nae.equals("") && sj.equals("") && dz.equals("")) {
			Toast.makeText(getApplicationContext(), "输入不能为空", 3000).show();
			queding.setClickable(true);
		} else if (sj.length() < 11) {
			Toast.makeText(getApplicationContext(), "手机号格式不正确", 3000).show();
			queding.setClickable(true);
		} else if (result1.getText().toString().trim().equals("")) {
			Toast.makeText(getApplicationContext(), "请选择省份", 3000).show();
			queding.setClickable(true);
		} else if (result3.getText().toString().trim().equals("")) {
			Toast.makeText(getApplicationContext(), "请选择地区", 3000).show();
			queding.setClickable(true);
		} else if (dz.length() < 3) {
			Toast.makeText(getApplicationContext(), "输入的地址不详细", 3000).show();
			queding.setClickable(true);
		} else if (!d.equals("1")) {
			queding.setBackgroundColor(Color.parseColor("#FF0000"));
			// 添加
			params();
		} else {
			// 修改
			modify();
		}

	}


	
	private void params() {
		dz = dizi.getText().toString().trim();
		sj = souji.getText().toString().trim();
		nae = name.getText().toString().trim();
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
					Log.i("TAG", entity.returnMsg + "|" + entity.returnValue);
					if (entity != null && entity.returnValue == 0) {
						params.clear();

						o = new JSONObject();

						newAddress = new AddressResponse2();
						newAddress.province = result1.getText().toString();
						newAddress.city = result2.getText().toString();
						newAddress.district = result3.getText().toString();
						newAddress.street = dz; // streetAddr
						newAddress.mobileNo = sj;
						newAddress.name = nae;
						newAddress.isDefault = moren.isChecked();

						o.put("cityNo", cod);
						o.put("district", result3.getText().toString());
						o.put("streetAddr", dz);
						o.put("mobileNo", sj);
						o.put("name", nae);
						o.put("isDefault", moren.isChecked());
						params.add(new BasicNameValuePair("para", o.toString()));

						result = helper.executePost("http://uat.wemart.cn/api/usermng/buyer/address", params);
						BaseResponse list = gson.fromJson(result, BaseResponse.class);
						Log.i("TAG", list.returnMsg + "|" + list.returnValue);
						if (list.returnValue == 0) {
							Message msg = new Message();
							msg.arg1 = 1;
							// msg.obj = list.returnMsg;

							handler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.arg1 = 7;
							msg.obj = list.returnMsg;
							handler.sendMessage(msg);
						}

					}

				} catch (Exception ex) {
					ex.printStackTrace();
					Message msg = new Message();
					msg.arg1 = 8;
					handler.sendMessage(msg);
				}
			}
		}).start();

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		// dz = dizi.getText().toString().trim();
		// sj = souji.getText().toString().trim();
		// nae = name.getText().toString().trim();
		// String r1 = result1.getText().toString().trim();
		// String r2 = result2.getText().toString().trim();
		// String r3 = result3.getText().toString().trim();
		//
		// if(dz!=null&&sj.length()==11&&nae!=null){
		// queding.setBackgroundColor(Color.parseColor("#FF0000"));
		// }
		//
	}

	@Override
	public void afterTextChanged(Editable s) {
		
		dz = dizi.getText().toString().trim();
		sj = souji.getText().toString().trim();
		nae = name.getText().toString().trim();
		String r1 = result1.getText().toString().trim();
		String r2 = result2.getText().toString().trim();
		String r3 = result3.getText().toString().trim();

		if (sj.length() == 11 && nae.length() >= 1 && dz.length() >= 1 && r1.length() >= 1 && r2.length() >= 1
				&& r3.length() >= 1) {
			// queding.setBackgroundColor(Color.parseColor("#FF0000"));
			queding.setBackgroundResource(R.drawable.wemart_btn_shape_on);
			queding.setClickable(true);
		} else {
			// queding.setBackgroundColor(Color.parseColor("#DCDCDC"));
			queding.setBackgroundResource(R.drawable.wemart_btn_shape_off);
			queding.setClickable(true);
		}
	}

}
