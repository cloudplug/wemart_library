package cn.wemart.pojo;

import java.io.Serializable;

public class BuyOperateEntity implements Serializable {
	public BuyerEntity buyer;
	public String addressOperate;
	public GoodEntity[] goodsList;
}
