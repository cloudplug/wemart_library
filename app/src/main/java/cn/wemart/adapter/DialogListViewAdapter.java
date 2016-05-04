package cn.wemart.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.wemart.sdk.R;

public class DialogListViewAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> list;

	public DialogListViewAdapter(Context context, ArrayList<String> lists) {
		this.context = context;
		this.list = lists;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.wemart_blend_dialog_list_item, null);
			holder = new Holder();
			holder.blend_dialog_list_item_textview = (TextView) convertView
					.findViewById(R.id.wemart_blend_dialog_list_item_textview);
			// holder.blend_dialog_list_item_textview=(TextView)
			// convertView.findViewById(R.id.blend_dialog_list_item_textview2);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.blend_dialog_list_item_textview.setText(list.get(position));
		// holder.blend_dialog_list_item_textview2.setText(list.get(position));

		return convertView;
	}

	class Holder {
		TextView blend_dialog_list_item_textview,
				blend_dialog_list_item_textview2;

	}

}
