package cn.wemart.pojo;

import org.json.JSONObject;

/**
 * @author Administrator
 *	商品是否可买的校对结果
 */
public class OrderConfirmSubResponse1 {
	/**商品的可购买状态*/
	public JSONObject goodsResult;// OrderConfirmSubItemResponse
	/**是否可买 true 可买；false 不可买*/
	public boolean status; 
}
