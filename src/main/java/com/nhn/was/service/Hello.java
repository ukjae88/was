package com.nhn.was.service;

import com.nhn.was.http.HttpRequest;
import com.nhn.was.http.HttpResponse;
import com.nhn.was.servlet.SimpleServlet;

public class Hello implements SimpleServlet {
	
	@Override
	public void service(HttpRequest req, HttpResponse res) throws Exception {
		String body = getBody();
		res.sendBody(body, "HTTP/1.0 200 OK", "text/html; charset=utf-8");
	}
	
	public String getBody() {
		return "Welcome, service.Hello";
	}
}
