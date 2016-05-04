package cn.wemart.pojo;

/**
 * @author Administrator
 *	确认订单 返回结果
 */
public class OrderConfirmResponse1 {
	/**返回的数据 */
	public OrderConfirmResponse2 data;
	/**返回的值 0 为找到了地址 2014为没有找到地址，需要新增收获地址*/
	public int returnValue; 
	/**返回的信息*/
	public String returnMsg;
}
