package cn.wemart.sdk.activity;

import java.util.LinkedList;
import java.util.List;

import cn.wemart.app.App;
import cn.wemart.sdk.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseActivity extends FragmentActivity {
	public static LinkedList<Activity> allActivitys = new LinkedList<Activity>();
	private ProgressDialog mProgressDialog;

	/**
	 * 
	 * 绑定控件id
	 */
	protected abstract void findViewById();

	/**
	 * 
	 * 为控件设置监听
	 */
	protected abstract void setListeners();

	/**
	 * 
	 * 初始化控件
	 */
	protected abstract void initView();

	protected void templatedMethod() {
		findViewById();
		setListeners();
		initView();
	}

	protected NotificationManager notificationManager;
	
	
	public void setHead(String headBgColor,String headtextcolor,String leftIcon){
		App.getInstance().setheadBgColor(headBgColor);
		App.getInstance().setheadtextcolor(headtextcolor);
//		App.getInstance().setleftIcon(leftIcon);
	}

	public void setHead(HeadType headType, String head, int leftResId, String right, OnClickListener onClickListener) {
		TextView tv_title = (TextView) findViewById(R.id.tv_wemart_head_title); // 中间标题

		ImageView btn_left = (ImageView) findViewById(R.id.btn_wemart_head_left); // 在边返回键

		// TextView btn_right = (TextView) findViewById(R.id.tv_head_share); //

		// 右边

		ImageView btn_head_share = (ImageView) findViewById(R.id.btn_wemart_head_share); // 右边

		// findViewById(R.id.tv_title_message).setVisibility(View.GONE);

		tv_title.setText(head);

		if (headType == HeadType.NORMAL) {

			btn_left.setVisibility(View.VISIBLE);
			// btn_right.setVisibility(View.VISIBLE);

			btn_head_share.setVisibility(View.VISIBLE);
			tv_title.setVisibility(View.VISIBLE);
			if (leftResId < 0) {
				btn_left.setVisibility(View.INVISIBLE);
			} else {
				btn_left.setVisibility(View.VISIBLE);
				btn_left.setImageResource(leftResId);
				btn_left.setOnClickListener(onClickListener);

			}
			if (right != null && right.length() > 0) {
				btn_head_share.setOnClickListener(onClickListener);
				// btn_right.setText(right);

				// btn_right.setOnClickListener(onClickListener);

			} else {
				btn_head_share.setVisibility(View.GONE);
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		allActivitys.remove(this);
		System.gc();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// setBarTintColor();

		allActivitys.add(this);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	/**
	 * 
	 * 返回
	 * 
	 *
	 * 
	 * @param view
	 */
	public void back(View view) {

		finish();
	}

	public static void exit() {
		for (Activity activity : allActivitys) {
			activity.finish();
		}
		allActivitys.clear();
		// 这个主要是用来关闭进程的, 光把所有activity finish的话，进程是不会关闭的

		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void stopWaiting() {
		if (null != mProgressDialog && !isFinishing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	public void finishAllActivity() {
		for (int i = 0, size = allActivitys.size(); i < size; i++) {
			if (null != allActivitys.get(i)) {
				allActivitys.get(i).finish();
			}
		}
		allActivitys.clear();
	}

	/*
	 * 
	 * public void setBarTintColor() { if (Build.VERSION.SDK_INT >= 21) {
	 * 
	 * getWindow().addFlags(WindowManager.LayoutParams.
	 * 
	 * FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
	 * 
	 * getWindow().setStatusBarColor(Color.parseColor("#f82981e5")); } }
	 */

	/**
	 * 
	 * 获取当前任务栈栈顶上的Activity
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getTopActivity(Activity context) {
		String className = "";
		ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		if (runningTaskInfos != null) {
			ComponentName t = runningTaskInfos.get(0).topActivity;
			className = t.getClassName();
			return className;
		} else
			return className;
	}

	/**
	 * 
	 * 程序是否在前台运行
	 * 
	 *
	 * 
	 * @return boolean
	 */
	public boolean isAppOnForeground() {
		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.

			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

}
