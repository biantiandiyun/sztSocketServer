/*
 * Copyright 2013-2014 lakala.
 */

package com.lakala.ips.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.springframework.beans.BeanUtils;

import com.lakala.amber.util.pojo.Populator;
import com.lakala.amber.util.pojo.Unpopulator;

/**
 *  ips utils
 * <p>
 * Created on 2015-4-7 
 * <p>
 * @author chenjian
 * @since 2015-4-7
 */
public class IpsUtils {

	private static Populator populatornested = new Populator(true, false, false);
	private static Unpopulator unpopulatornested = new Unpopulator(true, true);
	
	private IpsUtils(){
		
	}
	
	public static void populate(Map<String, Object> source, Object target) {
		populatornested.populate(source, target);
	}

	public static void unpopulate(Object source, Map<String, Object> target) {
		unpopulatornested.unpopulate(source, target);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> unpopulatelist(Object source){
		if(!(source instanceof List)){
			throw new IllegalArgumentException("source must be list");
		}
		List<Map> list = new ArrayList<Map>();
		for(Iterator<Object> it = ((List) source).listIterator();it.hasNext();){
			Object obj = it.next();
			Map map = new HashMap();
			unpopulatornested.unpopulate(obj, map);
			list.add(map);
		}
		return list;
	}
	
	public static void unpopulatenested(Object source ,  Map<String, Object> target) throws Exception{
		PropertyDescriptor[] pdrs = BeanUtils.getPropertyDescriptors(source.getClass());
		for(PropertyDescriptor pdr:pdrs){
			Method readMethod = pdr.getReadMethod();
			Method writeMethod = pdr.getWriteMethod();
			String name = pdr.getName();
			if(readMethod==null){
				continue;
			}
			try {
				Object value = readMethod.invoke(source);
				if(value==null){
					continue;
				}
				if(BeanUtils.isSimpleProperty(value.getClass())){
					target.put(name, value);
				}else if(value instanceof List){
					List list = new ArrayList<Map>(((List) value).size());
					for(Iterator it = ((List) value).iterator();it.hasNext();){
						Map innermap = new HashMap();
						Object obj = it.next();
						unpopulatenested(obj,innermap);
						list.add(innermap);
					}
					target.put(name, list);
				}else{
					Map map = new HashMap();
					target.put(name, map);
					unpopulatenested(value,map);
				}
			} catch (Exception e){
				throw new Exception("unpopulate error");
			}
		}
	}
	
	public static void populatenested(Map<String, Object> source,Object target ) throws Exception{
		try{
			PropertyDescriptor[] pdrs = BeanUtils.getPropertyDescriptors(target.getClass());
			for(PropertyDescriptor pdr:pdrs){
				Method readMethod = pdr.getReadMethod();
				Method writeMethod = pdr.getWriteMethod();
				String name = pdr.getName();
				if(readMethod==null||writeMethod==null){
					continue;
				}
				Object param = toPojo(source.get(name),pdr.getPropertyType());
				writeMethod.invoke(target, param);
			}
		}catch(Exception e){
			throw new Exception("populate error");
		}
	}
	
	static Object toPojo(Object value, Class<?> propertype) throws Exception{
			Object newInstance = propertype.newInstance();
			if(BeanUtils.isSimpleProperty(propertype)){
				newInstance = value;
			}else if(propertype.isArray()&&!BeanUtils.isSimpleValueType(propertype.getComponentType())){
				Class<?> componentType = propertype.getComponentType();
				int len = Array.getLength(value);
				for (int i = 0; i < len; i++) {
					Object object = Array.get(value, i);
					Object componentObject = toPojo(object,componentType);
					Array.set(newInstance, i, componentObject);
				}
			}else if(value instanceof Map){
				PropertyDescriptor[] pdrs = BeanUtils.getPropertyDescriptors(propertype);
				for(PropertyDescriptor pdr:pdrs){
					Method readMethod = pdr.getReadMethod();
					Method writeMethod = pdr.getWriteMethod();
					String name = pdr.getName();
					if(readMethod==null||writeMethod==null){
						continue;
					}
					Object param = toPojo(((Map)value).get(name),pdr.getPropertyType());
					writeMethod.invoke(newInstance, param);
				}
			}else if(value instanceof List){
				Class<?> componentType = propertype.getComponentType();
				List<?> values = (List<?>) value;
				for(Iterator<?> it = values.iterator();it.hasNext();){
					Object entry = it.next();
					Object convertEntry = toPojo(entry, componentType);
					((List)newInstance).add(convertEntry);
				}
				
			}
			return newInstance;
	}


	public static String createRandom(){
		String formatStr = "0123456789ABCDE";
		int length = 8;
		Random random = new Random();
		String result = "";
		int len = formatStr.length() - 1;
		for(int i = 0;i < length; i++){
			int itmp = random.nextInt(len);
			char ctmp = formatStr.charAt(itmp);
			result += ctmp;
		}
		return result;
	}
	public static void main(String []args){
		List list = new ArrayList();
		System.out.println(list.getClass().isArray());
	}
}
