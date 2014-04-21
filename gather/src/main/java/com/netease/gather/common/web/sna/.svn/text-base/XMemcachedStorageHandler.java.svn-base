/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netease.gather.common.web.sna;

import net.rubyeye.xmemcached.XMemcachedClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 基于XMemcached缓存实现的会话处理器, 达到分布式环境会话一致的效果
 *
 * @author hujh
 */
public class XMemcachedStorageHandler implements StorageHandler {

	private XMemcachedClient client;
	private String keyPrefix = "";
	private int valueTTL = 24 * 3600;

	public XMemcachedStorageHandler() {
	}

	public XMemcachedStorageHandler(XMemcachedClient client) {
		this.client = client;
	}

	@Override
	public void initialize(String sessionId) throws Exception {
		String key = sessionKey(sessionId);
		client.set(key, valueTTL, Boolean.TRUE);
	}

	@Override
	public void invalidate(String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String key = sessionKey(sessionId);
		client.set(key, valueTTL, Boolean.FALSE);
	}

	@Override
	public String createSessionId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sessionId = null;
		for (int i = 0; i < 10; i++) {
			sessionId = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
			String key = sessionKey(sessionId);
			if (client.add(key, valueTTL, Boolean.TRUE)) {
				break;
			}
		}
		return sessionId;
	}

	@Override
	public void removeSessionId(String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String key = sessionKey(sessionId);
		client.delete(key);
	}

	@Override
	public boolean existsSessionId(String sessionId) throws Exception {
		String key = sessionKey(sessionId);
		Boolean value = client.get(key);
		return Boolean.TRUE.equals(value);
	}

	@Override
	public void setAttribute(String sessionId, HttpServletRequest request, HttpServletResponse response, String name, Object value) throws Exception {
		String key = attributeKey(sessionId, name);
		client.set(key, valueTTL, value);
	}

	@Override
	public Object getAttribute(String sessionId, HttpServletRequest request, HttpServletResponse response, String name) throws Exception {
		String key = attributeKey(sessionId, name);
		return client.get(key);
	}

	@Override
	public void removeAttribute(String sessionId, HttpServletRequest request, HttpServletResponse response, String name) throws Exception {
		String key = attributeKey(sessionId, name);
		client.delete(key);
	}

	public XMemcachedClient getClient() {
		return client;
	}

	public void setClient(XMemcachedClient client) {
		this.client = client;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public int getValueTTL() {
		return valueTTL;
	}

	public void setValueTTL(int valueTTL) {
		this.valueTTL = valueTTL;
	}

	private String sessionKey(String sessionId) {
		return keyPrefix + "sna.session." + sessionId;
	}

	private String attributeKey(String sessionId, String name) {
		return keyPrefix + "sna.session." + sessionId + "#" + name;
	}
}
