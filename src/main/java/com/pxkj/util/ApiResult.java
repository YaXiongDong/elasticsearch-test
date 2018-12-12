package com.pxkj.util;

/**
 * 操作结果
 * 
 * @author hugh
 *
 * @param <T>
 */
public class ApiResult<T> {
	private boolean success;
	private String msg;
	private T data;

	public ApiResult(String message, T data) {
		this.success = OpEnum.SUCCESS.getValue();
		this.msg = "";
		this.msg = message;
		this.data = data;
	}

	public ApiResult(String message) {
		this.success = OpEnum.FAILURE.getValue();
		this.msg = message;
		this.data = null;
	}

	public ApiResult() {

	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
