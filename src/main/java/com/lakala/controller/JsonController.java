package com.lakala.controller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.lakala.amber.core.CoreException;
import com.lakala.amber.util.Hex;
import com.lakala.ips.router.szt.SztTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class JsonController {
	
	private Logger logger = LoggerFactory.getLogger(JsonController.class);
	@Autowired(required = false)
	private SztTransport sztTransport;

	@ResponseBody
	@RequestMapping(value = "/sztTransport",method = RequestMethod.POST)
	public Map<String,Object> sztTransport( @RequestBody  @Valid Map<String,Object> request){
		logger.info("input:"+request);
		Map<String,Class> keyTypeMap = new HashMap<String, Class>();
		keyTypeMap.put("ApduIndex",Byte.class);
		transferIn(request,keyTypeMap);
		logger.info("transferIn:"+request);
		Map<String,Object> retMap = new HashMap<String, Object>();
		try {
			retMap = (Map<String, Object>) sztTransport.submit(request);
			transferOut(retMap);
		} catch (CoreException e) {
			logger.error("",e);
			return null;
		}

		logger.info("outPut:"+retMap);
		
		return retMap;
	}
	@ResponseBody
	@RequestMapping(value = "/closeSocket/{sequenceId}",method = RequestMethod.GET)
	public Map<String,Object> closeSocket(@PathVariable String sequenceId){
		logger.info("input:"+sequenceId);
		Map<String,Object> retMap = new HashMap<String, Object>();
		sztTransport.closeSocket(sequenceId);
		logger.info("outPut:"+retMap);

		return retMap;
	}
	private  void transferIn(Map<String,Object> map,Map<String,Class> keyTypeMap){
		for(Map.Entry<String,Object> entry: map.entrySet()) {
			if (entry.getValue() instanceof String||entry.getValue() instanceof Integer) {
				if (!keyTypeMap.containsKey(entry.getKey())){
					continue;
				}
				if (keyTypeMap.get(entry.getKey()) == Byte.class){
					map.put(entry.getKey(),Byte.valueOf(entry.getValue()+""));
				}
			}if (entry.getValue() instanceof List) {
				List list = new ArrayList();
				for(Object obj:(List)entry.getValue()){
					 if(obj instanceof Map){
						transferIn((Map)obj,keyTypeMap);
						list.add(obj);
					}else {
						list.add(obj);
					}
				}
				map.put(entry.getKey(),list);
			}else if (entry.getValue() instanceof Map) {
				transferIn((Map) entry.getValue(),keyTypeMap);
			}
		}
	}
	private  void transferOut(Map<String,Object> map){
		for(Map.Entry<String,Object> entry: map.entrySet()) {
			if (entry.getValue() instanceof String) {
				continue;
			}if (entry.getValue() instanceof List) {
				List list = new ArrayList();
				for(Object obj:(List)entry.getValue()){
					if(obj instanceof  byte[]){
						list.add( Hex.encode((byte[]) entry.getValue()));
					}else if(obj instanceof Map){
						transferOut((Map)obj);
						list.add(obj);
					}else {
						list.add(obj);
					}
				}
				map.put(entry.getKey(),list);
			}else if (entry.getValue() instanceof byte[] ) {
				map.put(entry.getKey(), Hex.encode((byte[]) entry.getValue()));
			}  else if (entry.getValue() instanceof Map) {
				transferOut((Map) entry.getValue());
			}
		}
	}
}
