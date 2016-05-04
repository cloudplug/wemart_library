package cn.wemart.pojo;

public class OrderConfirmSubResponse21 {
	/**分销商Id*/
	public String commSellerId;
	/**拆单的中运费*/
	public int fareMoney;
	/**商品列表*/
	public OrderGoodSubIResponse[] goods; 
	/**商品数量*/
	public int goodsNum;
	/**分组名称*/
	public String groupName; 
	/**供货商ID*/
	public String supplySellerId;  
	/**拆单的总金额*/
	public Double totalPrice;
	/**备注提示*/
	public String remarkContent; 
	/** 备注选择列表*/
	public OrderRemarkSubIResponse[] remarkList;   


}
