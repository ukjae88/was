package com.nhn.was.json;

public class JsonConfig {
	private String rootPath;
	private String error403;
	private String error404;
	private String error500;
	
	public JsonConfig(String rootPath, String error403, String error404, String error500) {
		this.rootPath = rootPath;
		this.error403 = error403;
		this.error404 = error404;
		this.error500 = error500;
	}
	
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getError403() {
		return error403;
	}

	public void setError403(String error403) {
		this.error403 = error403;
	}

	public String getError404() {
		return error404;
	}

	public void setError404(String error404) {
		this.error404 = error404;
	}

	public String getError500() {
		return error500;
	}

	public void setError500(String error500) {
		this.error500 = error500;
	}

}
