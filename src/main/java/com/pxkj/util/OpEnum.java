package com.pxkj.util;

/**
 * 操作结果枚举
 * @author hugh
 *
 */
public enum OpEnum {
	SUCCESS(true),FAILURE(false);
	private boolean value;
	private OpEnum(boolean value){
		this.value=value;
	}

    public boolean getValue() { 
     return value;
    }
}
