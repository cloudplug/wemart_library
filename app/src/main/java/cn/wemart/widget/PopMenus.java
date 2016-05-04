package cn.wemart.widget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import cn.wemart.sdk.R;
import cn.wemart.sdk.activity.MallActivity;

public class PopMenus {
	private JSONArray jsonArray;
	private MallActivity context;
	private PopupWindow popupWindow;
	private LinearLayout listView;
	private int width, height;
	private View containerView;

	public PopMenus(MallActivity context, JSONArray _jsonArray, int _width,
			int _height) {
		this.context = context;
		this.jsonArray = _jsonArray;
		this.width = _width;
		this.height = _height;
		containerView = LayoutInflater.from(context).inflate(
				R.layout.wemart_wechatmenu_popmenus, null);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		containerView.setLayoutParams(lp);
		// 设置 listview
		listView = (LinearLayout) containerView
				.findViewById(R.id.wemart_wechatmenu_layout_subcustommenu);
		try {
			setSubMenu();
		} catch (JSONException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		listView.setBackgroundColor(Color.argb(0xff, 0x64, 0x64, 0x64));
		listView.setFocusableInTouchMode(true);
		listView.setFocusable(true);

		popupWindow = new PopupWindow(containerView,
				width == 0 ? LayoutParams.WRAP_CONTENT : width,
				height == 0 ? LayoutParams.WRAP_CONTENT : height);
	}

	// 下拉式 弹出 pop菜单 parent 右下角
	public void showAsDropDown(View parent) {
		popupWindow.setBackgroundDrawable(new ColorDrawable());
		popupWindow.showAsDropDown(parent);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 刷新状态
		popupWindow.update();
		popupWindow.setOnDismissListener(new OnDismissListener() {
			// 在dismiss中恢复透明度
			@Override
			public void onDismiss() {
			}
		});
	}

	public void showAtLocation(View parent) {
		popupWindow.setBackgroundDrawable(new ColorDrawable());
		containerView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int[] location = new int[2];
		parent.getLocationOnScreen(location);
		int x = location[0] - 25;
		int y = parent.getHeight() + 10;// - (parent.getHeight() / 3)

		// Utils.toast(context, y +""); //location[1] - popupHeight -
		// parent.getHeight()
		popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.BOTTOM, x, y);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 刷新状态
		popupWindow.update();
		popupWindow.setOnDismissListener(new OnDismissListener() {
			// 在dismiss中恢复透明度
			@Override
			public void onDismiss() {
			}
		});
	}

	// 隐藏菜单
	public void dismiss() {
		popupWindow.dismiss();
	}

	void setSubMenu() throws JSONException {
		listView.removeAllViews();
		for (int i = 0; i < jsonArray.length(); i++) {
			final JSONObject ob = jsonArray.getJSONObject(i);
			LinearLayout layoutItem = (LinearLayout) ((LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.wemart_wechatmenu_popmenus_menuitem, null);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
			containerView.setLayoutParams(lp);
			layoutItem.setFocusable(true);
			TextView tv_funbtntitle = (TextView) layoutItem
					.findViewById(R.id.wemart_wechatmenu_popmenus_menuitem_textView);
			View pop_item_line = layoutItem
					.findViewById(R.id.wemart_wechatmenu_popmenus_menuitem_splitline);
			if ((i + 1) == jsonArray.length()) {
				pop_item_line.setVisibility(View.GONE);
			}
			tv_funbtntitle.setText(ob.getString("name"));
			layoutItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Toast.makeText(context, "子菜单点击事件", 0).show();

					try {
						context.clickMenu(ob.getString("url"));
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}

					dismiss();
				}
			});
			listView.addView(layoutItem);
		}
		listView.setVisibility(View.VISIBLE);
	}

}
