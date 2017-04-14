/*
 * Copyright 2013-2014 lakala.
 */

package com.lakala.ips.router.szt;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import os.juno.util.Hex;

import com.lakala.amber.AmberException;
import com.lakala.amber.channel.Transform;
import com.lakala.amber.core.Context;
import com.lakala.amber.core.CoreException;
import com.lakala.ips.service.common.SErrorMSG;


/**
 *  header parser
 * <p>
 * Created on 2015年12月14日 
 * <p>
 * @author chenjian
 * @since 2015年12月14日
 */
public class HeaderDecoder implements Transform {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MacUtil macUtil;
	
	private boolean IsEncrypt = true;
	
	@Override
	public boolean support(Object in) {
		if (in instanceof byte[]) {
			return true;
		}
		return false;
	}

	@Override
	public Object transform(Object in, Context ctx) throws AmberException {
		if(!IsEncrypt){
			return in;
		}
		byte[] bytes = (byte[]) in;
		byte[] entryptBytes = new byte[bytes.length-33];
		System.arraycopy(bytes, 33, entryptBytes, 0, entryptBytes.length);
		byte[] plainBodyBytes = null;//body明文
		//解密
		try{
			plainBodyBytes = macUtil.decrypt(entryptBytes);
		}catch(Exception e){
			throw new CoreException(SErrorMSG.SZ0002,"decrypt fail",e);
		}
		byte[] plainBytes = new byte[33+plainBodyBytes.length];//整个报文明文数据
		System.arraycopy(bytes, 0, plainBytes, 0, 33);
		System.arraycopy(plainBodyBytes, 0, plainBytes, 33, plainBodyBytes.length);
		
		//mac校验
		byte [] toMacBytes = new byte[plainBodyBytes.length+4+8+8];
		System.arraycopy(plainBytes, 4+1+8, toMacBytes, 0, toMacBytes.length);
		byte[] resceiveMac = new byte[8];
		System.arraycopy(plainBytes, 4+1, resceiveMac, 0, 8);
		logger.debug("mac plaintext:{}",Hex.encode(toMacBytes));
		byte[] calculateMac = macUtil.mac(toMacBytes);
		if(!Arrays.equals(resceiveMac,calculateMac)){
			logger.error("receive mac:{},calculate mac:{}", Hex.encode(resceiveMac),Hex.encode(calculateMac));
			throw new CoreException(SErrorMSG.SZ0001,"mac not match");
		}
		logger.info("解密报文明文数据-->" + Hex.encode(plainBytes));
		return plainBytes;
	}

}
