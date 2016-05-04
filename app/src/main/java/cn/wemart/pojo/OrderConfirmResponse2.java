package cn.wemart.pojo;

/**
 * @author Administrator
 *	返回的Data
 */	
public class OrderConfirmResponse2 {
	/**商品是否可买检测结果 */
	public OrderConfirmSubResponse1 checkGoodsIsSale; 
	/**拆单后的订单列表*/
	public OrderConfirmSubResponse21[] order;
	/**支付方式*/
	public OrderConfirmSubResponse31[] payTypeList;
	/**收货信息*/
	public OrderConfirmSubResponse4 receive; // 收货信息
	/** 订单总金额 精确到分*/
	public long totalMoney; 

}
