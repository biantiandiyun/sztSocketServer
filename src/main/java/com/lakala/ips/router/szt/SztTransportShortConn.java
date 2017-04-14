/*
 * Copyright 2013-2014 lakala.
 */

package com.lakala.ips.router.szt;

import com.lakala.amber.AmberException;
import com.lakala.amber.channel.CommunicationException;
import com.lakala.amber.channel.Transform;
import com.lakala.amber.channel.tcp.SocketGateway;
import com.lakala.amber.common.ReturnCodeValidator;
import com.lakala.amber.core.Context;
import com.lakala.amber.core.CoreException;
import com.lakala.amber.core.CoreRuntimeException;
import com.lakala.amber.core.impl.DefaultContextEx;
import com.lakala.amber.service.id.IdCreator;
import com.lakala.amber.util.Hex;
import com.lakala.ips.service.common.SErrorMSG;
import com.lakala.ips.service.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import os.juno.util.ByteArrayType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *  深圳通通讯服务
 * <p>
 * Created on 2015年12月14日 
 * <p>
 * @author chenjian
 * @since 2015年12月14日
 */
public class SztTransportShortConn implements Transport{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Transform encodeTransform;
	
	private Transform decodeTransform;
	
	private SocketGateway gateway;
	
	private ReturnCodeValidator validator;
	
	private IdCreator serialIdCreator;
	
	@Override
	public Object submit(Object in) throws CoreException {
		Map<String,Object> models = new HashMap<String,Object>();
		if(in instanceof Map){
			models = (Map) in;
		}
		else{
			throw new CoreRuntimeException("transport only support map request.");
		}
		short sid = generateSid();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String currentDate = sdf.format(new Date());
		byte[] bcd_date = BCD.DecToBCDArray(Long.valueOf(currentDate));
		models.put("SequenceDate", bcd_date);
		models.put("SequenceId", sid);
		models.put("CommandId", (String)models.get("HostTransCode"));
		Context ctx = new DefaultContextEx();
		ctx.setModels(models);
		ctx.setVariable(Context.IDENTITY, models.get("HostTransCode"));
		Object out = null;
		try{
			byte[] reqPacket = (byte[]) encodeTransform.transform(models, ctx);
			//sid为8字节 6字节的bcd(yyyyMMddHHmm)+2字节short(自增长)
			byte[] sidBytes = new byte[8];
			System.arraycopy(bcd_date, 0, sidBytes, 0, 6);
			System.arraycopy(ByteArrayType.toBytes(sid), 0, sidBytes, 6, 2);
			logger.info("szt transport: sid {} request {}",Hex.encode(sidBytes),Hex.encode(reqPacket));
//			byte[] resPacket = (byte[]) gateway.sendAndReceive(reqPacket, null);
			byte[] resPacket = (byte[]) gateway.sendAndReceive(reqPacket, null);

//			byte[] resPacket =Hex.decode("0000004B005495ECC13441E6B0081000032015120909273890323031353130313087B056CB3D0CF3A07CE2FFB05BD0F039E7476A325BDC3F8D56964C58194048340B8E9F14B4A2CF3321261D262AB97E42");
			logger.info("szt transport: response {}",Hex.encode(resPacket));
			out = (Map<String,Object>)decodeTransform.transform(resPacket, ctx);
		}catch(AmberException e){
			throw new CoreException(SErrorMSG.RS0003,e);//throw this when encode decode 
		}
		catch (CommunicationException e) {
			throw new CoreException(SErrorMSG.RS0005,e);//throw this when http ioexception
		} 
		validator.check(out);
		return out;
	}

	public void setEncodeTransform(Transform encodeTransform) {
		this.encodeTransform = encodeTransform;
	}

	public void setDecodeTransform(Transform decodeTransform) {
		this.decodeTransform = decodeTransform;
	}

	public void setGateway(SocketGateway gateway) {
		this.gateway = gateway;
	}

	public void setValidator(ReturnCodeValidator validator) {
		this.validator = validator;
	}

	public void setSerialIdCreator(IdCreator serialIdCreator) {
		this.serialIdCreator = serialIdCreator;
	}
	
	private short generateSid(){
		Integer serialId =Integer.parseInt(serialIdCreator.create().toString());
		return serialId.shortValue();
	}

}
