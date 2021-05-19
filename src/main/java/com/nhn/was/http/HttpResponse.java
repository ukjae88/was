package com.nhn.was.http;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Date;

public class HttpResponse {
	private OutputStream outputStream;
	private Writer writer;
	
	public HttpResponse(OutputStream out) throws Exception {
		this.outputStream = new BufferedOutputStream(out);
		this.writer = new OutputStreamWriter(this.outputStream);
	}
	
	/*
     * send File
     */
	public void sendFile(File file, String responseCode, String contentType) throws Exception {
		byte[] theData = Files.readAllBytes(file.toPath());
        this.sendHeader(responseCode, contentType, theData.length);
        // send the file; it may be an image or other binary data, use output stream
        outputStream.write(theData);
        outputStream.flush();
    }
	
	/*
     * send Body
     */
	public void sendBody(String body, String responseCode, String contentType) throws Exception {
        this.sendHeader(responseCode, contentType, body.length());
        writer.write(body);
        writer.flush();
    }
	
	/*
     * send a MIME header
     */
	public void sendHeader(String responseCode, String contentType, int length) throws Exception {
    	writer.write(responseCode + "\r\n");
        Date now = new Date();
        writer.write("Date: " + now + "\r\n");
        writer.write("Server: JHTTP 2.0\r\n");
        writer.write("Content-length: " + length + "\r\n");
        writer.write("Content-type: " + contentType + "\r\n\r\n");
        writer.flush();
    }
	
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}
	
}
