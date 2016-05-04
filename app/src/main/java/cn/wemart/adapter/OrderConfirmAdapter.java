package cn.wemart.adapter;

import java.text.DecimalFormat;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.wemart.pojo.AddressResponse2;
import cn.wemart.pojo.BuyerEntity;
import cn.wemart.pojo.OrderConfirmSubResponse21;
import cn.wemart.sdk.R;
import cn.wemart.widget.ListViewForScrollView;

/**
 * @author Administrator
 *	确认订单适配器
 */
public class OrderConfirmAdapter extends BaseAdapter {

	// private OrderConfirmSubResponse21[] mList = null;

	private List<OrderConfirmSubResponse21> mList = null;
	private Context context;
	private JSONObject checkGoodsIsSale;
	private Gson gson = new Gson();
	private ListViewForScrollView lv2;
	private OrderConfirmItemAdapter ItemAdapt;
	private AddressResponse2 newAddress;
	private BuyerEntity buyer;
	private DecimalFormat moneyFormat = new DecimalFormat("0.00");

	public void setCheckGoodsIsSale(JSONObject checkGoodsIsSale) {
		this.checkGoodsIsSale = checkGoodsIsSale;
	}

	public OrderConfirmAdapter(Context context, List<OrderConfirmSubResponse21> mDatas, BuyerEntity buyer,
			AddressResponse2 newAddress) {
		this.context = context;
		this.mList = mDatas;
		this.checkGoodsIsSale = checkGoodsIsSale;
		this.newAddress = newAddress;
		this.buyer = buyer;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.wemart_item_order_confirm, null);

			vh.tv_sendname = (TextView) convertView.findViewById(R.id.tv_wemart_sendname);// 发货

			vh.tv_freight = (TextView) convertView.findViewById(R.id.tv_wemart_freight);// 运费

			vh.et_message = (EditText) convertView.findViewById(R.id.et_wemart_message);// 留言

			vh.tv_price = (TextView) convertView.findViewById(R.id.tv_wemart_itemprice);// 价格

			vh.tv_piece = (TextView) convertView.findViewById(R.id.tv_wemart_itempiece);

			vh.lv_order2 = (ListViewForScrollView) convertView.findViewById(R.id.lv_wemart_order2);

			// vh.tv_cancel = (TextView)
			// convertView.findViewById(R.id.tv_cancel);
			// vh.tv_goPay = (TextView) convertView.findViewById(R.id.tv_goPay);

			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		
		
		OrderConfirmSubResponse21 item = mList.get(position);
		// sGoodEntity item = mList.goodsList[position];

		vh.tv_sendname.setText(item.groupName);
		vh.tv_freight.setText("¥" + moneyFormat.format(Double.parseDouble(item.fareMoney + "") / 100) + "");
		vh.tv_price.setText(moneyFormat.format(item.totalPrice / 100));
		vh.tv_piece.setText("共" + item.goodsNum + "件商品"); // 购买商品

		final EditText et_message = vh.et_message;
		vh.et_message.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String str = et_message.getText().toString();
				mList.get(position).remarkContent = str;
			}
		});
		vh.et_message.setOnFocusChangeListener(new

		android.view.View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// 此处为失去焦点时的处理
					// item.remarkContent="";
				}
			}
		});

		ItemAdapt = new OrderConfirmItemAdapter(context, mList, item.goods, checkGoodsIsSale, buyer, newAddress);
		vh.lv_order2.setAdapter(ItemAdapt);

		return convertView;
	}

	private final class ViewHolder {
		TextView tv_sendname, tv_freight, tv_price, tv_piece;
		LinearLayout ll_order;
		EditText et_message;
		ListViewForScrollView lv_order2;
	}

}
