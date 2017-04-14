/*
 * Copyright 2013-2014 lakala.
 */

package com.lakala.ips.router.szt;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import os.juno.util.ByteArrayType;
import os.juno.util.Hex;

import com.lakala.amber.security.TripleDES;
import com.lakala.amber.mac.DESMtransfer;

/**
 *  mac 3des 
 * <p>
 * Created on 2015年12月15日 
 * <p>
 * @author chenjian
 * @since 2015年12月15日
 */
public  class MacUtil {

	@Value("${szt.mackey}")
	private String macKey ;
	
	@Value("${szt.deskey}")
	private String desKey ;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public byte[] mac(byte[] plainBytes){
		String mac = DESMtransfer.MACWeb(macKey,null,Hex.encode(plainBytes),0);
		if(null != mac && mac.length()>=16){
			 mac = mac.substring(0,16);
		}
		return Hex.decode(mac);
	}
	
	/*
	 * 3DES-ECB + padding 0x20
	 * 原文为 2 字节长度 (Message Body 的总长度)+Message Body
	 */
	public byte[] encrypt(byte[] plainBytes) throws Exception{
		TripleDES td = new TripleDES("NoPadding");
		td.initKey(Hex.decode(desKey)); 
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(ByteArrayType.toBytes(((Integer)plainBytes.length).shortValue()));
		bos.write(plainBytes);
		int paddingLength = (plainBytes.length+2)%8;
		if(paddingLength!=0){
			paddingLength = 8- paddingLength;
			while(paddingLength-->0){
				bos.write((byte)0x20);
			}
		}
		byte[] encrypted = td.encrypt(bos.toByteArray());
		return encrypted;
	}
	
	/*
	 * 3DES-ECB + padding 0x20
	 * 原文为 2 字节长度 (Message Body 的总长度)+Message Body
	 */
	public byte[] decrypt(byte[] entryptBytes) throws Exception{
		TripleDES td = new TripleDES("NoPadding");
		td.initKey(Hex.decode(desKey));
		logger.debug("decrypt:{}",Hex.encode(entryptBytes));
		byte[] plainBytes = td.decrypt(entryptBytes);
		logger.debug("decrypt result :{}",Hex.encode(plainBytes));
		int bodylength = ByteArrayType.toShort(new byte[] {plainBytes[0],plainBytes[1]});
		byte[] bodyBytes = new byte[bodylength];
		System.arraycopy(plainBytes, 2, bodyBytes, 0, bodylength);
		return bodyBytes;
	}
}
