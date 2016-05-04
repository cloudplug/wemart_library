package cn.wemart.app;

import android.app.Application;

public class App extends Application{
	
	public String headBgColor ;
	public String headtextcolor ;
	public String leftIcon;
	public String headheight;
	
	private static App instance;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		instance = this;
		super.onCreate();
	}
	
	public static App getInstance() {
		if(instance ==null){
			instance = new App();
		}
		return instance;
	}

	public void setheadBgColor(String headBgColor){
		this.headBgColor = headBgColor;
	}
	public String getheadBgColor(){
		return headBgColor;
	}
	
	public void setheadtextcolor(String headtextcolor){
		this.headtextcolor= headtextcolor;
	}
	public String getheadtextcolor(){
		return headtextcolor;
	}
	
	public void setleftIcon(String leftIcon){
		this.leftIcon = leftIcon;
	}
	public String getleftIcon(){
		return leftIcon;
	}
	
	public void setheadheight(String headheight){
		this.headheight = headheight;
	}
	public String getheadheight(){
		return headheight;
		
	}
	
	
}
