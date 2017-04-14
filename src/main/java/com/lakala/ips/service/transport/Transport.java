/*
 * Copyright 2013-2014 lakala.
 */

package com.lakala.ips.service.transport;

import com.lakala.amber.core.CoreException;

/**
 *  mfs transport
 * <p>
 * Created on 2015-3-11 
 * <p>
 * @author chenjian
 * @since 2015-3-11
 */
public interface Transport {
	
	public Object submit(Object in) throws CoreException;

}
