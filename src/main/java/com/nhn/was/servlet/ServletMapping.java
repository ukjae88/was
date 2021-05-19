package com.nhn.was.servlet;

import java.util.HashMap;
import java.util.Map;

public class ServletMapping {
	
	private Map<String, String> mappingInfo;
	private Map<String, SimpleServlet> survletMap;

	public ServletMapping() {
		mappingInfo = new HashMap<>();
		survletMap = new HashMap<>();
		this.initMap();
	}
	
	public void initMap() {
		// Servlet Mapping
		mappingInfo.put("/Greeting", "com.nhn.was.Hello");
		mappingInfo.put("/super.Greeting", "com.nhn.was.service.Hello");
		mappingInfo.put("/time", "com.nhn.was.service.CurrentTime");
	}
	
	public boolean isMapping(String url) {
		if(mappingInfo.containsKey(url)) {
			return true;
		}
		return false;
	}
	
	public SimpleServlet getInstance(String url) throws Exception {
		if(!this.isMapping(url)) {
			return null;
		}
		
		// �̹� ������ Ŭ������ ��� ����
		String key = mappingInfo.get(url);
		if(survletMap.containsKey(key)) {
			return survletMap.get(key);
		}
		
		// Ŭ���� �ε�
		Class<?> clazz = Class.forName(key);
    	SimpleServlet servlet = (SimpleServlet) clazz.newInstance();
    	survletMap.put(key, servlet);
		return servlet;
	}
		
}
