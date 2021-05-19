package com.nhn.was.json;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonConfigReader {
	private JsonObject jsonObject;
    private int port;
    private Map<String, JsonConfig> mapConfig;
    
	public void loadJson(String path) throws IOException {
		this.port = 80;
		this.mapConfig = new HashMap<>();
        this.jsonObject = (JsonObject) JsonParser.parseReader(new FileReader(path));
        setVariables();
    }
    
    public void setVariables() {
    	// Set Port
    	port = jsonObject.get("port").getAsInt();
    	// mapConfig : Host 별 설정정보
    	JsonArray memberArray = (JsonArray) jsonObject.get("hosts");
    	for (JsonElement element : memberArray) {
    		jsonObject = element.getAsJsonObject();
    		String name = jsonObject.get("name").getAsString();
    		String rootPath = jsonObject.get("root_path").getAsString();
    		String error403 = jsonObject.get("error403").getAsString();
    		String error404 = jsonObject.get("error404").getAsString();
    		String error500 = jsonObject.get("error500").getAsString();
    		
    		JsonConfig jsonConfig = new JsonConfig(rootPath, error403, error404, error500);
    		mapConfig.put(name, jsonConfig);
		}
    }
    	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Map<String, JsonConfig> getMapConfig() {
		return mapConfig;
	}

	public void setMapConfig(Map<String, JsonConfig> mapConfig) {
		this.mapConfig = mapConfig;
	}

}
