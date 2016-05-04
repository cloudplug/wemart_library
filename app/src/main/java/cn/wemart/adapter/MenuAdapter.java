package cn.wemart.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.wemart.pojo.MenuItemEntity;
import cn.wemart.sdk.R;
import cn.wemart.sdk.activity.MallActivity;
import cn.wemart.tools.VolleyLoadPicture;

public class MenuAdapter extends BaseAdapter {
	private int len = 0;
	private List<MenuItemEntity> mList = new ArrayList<MenuItemEntity>();
	private MallActivity mContext;

	public MenuAdapter(MallActivity context, List<MenuItemEntity> mList) {
		this.mContext = context;
		this.mList = mList;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			convertView = layoutInflater.inflate(
					R.layout.wemart_custommenu_gridview_item, null);
			holder = new ViewHolder();
			holder.rl_h1 = (RelativeLayout) convertView
					.findViewById(R.id.rl_wemart_custommenu_gridview_item_h1);
			holder.iv_icon = (ImageView) convertView
					.findViewById(R.id.iv_wemart_custommenu_gridview_item_icon);
			holder.v_line = (View) convertView
					.findViewById(R.id.v_wemart_custommenu_gridview_item_splitline);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		len = mList.size();
		final MenuItemEntity item = mList.get(position);
		VolleyLoadPicture vlp = new VolleyLoadPicture(this.mContext,
				holder.iv_icon);
		vlp.getmImageLoader().get(item.icon, vlp.getOne_listener());

		if (position == len - 1) {
			holder.v_line.setVisibility(View.GONE);
		}

		holder.rl_h1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.clickMenu(item.url);
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView iv_icon;
		RelativeLayout rl_h1;
		View v_line;
	}

}
