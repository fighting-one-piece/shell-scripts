/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netease.gather.common.web.sna;

/**
 * 会话异常
 *
 * @author hujh
 */
public class SessionException extends RuntimeException {

	public SessionException() {
	}

	public SessionException(String message) {
		super(message);
	}

	public SessionException(Throwable cause) {
		super(cause);
	}

	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}
}
