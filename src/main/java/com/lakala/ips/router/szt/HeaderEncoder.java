/*
 * Copyright 2013-2014 lakala.
 */

package com.lakala.ips.router.szt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import os.juno.util.ByteArrayType;

import com.lakala.amber.AmberException;
import com.lakala.amber.channel.Transform;
import com.lakala.amber.core.Context;
import com.lakala.amber.core.CoreException;
import com.lakala.amber.transform.bytes.util.BytesOutputStream;
import com.lakala.amber.util.Hex;
import com.lakala.ips.service.common.SErrorMSG;


/**
 *  mac calculation
 * <p>
 * Created on 2015年12月14日 
 * <p>
 * @author chenjian
 * @since 2015年12月14日
 */
public class HeaderEncoder implements Transform{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MacUtil macUtil;
	
	private boolean IsEncrpyt = true;

	@Override
	public boolean support(Object in) {
		if(in instanceof byte[]){
			return true;
		}
		return false;
	}

	@Override
	public Object transform(Object in, Context ctx) throws AmberException {
		//in 为format后的bytes，不包mac值，且body为明文
		byte[] inBytes = (byte[]) in;
		
		//计算mac 源数据从 Command_Id 至报文结束,此时数据为明文
		int toMacBytesLength = inBytes.length-4-1;
		byte[] toMacBytes = new byte[toMacBytesLength];
		System.arraycopy(inBytes, 5, toMacBytes, 0, inBytes.length-5);
		logger.debug("to be mac {}",Hex.encode(toMacBytes));
		byte[] mac = macUtil.mac(toMacBytes);
		logger.debug("mac {}",Hex.encode(mac));
		
		//des body 对body进行加密
		int bodyBytesLength = inBytes.length-4-1-4-8-8;
		byte[] bodyBytes = new byte[bodyBytesLength];
		System.arraycopy(inBytes, inBytes.length-bodyBytesLength, bodyBytes, 0, bodyBytesLength);
		if(IsEncrpyt){
			try{
				logger.info("原数据 ： {}",Hex.encode(bodyBytes));
				bodyBytes = macUtil.encrypt(bodyBytes);
				logger.info("加密后： {}",Hex.encode(bodyBytes));
				
			}catch(Exception e){
				throw new CoreException(SErrorMSG.SZ0003,"encrypt fail",e);
			}
		}
		
		//header length is 33
		int packetLength = 33 + bodyBytes.length;
		byte[] headerwithoutMac = new byte[20];
		System.arraycopy(inBytes, 5, headerwithoutMac, 0, 20);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bos.write(ByteArrayType.toBytes(packetLength));
			bos.write(inBytes[4]);//version
			bos.write(mac);
			bos.write(headerwithoutMac);
			bos.write(bodyBytes);
		} catch (IOException e) {
			throw new CoreException(SErrorMSG.SZ0004,"calculate mac error", e);
		}
		return bos.toByteArray();
	}
	
	
}
