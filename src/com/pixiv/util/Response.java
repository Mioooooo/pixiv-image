package com.pixiv.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Response implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 响应码
	 */
	private int responseCode = 0;
	/**
	 * 响应内容
	 */
	private String responseMsg = "success";
	
	/**
	 * bodyData
	 */
	private Object data;
    
    public Response(int responseCode, String responseMsg) {
    	this.responseCode = responseCode;
    	this.responseMsg = responseMsg;
    }
    
    public Response(Object data) {
    	this.data = data;
    }
    
}
