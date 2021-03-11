package com.example.page;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
 

public class HS100 {
 
    public static final String COMMAND_SWITCH_ON = "{\"system\":{\"set_relay_state\":{\"state\":1}}}}";
    public static final String COMMAND_SWITCH_OFF = "{\"system\":{\"set_relay_state\":{\"state\":0}}}}";
    public static final String COMMAND_INFO = "{\"system\":{\"get_sysinfo\":null}}";
 

    public static final int STATE_ON = 1;
    public static final int STATE_OFF = 2;
    private String ip;
    private int port = 9999;
    
    public HS100(String ip) {
        this.ip = ip;
    }
 
    public HS100(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
 
    public String getIp() {
        return ip;
    }
 
    public void setIp(String ip) {
        this.ip = ip;
    }
 
    public int getPort() {
        return port;
    }
 
    public void setPort(int port) {
        this.port = port;
    }
 
    public boolean isPresent() {
 
        try {
 
            InetAddress ip = InetAddress.getByName(getIp());
            return ip.isReachable(500);
        } catch (IOException ex) {}
        return false;
    }
 
    public boolean switchOn() throws IOException {
 
        String jsonData = sendCommand(COMMAND_SWITCH_ON);
        if(jsonData.length() > 0) {
 
            JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
            int errorCode = jo.get("system").getAsJsonObject().get("set_relay_state").getAsJsonObject().get("err_code").getAsInt();
            return errorCode == 0;
        }
        return false;
    }
 
    public boolean switchOff() throws IOException {
 
        String jsonData = sendCommand(COMMAND_SWITCH_OFF);
        if(jsonData.length() > 0) {
 
            JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
            int errorCode = jo.get("system").getAsJsonObject().get("set_relay_state").getAsJsonObject().get("err_code").getAsInt();
            return errorCode == 0;
        }
        return false;
    }
 
    public boolean isOn() throws IOException {
 
        String jsonData = sendCommand(COMMAND_INFO);
        if(jsonData.length() > 0) {
 
            JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
            int state = jo.get("system").getAsJsonObject().get("get_sysinfo").getAsJsonObject().get("relay_state").getAsInt();
            return state == 1 ? true : false;
        }
        return false;
    }
 
    public Map<String, String> getInfo() throws IOException {
 
        Map<String, String> result = new HashMap<>();
        String jsonData = sendCommand(COMMAND_INFO);
        if(jsonData.length() > 0) {
 
            JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
            JsonObject systemInfo = jo.get("system").getAsJsonObject().get("get_sysinfo").getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : systemInfo.entrySet()) {
 
                result.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        return result;
    }
 
    /**
     * send <code>command</code> to plug
     *
     * @param command Command
     * @return Json String of the returned data
     * @throws IOException
     */
    protected String sendCommand(String command) throws IOException {
 
        Socket socket = new Socket(getIp(), 9999);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(encryptWithHeader(command));
 
        InputStream inputStream = socket.getInputStream();
        String data = decrypt(inputStream);
 
        outputStream.close();
        inputStream.close();
        socket.close();
 
        return data;
    }
    
    
    /**
     * Decrypt given data from InputStream
     *  
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String decrypt(InputStream inputStream) throws IOException {
 
        int in;
        int key = 0x2B;
        int nextKey;
        StringBuilder sb = new StringBuilder();
        while((in = inputStream.read()) != -1) {
 
            nextKey = in;
            in = in ^ key;
            key = nextKey;
            sb.append((char) in);
        }
        return "{" + sb.toString().substring(5);
    }
    /**
     * Encrypt a command into plug-readable bytecode
     * 
     * @param command
     * @return
     */
    private int[] encrypt(String command) {
 
        int[] buffer = new int[command.length()];
        int key = 0xAB;
        for(int i = 0; i < command.length(); i++) {
 
            buffer[i] = command.charAt(i) ^ key;
            key = buffer[i];
        }
        return buffer;
    }
    /**
     * Encrypt a command into plug-readable bytecode with header
     * 
     * @param command
     * @return
     */
    private byte[] encryptWithHeader(String command) {
 
        int[] data = encrypt(command);
        byte[] bufferHeader = ByteBuffer.allocate(4).putInt(command.length()).array();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferHeader.length + data.length).put(bufferHeader);
        for(int in : data) {
 
            byteBuffer.put((byte) in);
        }
        return byteBuffer.array();
    }
    
    public String getEnergy() throws IOException {
		String temp = sendCommand("{\"emeter\":{\"get_realtime\":{}}}");

		String[] erg = null;

		if (temp.startsWith("{\"emeter\":{\"get_realtime\":{")) {
			temp = temp.substring(temp.indexOf("\"voltage_mv\""), temp.indexOf("\"err_code\":0")).replace("\"", "")
					.trim();
			erg = temp.split(",");
		} else {
			String ergError = "ERROR";
			erg = new String[1];
			erg[0] = ergError;
		}

		String result = String.join(", ", erg);
		
		return result;
	}
    
}