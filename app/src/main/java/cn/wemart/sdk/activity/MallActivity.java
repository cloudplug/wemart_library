package cn.wemart.sdk.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.wemart.adapter.MenuAdapter;
import cn.wemart.app.App;
import cn.wemart.pojo.MenuEntity;
import cn.wemart.pojo.MenuItemEntity;
import cn.wemart.pojo.ShareEntity;
import cn.wemart.sdk.R;
import cn.wemart.tools.VolleyLoadPicture;
import cn.wemart.tools.WebViewJavascriptBridge;
import cn.wemart.widget.PopMenus;
import cn.wemart.widget.ShareSDKDialog;

@SuppressLint("SetJavaScriptEnabled")
public class MallActivity extends Activity implements OnClickListener {
	private ImageView btn_left;
	private WebView webView;
	private ShareEntity shareEntity;
	private PopMenus popupWindow_custommenu;
//	private String startLoadUrl = "http://dev.wemart.cn/mobile/?chanId=110&sellerId=1&a=shelf&m=index&scenType=1&userId=222&appId=72&sign=SY8Gs4GtyUydXIAsd8Z6/BQkPxaOgJPFkg5ai10likh1o2JzxLYhBeu7HJMplDOKPz4nUEuS4z5urwgwry55y0amjKNTVFuvcEyGneG4w7Uyq9EYWYinuCvfYMP71GCYSVs31waGMVpDfTF2Fpr2jHm+lvldG6GaYr720+/+17o=";
	private String startLoadUrl = "http://uat.wemart.cn/mobile/?chanId=782&sellerId=137&a=shelf&m=index&scenType=1&userId=13916394598&appId=76&sign=M+hzBxrAiltMjMaW1lj3/WSEbSDlHXNokKqfv1zUNYshAyA3qlrF5JDX7Xb/RN68IlQpLfSW3J9CGoKJ8kM+gwm1mZ3VWoICgYTNwMurrUlaOf7cWrssOtJoSxh8tM/vBHyzxhTcES20cO1mVwwNTZoxIkyQYNVWzfp33JUYnRE=";
	private Handler handler = new Handler(new Callback() {

		@SuppressLint("InflateParams")
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				// 菜单绑定
				GridView gv_menu;
				MenuAdapter menuAdapter;
				List<MenuItemEntity> menus = new ArrayList<MenuItemEntity>();
				LinearLayout normal_menu, wechat_menu;
				normal_menu = (LinearLayout) findViewById(R.id.wemart_ac_normal_menu);
				wechat_menu = (LinearLayout) findViewById(R.id.wemart_ac_wechat_menu);

				final MenuEntity menuEntity = (MenuEntity) msg.obj;
				if (menuEntity.style == 0) {
					// 不显示

					menus.clear();
					normal_menu.setVisibility(View.GONE);
					wechat_menu.setVisibility(View.GONE);
				} else if (menuEntity.style == 1) {
					// 普通菜单
					gv_menu = (GridView) findViewById(R.id.wemart_ac_gv_menu);
					gv_menu.setOnTouchListener(new OnTouchListener() {

						@SuppressLint("ClickableViewAccessibility")
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							return MotionEvent.ACTION_MOVE == event.getAction() ? true : false;
						}
					});
					menuAdapter = new MenuAdapter(MallActivity.this, menus);
					gv_menu.setAdapter(menuAdapter);

					menus.clear();
					for (int i = 0; i < menuEntity.menu.length; i++) {

						menus.add(menuEntity.menu[i]);
					}
					int count = menuAdapter.getCount();
					gv_menu.setNumColumns(count);
					menuAdapter.notifyDataSetChanged();
					normal_menu.setVisibility(View.VISIBLE);
					wechat_menu.setVisibility(View.GONE);
				} else if (menuEntity.style == 2) {
					// 微信菜单
					LinearLayout layout_exchange, layout_customemenu, layout_custommenu;
					layout_customemenu = (LinearLayout) findViewById(R.id.wemart_ac_layout_customemenu);
					layout_custommenu = (LinearLayout) findViewById(R.id.wemart_ac_layout_custommenu);
					layout_exchange = (LinearLayout) findViewById(R.id.wemart_ac_layout_exchange);
					layout_customemenu.setVisibility(View.VISIBLE);
					layout_custommenu.removeAllViews();
					layout_exchange.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO 自动生成的方法存根
							if (menuEntity.menu.length > 0)
								webView.loadUrl(menuEntity.menu[0].url);
						}
					});

					for (int i = 0; i < menuEntity.menu.length; i++) {
						LinearLayout layout = (LinearLayout) ((LayoutInflater) getSystemService(
								Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.wemart_wechatmenu_item, null);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT, 1.0f);
						layout.setLayoutParams(lp);
						TextView tv_custommenu_name = (TextView) layout
								.findViewById(R.id.tv_wemart_wechatmenu_item_custommenu_name);
						tv_custommenu_name.setText(menuEntity.menu[i].name);
						if (menuEntity.menu[i].sub_button != null && menuEntity.menu[i].sub_button.length > 0) // 显示三角

						{
							tv_custommenu_name.setCompoundDrawablesWithIntrinsicBounds(0,
									R.drawable.wemart_wechatmenu_ic_arrow_up_black, 0, 0);
						} else {
							// 隐藏三角
							tv_custommenu_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
						}
						final int position = i;
						layout.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								try {

									if (menuEntity.menu[position].sub_button == null
											|| menuEntity.menu[position].sub_button.length == 0) {
										// 这里做webview的跳转
										webView.loadUrl(menuEntity.menu[position].url);
									} else {
										JSONArray sub_button = new JSONArray();
										for (int j = 0; j < menuEntity.menu[position].sub_button.length; j++) {
											JSONObject item = new JSONObject();
											item.put("name", menuEntity.menu[position].sub_button[j].name);
											item.put("url", menuEntity.menu[position].sub_button[j].url);
											sub_button.put(item);
										}
										popupWindow_custommenu = new PopMenus(MallActivity.this, sub_button,
												v.getWidth(), 0);// v.getWidth()
																	// + 10
										popupWindow_custommenu.showAtLocation(v);
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block

									e.printStackTrace();
								}
							}
						});
						layout_custommenu.addView(layout);
					}

					normal_menu.setVisibility(View.GONE);
					wechat_menu.setVisibility(View.VISIBLE);
				}

			} else if (msg.arg1 == 2) {
				// 菜单点击
				webView.loadUrl(msg.obj.toString());
			} else if (msg.arg1 == 3) {
				// 分享
				JSONObject entity = (JSONObject) msg.obj;
				try {
					shareEntity = new ShareEntity();
					shareEntity.title = entity.getString("title");
					shareEntity.description = entity.getString("description");
					shareEntity.thumbUrl = entity.getString("thumbUrl");
					shareEntity.webpageUrl = entity.getString("webpageUrl");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.arg1 == 7) {
				String toastmsg = (String) msg.obj;
				Toast.makeText(MallActivity.this, toastmsg, Toast.LENGTH_SHORT).show();
			}

			return false;
		}
	});

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wemart_ac_mall);

		Intent intent = getIntent();
		if (intent != null && !TextUtils.isEmpty(intent.getStringExtra("startLoadUrl")))
			startLoadUrl = intent.getStringExtra("startLoadUrl");

		btn_left = (ImageView) findViewById(R.id.btn_wemart_head_left);
		btn_left.setOnClickListener(this);

		webView = (WebView) findViewById(R.id.wv_wemart_ac);

		// Enable JavaScript
		WebSettings set = webView.getSettings();
		set.setJavaScriptEnabled(true);
		set.setJavaScriptCanOpenWindowsAutomatically(true);
		set.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);

		// Enable database
		// We keep this disabled because we use or shim to get around
		// DOM_EXCEPTION_ERROR_16
		String databasePath = getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
		set.setDatabaseEnabled(true);
		set.setDatabasePath(databasePath);

		// Enable DOM storage
		set.setDomStorageEnabled(true);

		// Enable AppCache
		// Fix for CB-2282
		set.setAppCacheMaxSize(5 * 1048576);
		set.setAppCachePath(databasePath);
		set.setAppCacheEnabled(true);

		// set.setAppCacheEnabled(true);
		// set.setBuiltInZoomControls(true);
		// set.setCacheMode(WebSettings.LOAD_DEFAULT);

		// set.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// set.setLoadWithOverviewMode(true);
		// set.setSupportZoom(true);
		// set.setUseWideViewPort(true);

//		webView.setWebChromeClient(new WebChromeClient());
//		webView.loadUrl("file:///android_asset/jsdemo.html");
		
		
		webView.loadUrl(startLoadUrl);
		webView.addJavascriptInterface(new WebViewJavascriptBridge(this), "WebViewJavascriptBridge");

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				view.loadUrl(url);
				return true;
			}

		});

		// String pix = "83";
		// DisplayMetrics metric = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(metric);
		// int width = metric.widthPixels; // 屏幕宽度（像素）
		// Toast.makeText(
		// this,
		// DensityUtil.px2dip(MallActivity.this, Float.parseFloat(pix)
		// / 750 * width)
		// + "dp", Toast.LENGTH_SHORT).show();
//		setHeader("测试", "#DCDCDC", null, null, "#FF0000", "80/720");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_wemart_head_left) {
			// 返回
			// Toast.makeText(MallActivity.this,
			// webView.copyBackForwardList().getSize() + "",
			// Toast.LENGTH_SHORT).show();

			if (webView.canGoBack())
				webView.goBack();
			else
				finish();
		} else if (v.getId() == R.id.btn_wemart_head_share) {
			// 分享
			Intent intent = new Intent();
			intent.putExtra("shareEntity", (Serializable) shareEntity);
			intent.setClass(MallActivity.this, ShareSDKDialog.class);
			startActivity(intent);
		}
	}

	/**
	 * 设置分享内容
	 * 
	 * @param entity
	 */
	public void setShareContent(JSONObject entity) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.obj = entity;

		handler.sendMessage(msg);
	}

	public void setHeader(String head, String headSize, String headBgColor, String leftIcon, String rightIcon,
			String headtextcolor, String headheight) {

		setHeader(head, headBgColor, leftIcon, rightIcon, headtextcolor, headheight);
		TextView tv_title = (TextView) findViewById(R.id.tv_wemart_head_title);
		float size = Float.parseFloat(headSize);
		tv_title.setTextSize(size);

	}

	/**
	 * 设置头部样式
	 * 
	 * @param head
	 * @param headBgColor
	 * @param leftIcon
	 * @param rightIcon
	 */
	public  void setHeader(String head, String headBgColor, String leftIcon, String rightIcon, String headtextcolor,
			String headheight) {
		RelativeLayout bg_head = (RelativeLayout) findViewById(R.id.wemart_head);
		TextView tv_title = (TextView) findViewById(R.id.tv_wemart_head_title);
		ImageView btn_head_share = (ImageView) findViewById(R.id.btn_wemart_head_share);

		if (headBgColor != null && !"".equals(headBgColor)) {
			bg_head.setBackgroundColor(Color.parseColor(headBgColor));
		}
			
		tv_title.setText(head);

		if (headtextcolor != null && !"".equals(headtextcolor)) {
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

		if (leftIcon != null && !"".equals(leftIcon)) {
			btn_left.setVisibility(View.VISIBLE);
			VolleyLoadPicture vlp = new VolleyLoadPicture(getApplicationContext(), btn_left);
			vlp.getmImageLoader().get(leftIcon, vlp.getOne_listener());

		}

		if (rightIcon != null && !"".equals(rightIcon)) {
			btn_head_share.setVisibility(View.VISIBLE);
			VolleyLoadPicture vlp = new VolleyLoadPicture(getApplicationContext(), btn_head_share);
			vlp.getmImageLoader().get(rightIcon, vlp.getOne_listener());
			btn_head_share.setOnClickListener(this);
		}

		
		App.getInstance().setheadBgColor(headBgColor);
		App.getInstance().setheadtextcolor(headtextcolor);
		App.getInstance().setleftIcon(leftIcon);
	}

	/**
	 * 设置底部菜单
	 * 
	 * @param menuEntity
	 */
	public void createMenu(MenuEntity menuEntity) {
		// 测试
		// menuEntity.style = 2;

		Message msg = new Message();
		msg.arg1 = 1;
		msg.obj = menuEntity;
		handler.sendMessage(msg);
	}

	/**
	 * 点击菜单事件
	 * 
	 * @param url
	 */
	public void clickMenu(String url) {
		if (url != null && url.length() > 0) {
			Message msg = new Message();
			msg.arg1 = 2;
			msg.obj = url;

			handler.sendMessage(msg);
		}
	}

}
