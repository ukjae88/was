package com.nhn.was.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HelloTest {

	@Test
	public void test() {
		Hello hello = new Hello();
		String result = hello.getBody();
		assertEquals("Welcome, service.Hello", result);
	}

}
