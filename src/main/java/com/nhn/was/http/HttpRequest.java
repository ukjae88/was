package com.nhn.was.http;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLConnection;
import java.util.Map;

import com.nhn.was.json.JsonConfig;

public class HttpRequest {
	private Reader reader;
	private String indexFileName;
	private Map<String, JsonConfig> mapConfig;
	
	private String requestLine;
	private String method;
	private String fileName;
	private String version;
	private String hostName;
	private String contentType;
	
	private File rootDirectory;
    private String error403FileNm;
    private String error404FileNm;
    private String error500FileNm;
	
	public HttpRequest(InputStream in, String indexFileName, Map<String, JsonConfig> mapConfig) throws Exception {
		this.reader = new InputStreamReader(new BufferedInputStream(in), "UTF-8");
		this.indexFileName = indexFileName;
		this.mapConfig = mapConfig;
		this.readRequestLine();
	}
	
	/*
     * Read Request Info
     */
	public void readRequestLine() throws Exception {
		StringBuilder requestLine = new StringBuilder();
        StringBuilder hostLine = new StringBuilder();
		
		boolean isRequestLine = false;
        while (true) {
            int c = this.reader.read();
            if (c == '\r' || c == '\n')	isRequestLine = true;
            if(!isRequestLine)
            	requestLine.append((char) c);
            
            if(hostLine.indexOf("Host") != -1) {
            	if (c == '\r' || c == '\n')
                    break;
            }
            hostLine.append((char) c);
        }
        // Set Host Name
        String hostNm = hostLine.substring(hostLine.indexOf("Host")+6);
        if(hostNm.lastIndexOf(":") != -1)
        	this.hostName = hostNm.substring(0, hostNm.lastIndexOf(":"));
        
        // Set Request String
        this.requestLine = requestLine.toString();
        
        // Split Request String
        // tokens[0] = HTTP method / tokens[1] : fileName / tokens[2] : HTTP Version
        String[] tokens = this.requestLine.split("\\s+");
        this.method = tokens[0];
        
        if ("GET".equals(this.method)) {
        	this.fileName = tokens[1];
        	
        	// Root 디렉토리 요청인 경우, index.html 응답
            if (fileName.endsWith("/"))
            	fileName += this.indexFileName;
        	
            // Set version
            if (tokens.length > 2) {
                version = tokens[2];
            }
            
            // Set contentType
            this.contentType = URLConnection.getFileNameMap().getContentTypeFor(this.fileName);
        }
        
        // Set root path (Host Name 별로 설정)
        this.setRootPath(this.hostName);
        this.setErrorPage(this.hostName);
	}
	
	/*
     * Host 별로 Root Path 설정
     */
    private void setRootPath(String hostName) throws Exception{
    	// hosts 별 config 참조
    	String pathName = "";
    	if(this.mapConfig != null && this.mapConfig.containsKey(hostName)) {
    		pathName = this.mapConfig.get(hostName).getRootPath();
    	}
    	
    	File rootDirectory = new File(pathName);
    	if (rootDirectory.isFile()) {
            throw new IllegalArgumentException(
                    "rootDirectory must be a directory, not a file");
        }

        // Set rootDirectory
        this.rootDirectory = rootDirectory.getCanonicalFile();
    }
    
    /*
     * Host 별로 ErrorPage 설정
     */
    private void setErrorPage(String hostName){
    	// hosts 별 config 참조
    	if(this.mapConfig != null && this.mapConfig.containsKey(hostName)) {
    		this.error403FileNm = this.mapConfig.get(hostName).getError403();
    		this.error404FileNm = this.mapConfig.get(hostName).getError404();
    		this.error500FileNm = this.mapConfig.get(hostName).getError500();
    	}
    }
	
	public Reader getReader() {
		return reader;
	}

	public void setReader(Reader reader) {
		this.reader = reader;
	}

	public String getRequestLine() {
		return requestLine;
	}

	public void setRequestLine(String requestLine) {
		this.requestLine = requestLine;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	public void setRootDirectory(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public String getError403FileNm() {
		return error403FileNm;
	}

	public void setError403FileNm(String error403FileNm) {
		this.error403FileNm = error403FileNm;
	}

	public String getError404FileNm() {
		return error404FileNm;
	}

	public void setError404FileNm(String error404FileNm) {
		this.error404FileNm = error404FileNm;
	}

	public String getError500FileNm() {
		return error500FileNm;
	}

	public void setError500FileNm(String error500FileNm) {
		this.error500FileNm = error500FileNm;
	}
	
}
