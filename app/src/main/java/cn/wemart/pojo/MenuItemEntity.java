package cn.wemart.pojo;

import java.io.Serializable;

public class MenuItemEntity implements Serializable {
	
	public String name;
	public String icon;
	public String url;
	public MenuItemEntity[] sub_button;
}
