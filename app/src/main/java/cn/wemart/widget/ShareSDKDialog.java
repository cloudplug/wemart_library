package cn.wemart.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import cn.wemart.pojo.ShareEntity;
import cn.wemart.sdk.R;

public class ShareSDKDialog extends Activity implements View.OnClickListener {

	private LinearLayout ll_share_qq, ll_shared_sina, ll_shared_weixin,
			ll_shared_friend, ll_shared_cancle;
	private ShareEntity shareEntity; // 分享的实体

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wemart_ac_share_dialog);

		Window window = getWindow();
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		shareEntity = (ShareEntity) getIntent().getSerializableExtra(
				"shareEntity");

		initView();
	}

	private void initView() {
		ll_shared_cancle = (LinearLayout) findViewById(R.id.ll_wemart_ac_shared_cancle);
		ll_share_qq = (LinearLayout) findViewById(R.id.ll_wemart_ac_shared_qq);
		ll_shared_sina = (LinearLayout) findViewById(R.id.ll_wemart_ac_shared_sina);
		ll_shared_weixin = (LinearLayout) findViewById(R.id.ll_wemart_ac_shared_weixin);
		ll_shared_friend = (LinearLayout) findViewById(R.id.ll_wemart_ac_shared_friend);
		ll_share_qq.setOnClickListener(this);
		ll_shared_sina.setOnClickListener(this);
		ll_shared_weixin.setOnClickListener(this);
		ll_shared_friend.setOnClickListener(this);
		ll_shared_cancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int _id = v.getId();
		if (_id == R.id.ll_wemart_ac_shared_sina) {

		} else if (_id == R.id.ll_wemart_ac_shared_qq) {

		} else if (_id == R.id.ll_wemart_ac_shared_weixin) {

		} else if (_id == R.id.ll_wemart_ac_shared_friend) {

		} else if (_id == R.id.ll_wemart_ac_shared_cancle) {
			finish();
		}
	}
}