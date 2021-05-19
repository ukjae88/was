package com.nhn.was.servlet;

import com.nhn.was.http.HttpRequest;
import com.nhn.was.http.HttpResponse;

public interface SimpleServlet {
	
	public void service(HttpRequest req, HttpResponse res) throws Exception;
	
}
