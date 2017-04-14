package com.lakala.ips.router.szt;

import java.util.Map;

import com.lakala.amber.common.ReturnCodeValidator;
import com.lakala.amber.core.CoreException;
import com.lakala.amber.core.HostException;
import com.lakala.amber.service.dict.ValidationException;

public class StdReturnCodeValidator implements ReturnCodeValidator {

	private String keyName;
	private String[] messageKeyNames;
	private String errorKeyPrefix = "host_";
	private String remoteJnlNoKeyName;
	private String[] successReturnCode;

	public StdReturnCodeValidator() {
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public void setMessageKeyName(String string) {
		messageKeyNames = string.split(",");
	}

	public void setErrorKeyPrefix(String errorKeyPrefix) {
		this.errorKeyPrefix = errorKeyPrefix;
	}

	public void setRemoteJnlNoKeyName(String remoteJnlNoKeyName) {
		this.remoteJnlNoKeyName = remoteJnlNoKeyName;
	}
	
	public void setSuccessReturnCode(String[] successReturnCode) {
		this.successReturnCode = successReturnCode;
	}

	@SuppressWarnings("unchecked")
	public void check(Object object) throws CoreException {

		if (object == null) {
			throw new ValidationException("validation_param_object_is_null");
		}
		Map<String, Object> map = (Map<String, Object>) object;

		String returnCode = (String) map.get(keyName);
		if (successReturnCode[0].equals(returnCode)||successReturnCode[1].equals(returnCode))
			return;
		// throw jnlno
		String remoteJnlNo = (String) map.get(remoteJnlNoKeyName);
		if (messageKeyNames != null) {
			StringBuffer msgSb = new StringBuffer();
			for (int i = 0; i < messageKeyNames.length; i++) {
				Object message = map.get(messageKeyNames[i]);
				if (message != null) {
					msgSb.append(message);
				}
			}
			HostException ve = new HostException(errorKeyPrefix + returnCode,
					msgSb.toString());
			ve.setDefaultMsg(true);
			throw ve;
		}
		HostException ve = new HostException(errorKeyPrefix + returnCode, null);
		ve.setDefaultMsg(true);
		throw ve;
	}

}
