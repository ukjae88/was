package com.nhn.was.service;

import java.time.LocalTime;

import com.nhn.was.http.HttpRequest;
import com.nhn.was.http.HttpResponse;
import com.nhn.was.servlet.SimpleServlet;

public class CurrentTime implements SimpleServlet {
	
	@Override
	public void service(HttpRequest req, HttpResponse res) throws Exception {
		String body = getBody();
		res.sendBody(body, "HTTP/1.0 200 OK", "text/html; charset=utf-8");
	}
	
	public String getBody() {
		LocalTime currentTime = LocalTime.now();
		return "Current Time : " + currentTime.toString();
	}
}
