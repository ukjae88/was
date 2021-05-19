package com.nhn.was;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhn.was.json.JsonConfig;
import com.nhn.was.json.JsonConfigReader;
import com.nhn.was.servlet.ServletMapping;

/**
 * Created by cybaek on 15. 5. 22..
 */
public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private static final int NUM_THREADS = 50;
    private final Map<String, JsonConfig> mapConfig;
    private final int port;
    private final ServletMapping servletMap;
    
    public HttpServer(JsonConfigReader jsonConfigReader, int port, ServletMapping servletMap) throws IOException {
    	// get the Document root path
    	Map<String, JsonConfig> map = jsonConfigReader.getMapConfig();	
    	for (String name : map.keySet()) {
    		File rootDirectory = new File(map.get(name).getRootPath());
    		if (!rootDirectory.isDirectory()) {
                throw new IOException(rootDirectory
                        + " does not exist as a directory");
            }
		}
    	// set mapConfig, port
    	this.mapConfig = map;
        this.port = port;
        this.servletMap = servletMap;
    }
    
    public void start() throws IOException {
    	// Set ThreadPool
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        // Open ServerSocket
        try (ServerSocket server = new ServerSocket(port)) {
            // Logging : port, Document Root
        	logger.info("Accepting connections on port " + server.getLocalPort());
            StringBuffer sb = new StringBuffer();
            sb.append("Document Root: ");
            for (String name : mapConfig.keySet()) {
            	sb.append("\n" + name + " / " + mapConfig.get(name).getRootPath());
    		}
            logger.info(sb.toString());
            
            // Run RequestProcessor
            while (true) {
                try {
                    Socket request = server.accept();
                    Runnable r = new RequestProcessor(mapConfig, request, servletMap);
                    pool.submit(r);
                } catch (IOException ex) {
                    logger.warn("Error accepting connection", ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        //Read config
    	JsonConfigReader jsonConfigReader = new JsonConfigReader();
    	try {
    		jsonConfigReader.loadJson("src/main/resources/config.json");
		} catch (IOException e) {
			System.out.println("config.json file not exist");
			return;
		}
    	
    	// Servlet Mapping
    	ServletMapping servletMap = new ServletMapping();
    	
    	// get the Document root
    	Map<String, JsonConfig> map = jsonConfigReader.getMapConfig();
    	if(map.isEmpty()) {
    		System.out.println("Usage: java JHTTP docroot port");
    		return;
    	}
    	
        // set the port to listen on
        int port;
        try {
            port = jsonConfigReader.getPort();
            if (port < 0 || port > 65535) port = 80;
        } catch (RuntimeException ex) {
            port = 80;
        }
        
        // HttpServer start
        try {
            HttpServer webserver = new HttpServer(jsonConfigReader, port, servletMap);
            webserver.start();
        } catch (IOException ex) {
            logger.warn("Server could not start", ex);
        }
    }
}