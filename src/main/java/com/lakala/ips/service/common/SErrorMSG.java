/*
 * Copyright 2013-2014 lakala.
 */

package com.lakala.ips.service.common;

/**
 *  错误码定义
 * <p>
 * Created on 2015-4-10 
 * <p>
 * @author chenjian
 * @since 2015-4-10
 */
public class SErrorMSG {

	public static final String RS0001 = "RS0001";//连接失败
	
	public static final String RS0002 = "RS0002";//连接超时
	
	public static final String RS0003 = "RS0003";//报文格式有误
	
	public static final String RS0004 = "RS0004";//请求地址有误
	
	public static final String RS0005 = "RS0005";//通讯异常
	
	public static final String RS0006 = "RS0006";//业务关闭
	
	public static final String RS0007 = "RS0007";//渠道关停
	
	public static final String RS0008 = "RS0008";//读超时
	
	public static final String RS0009 = "RS0009";//调用失败，未发起交易
	
	public static final String RS0010 = "RS0010";//执行写卡指令失败
	
	public static final String SP0001 = "SP0001";//账单状态异常
	
	public static final String SP0002 = "SP0002";//支付密码未输入
	
	public static final String F44001 = "F44001";// 单笔限额超限，限额为{0}。

	public static final String F44002 = "F44002";// 单日限额超限，限额为{0}。

	public static final String F44003 = "F44003";// 单月限额超限，限额为{0}。

	public static final String F44004 = "F44004";// 单日交易次数超限，已交易{0}次。

	public static final String F44005 = "F44005";// 单月交易次数超限，已交易{0}次。

	public static final String F44006 = "F44006";//交易{0}未定义限额类型

	public static final String F44007 = "F44007";//交易最低金额为{0}

	public static final String F44008 = "F44008";//限额参数缺失
	
	public static final String F44009 = "F44009";//储蓄卡交易最低金额为{0}
	
	public static final String F44010 = "F44010";//储蓄卡单笔交易金额为{0}

	public static final String F44011 = "F44011";//当前上传数为{},超过最大上传数{}

	public static final String SW0001 = "SW0001";//提现不支持该银行
	
	
	public static final String BA0001 = "BA0001";//充值接口初始错误，请退款
	public static final String BA0002 = "BA0002";//补充自申请异常，请手工补充值申请
	public static final String BA0003 = "BA0003";//错误记录billo
	public static final String BA0004 = "BA0004";//有充值记录不允许发充值接口
	public static final String BA0005 = "BA0005";//未扣款
	public static final String BA0006 = "BA0006";//"待审核通过，手工发起补充值"
	public static final String BA1001 = "BA1001";//"充值异常请发送补充值申请"
	public static final String BA1002 = "BA1002";//"查询无数据"
	public static final String BA1003 = "BA1003";// 充值失败
	public static final String BA2001 = "BA2001";//
	
	
	public static final String FG0001 = "FG0001";// 无红包账户
	public static final String FP0001 = "FP0001";// 无法获取消息
	
	public static final String F52005 = "F52005";// 无法获取消息
	
	
	public static final String CB0001 = "CB0001";// 光大更新MAC密钥失败
	public static final String CB0002 = "CB0002";// 光大秘钥已更新
	public static final String CB0003 = "CB0003";// 解析55域异常
	public static final String CB0004 = "CB0004";//激活失败
	public static final String CB0005 = "CB0005";//已经激活
	public static final String CB0006 = "CB0006";//圈存失败
	public static final String CB0007 = "CB0007";//冲正失败
	
	public static final String SZ0001 = "SZ0001";//mac校验失败
	public static final String SZ0002 = "SZ0002";//解密失败
	public static final String SZ0003 = "SZ0003";//加密失败
	public static final String SZ0004 = "SZ0004";//mac计算失败
	public static final String SZ0005 = "SZ0005";//重复充值错误

	/*------------------------中银通脱机错误码------------------*/
	public static final String ZYT001 = "ZYT001";//账单信息有误
	public static final String ZYT002 = "ZYT002";//非法账单号
	public static final String ZYT101 = "ZYT101";//证件信息有误
	
	public static final String SM0001 = "SM0001";//短信验证码过期
	public static final String SM0002 = "SM0002";//短信验证码有误
	public static final String SM0003 = "SM0003";//短信验证码发送太频繁，请稍后再试
	public static final String SM0004 = "SM0004";//短信验证码发送失败，请稍后再试
		
	public static final String ZFB001 = "ZFB001";//签名失败
	
	public static final String MZK001 = "MZK001";//报文校验失败

	public static final String CPY001 = "CPY001";//优惠券错误，无此优惠券
	public static final String CPY002 = "CPY002";//优惠券无效
	public static final String CPY003 = "CPY003";//优惠券不可在此业务中使用
	public static final String CPY004 = "CPY004";//优惠券已被领完
	public static final String CPY005 = "CPY005";//已经领取优惠券
	public static final String CPY006 = "CPY006";//支付金额小于优惠券金额
	public static final String CPY007 = "CPY007";//优惠券不适用此支付方式
	
	public static final String BPY001 = "BPY001";//支付金额错误
	



	public static final String HF0001 = "HF0001";// 合肥通更新MAC密钥失败
	public static final String HF0002 = "HF0002";// 合肥通秘钥已更新
}
