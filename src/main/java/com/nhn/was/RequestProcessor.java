package com.nhn.was;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhn.was.http.HttpRequest;
import com.nhn.was.http.HttpResponse;
import com.nhn.was.json.JsonConfig;
import com.nhn.was.servlet.ServletMapping;
import com.nhn.was.servlet.SimpleServlet;

public class RequestProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);
    private static final String INDEX_FILE_NAME = "index.html";
    private static final String ERROR_FILE_501 = "error501.html";
    private Map<String, JsonConfig> mapConfig;
    private Socket connection;
    private ServletMapping servletMap;
    
    public RequestProcessor(Map<String, JsonConfig> mapConfig, Socket connection, ServletMapping servletMap) {
        // Set mapConfig, connection, servletMap
    	this.mapConfig = mapConfig;
        this.connection = connection;
        this.servletMap = servletMap;
    }

    @Override
    public void run() {
    	HttpResponse response = null;
    	File rootDirectory = null;
    	String error500FileNm = "";
        try {
        	// Set Reader, Writer
        	HttpRequest request = new HttpRequest(connection.getInputStream(), INDEX_FILE_NAME, mapConfig);
        	response = new HttpResponse(connection.getOutputStream());
            
        	// Request 정보 설정
            String method = request.getMethod();
            String fileName = request.getFileName();
            String contentType = request.getContentType();
            String get = request.getRequestLine();
            logger.info(connection.getRemoteSocketAddress() + " : " + get);
            
            rootDirectory = request.getRootDirectory();
            String error403FileNm = request.getError403FileNm();
            String error404FileNm = request.getError404FileNm();
            error500FileNm = request.getError500FileNm();
                        
            if (method.equals("GET")) {

            	// Servlet 매핑인 경우
            	if(servletMap.isMapping(fileName)) {
            		SimpleServlet servlet = servletMap.getInstance(fileName);
            		servlet.service(request, response);
            		return;
            	}
            	                
            	// ROOT 디렉터리의 상위에 접근하는 경우 && 확장자가 exe 인 경우
                if (fileName.lastIndexOf("/") > 0 || fileName.endsWith(".exe")) {
                	// Forbidden Access : 403 Error
                	File theFile = new File(rootDirectory, error403FileNm);
                	response.sendFile(theFile, "HTTP/1.0 403 Forbidden", "text/html; charset=utf-8");
                	logger.warn("Warning Log : " + get + " - 403 Forbidden");
                	return;
                }
                
                // Set File
                File theFile = new File(rootDirectory, fileName.substring(1, fileName.length()));
                if (theFile.canRead()) {
                	// HTML 파일이 존재하는 경우 : 200 OK
                	response.sendFile(theFile, "HTTP/1.0 200 OK", contentType);
                	
                } else {
                    // can't find the file : 404 Error
                	theFile = new File(rootDirectory, error404FileNm);
                	response.sendFile(theFile, "HTTP/1.0 404 File Not Found", "text/html; charset=utf-8");
                	logger.warn("Warning Log : " + get + " - 404 File Not Found");
                }
            } else {
                // method does not equal "GET"
            	File theFile = new File(rootDirectory, ERROR_FILE_501);
            	response.sendFile(theFile, "HTTP/1.0 501 Not Implemented", "text/html; charset=utf-8");
            }
        } catch (Exception ex) {
            logger.error("Error Log : " + connection.getRemoteSocketAddress(), ex);
            
            // Internal Server Error : 500 Error
        	try {
        		File theFile = new File(rootDirectory, error500FileNm);
            	response.sendFile(theFile, "HTTP/1.0 500 Internal Server Error", "text/html; charset=utf-8");
			} catch (Exception e) {
				logger.error("Error Log : " + connection.getRemoteSocketAddress(), ex);
			}
        	
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {
            }
        }
    }
                
}