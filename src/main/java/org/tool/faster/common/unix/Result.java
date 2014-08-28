package org.tool.faster.common.unix;

import java.util.List;

public class Result {

	public int exitStatus;

	public List<String> exlog;

	// public String error_msg;

	public boolean isSuccess;

	void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	void setLog(List<String> exlog) {
		this.exlog = exlog;
	}

}
