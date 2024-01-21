package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

        	BufferedReader br = new BufferedReader(new InputStreamReader(in));
        	String line = br.readLine();
        	System.out.println("----line----:"+line);
        	if(line == null) {
        		return;
        	}
        	System.out.println("ddd");
        	String url = HttpRequestUtils.getURL(line);
        	System.out.println("1--------URL : "+url);
        	
        	if(url.startsWith("/user/create")){
        		System.out.println("2--------URL : "+url);
        		HashMap<String,String> map = new HashMap<String,String>();
        		String[] splited = url.split("\\?");
        		String path = splited[1];
        		String[] data = path.split("&");
        		
        		
            	for(int i=0; i< data.length; i++) {
            		String[] key = data[i].split("=");
            		map.put(key[0], key[1]);
            		
            	}
            	User us = new User(map.get("userId"),map.get("password"),map.get("name"),map.get("email"));
            	System.out.println(us.toString());
        		
        		url = "/index.html";
        	}
        	
            			
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
